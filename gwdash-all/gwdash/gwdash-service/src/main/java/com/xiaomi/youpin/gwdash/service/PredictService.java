/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.service;

import com.google.gson.Gson;
import com.site.lookup.util.StringUtils;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gwdash.bo.ReplicatesInfo;
import com.xiaomi.youpin.gwdash.bo.openApi.ReplicateBo;
import com.xiaomi.youpin.gwdash.common.PredictStatusEnum;
import com.xiaomi.youpin.gwdash.dao.model.PredictConfig;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
import com.xiaomi.youpin.oracle.api.service.OracleService;
import com.xiaomi.youpin.oracle.api.service.bo.PredictResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.xiaomi.youpin.gwdash.common.Keys;

@Service
@Slf4j
public class PredictService {
    @Autowired
    private Dao dao;
    @Autowired
    private Redis redis;
    @Autowired
    private ProjectEnvService projectEnvService;

    @Reference(check = false, interfaceClass = OracleService.class, group = "${ref.oracle.service.group}")
    private OracleService oracleService;
    private static final int DEFAULT_THROUGHPUT = 20000;


    private ConcurrentHashMap<String, PredictResult> predictMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, PredictConfig> configMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, int[]> replicatesMap = new ConcurrentHashMap<>();
    private Gson gson = new Gson();
    private static final int DAY_MINUTES = 24 * 60;
    private static final int PERIOD = 1;

    @PostConstruct
    public void init() {
        List<PredictConfig> configs = getActiveConfigs();
        for (PredictConfig config : configs) {
            configMap.put(config.getDomain(), config);
        }

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                for (PredictConfig config : configMap.values()) {
                    if (config.getStatus() == PredictStatusEnum.ON.getCode()) {
                        com.xiaomi.youpin.gwdash.common.Result<List<ProjectEnv>> list = projectEnvService.getList(config.getProjectId());
                        List<ProjectEnv> projectEnvs = list.getData();
                        if (projectEnvs != null) {
                            for (ProjectEnv projectEnv : projectEnvs) {


                                ReplicatesInfo replicatesInfo = getReplicatesInfo(config.getProjectId(), projectEnv.getId());

                                String key = Keys.replicatesKey(config.getProjectId(), projectEnv.getId());

                                int[] replicates = replicatesInfo.getReplicates();

                                ZonedDateTime time = ZonedDateTime.now();
                                int hour = time.getHour();
                                int minute = time.getMinute();
                                ReplicateBo replicateBo = projectEnvService.getReplicateInfo(projectEnv.getId());
                                int timeIndex = (hour * 60 + minute) / PERIOD;
                                if (replicateBo != null && timeIndex >= 0 && timeIndex < replicates.length) {
                                    replicates[timeIndex] = replicateBo.getCurrentReplicates();
                                    redis.del(key);
                                    redis.set(key, gson.toJson(replicates), (int) (TimeUnit.HOURS.toMillis(1)));
                                }

                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.toString());
            }

        }, 0, PERIOD, TimeUnit.MINUTES);

    }

    public ReplicatesInfo getReplicatesInfo(long projectId, long envId) {
        String key = Keys.replicatesKey(projectId, envId);

        int[] replicates = replicatesMap.get(key);
        if (replicates == null || replicates.length != DAY_MINUTES / PERIOD) {
            replicates = parseReplicatesValue(key);
        }

        if (replicates == null || replicates.length != DAY_MINUTES / PERIOD) {
            replicates = new int[DAY_MINUTES / PERIOD];
        }
        replicatesMap.put(key, replicates);
        ReplicatesInfo info = new ReplicatesInfo();
        info.setReplicates(replicates);
        info.setPeriod(PERIOD);
        return info;
    }

    private int[] parseReplicatesValue(String key) {
        String value = redis.get(key);
        int[] replicates = null;
        if (StringUtils.isNotEmpty(value)) {
            replicates = gson.fromJson(value, int[].class);
        }
        return replicates;
    }


    public PredictConfig getConfigByProjectId(long projectId) {
        return dao.fetch(PredictConfig.class, Cnd.where("project_id", "=", projectId));
    }

    public PredictConfig insert(PredictConfig config) {
        long now = System.currentTimeMillis();
        config.setCtime(now);
        config.setUtime(now);
        config.setStatus(config.getStatus());
        config.setTaskId(0);
        configMap.put(config.getDomain(), config);
        return dao.insert(config);
    }

    public boolean updateConfig(PredictConfig config) {
        config.setUtime(System.currentTimeMillis());
        int update = dao.update(config);
        if (update == 1) {
            configMap.put(config.getDomain(), config);
            return true;
        }
        return false;
    }

    public List<PredictConfig> getAllConfigs() {
        return dao.query(PredictConfig.class, null);
    }

    public List<PredictConfig> getActiveConfigs() {
        return dao.query(PredictConfig.class, Cnd.where("status", "=", PredictStatusEnum.ON.getCode()));
    }

    public void setPredict(String domain, PredictResult predictResult) {
        if (StringUtils.isNotEmpty(domain) && predictResult != null) {
            predictResult.setCapacity(makeCapacity(predictResult.getForecast(), configMap.get(domain)));
            predictMap.put(domain, predictResult);
        }
    }

    private int[] makeCapacity(double[] forecast, PredictConfig config) {
        if (forecast == null || config == null) {
            return null;
        }
        int throughput = throughput(config);

        int[] capacity = new int[forecast.length];
        for (int i = 0; i < capacity.length; i++) {
            capacity[i] = Math.max(1, (int) Math.ceil(forecast[i] / throughput));
        }
        return capacity;
    }

    private int throughput(PredictConfig config) {
        return config.getQps() <= 0 ? DEFAULT_THROUGHPUT : config.getQps();
    }

    public PredictResult predict(String domain, String type) {
        return oracleService.predict(domain, type);
    }
}

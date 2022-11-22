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

package com.xiaomi.youpin.gwdash.context;

import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.bo.GatewayServerInfo;
import com.youpin.xiaomi.tesla.bo.GatewayInfo;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;

/**
 * @author goodjava@qq.com
 */
@Component
public class DashServerContext {

    private static final Logger logger = LoggerFactory.getLogger(DashServerContext.class);


    private ConcurrentHashMap<String, GatewayInfo> gatewayMap = new ConcurrentHashMap<>();


    @Autowired
    private Dao dao;


    public GatewayServerInfo addOrUpdateGateWayInfo(GatewayInfo info) {
        long now = System.currentTimeMillis();
        logger.debug("addOrUpdateGateWayInfo:{}", new Gson().toJson(info));
        gatewayMap.put(info.getKey(), info);

        String host = info.getIp();
        int port = info.getPort();

        GatewayServerInfo gsi = dao.fetch(GatewayServerInfo.class, Cnd.where("host", "=", host).and("port", "=", port));
        if (null == gsi) {
            gsi = new GatewayServerInfo();
            gsi.setHost(host);
            gsi.setPort(port);
            gsi.setKey(info.getKey());
            gsi.setCtime(now);
            gsi.setUtime(now);
            gsi.setTenant(info.getTenant());
            dao.insert(gsi);
        } else {
            gsi.setUtime(now);
            dao.update(gsi);
        }
        return gsi;

    }

    /**
     * 只用来更新添加group信息
     */
    public void addOrUpdateGatewayInfoGroup(GatewayInfo info) {
        // 更新db
        int port = info.getPort();
        String host = info.getIp();
        GatewayServerInfo gatewayServerInfo = dao.fetch(GatewayServerInfo.class, Cnd.where("host", "=", host).and("port", "=", port));
        if (gatewayServerInfo != null) {
            gatewayServerInfo.setGroup(info.getGroup());
            dao.insertOrUpdate(gatewayServerInfo);
        }

        // 更新cache
        GatewayInfo gatewayInfo = gatewayMap.get(info.getKey());
        if (gatewayInfo != null) {
            gatewayInfo.setGroup(info.getGroup());
            gatewayMap.put(info.getKey(), gatewayInfo);
        }
    }

    public List<GatewayInfo> gatewayInfoList() {
        List<GatewayInfo> result = new ArrayList<>();
        for (Map.Entry<String, GatewayInfo> entry : gatewayMap.entrySet()) {
            GatewayInfo gatewayInfo = entry.getValue();
            if (StringUtils.isBlank(gatewayInfo.getGroup())) {
                String group = getGroupInfo(gatewayInfo.getIp(), gatewayInfo.getPort());
                if (StringUtils.isNotBlank(group)) {
                    gatewayInfo.setGroup(group);
                }
            }
            result.add(gatewayInfo);
        }
        return result;
    }

    private String getGroupInfo(String ip, Integer port) {
        GatewayServerInfo gatewayServerInfo = dao.fetch(GatewayServerInfo.class, Cnd.where("host", "=", ip).and("port", "=", port));
        if (gatewayServerInfo != null) {
            return gatewayServerInfo.getGroup();
        }
        return null;
    }

    /**
     * 获取在线的agent数量
     * <p>
     * 只查询相同分组的
     *
     * @return
     */
    public long getOnlineAgentNum(String group) {
        //一分钟内更新过的都算在线
        LocalDateTime ldt = LocalDateTime.now().plusMinutes(-1);
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        return this.gatewayMap.entrySet().stream()
                .filter(it -> {
                    String g = it.getValue().getGroup();
                    if (null == g) {
                        g = "";
                    }
                    return StringUtils.equals(group, g) && it.getValue().getUpdateTime().after(Date.from(zdt.toInstant()));
                })
                .count();
    }


    public void delGatewayInfo(String key) {
        gatewayMap.remove(key);
    }


    @PostConstruct
    public void init() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            logger.info("DashServerContext schedule");
        }, 0, 15, TimeUnit.SECONDS);
    }


}

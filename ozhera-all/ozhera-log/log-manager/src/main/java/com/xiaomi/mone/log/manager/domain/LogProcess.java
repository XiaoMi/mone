/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.domain;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.model.vo.AgentLogProcessDTO;
import com.xiaomi.mone.log.api.model.vo.TailLogProcessDTO;
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.api.service.LogProcessCollector;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LogProcess {

    @Resource
    private MilogLogTailDao logtailDao;

    @Getter
    private Map<String, List<UpdateLogProcessCmd.CollectDetail>> tailProgressMap = new ConcurrentHashMap<>(256);

    @Reference(interfaceClass = LogProcessCollector.class, group = "$dubbo.env.group", check = false, timeout = 14000)
    private LogProcessCollector logProcessCollector;

    /**
     * Update log collection progress
     *
     * @param cmd
     */
    public void updateLogProcess(UpdateLogProcessCmd cmd) {
        log.debug("[LogProcess.updateLogProcess] cmd:{} ", cmd);
        if (cmd == null || StringUtils.isEmpty(cmd.getIp())) {
            return;
        }
        tailProgressMap.put(cmd.getIp(), cmd.getCollectList());
    }

    /**
     * Get the progress of agent log collection
     *
     * @param ip
     * @return
     */
    public List<AgentLogProcessDTO> getAgentLogProcess(String ip) {
        return logProcessCollector.getAgentLogProcess(ip);
    }

    /**
     * Get the log collection progress of tail
     *
     * @param tailId
     * @return
     */
    public List<TailLogProcessDTO> getTailLogProcess(Long tailId, String targetIp) {
        if (tailId == null) {
            return Lists.newArrayList();
        }
        MilogLogTailDo logTail = logtailDao.queryById(tailId);
        if (null == logTail) {
            return Lists.newArrayList();
        }
        return logProcessCollector.getTailLogProcess(tailId, logTail.getTail(), targetIp);
    }

    /**
     * Get the log collection progress of the store
     *
     * @param storeId
     * @return
     */
    public List<TailLogProcessDTO> getStoreLogProcess(Long storeId, String targetIp) {
        if (storeId == null) {
            return new ArrayList<>();
        }
        List<MilogLogTailDo> logtailList = logtailDao.getMilogLogtailByStoreId(storeId);
        List<TailLogProcessDTO> dtoList = new ArrayList<>();
        List<TailLogProcessDTO> processList;
        for (MilogLogTailDo milogLogtailDo : logtailList) {
            processList = getTailLogProcess(milogLogtailDo.getId(), targetIp);
            if (!processList.isEmpty()) {
                dtoList.addAll(processList);
            }
        }
        return dtoList;
    }

    public List<UpdateLogProcessCmd.CollectDetail> getColProcessImperfect(Double progressRation) {
        return logProcessCollector.getColProcessImperfect(progressRation);
    }
}

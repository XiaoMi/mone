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
package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.model.meta.AppLogMeta;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.model.meta.LogPattern;
import com.xiaomi.mone.log.manager.dao.MilogAppTopicRelDao;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.service.MiLogMetaManageService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shanwb
 * @date 2021-07-09
 */
@Slf4j
@Service
public class MiLogMetaManageServiceImpl implements MiLogMetaManageService {

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private MilogAppTopicRelDao milogAppTopicRelDao;

    @Override
    public LogCollectMeta queryLogCollectMeta(String agentId, String agentIp) {
        List<Long> appIdList = Lists.newArrayList();
        Map<Long, List<MilogLogTailDo>> miLogTailMap = milogLogtailDao.getMilogLogtailByAppId(appIdList);
        LogCollectMeta meta = new LogCollectMeta();
        meta.setAgentId(agentId);
        meta.setAgentIp(agentIp);
        meta.setAgentMachine(null);
        List<AppLogMeta> metaList = new ArrayList<>();
        for (Map.Entry<Long, List<MilogLogTailDo>> entry : miLogTailMap.entrySet()) {
            AppLogMeta appLogMeta = new AppLogMeta();
            appLogMeta.setAppId(entry.getKey());
            appLogMeta.setAppName(null);
//            MilogAppTopicRel topicRel = milogAppTopicRelDao.queryByAppId(entry.getKey(), MoneContext.getCurrentUser().getZone());
//            MQConfig mqConfig = new MQConfig();
//            try {
//                BeanUtilsBean.getInstance().copyProperties(mqConfig, topicRel.getMq_config());
//            } catch (Exception e) {
//                log.error("Data copy exception:{}", new Gson().toJson(topicRel));
//            }
//            appLogMeta.setMQConfig(mqConfig);
            List<LogPattern> logPatternList = new ArrayList<>();
            for (MilogLogTailDo milogLogtail : entry.getValue()) {
                LogPattern logPattern = new LogPattern();
                logPattern.setLogtailId(milogLogtail.getId());
                // TODO
                logPattern.setLogType(milogLogtail.getParseType());
                logPatternList.add(logPattern);
            }
            appLogMeta.setLogPatternList(logPatternList);
            metaList.add(appLogMeta);
        }
        meta.setAppLogMetaList(metaList);
        return meta;
    }


}

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
package com.xiaomi.mone.log.manager.service.init_sql;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateDetailMapper;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDetailDO;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/3/3 10:46
 */
@Service
public class HeraLogTemplateService extends BaseService {

    @Resource
    private MilogLogTemplateMapper milogLogTemplateMapper;
    @Resource
    private MilogLogTemplateDetailMapper milogLogTemplateDetailMapper;

    public void init() {
        for (LogTypeEnum typeEnum : LogTypeEnum.values()) {
            //查询日志类型
            QueryWrapper<MilogLogTemplateDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type", typeEnum.getType());
            List<MilogLogTemplateDO> templateDOS = milogLogTemplateMapper.selectList(queryWrapper);
            if (CollectionUtils.isEmpty(templateDOS)) {
                MilogLogTemplateDO logTemplateDO = getInsertLogTemplateDO(typeEnum,
                        Arrays.stream(MachineRegionEnum.values()).map(MachineRegionEnum::getEn)
                                .collect(Collectors.joining(SYMBOL_COMMA)));
                //查询类型模板是否存在
                MilogLogTemplateDetailDO detailDO = milogLogTemplateDetailMapper.getByTemplateId(logTemplateDO.getId());
                if (null == detailDO) {
                    insertTemplateDetail(typeEnum, logTemplateDO);
                }
            }
        }
    }

    private void insertTemplateDetail(LogTypeEnum typeEnum, MilogLogTemplateDO logTemplateDO) {
        MilogLogTemplateDetailDO detailDO;
        detailDO = new MilogLogTemplateDetailDO();
        detailDO.setTemplateId(logTemplateDO.getId().toString());
        if (typeEnum == LogTypeEnum.APP_LOG_MULTI) {
            detailDO.setPropertiesKey("timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,methodName:1,message:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3,podName:1");
            detailDO.setPropertiesType("date,keyword,keyword,text,text,keyword,keyword,text,keyword,keyword,keyword,keyword,keyword,keyword,long,keyword");
        }
        if (typeEnum == LogTypeEnum.NGINX) {
            detailDO.setPropertiesKey("message:1,hostname:1,http_code:1,method:1,protocol:1,referer:1,timestamp:1,ua:1,url:1,linenumber:3,logip:3");
            detailDO.setPropertiesType("text,text,keyword,keyword,keyword,text,timestamp,text,text,long,keyword");
        }
        if (typeEnum == LogTypeEnum.OPENTELEMETRY) {
            detailDO.setPropertiesKey("logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3");
            detailDO.setPropertiesType("keyword,keyword,keyword,keyword,keyword,keyword,long");
        }
        if (typeEnum == LogTypeEnum.DOCKER) {
            detailDO.setPropertiesKey("logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3");
            detailDO.setPropertiesType("keyword,keyword,keyword,keyword,keyword,keyword,long");
        }
        if (typeEnum == LogTypeEnum.APP_LOG_SIGNAL) {
            detailDO.setPropertiesKey("timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,methodName:1,message:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3,podName:1");
            detailDO.setPropertiesType("date,keyword,keyword,text,text,keyword,keyword,text,keyword,keyword,keyword,keyword,keyword,keyword,long,keyword");
        }
        if (typeEnum == LogTypeEnum.ORIGIN_LOG) {
            detailDO.setPropertiesKey("timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,methodName:1,message:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3,podName:1");
            detailDO.setPropertiesType("date,keyword,keyword,text,text,keyword,keyword,text,keyword,keyword,keyword,keyword,keyword,keyword,long,keyword");
        }
        if (typeEnum == LogTypeEnum.FREE) {
            detailDO.setPropertiesKey("logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3");
            detailDO.setPropertiesType("keyword,keyword,keyword,keyword,keyword,keyword,long");
        }
        detailDO.setCtime(Instant.now().toEpochMilli());
        detailDO.setUtime(Instant.now().toEpochMilli());
        milogLogTemplateDetailMapper.insert(detailDO);
    }

    @NotNull
    private MilogLogTemplateDO getInsertLogTemplateDO(LogTypeEnum typeEnum, String supportArea) {
        MilogLogTemplateDO logTemplateDO = new MilogLogTemplateDO();
        logTemplateDO.setTemplateName(typeEnum.getTypeName());
        logTemplateDO.setType(typeEnum.getType());
        logTemplateDO.setOrderCol(typeEnum.getSort());
        logTemplateDO.setSupportArea(supportArea);
        logTemplateDO.setSupportedConsume(typeEnum.getSupportedConsume());
        logTemplateDO.setCtime(Instant.now().toEpochMilli());
        logTemplateDO.setUtime(Instant.now().toEpochMilli());
        milogLogTemplateMapper.insert(logTemplateDO);
        return logTemplateDO;
    }
}

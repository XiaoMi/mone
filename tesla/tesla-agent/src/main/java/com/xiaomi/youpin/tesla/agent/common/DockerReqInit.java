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

package com.xiaomi.youpin.tesla.agent.common;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import com.xiaomi.youpin.tesla.agent.service.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
@Slf4j
public class DockerReqInit {

    public void initReq(DockerReq req) {
        initAttachments(req);
        String memLimitStr = LabelService.ins().getLabelValue(req.getLabels(), LabelService.MEM_LIMIT);
        int memLimit = StringUtils.isNotEmpty(memLimitStr) ? Integer.valueOf(memLimitStr) : 0;
        log.info("mem_limit:{}", memLimit);
        req.setMemLimit(memLimit);


        String MAX_DIRECTMEMORY_SIZE = LabelService.ins().getLabelValue(req.getLabels(), LabelService.MAX_DIRECTMEMORY_SIZE);
        int mds = StringUtils.isNotEmpty(MAX_DIRECTMEMORY_SIZE) ? Integer.valueOf(MAX_DIRECTMEMORY_SIZE) : 256;
        log.info("max_directmemory_size:{}", mds);
        req.setMaxDirectMemorySize(mds);


        String mmsStr = LabelService.ins().getLabelValue(req.getLabels(), LabelService.MMS);
        int mms = StringUtils.isNotEmpty(mmsStr) ? Integer.valueOf(mmsStr) : 256;
        log.info("mms:{}", mms);
        req.setMms(mms);


        String lp = LabelService.ins().getLabelValue(req.getLabels(), LabelService.LOG_PATH);
        if (StringUtils.isNotEmpty(lp)) {
            req.setLogPath(lp);
        }
    }


    private void initAttachments(DockerReq req) {
        if (null == req.getAttachments()) {
            req.setAttachments(Maps.newHashMap());
        }
    }
}

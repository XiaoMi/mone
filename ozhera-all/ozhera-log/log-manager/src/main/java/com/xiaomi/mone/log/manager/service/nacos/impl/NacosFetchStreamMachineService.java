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
package com.xiaomi.mone.log.manager.service.nacos.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Lists;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.nacos.FetchStreamMachineService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.STREAM_CONTAINER_POD_NAME_KEY;
import static com.xiaomi.mone.log.common.Constant.STRIKETHROUGH_SYMBOL;

@Slf4j
public class NacosFetchStreamMachineService implements FetchStreamMachineService {

    private final NacosNaming nacosNaming;

    public NacosFetchStreamMachineService(NacosNaming nacosNaming) {
        this.nacosNaming = nacosNaming;
    }

    @Override
    public List<String> streamMachineUnique() {
        if (null == nacosNaming) {
            throw new MilogManageException("please set nacos naming first");
        }
        List<String> uniqueKeys = Lists.newArrayList();
        try {
            List<Instance> allInstances = nacosNaming.getAllInstances(CommonExtensionServiceFactory.getCommonExtensionService().getHeraLogStreamServerName());
            for (Instance instance : allInstances) {
                if (instance.getMetadata().containsKey(STREAM_CONTAINER_POD_NAME_KEY)) {
                    uniqueKeys.add(StringUtils.substringAfterLast(instance.getMetadata().get(STREAM_CONTAINER_POD_NAME_KEY), STRIKETHROUGH_SYMBOL));
                } else {
                    uniqueKeys.add(instance.getIp());
                }
            }
            uniqueKeys = uniqueKeys.stream().distinct().collect(Collectors.toList());
        } catch (NacosException e) {
            log.error("nacos queryStreamMachineIps error", e);
        }
        return uniqueKeys;
    }
}

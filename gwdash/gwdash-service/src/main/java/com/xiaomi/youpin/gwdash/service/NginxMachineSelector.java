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

import com.xiaomi.youpin.gwdash.bo.DockerQueryParam;
import com.xiaomi.youpin.gwdash.bo.MachineBo;
import com.xiaomi.youpin.gwdash.bo.MachineLabels;
import com.xiaomi.youpin.gwdash.selector.Selector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * 用来筛选nginx机器
 */
@Service
public class NginxMachineSelector implements Selector<MachineBo, DockerQueryParam> {

    @Autowired
    private MachineManagementService machineManagementService;

    /**
     * type = docker
     *
     * @param param
     * @return
     */
    @Override
    public List<MachineBo> select(DockerQueryParam param) {
        List<MachineBo> list = machineManagementService.queryMachineListByLabel("nginx", "true");
        return list.stream().collect(Collectors.toList());
    }
}

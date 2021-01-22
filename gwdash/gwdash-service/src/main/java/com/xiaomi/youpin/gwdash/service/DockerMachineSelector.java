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
import com.xiaomi.youpin.gwdash.bo.MachineLabels;
import com.xiaomi.youpin.gwdash.bo.MachineBo;
import com.xiaomi.youpin.gwdash.dao.model.MachineLabel;
import com.xiaomi.youpin.gwdash.selector.Selector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * 用来筛选docker机器
 */
@Service
public class DockerMachineSelector implements Selector<MachineBo, DockerQueryParam> {

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
        List<MachineBo> list = machineManagementService.queryMachineListByLabel(param.getPair().getKey(), param.getPair().getValue());


        List<String> installedList = param.getInstalledIps();

        if (param.isExpansion()) {
            list = list.stream().filter(it -> !installedList.contains(it.getIp())).collect(Collectors.toList());
        } else {
            //优先选择已经安装过的
            list = list.stream().map(it -> {
                if (installedList.contains(it.getIp())) {
                    it.setOrder(10000);
                }
                return it;
            }).sorted((a, b) -> b.getOrder().compareTo(a.getOrder()))
                    .collect(Collectors.toList());
        }


        return list.stream().filter(it -> {
            //之前已经安装过了
            if (it.getOrder().equals(10000)) {
                return true;
            }


            long prepareCpu = it.getPrepareLabelValue(MachineLabels.Cpu);
            long prepareMem = it.getPrepareLabelValue(MachineLabels.Mem);

            int cpu = Integer.parseInt(it.getLabels().get(MachineLabels.Cpu));
            int useCpu = Integer.parseInt(it.getLabels().get(MachineLabels.UseCpu));

            if (cpu - useCpu - prepareCpu < param.getCpuNum()) {
                return false;
            }

            long mem = Long.valueOf(it.getLabels().get(MachineLabels.Mem));
            long useMem = Long.valueOf(it.getLabels().get(MachineLabels.UseMem));

            if (mem - useMem - prepareMem < param.getMem()) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }
}

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

import ao.CaseAO;
import ao.ChainLayoutAO;
import cst.api.service.ChainTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description: 链路压测
 * @author zhenghao
 *
 */
@Slf4j
@Service
public class ChainService {

    @Reference(group = "${ref.chain.service.group}", interfaceClass = cst.api.service.ChainTask.class, check = false)
    private cst.api.service.ChainTask chainTask;

    public String addChain(List<ChainLayoutAO> layoutList) {
        return chainTask.addChain(layoutList);
    }

    public List<ChainLayoutAO> executeChain(String uuid) {
        return chainTask.executeChain(uuid);
    }

    public List<CaseAO> showCaseList() {
         return chainTask.showCaseList();
    }

    public Map<String, Object> chainPage(String uid, Integer page, Integer pageSize, String chainAliasName) {
        return chainTask.chainPage(uid, page, pageSize, chainAliasName);
    }

    public List<ChainLayoutAO> getChainByUUid(String uuid) {
        return chainTask.getChainByUUid(uuid);
    }

    public String updateChain(List<ChainLayoutAO> layoutList, String uuid) {
        return chainTask.updateChain(layoutList, uuid);
    }

    public String deleteChain(String uuid) {
        return chainTask.deleteChain(uuid);
    }

}
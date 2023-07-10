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
package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.MachineParamParam;
import com.xiaomi.mone.log.manager.model.bo.MachineQueryParam;

import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/16 11:32
 */
public interface MilogMachineService {

    Result<String> addMachineInfo(MachineParamParam param);

    Result<String> deleteMachineInfo(Long id);

    Result<Map<String, Object>> queryMachineByPage(MachineQueryParam param);

    Result<MachineParamParam> queryMachineInfoById(Long id);
}

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

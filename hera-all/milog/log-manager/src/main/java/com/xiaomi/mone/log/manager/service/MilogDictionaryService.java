package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.MilogDictionaryParam;
import com.xiaomi.mone.log.manager.model.dto.DictionaryDTO;

import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/26 15:34
 */
public interface MilogDictionaryService {

    Result<Map<Integer,List<DictionaryDTO<?>>>> queryDictionaryList(MilogDictionaryParam codes);

    Result<String> downLoadFile();

    Result<String> fixLogTailMilogAppId(String appName);

}

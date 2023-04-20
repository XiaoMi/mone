package com.xiaomi.mone.log.manager.service.extension.dictionary;

import com.xiaomi.mone.log.manager.model.dto.DictionaryDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/12 10:35
 */
public interface DictionaryExtensionService {

    String DEFAULT_DICTIONARY_EXTENSION_SERVICE_KEY = "defaultDictionaryExtensionService";

    List<DictionaryDTO<?>> queryMiddlewareConfigDictionary(String monitorRoomEn);

    List<DictionaryDTO<?>> queryMqTypeDictionary();

    List<DictionaryDTO<?>> queryAppType();

    List<MilogLogTailDo> querySpecialTails();

    List<DictionaryDTO<?>> queryMachineRegion();

    List<DictionaryDTO<?>> queryDeployWay();

    List<DictionaryDTO<?>> queryResourceTypeDictionary();

    List<DictionaryDTO> queryExistsTopic(String ak, String sk, String nameServer, String serviceUrl,
                                         String authorization, String orgId, String teamId);
}

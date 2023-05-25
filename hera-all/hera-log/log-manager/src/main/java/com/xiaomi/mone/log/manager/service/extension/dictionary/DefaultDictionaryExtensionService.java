package com.xiaomi.mone.log.manager.service.extension.dictionary;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.enums.DeployWayEnum;
import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.enums.ProjectTypeEnum;
import com.xiaomi.mone.log.manager.dao.MilogMiddlewareConfigDao;
import com.xiaomi.mone.log.manager.model.dto.DictionaryDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.manager.service.extension.dictionary.DictionaryExtensionService.DEFAULT_DICTIONARY_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/12 10:36
 */
@Service(name = DEFAULT_DICTIONARY_EXTENSION_SERVICE_KEY)
@Slf4j
public class DefaultDictionaryExtensionService implements DictionaryExtensionService {

    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;

    @Override
    public List<DictionaryDTO<?>> queryMiddlewareConfigDictionary(String monitorRoomEn) {
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigDao.queryCurrentMontorRoomMQ(monitorRoomEn);
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        Arrays.stream(MiddlewareEnum.values())
                .filter(middlewareEnum -> middlewareEnum == MiddlewareEnum.ROCKETMQ)
                .forEach(middlewareEnum -> {
                    DictionaryDTO dictionaryDTO = new DictionaryDTO<>();
                    dictionaryDTO.setValue(middlewareEnum.getCode());
                    dictionaryDTO.setLabel(middlewareEnum.getName());
                    if (CollectionUtils.isNotEmpty(milogMiddlewareConfigs)) {
                        dictionaryDTO.setChildren(milogMiddlewareConfigs.stream().filter(middlewareConfig -> middlewareEnum.getCode().equals(middlewareConfig.getType())).map(middlewareConfig -> {
                            DictionaryDTO<Long> childDictionaryDTO = new DictionaryDTO<>();
                            childDictionaryDTO.setValue(middlewareConfig.getId());
                            childDictionaryDTO.setLabel(middlewareConfig.getAlias());
//                    if (MiddlewareEnum.ROCKETMQ.getCode().equals(middlewareConfig.getType())) {
//                        List<DictionaryDTO> existsTopic = rocketMqConfigService.queryExistsTopic(middlewareConfig.getAk(), middlewareConfig.getSk(),
//                                middlewareConfig.getNameServer(), middlewareConfig.getServiceUrl(), middlewareConfig.getAuthorization(), middlewareConfig.getOrgId(), middlewareConfig.getTeamId());
//                        childDictionaryDTO.setChildren(existsTopic);
//                    }
                            return childDictionaryDTO;
                        }).collect(Collectors.toList()));
                    }
                    dictionaryDTOS.add(dictionaryDTO);
                });
        return dictionaryDTOS;
    }

    @Override
    public List<DictionaryDTO<?>> queryMqTypeDictionary() {
        return generateCommonDictionary(middlewareEnum -> Boolean.TRUE);
    }

    @Override
    public List<DictionaryDTO<?>> queryAppType() {
        return Arrays.stream(ProjectTypeEnum.values())
                .map(projectTypeEnum -> {
                    DictionaryDTO<Integer> dictionaryDTO = new DictionaryDTO<>();
                    dictionaryDTO.setValue(projectTypeEnum.getCode());
                    dictionaryDTO.setLabel(projectTypeEnum.getType());

                    dictionaryDTO.setShowDeploymentType(Boolean.TRUE);
                    dictionaryDTO.setShowEnvGroup(Boolean.TRUE);
                    dictionaryDTO.setShowServiceIp(Boolean.TRUE);
                    dictionaryDTO.setShowMqConfig(Boolean.TRUE);

                    return dictionaryDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MilogLogTailDo> querySpecialTails() {
        return Lists.newArrayList();
    }

    @Override
    public List<DictionaryDTO<?>> queryMachineRegion() {
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        for (MachineRegionEnum value : MachineRegionEnum.values()) {
            DictionaryDTO dictionaryDTO = new DictionaryDTO();
            dictionaryDTO.setLabel(value.getCn());
            dictionaryDTO.setValue(value.getEn());
            dictionaryDTOS.add(dictionaryDTO);
        }
        return dictionaryDTOS;
    }

    @Override
    public List<DictionaryDTO<?>> queryDeployWay() {
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        for (DeployWayEnum value : DeployWayEnum.values()) {
            DictionaryDTO dictionaryDTO = new DictionaryDTO();
            dictionaryDTO.setLabel(value.getName());
            dictionaryDTO.setValue(value.getCode());
            dictionaryDTOS.add(dictionaryDTO);
        }
        return dictionaryDTOS;
    }

    @Override
    public List<DictionaryDTO<?>> queryResourceTypeDictionary() {
        return generateCommonDictionary(middlewareEnum -> MiddlewareEnum.ROCKETMQ == middlewareEnum |
                MiddlewareEnum.ELASTICSEARCH == middlewareEnum);
    }

    @Override
    public List<DictionaryDTO> queryExistsTopic(String ak, String sk, String nameServer, String serviceUrl, String authorization, String orgId, String teamId) {
        return Lists.newArrayList();
    }

    private List<DictionaryDTO<?>> generateCommonDictionary(Predicate<MiddlewareEnum> filter) {
        List<DictionaryDTO> rDictionaryDTOS = Arrays.stream(MachineRegionEnum.values())
                .map(machineRegionEnum ->
                        DictionaryDTO.Of(machineRegionEnum.getEn(), machineRegionEnum.getCn()))
                .collect(Collectors.toList());
        return Arrays.stream(MiddlewareEnum.values())
                .filter(filter)
                .map(middlewareEnum -> {
                    DictionaryDTO<Integer> dictionaryDTO = new DictionaryDTO<>();
                    dictionaryDTO.setValue(middlewareEnum.getCode());
                    dictionaryDTO.setLabel(middlewareEnum.getName());
                    dictionaryDTO.setChildren(rDictionaryDTOS);
                    return dictionaryDTO;
                }).collect(Collectors.toList());
    }
}

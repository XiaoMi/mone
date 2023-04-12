package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogMiddlewareConfigDao;
import com.xiaomi.mone.log.manager.model.bo.MilogDictionaryParam;
import com.xiaomi.mone.log.manager.model.dto.DictionaryDTO;
import com.xiaomi.mone.log.manager.model.dto.MotorRoomDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.service.MilogDictionaryService;
import com.xiaomi.mone.log.manager.service.extension.dictionary.DictionaryExtensionService;
import com.xiaomi.mone.log.manager.service.extension.dictionary.DictionaryExtensionServiceFactory;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/26 15:34
 */
@Slf4j
@Service
public class MilogDictionaryServiceImpl implements MilogDictionaryService {

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private MilogLogstoreDao milogLogstoreDao;

    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;

    private DictionaryExtensionService dictionaryExtensionService;

    public void init() {
        dictionaryExtensionService = DictionaryExtensionServiceFactory.getAgentExtensionService();
    }

    /**
     * @param dictionaryParam code :
     *                        1001:创建tail的时候选择采集的mq配置
     *                        1002：mq类型
     *                        1003: mq类型及下边的topic信息
     *                        1004：应用类型
     *                        1005: 机房下的store及应用
     *                        1006:机房信息
     *                        1007:部署方式
     *                        1008:资源tab页码
     * @return
     */
    @Override
    public Result<Map<Integer, List<DictionaryDTO<?>>>> queryDictionaryList(MilogDictionaryParam dictionaryParam) {
        if (null == dictionaryParam || CollectionUtils.isEmpty(dictionaryParam.getCodes())) {
            return Result.failParam("code 不能为空");
        }
        if (CollectionUtils.isNotEmpty(dictionaryParam.getCodes().stream().filter(code -> code.intValue() == 1003).collect(Collectors.toList())) && null == dictionaryParam.getMiddlewareId()) {
            return Result.failParam("middlewareId 不能为空");
        }
        if (CollectionUtils.isNotEmpty(dictionaryParam.getCodes().stream().filter(code -> code.intValue() == 1005).collect(Collectors.toList())) && StringUtils.isEmpty(dictionaryParam.getNameEn())) {
            return Result.failParam("nameEn 不能为空");
        }
        Map<Integer, List<DictionaryDTO<?>>> dictionaryDTO = Maps.newHashMap();
        dictionaryParam.getCodes().stream().forEach(code -> {
            switch (code) {
                case 1001:
                    dictionaryDTO.put(code, dictionaryExtensionService.queryMiddlewareConfigDictionary(MachineRegionEnum.CN_MACHINE.getEn()));
                    break;
                case 1002:
                    dictionaryDTO.put(code, dictionaryExtensionService.queryMqTypeDictionary());
                    break;
                case 1003:
                    dictionaryDTO.put(code, queryAllRocketMqTopic(dictionaryParam.getMiddlewareId()));
                case 1004:
                    dictionaryDTO.put(code, dictionaryExtensionService.queryAppType());
                    break;
                case 1005:
                    dictionaryDTO.put(code, queryStoreTailByEnName(dictionaryParam.getNameEn()));
                    break;
                case 1006:
                    dictionaryDTO.put(code, dictionaryExtensionService.queryMachineRegion());
                    break;
                case 1007:
                    dictionaryDTO.put(code, dictionaryExtensionService.queryDeployWay());
                    break;
                case 1008:
                    dictionaryDTO.put(code, dictionaryExtensionService.queryResourceTypeDictionary());
            }
        });
        log.debug("返回值：{}", new Gson().toJson(dictionaryDTO));
        return Result.success(dictionaryDTO);
    }

    private List<DictionaryDTO<?>> queryStoreTailByEnName(String nameEn) {
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        List<MilogLogTailDo> milogLogtailDos = dictionaryExtensionService.querySpecialTails();
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            List<Long> storedIds = milogLogtailDos.stream()
                    .filter(milogLogtailDo -> milogLogtailDo.getMotorRooms().stream()
                            .map(MotorRoomDTO::getNameEn).collect(Collectors.toList()).contains(nameEn))
                    .map(MilogLogTailDo::getStoreId).collect(Collectors.toList());
            storedIds.forEach(storeId -> {
                MilogLogStoreDO milogLogstoreDO = milogLogstoreDao.queryById(storeId);
                DictionaryDTO dictionaryDTO = new DictionaryDTO();
                dictionaryDTO.setLabel(milogLogstoreDO.getLogstoreName());
                dictionaryDTO.setValue(milogLogstoreDO.getId());
                dictionaryDTO.setChildren(queryTailByStore(milogLogtailDos, storeId, nameEn));
                dictionaryDTOS.add(dictionaryDTO);
            });
        }
        return dictionaryDTOS;
    }

    private List<DictionaryDTO<?>> queryTailByStore(List<MilogLogTailDo> milogLogtailDos, Long storeId, String nameEn) {
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            milogLogtailDos = milogLogtailDos.stream()
                    .filter(milogLogtailDo -> storeId.equals(milogLogtailDo.getStoreId()))
                    .filter(milogLogtailDo -> milogLogtailDo.getMotorRooms().stream()
                            .map(MotorRoomDTO::getNameEn).collect(Collectors.toList()).contains(nameEn))
                    .collect(Collectors.toList());
            milogLogtailDos.forEach(milogLogtailDo -> {
                DictionaryDTO dictionaryDTO = new DictionaryDTO();
                dictionaryDTO.setLabel(milogLogtailDo.getTail());
                dictionaryDTO.setValue(milogLogtailDo.getId());
                dictionaryDTOS.add(dictionaryDTO);
            });
        }
        return dictionaryDTOS;
    }

    @Override
    public Result<String> downLoadFile() {
        File file = new File("D:\\work\\rocketmq.log");
        String str = "";
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Result<String> fixLogTailMilogAppId(String appName) {
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryTailByAppName(appName);
        log.info("同步修复tail的milogAppId,共有{}条", milogLogtailDos.size());
        int count = 0;
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (MilogLogTailDo milogLogtailDo : milogLogtailDos) {
            ++count;
            log.info("开始同步修复tail的第{}条，还剩下{}跳", count, milogLogtailDos.size() - count);
            if (null == milogLogtailDo.getMilogAppId()) {
            }
        }
        stopwatch.stop();
        log.info("同步修复tail的milogAppId，花费时间：{} s", stopwatch.elapsed().getSeconds());
        return Result.success();
    }

    private List<DictionaryDTO<?>> queryAllRocketMqTopic(Long middlewareId) {
        MilogMiddlewareConfig middlewareConfig = milogMiddlewareConfigDao.queryById(middlewareId);
        List<DictionaryDTO> dictionaryDTOS = dictionaryExtensionService.queryExistsTopic(middlewareConfig.getAk(), middlewareConfig.getSk(), middlewareConfig.getNameServer(),
                middlewareConfig.getServiceUrl(), middlewareConfig.getAuthorization(), middlewareConfig.getOrgId(), middlewareConfig.getTeamId());
        Set<String> existTopics = dictionaryDTOS.stream().map(dictionaryDTO -> dictionaryDTO.getValue().toString()).collect(Collectors.toSet());
        List<DictionaryDTO<?>> dictionaryDTOList = Lists.newArrayList();
        existTopics.stream().map(s -> dictionaryDTOList.add(DictionaryDTO.Of(s, s)));
        return dictionaryDTOList;
    }

}

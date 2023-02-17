package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.log.api.enums.DeployWayEnum;
import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.enums.ProjectTypeEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.dao.*;
import com.xiaomi.mone.log.manager.model.bo.MilogDictionaryParam;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.service.MilogDictionaryService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
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
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;
    @Resource
    private RocketMqConfigService rocketMqConfigService;
    @Resource
    private OkHttpClient okHttpClient;
    @Resource
    private Gson gson;
    @Resource
    private MilogAppTopicServiceImpl milogAppTopicService;
    @Resource
    private MilogAppTopicRelDao milogAppTopicRelDao;
    @Resource
    private MilogLogTailDao milogLogtailDao;
    @Resource
    private LogstoreDao logstoreDao;
    @Resource
    private SpaceDao milogSpaceDao;

    @Value("$mis.url")
    private String misUrl;

    @Value("$radar.url")
    private String radarUrl;

    @Value("$mis.token")
    private String misToken;

    @Value("$app.env")
    private String env;

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
                    dictionaryDTO.put(code, generateMiddlewareConfigDictionary(MachineRegionEnum.CN_MACHINE.getEn()));
                    break;
                case 1002:
                    dictionaryDTO.put(code, generateMqTypeDictionary());
                    break;
                case 1003:
                    dictionaryDTO.put(code, queryAllRocketMqTopic(dictionaryParam.getMiddlewareId()));
                case 1004:
                    dictionaryDTO.put(code, generateAppType());
                    break;
                case 1005:
                    dictionaryDTO.put(code, queryStoreTailByEnName(dictionaryParam.getNameEn()));
                    break;
                case 1006:
                    dictionaryDTO.put(code, queryMachineRegion());
                    break;
                case 1007:
                    dictionaryDTO.put(code, queryDeployWay());
                    break;
                case 1008:
                    dictionaryDTO.put(code, generateResourceTypeDictionary());
            }
        });
        log.debug("返回值：{}", new Gson().toJson(dictionaryDTO));
        return Result.success(dictionaryDTO);
    }

    private List<DictionaryDTO<?>> queryDeployWay() {
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        for (DeployWayEnum value : DeployWayEnum.values()) {
            DictionaryDTO dictionaryDTO = new DictionaryDTO();
            dictionaryDTO.setLabel(value.getName());
            dictionaryDTO.setValue(value.getCode());
            dictionaryDTOS.add(dictionaryDTO);
        }
        return dictionaryDTOS;
    }

    private List<DictionaryDTO<?>> queryMachineRegion() {
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        for (MachineRegionEnum value : MachineRegionEnum.values()) {
            DictionaryDTO dictionaryDTO = new DictionaryDTO();
            dictionaryDTO.setLabel(value.getCn());
            dictionaryDTO.setValue(value.getEn());
            dictionaryDTOS.add(dictionaryDTO);
        }
        return dictionaryDTOS;
    }

    private List<DictionaryDTO<?>> queryStoreTailByEnName(String nameEn) {
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryMisTail();
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            List<Long> storedIds = milogLogtailDos.stream()
                    .filter(milogLogtailDo -> milogLogtailDo.getMotorRooms().stream()
                            .map(MotorRoomDTO::getNameEn).collect(Collectors.toList()).contains(nameEn))
                    .map(MilogLogTailDo::getStoreId).collect(Collectors.toList());
            storedIds.forEach(storeId -> {
                MilogLogStoreDO milogLogstoreDO = logstoreDao.queryById(storeId);
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

    private List<DictionaryDTO<?>> generateAppType() {
        return Arrays.stream(ProjectTypeEnum.values())
                .map(projectTypeEnum -> {
                    DictionaryDTO<Integer> dictionaryDTO = new DictionaryDTO<>();
                    dictionaryDTO.setValue(projectTypeEnum.getCode());
                    dictionaryDTO.setLabel(projectTypeEnum.getType());
                    return dictionaryDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Result<String> synchronousMisApp(String serviceName) {
        String url = misUrl + "/api/service/allservice";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(new FormBody.Builder().add("token", misToken)
                            .add("service_name", serviceName).build())
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String rstJson = response.body().string();
                if (StringUtils.isNotEmpty(serviceName)) {
                    MisResponseDTO<MisAppInfoDTO> rst = gson.fromJson(rstJson, new TypeToken<MisResponseDTO<MisAppInfoDTO>>() {
                    }.getType());
                    MisAppInfoDTO data = rst.getData();
                    milogAppTopicService.synchronousMisApp(Arrays.asList(data));
                } else {
                    MisResponseDTO<List<MisAppInfoDTO>> rst = gson.fromJson(rstJson, new TypeToken<MisResponseDTO<List<MisAppInfoDTO>>>() {
                    }.getType());
                    List<MisAppInfoDTO> data = rst.getData();
                    milogAppTopicService.synchronousMisApp(data);
                }
            }
            return null;
        } catch (Exception e) {
            log.error(String.format("http query from mis system error,url:%s,token:%s", url, misToken), e);
        }
        return Result.success();
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

    @Override
    public Result<String> synchronousRadarApp(String serviceName) {
        boolean isEnd = false;
        int pageNum = 1;
        int pageSize = 100;
        if ("prod".equals(env)) {
            env = "pro";
        }
        while (!isEnd) {
            String url = radarUrl + "/radar/getlist" + String.format("?page=%s&pageSize=%s&appName=%s&appType=1&user=&env=%s", pageNum, pageSize, serviceName, env);
            try {
                Request request = new Request.Builder()
                        .url(url).build();
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String rstJson = response.body().string();
                    RadarResponseDTO<List<RadarAppInfoDTO>> rst = gson.fromJson(rstJson, new TypeToken<RadarResponseDTO<List<RadarAppInfoDTO>>>() {
                    }.getType());
                    RadarResponseDTO.RadarData<List<RadarAppInfoDTO>> radarData = rst.getData();
                    List<RadarAppInfoDTO> radarAppInfoDTOS = radarData.getList();
                    if (CollectionUtils.isNotEmpty(radarAppInfoDTOS)) {
                        milogAppTopicService.synchronousRadarApp(radarAppInfoDTOS);
                    } else {
                        isEnd = true;
                    }
                    log.info("start sync radar app,current pageNum:{},total pageNum:{},remain num:{} ", pageNum, radarData.getPage(), radarData.getPage() - pageNum);
                }
                ++pageNum;
            } catch (Exception e) {
                log.error(String.format("http query from radar system error,url:%s", url), e);
            }
        }
        return Result.success();
    }

    private List<DictionaryDTO<?>> queryAllRocketMqTopic(Long middlewareId) {
        Set<String> existTopics = rocketMqConfigService.queryExistTopic(middlewareId);
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        existTopics.stream().forEach(s -> dictionaryDTOS.add(DictionaryDTO.Of(s, s)));
        return dictionaryDTOS;
    }

    private List<DictionaryDTO<?>> generateMqTypeDictionary() {
        return generateCommonDictionary(middlewareEnum -> Boolean.TRUE);
    }

    private List<DictionaryDTO<?>> generateResourceTypeDictionary() {
        return generateCommonDictionary(middlewareEnum -> MiddlewareEnum.ROCKETMQ == middlewareEnum |
                MiddlewareEnum.ELASTICSEARCH == middlewareEnum);
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

    private List<DictionaryDTO<?>> generateMiddlewareConfigDictionary(String montorRoomEn) {
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigDao.queryCurrentMontorRoomMQ(montorRoomEn);
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        Arrays.stream(MiddlewareEnum.values()).forEach(middlewareEnum -> {
            DictionaryDTO dictionaryDTO = new DictionaryDTO<>();
            dictionaryDTO.setValue(middlewareEnum.getCode());
            dictionaryDTO.setLabel(middlewareEnum.getName());
            if (CollectionUtils.isNotEmpty(milogMiddlewareConfigs)) {
                dictionaryDTO.setChildren(milogMiddlewareConfigs.stream().filter(middlewareConfig -> middlewareEnum.getCode().equals(middlewareConfig.getType())).map(middlewareConfig -> {
                    DictionaryDTO<Long> childDictionaryDTO = new DictionaryDTO<>();
                    childDictionaryDTO.setValue(middlewareConfig.getId());
                    childDictionaryDTO.setLabel(middlewareConfig.getAlias());
                    if (MiddlewareEnum.ROCKETMQ.getCode().equals(middlewareConfig.getType())) {
                        List<DictionaryDTO> existsTopic = rocketMqConfigService.queryExistsTopic(middlewareConfig.getAk(), middlewareConfig.getSk(),
                                middlewareConfig.getNameServer(), middlewareConfig.getServiceUrl(), middlewareConfig.getAuthorization(), middlewareConfig.getOrgId(), middlewareConfig.getTeamId());
                        childDictionaryDTO.setChildren(existsTopic);
                    }
                    return childDictionaryDTO;
                }).collect(Collectors.toList()));
            }
            dictionaryDTOS.add(dictionaryDTO);
        });
        return dictionaryDTOS;
    }
}

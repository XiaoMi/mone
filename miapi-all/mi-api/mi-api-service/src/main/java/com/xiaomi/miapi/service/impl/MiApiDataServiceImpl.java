package com.xiaomi.miapi.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.miapi.api.service.MiApiDataService;
import com.xiaomi.miapi.api.service.bo.*;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.mapper.DubboPushDataMapper;
import com.xiaomi.miapi.mapper.HttpPushDataMapper;
import com.xiaomi.miapi.mapper.ModuleNameDataMapper;
import com.xiaomi.miapi.pojo.*;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import com.xiaomi.miapi.mapper.SidecarPushDataMapper;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@DubboService(group = "${dubbo.group}", version = "1.0")
@Slf4j
class MiApiDataServiceImpl implements MiApiDataService {

    @DubboReference(check = false, group = "${ref.hermes.service.group}")
    private BusProjectService busProjectService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private HttpPushDataMapper httpPushDataMapper;

    @Autowired
    private DubboPushDataMapper dubboPushDataMapper;

    @Autowired
    private SidecarPushDataMapper sidecarPushDataMapper;

    @Autowired
    private ModuleNameDataMapper moduleMapper;

    private final ScheduledExecutorService scheduledPool = Executors.newSingleThreadScheduledExecutor();

    private final ExecutorService pushDataPool = Executors.newCachedThreadPool();

    public static final Gson gson = new Gson();

    @PostConstruct
    public void clearExpireInstances() {
        scheduledPool.scheduleAtFixedRate(this::doClearExpireInstancesData, 0, 5, TimeUnit.MINUTES);
    }

    private void doClearExpireInstancesData() {
        //online instance
        Set<String> currentAddrs = new HashSet<>();
        ModuleNameDataExample example = new ModuleNameDataExample();
        example.createCriteria().andAddressIsNotNull();
        List<ModuleNameData> moduleList = moduleMapper.selectByExample(example);
        moduleList.forEach(moduleNameData -> currentAddrs.add(moduleNameData.getAddress()));

        HttpPushDataExample httpPushDataExample = new HttpPushDataExample();
        httpPushDataExample.createCriteria().andAddressNotIn(Lists.newArrayList(currentAddrs));
        DubboPushDataExample dubboPushDataExample = new DubboPushDataExample();
        dubboPushDataExample.createCriteria().andAddressNotIn(Lists.newArrayList(currentAddrs));
        SidecarPushDataExample sidecarPushDataExample = new SidecarPushDataExample();
        sidecarPushDataExample.createCriteria().andAddressNotIn(Lists.newArrayList(currentAddrs));
        try {
            if (currentAddrs.size() != 0){
                httpPushDataMapper.deleteByExample(httpPushDataExample);
                dubboPushDataMapper.deleteByExample(dubboPushDataExample);
                sidecarPushDataMapper.deleteByExample(sidecarPushDataExample);
            }
        } catch (Exception e) {
            log.error("doClearExpireInstancesData failed:{}", e.getMessage());
        }
    }


    @Override
    public Result<Boolean> feiShuDubboApplyCallback(DubboApplyDTO dto) {
        if (dto.getPass()) {
            int days;
            if (dto.getDays() == null || dto.getDays() == 0) {
                days = 1;
            }else {
                days = dto.getDays();
            }
            String rKey = String.join(":", dto.getServiceName(), dto.getGroupName(), dto.getVersion(), dto.getUsername(), dto.getUserId());
            redisUtil.saveEpKey(rKey, Consts.SUCCESS_MSG, 60 * 60 * 24 * days);
        }
        return Result.success(true);
    }


    @Override
    public void pushServiceDocDataToMiApi(DubboDocDataBo dubboDocDataBo) {
        pushDataPool.submit(() -> {
            DubboPushData dubboPushData = new DubboPushData();
            dubboPushData.setAddress(dubboDocDataBo.getAddress());
            dubboPushData.setApimodulelist(dubboDocDataBo.getApiModuleList());
            dubboPushData.setApimoduleinfo(dubboDocDataBo.getApiModuleInfo());
            dubboPushData.setApiparamsresponseinfo(dubboDocDataBo.getApiParamsResponseInfo());
            DubboPushDataExample example = new DubboPushDataExample();
            example.createCriteria().andAddressEqualTo(dubboDocDataBo.getAddress());
            List<DubboPushData> dubboPushDataList = dubboPushDataMapper.selectByExampleWithBLOBs(example);
            if (dubboPushDataList == null || dubboPushDataList.size() == 0) {
                log.info("pushDubboDocData insert,address:{}", dubboDocDataBo.getAddress());
                dubboPushDataMapper.insert(dubboPushData);
            } else {
                log.info("pushDubboDocData up,address:{}", dubboDocDataBo.getAddress());
                DubboPushData data = dubboPushDataList.get(0);
                data.setApimodulelist(dubboDocDataBo.getApiModuleList());
                data.setApimoduleinfo(dubboDocDataBo.getApiModuleInfo());
                data.setApiparamsresponseinfo(dubboDocDataBo.getApiParamsResponseInfo());
                dubboPushDataMapper.updateByPrimaryKeyWithBLOBs(data);
            }
        });
    }

    @Override
    public void pushServiceDocDataToMiApi(HttpDocDataBo httpDocDataBo) {
        pushDataPool.submit(() -> {
            HttpPushData httpPushData = new HttpPushData();
            httpPushData.setAddress(httpDocDataBo.getAddress());
            httpPushData.setHttpapimoduleinfo(httpDocDataBo.getHttpApiModuleInfo());
            httpPushData.setHttpapimodulelistandapiinfo(httpDocDataBo.getHttpApiModuleListAndApiInfo());
            httpPushData.setHttpapiparamsresponseinfo(httpDocDataBo.getHttpApiParamsResponseInfo());
            HttpPushDataExample example = new HttpPushDataExample();
            example.createCriteria().andAddressEqualTo(httpDocDataBo.getAddress());
            List<HttpPushData> httpPushDataList = httpPushDataMapper.selectByExampleWithBLOBs(example);
            if (httpPushDataList == null || httpPushDataList.size() == 0) {
                log.info("pushHttpDocData insert,address:{}", httpDocDataBo.getAddress());
                httpPushDataMapper.insert(httpPushData);
            } else {
                HttpPushData data = httpPushDataList.get(0);
                data.setHttpapimoduleinfo(httpDocDataBo.getHttpApiModuleInfo());
                data.setHttpapimodulelistandapiinfo(httpDocDataBo.getHttpApiModuleListAndApiInfo());
                data.setHttpapiparamsresponseinfo(httpDocDataBo.getHttpApiParamsResponseInfo());
                log.info("pushHttpDocData up,address:{}", httpDocDataBo.getAddress());
                httpPushDataMapper.updateByPrimaryKeyWithBLOBs(data);
            }
        });
    }

    @Override
    public void pushServiceDocDataToMiApi(SidecarDocDataBo sidecarDocDataBo) {
        pushDataPool.submit(() -> {
            SidecarPushData sidecarPushData = new SidecarPushData();
            sidecarPushData.setAddress(sidecarDocDataBo.getAddress());
            sidecarPushData.setSidecarapimoduleinfo(sidecarDocDataBo.getSidecarApiModuleInfo());
            sidecarPushData.setSidecarapimodulelistandapiinfo(sidecarDocDataBo.getSidecarApiModuleListAndApiInfo());
            sidecarPushData.setSidecarapiparamsresponseinfo(sidecarDocDataBo.getSidecarApiParamsResponseInfo());
            SidecarPushDataExample example = new SidecarPushDataExample();
            example.createCriteria().andAddressEqualTo(sidecarDocDataBo.getAddress());
            List<SidecarPushData> sidecarPushDataList = sidecarPushDataMapper.selectByExampleWithBLOBs(example);
            if (sidecarPushDataList == null || sidecarPushDataList.size() == 0) {
                log.info("pushSidecarDocData insert,address:{}", sidecarDocDataBo.getAddress());
                try {
                    sidecarPushDataMapper.insert(sidecarPushData);
                } catch (Exception e) {
                    log.error("pushSidecarDocData add,address:{},error:{}", sidecarDocDataBo.getAddress(), e);
                }
            } else {
                SidecarPushData data = sidecarPushDataList.get(0);
                data.setSidecarapimoduleinfo(sidecarDocDataBo.getSidecarApiModuleInfo());
                data.setSidecarapimodulelistandapiinfo(sidecarDocDataBo.getSidecarApiModuleListAndApiInfo());
                data.setSidecarapiparamsresponseinfo(sidecarDocDataBo.getSidecarApiParamsResponseInfo());
                log.info("pushSidecarDocData up,address:{}", sidecarDocDataBo.getAddress());
                try {
                    sidecarPushDataMapper.updateByPrimaryKeyWithBLOBs(data);
                } catch (Exception e) {
                    log.error("pushSidecarDocData up,address:{},error:{}", sidecarDocDataBo.getAddress(), e);
                }
            }
        });
    }
}

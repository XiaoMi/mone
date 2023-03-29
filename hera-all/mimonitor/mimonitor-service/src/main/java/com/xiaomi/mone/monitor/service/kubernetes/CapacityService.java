package com.xiaomi.mone.monitor.service.kubernetes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.dao.AppCapacityAutoAdjustRecordDao;
import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjustRecord;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.http.MoneSpec;
import com.xiaomi.mone.monitor.service.http.RequestParam;
import com.xiaomi.mone.monitor.service.http.RestTemplateService;
import com.xiaomi.mone.monitor.service.model.CapacityAdjustRecordRequest;
import com.xiaomi.mone.monitor.service.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author gaoxihui
 * @date 2022/5/30 4:58 下午
 */
@Slf4j
@Service
public class CapacityService {

    @Value("${k8s.capacity.adjust.url:noconfig}")
    private String url;

    @Autowired
    RestTemplateService restTemplateService;

    @Autowired
    AppCapacityAutoAdjustRecordDao adjustRecordDao;

    @Autowired
    CapacityAdjustMessageService capacityAdjustMessageService;


    @NacosValue(value = "${k8s.capacity.adjust.check.interval:1}",autoRefreshed = true)
    private Integer adjustCheckInterval;

    public String capacityAdjust(RequestParam param){
        String s = new Gson().toJson(param);
        JSONObject paramJson = (JSONObject) JSON.parse(s);
        String httpMPost = restTemplateService.getHttpMPost(url, paramJson, MediaType.APPLICATION_JSON);
        return httpMPost;
    }

    @PostConstruct
    private void consumeMessage() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(()->{
            while (true){
                if(capacityAdjustMessageService.queueSize() == 0){
                    Thread.sleep(1000);
                }
                MoneSpec consume = capacityAdjustMessageService.consume();
                if(consume != null){
                    log.info("CapacityService#consumeMessage : {}",consume);
                    capacityAdjustWithRecord(consume);
                }
            }
        });

    }

    private void capacityAdjustWithRecord(MoneSpec moneSpec){

        if(isInLimitTime(moneSpec)){
            return;
        }

        Integer recordId = recodCapacityAdjust(moneSpec);
        if(recordId == null || recordId.intValue() == 0){
            log.error("recodCapacityAdjust fail! MoneSpec :{}",moneSpec.toString());
        }

        RequestParam param = new RequestParam();
        moneSpec.init();
        param.setMoneSpec(moneSpec);
        param.init(recordId);

        String s = capacityAdjust(param);

        JsonObject result = new Gson().fromJson(s, JsonObject.class);
        if(result.get("code") == null || result.get("code").getAsInt() != 0){
            log.error("capacityAdjust error! response info : {}",s);
        }
    }

    private boolean isInLimitTime(MoneSpec moneSpec){
        AppCapacityAutoAdjustRecord condition = new AppCapacityAutoAdjustRecord();
        condition.setNameSpace(moneSpec.getNamespace());
        condition.setContainer(moneSpec.getContainer());
        condition.setEnvId(moneSpec.getEnvID());
        List<AppCapacityAutoAdjustRecord> query = adjustRecordDao.query(condition, 1, 1,false);
        if(CollectionUtils.isEmpty(query)){
            return false;
        }
        
        AppCapacityAutoAdjustRecord record = query.get(0);
        if(moneSpec.getTime() == null || record.getTime() == null){
            log.error("CapacityAdjust Check isInLimitTime data error!moneSpec:{},record:{}",moneSpec.toString(),record.toString());
            return false;
        }

        long intervalTime = moneSpec.getTime() - record.getTime();

        if(intervalTime < adjustCheckInterval * 1000 * 60){
            log.info("CapacityAdjust Check intervalTime isInLimitTime, stop execute!  moneSpec:{},record:{}",moneSpec.toString(),record.toString());
            return true;
        }

        return false;
    }


    public Integer recodCapacityAdjust(MoneSpec consume){
        if(null == consume){
            log.error("recodCapacityAdjust consume is null!");
            return 0;
        }
        AppCapacityAutoAdjustRecord record = new AppCapacityAutoAdjustRecord();
        record.setContainer(consume.getContainer());
        record.setNameSpace(consume.getNamespace());
        record.setEnvId(consume.getEnvID());
        record.setReplicas(consume.getReplicas());
        record.setSetReplicas(consume.getSetReplicas());
        record.setTime(consume.getTime());
        adjustRecordDao.create(record);
        return record.getId();
    }


    public Result listCapacityAdjustRecord(CapacityAdjustRecordRequest request){
        AppCapacityAutoAdjustRecord condition = new AppCapacityAutoAdjustRecord();
        if(request.getAppId() != null){
            condition.setContainer(request.getAppId() + "_0_");
        }

        PageData pd = new PageData();

        Long count = adjustRecordDao.count(condition, true);
        pd.setTotal(count);
        pd.setPage(request.getPage());
        pd.setPageSize(request.getPageSize());

        if(count.intValue() == 0){
            pd.setList(Lists.newLinkedList());
            return Result.success(pd);
        }

        List<AppCapacityAutoAdjustRecord> query = adjustRecordDao.query(condition, request.getPage(), request.getPageSize(),true);
        pd.setList(query);

        return Result.success(pd);
    }


}

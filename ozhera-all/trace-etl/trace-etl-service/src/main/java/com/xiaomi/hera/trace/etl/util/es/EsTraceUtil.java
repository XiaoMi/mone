package com.xiaomi.hera.trace.etl.util.es;

import com.alibaba.fastjson.JSONObject;
import com.xiaomi.mone.es.EsProcessor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
public class EsTraceUtil {

    private EsProcessor esProcessor;

    private EsProcessor errorEsProcessor;

    public EsTraceUtil(EsProcessor esProcessor, EsProcessor errorEsProcessor){
        this.esProcessor = esProcessor;
        this.errorEsProcessor = errorEsProcessor;
    }

    public void insertBulk(String index,String json){
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            esProcessor.bulkInsert(index, jsonObject);
        }catch (Exception e){
            log.error("Insert jaeger es data exception:",e);
        }
    }

    public void insertBulk(String index,Map jsonMap){
        try {
            esProcessor.bulkInsert(index, jsonMap);
        }catch (Exception e){
            log.error("Insert jaeger es data exception:",e);
        }
    }

    public void insertErrorBulk(String index,Map jsonMap){
        try {
            errorEsProcessor.bulkInsert(index, jsonMap);
        }catch (Exception e){
            log.error("insert error es exception：",e);
        }
    }
}

package com.xiaomi.mone.monitor.service.http;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author gaoxihui
 * @date 2022/5/30 6:35 下午
 */
@Data
public class RequestParam implements Serializable {

    private MoneSpec moneSpec;
    private String name;

    public void init(Integer recordId){
        name = recordId == null || recordId.intValue()==0 ? UUID.randomUUID().toString().replaceAll("-","") : String.valueOf(recordId);
    }
}

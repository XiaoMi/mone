package com.xiaomi.hera.trace.etl.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/7/6 7:49 下午
 */
@Service
public class HeraContextService {

    public Set<String> getHeraContextKeys(String heraContext){
        Set<String> result = new HashSet<>();
        String[] split = heraContext.split(";");
        for(String keyValue : split){
            String[] kv = keyValue.split(":");
            result.add(kv[0]);
        }
        return result;
    }

    public String getHeraContextValue(String heraContext, String key){
        String[] split = heraContext.split(";");
        for(String keyValue : split){
            String[] kv = keyValue.split(":");
            if(key.equals(kv[0])){
                return kv[1];
            }
        }
        return null;
    }
}

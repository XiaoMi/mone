/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor.service.helper;

import com.google.gson.Gson;
import com.xiaomi.mone.monitor.service.model.redis.AppAlarmData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author zhanggaofeng1
 */
//@Component
public class RedisHelper {

    private final Logger logger = LoggerFactory.getLogger(RedisHelper.class);
    private final String REDIS_PRE = "mimonitor_";
    private final String APP_ALARM_DATA_KEY = REDIS_PRE + "alarm_data_v1_";
    private final int APP_ALARM_DATA_KEY_EXPIRE_SECONDS = 60;
    public static String redisSwitch = "on";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存app告警统计数据
     *
     * @param appName
     * @param data
     * @return
     */
    public boolean setAppAlarmData(String appName, AppAlarmData data) {
        StringBuilder strBKey = new StringBuilder();
        strBKey.append(APP_ALARM_DATA_KEY).append(appName);
        return set(strBKey.toString(), data, APP_ALARM_DATA_KEY_EXPIRE_SECONDS);
    }

    /**
     * 获取app告警统计数据
     *
     * @param appName
     * @return
     */
    public AppAlarmData getAppAlarmData(String appName) {
        if(redisSwitch.equals("off")){
            return null;
        }
        StringBuilder strBKey = new StringBuilder();
        strBKey.append(APP_ALARM_DATA_KEY).append(appName);
        return get(strBKey.toString(), AppAlarmData.class);
    }

    public boolean set(String key, Object data, int seconds) {
        try {
            stringRedisTemplate.opsForValue().set(key, new Gson().toJson(data), seconds, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            logger.error("RedisHelper.set异常 key={}, data={}", key, data, e);
            return false;
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return new Gson().fromJson(json, clazz);
        } catch (Exception e) {
            logger.error("RedisHelper.get异常 key={}", key, e);
            return null;
        }
    }

}

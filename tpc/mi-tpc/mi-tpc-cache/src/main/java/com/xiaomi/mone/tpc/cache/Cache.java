package com.xiaomi.mone.tpc.cache;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 缓存工厂
 */
@Slf4j
@Service
public class Cache {

    protected final Map<Integer, CacheService> map = Maps.newConcurrentMap();

    @NacosValue("${cache.type:0}")
    private Integer cacheType;
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        String[] names = applicationContext.getBeanNamesForType(CacheService.class);
        if (names == null || names.length <= 0) {
            return;
        }
        CacheService service = null;
        for (String name : names) {
            service = (CacheService)applicationContext.getBean(name);
            if (service == null) {
                continue;
            }
            map.put(service.curCacheType, service);
        }
    }

    public final CacheService get() {
        CacheService service = map.get(cacheType);
        if (service == null) {
            throw new RuntimeException("没有支持的缓存");
        }
        return service;
    }


}

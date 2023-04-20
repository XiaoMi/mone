package com.xiaomi.mone.log.manager.bootstrap;

import com.xiaomi.mone.log.manager.service.impl.LogCountServiceImpl;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

@DOceanPlugin
@Slf4j
public class Cache implements IPlugin {
    @Resource
    private LogCountServiceImpl logCountService;

    @Override
    public void init() {
        // 构建日志量统计缓存
        buildLogCountCache();
    }

    public void buildLogCountCache() {
        logCountService.collectTopCount();
        log.info("日志量统计排行榜缓存已加");
    }
}

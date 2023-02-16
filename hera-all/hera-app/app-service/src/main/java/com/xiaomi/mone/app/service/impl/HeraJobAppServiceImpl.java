package com.xiaomi.mone.app.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.app.service.HeraAppBaseInfoService;
import com.xiaomi.mone.app.service.HeraJobAppService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.xiaomi.mone.app.common.Constant.SUCCESS_MESSAGE;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 16:56
 */
@Service
@Slf4j
public class HeraJobAppServiceImpl implements HeraJobAppService {

    private final HeraAppBaseInfoService heraAppBaseInfoService;

    @Resource
    private OkHttpClient okHttpClient;
    @Resource
    private Gson gson;

    public HeraJobAppServiceImpl(HeraAppBaseInfoService heraAppBaseInfoService) {
        this.heraAppBaseInfoService = heraAppBaseInfoService;
    }

    @Override
    public String synchronousMisApp(String serviceName) {
        return SUCCESS_MESSAGE;
    }
}

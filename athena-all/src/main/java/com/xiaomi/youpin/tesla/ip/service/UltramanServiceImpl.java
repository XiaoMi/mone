package com.xiaomi.youpin.tesla.ip.service;

import com.xiaomi.youpin.tesla.ip.common.Prompt;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/5 12:38
 */
@Slf4j
@Data
public class UltramanServiceImpl implements UltramanService {


    private boolean openHttpServer = false;


    @Override
    public void run() {
        log.info("athena run");
    }

    @Override
    public void init() {
        log.info("athena service init");
        ScriptService.ins();
        new Thread(() -> {
            try {
                Prompt.init();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }).start();
    }

}

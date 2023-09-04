package com.xiaomi.mone.hera.demo.server.service;

import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Service
public class DubboHealthServiceImpl implements DubboHealthService {
    private static final Logger logger = LoggerFactory.getLogger(DubboHealthServiceImpl.class);

    @Override
    public Result health() throws InterruptedException {
        logger.info("this is {}", "zxw_test_log");
        int max = 3000;
        int min = 2000;
        Random random = new Random();

        int s = random.nextInt(max) % (max - min + 1) + min;
        System.out.println(s);
        Thread.sleep(s);
        return Result.success(1);
    }

    @Override
    public String simple(int size) {
        return new String(new byte[size]);
    }
}
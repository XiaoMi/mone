package com.xiaomi.mone.hera.demo.server.service;

import com.xiaomi.youpin.infra.rpc.Result;

public interface DubboHealthService {

    Result health() throws InterruptedException;

    String simple(int size);

}
/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.mone.grpc.observer.client;

import io.grpc.stub.StreamObserver;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import run.mone.mesh.bo.SideCarPushMsg;
import run.mone.mesh.bo.SideCarRequest;

import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/23 16:38
 */
@Slf4j
public class PushMsgObserver implements StreamObserver<SideCarPushMsg> {


    private BiConsumer<SideCarPushMsg, StreamObserver<SideCarRequest>> consumer;

    private CountDownLatch latch;

    /**
     * 用来处理请求
     */
    @Setter
    private StreamObserver<SideCarRequest> obs;


    public PushMsgObserver(BiConsumer<SideCarPushMsg, StreamObserver<SideCarRequest>> consumer, CountDownLatch latch) {
        this.consumer = consumer;
        this.latch = latch;
    }

    @Override
    public void onNext(SideCarPushMsg value) {
        consumer.accept(value, obs);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("PushMsgObserver on error:" + throwable.getMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        log.info("PushMsgObserver on completed");
        latch.countDown();
    }
}

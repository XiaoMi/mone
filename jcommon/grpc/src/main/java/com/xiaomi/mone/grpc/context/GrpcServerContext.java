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

package com.xiaomi.mone.grpc.context;

import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import run.mone.mesh.bo.SideCarPushMsg;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/26 12:09
 */
@Slf4j
public class GrpcServerContext {


    private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    /**
     * key 是 app
     */
    @Getter
    private ConcurrentHashMap<String, StreamObserver<SideCarPushMsg>> streamMap = new ConcurrentHashMap<>();


    public void put(String key, Object value) {
        map.put(key, value);
    }


    public void remote(String key) {
        map.remove(key);
    }


    public boolean contains(String key) {
        return map.containsKey(key);
    }


    public void sideCarOffline(String addr) {
        map.entrySet().stream().filter(it -> {
            return it.getValue().equals(addr);
        }).findAny().ifPresent(it -> {
            log.info("sidecar:{} {} offline", it.getKey(), it.getValue());
            streamMap.remove(it.getKey());
        });
    }

}

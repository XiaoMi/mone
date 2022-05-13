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

package com.xiaomi.mone.docean.plugin.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/11 10:41
 */

public class EtcdClient {

    private Client client;

    /**
     * example:http://127.0.0.1:2379
     */
    @Setter
    private String hosts;

    public void initClient() {
        String[] urls = hosts.split(",");
        client = Client.builder().endpoints(urls).build();
    }


    @SneakyThrows
    public void put(String key, String value) {
        client.getKVClient().put(ByteSequence.from(key.getBytes()), ByteSequence.from(value.getBytes())).get();
    }


    @SneakyThrows
    public String get(String key) {
        List<KeyValue> list = client.getKVClient().get(ByteSequence.from("name".getBytes())).get().getKvs();
        if (list.size() > 0) {
            return list.get(0).getValue().toString();
        }
        return null;
    }

    @SneakyThrows
    public void delete(String key) {
        client.getKVClient().delete(ByteSequence.from(key.getBytes())).get();
    }


}

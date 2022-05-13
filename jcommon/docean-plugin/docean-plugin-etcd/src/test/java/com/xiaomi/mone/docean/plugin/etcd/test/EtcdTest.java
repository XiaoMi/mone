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

package com.xiaomi.mone.docean.plugin.etcd.test;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.kv.GetResponse;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/11 10:24
 */
public class EtcdTest {

    @Test
    public void testSet() throws ExecutionException, InterruptedException {
        String[] urls = "http://127.0.0.1:2379".split(",");
        Client etcdClient = Client.builder().endpoints(urls).build();
        etcdClient.getKVClient().put(ByteSequence.from("name".getBytes()),ByteSequence.from("zzy".getBytes())).get();
    }


    @Test
    public void testGet() throws ExecutionException, InterruptedException {
        String[] urls = "http://127.0.0.1:2379".split(",");
        Client etcdClient = Client.builder().endpoints(urls).build();
        CompletableFuture<GetResponse> v = etcdClient.getKVClient().get(ByteSequence.from("name".getBytes()));
        String s = v.get().getKvs().get(0).getValue().toString();
        System.out.println(s);
    }


    @Test
    public void testDelete() throws ExecutionException, InterruptedException {
        String[] urls = "http://127.0.0.1:2379".split(",");
        Client etcdClient = Client.builder().endpoints(urls).build();
        etcdClient.getKVClient().delete(ByteSequence.from("name".getBytes())).get();
    }


}

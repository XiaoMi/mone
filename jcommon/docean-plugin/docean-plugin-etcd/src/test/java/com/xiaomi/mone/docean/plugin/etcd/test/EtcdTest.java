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

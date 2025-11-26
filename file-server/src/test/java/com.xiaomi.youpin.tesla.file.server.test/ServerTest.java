package com.xiaomi.youpin.tesla.file.server.test;

import com.google.common.collect.Maps;
import com.xiaomi.data.push.client.HttpClientV2;
import lombok.SneakyThrows;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

/**
 * @author goodjava@qq.com
 * @date 2025/5/14 15:35
 */
public class ServerTest {


    private String serverUrl = "http://127.0.0.1:9777";

    String userKey = "";

    String userSecret = "";


    @SneakyThrows
    @Test
    public void testUpload() {
        System.out.println("upload");
        String token = "1";
        String name = "abc";
        File file = new File("/tmp/abc");
        HttpClientV2.upload(this.serverUrl + "/upload?name=" + name + "&userKey=&userSecret=&token=" + token, Files.readAllBytes(file.toPath()));
    }

    @SneakyThrows
    @Test
    public void testList() {
        String token = "1";
        String name = "abc";
        String res = HttpClientV2.get(this.serverUrl + "/list?name=" + name + "&userKey=&userSecret=&token=" + token, Maps.newHashMap());
        System.out.println(res);
    }

    @SneakyThrows
    @Test
    public void testDelete() {
        String token = "1";
        String name = "abc";
        String res = HttpClientV2.get(this.serverUrl + "/delete?name=" + name + "&userKey=&userSecret=&token=" + token, Maps.newHashMap());
        System.out.println(res);
    }


}

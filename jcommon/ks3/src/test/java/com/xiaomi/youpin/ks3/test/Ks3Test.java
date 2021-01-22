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

package com.xiaomi.youpin.ks3.test;

import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.youpin.ks3.KsyunService;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;


public class Ks3Test {

    /**
     * dd if=/dev/zero of=test bs=1m count=100
     */
    @Test
    public void testKs3() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Future<byte[]> f = pool.submit(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                try {
                    return download();
                } catch (Throwable ex) {
                    System.out.println(ex.getMessage());
                    throw ex;
                }
            }
        });

        try {
            byte[] data = f.get(30, TimeUnit.SECONDS);
            System.out.println(data.length);
        } catch (Throwable e) {
            System.out.println(e);
            f.cancel(true);
        }
        System.out.println("end");
    }

    static String key = "aaaabbb";

    public static byte[] getData() {
        try {
            KsyunService ksyunService = getService();


//        ksyunService.uploadFile(key, new File("/tmp/data/test"),(int) TimeUnit.HOURS.toSeconds(10));

            long begin = System.currentTimeMillis();
            byte[] data = ksyunService.getFileByKey(key);
            System.out.println(System.currentTimeMillis() - begin);
            return data;
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
        return new byte[]{};
    }

    private static KsyunService getService() {
        KsyunService ksyunService = new KsyunService();
        ksyunService.setAccessKeyID("your_key");
        ksyunService.setAccessKeySecret("your_secret");
        ksyunService.init();
        return ksyunService;
    }


    public void upload() {

    }

    public void testKey() {
        KsyunService ksyunService = getService();
    }


    @Test
    public void testDownload() {
        download();
    }


    private byte[] download() {
        String url = "http://127.0.0.1.ks3-cn-beijing.ksyun.com/youpin-arch/test?AccessKeyId=AKLTGblNdmcdRp6QKW3BtAWynQ&Expires=1584378233&Signature=PAEtGTn6efTbV33b%2FPnoUCZiP2k%3D";
        long now = System.currentTimeMillis();
        byte[] data = HttpClientV2.download(url, 3000);
        System.out.println("len:" + data.length);
        System.out.println(System.currentTimeMillis() - now);
        return data;
    }


    @Test
    public void testFileServerGetFile() throws IOException {
        KsyunService service = new KsyunService("http://xxxx");
        service.setToken("dprqfzzy123!");
        byte[] data = service.getFileByKey("detail-gateway-20200306123943316.jar");
        System.out.println(data.length);
        Files.write(Paths.get("/tmp/abcd.jar"),data);
    }


    @Test
    public void testFileServerUploadFile() {
        KsyunService service = new KsyunService("http://xxxx");
        service.setToken("dprqfzzy123!");
        service.uploadFile("test2", new File("/tmp/data/download/test"), 0);
    }

}

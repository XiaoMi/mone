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

package com.xiaomi.youpin.tesla.agent.test;

import com.xiaomi.youpin.nginx.NginxUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class NginxUtilsTest {


    @Test
    public void testListService() throws IOException {
        String config = getConfig();
        List<String> list = NginxUtils.getServers(config, "staging_detail_backend");
        list.stream().forEach(it -> System.out.println(it));
    }

    private String getConfig() throws IOException {
//        return new String(Files.readAllBytes(Paths.get("/usr/local/etc/nginx/nginx.conf")));
        return new String(Files.readAllBytes(Paths.get("/tmp/upstreams.conf")));
    }


    @Test
    public void testAdd() throws IOException {
        String config = getConfig();
        config = NginxUtils.addServer(config,"gateway","server 192.168.3.7:8081");
        System.out.println(config);
        List<String> list = NginxUtils.getServers(config, "gateway");
        list.stream().forEach(it -> System.out.println(it));
    }


    @Test
    public void testRemove() throws IOException {
        String config = getConfig();
        config = NginxUtils.removeServer(config,"gateway","server 192.168.3.7:8088");
        System.out.println(config);
        List<String> list = NginxUtils.getServers(config, "gateway");
        list.stream().forEach(it -> System.out.println(it));
    }

    @Test
    public void testFile() throws Exception {



        FileLock lock = null;
        FileOutputStream fileOutputStream = null;
        FileChannel channel = null;
        try {
            File file = new File("/Users/dingpei/workspace/tmp.txt");
            fileOutputStream = new FileOutputStream(file);
            channel = fileOutputStream.getChannel();
            while (lock == null) {
                lock = channel.tryLock();//无参lock()为独占锁
                Thread.sleep(1000);
            }
            fileOutputStream.write("aaaaaaaaaaa".getBytes("utf-8"));



            System.out.println("finish");
            Thread.sleep(1000000);
        } catch (FileNotFoundException e) {
//            log.warn("file not found error:{}", e.getMessage());
        } catch (IOException e) {
//            log.warn("copy config file error:{}", e.getMessage());
        } catch (Exception e) {
//            log.warn("writeConfigFile error:{}", e.getMessage());
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                    lock = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testFile1() throws Exception {
        FileLock lock = null;
        try {
            RandomAccessFile raf1 = new RandomAccessFile("/Users/dingpei/workspace/tmp.txt", "rw");
            FileChannel channel = raf1.getChannel();
            System.out.println("yyyy");
            while (lock == null) {
                lock = channel.tryLock();//无参lock()为独占锁
                Thread.sleep(1000);
                System.out.println("again");
            }

//            其它逻辑
            System.out.println("xxxsssxx");
            Thread.sleep(10000000);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                    lock = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

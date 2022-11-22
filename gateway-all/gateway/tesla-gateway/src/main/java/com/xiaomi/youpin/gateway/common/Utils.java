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

package com.xiaomi.youpin.gateway.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import io.netty.channel.Channel;
import io.netty.channel.epoll.Epoll;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.common.utils.NetUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author goodjava@qq.com
 */
public class Utils {

    public static final String OS_NAME = System.getProperty("os.name");

    private String pid = "";

    private static boolean isLinuxPlatform = false;


    static {
        if (OS_NAME != null && OS_NAME.toLowerCase().indexOf("linux") >= 0) {
            isLinuxPlatform = true;
        }
    }

    private Utils() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        pid = name.split("@")[0];
    }


    private static class LazyHolder {
        private static Utils ins = new Utils();
    }


    public static Utils ins() {
        return LazyHolder.ins;
    }

    public String ip() {
        return NetUtils.getLocalHost();
    }


    public String pid() {
        return pid;
    }

    public String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 是否使用epoll,就是判断是否使用的是linux
     *
     * @return
     */
    public static boolean useEpoll() {
        return isLinuxPlatform() && Epoll.isAvailable();
    }


    public static boolean isLinuxPlatform() {
        return isLinuxPlatform;
    }

    public static String md5(String originString) {
        if (originString.equals("")) {
            return "";
        }
        byte[] results = null;
        try {
            //创建具有指定算法名称的信息摘要
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
            results = md5.digest(originString.getBytes());
            //将加密后的数据转换为16进制数字
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        String md5code = new BigInteger(1, results).toString(16);
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 覆盖写文件
     *
     * @param path
     * @param fileName
     * @param content
     */
    public static void writeFile(String path, String fileName, String content) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

        // 如果文件不存在则创建
        File filename = new File(path + fileName);
        if (!filename.exists()) {
            try {
                filename.createNewFile();// 不存在直接创建
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 写入内容
        writeToBuffer(content, filename);
    }

    private static void writeToBuffer(String content, File filename) {
        try {
            FileWriter fw = new FileWriter(filename.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String path, String fileName) {
        try {
            // 可为文本相对路劲，可以文件夹绝对路径
            String file = path + fileName;
            // StringBuffer用来接收解析完了之后的文本内容
            StringBuffer sb = new StringBuffer();
            // 自定义函数读文本 返回一个StringBuffer
            readToBuffer(sb, file);
            // StringBuffer转为String显示
            return sb.toString();
        } catch (IOException e) {
            // ignore
            return "";
        }
    }

    private static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine();
        while (line != null) {
            buffer.append(line);
            buffer.append("\n");
            line = reader.readLine();
        }
        reader.close();
        is.close();
    }

    public static String getClientIp(FullHttpRequest request, Channel channel) {
        return HttpRequestUtils.getClientIp(request, channel);
    }


    public static String random(List<Pair<String, Integer>> list) {
        int sum = list.stream().map(it -> it.getValue()).reduce(0, (a, b) -> a + b);
        int v = new Random().nextInt(sum + 1);
        int t = 0;
        for (Pair<String, Integer> p : list) {
            t += p.getValue();
            if (t >= v) {
                return p.getKey();
            }
        }
        return list.get(0).getKey();
    }

    public static Gson getGson() {
        Gson gson = new GsonBuilder().create();
        try {
            Field factories = Gson.class.getDeclaredField("factories");
            factories.setAccessible(true);
            Object o = factories.get(gson);
            Class<?>[] declaredClasses = Collections.class.getDeclaredClasses();
            for (Class c : declaredClasses) {
                if ("java.util.Collections$UnmodifiableList".equals(c.getName())) {
                    Field listField = c.getDeclaredField("list");
                    listField.setAccessible(true);
                    List<TypeAdapterFactory> list = (List<TypeAdapterFactory>) listField.get(o);
                    int i = list.indexOf(ObjectTypeAdapter.FACTORY);
                    list.set(i, MapTypeAdapter.FACTORY);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gson;
    }
}

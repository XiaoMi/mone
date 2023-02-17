package com.xiaomi.mone.log.manager.test;

import cn.hutool.Hutool;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.log.model.MiLogStreamConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/8 10:30
 */
@Slf4j
public class StreamTest {

    Gson gson;

    @Before
    public void init() {
        gson = new Gson();
    }

    @Test
    public void test1() {
        Stream.of("one", "two", "three", "four")
                .filter(e -> e.length() > 3)
                .peek(e -> System.out.println("Filtered value: " + e))
                .map(String::toUpperCase)
                .peek(e -> System.out.println("Mapped value: " + e))
                .collect(Collectors.toList());
    }

    @Test
    public void testMapRemove() {
        String rules = "";
        MiLogStreamConfig miLogStreamConfig = gson.fromJson(rules, MiLogStreamConfig.class);
        Map<String, Map<Long, String>> config = miLogStreamConfig.getConfig();
        config.values().forEach(longStringMap -> {
            longStringMap.keySet().removeIf(key -> key.equals(1l));
            log.info(gson.toJson(longStringMap));
        });
        log.info(gson.toJson(config));
    }

    @Test
    public void test() {
        String str = "127.0.0.1:54014";
        String ip = StringUtils.substringBefore(str, ":");
        log.info("ip:{}", ip);
    }

    @Test
    public void testGson() {
        String data = "{\"code\":1210,\"message\":\"This name has been used!\",\"requestId\":\"d46567ad-458e-4d9c-aa22-1ced1fe70a46\",\"cost\":\"6 ms\",\"data\":null}";
        Map<String, Object> map = gson.fromJson(data, new TypeToken<Map>() {
        }.getType());

        Double dd = (Double) map.get("code");
        log.info("返回值：{}", dd.compareTo(1210.0));
//        if (!Strings.isEmpty(data) &&
//                Constant.SUCCESS_CODE == (int) gson.fromJson(data, new TypeToken<TreeMap<String, Object>>() {
//                }.getType()).get("code")
//                && Strings.equals(Constant.SUCCESS_MESSAGE, (String) gson.fromJson(data, Map.class).get("message"))) {
//            log.info("【RocketMQ创建topic】:成功", data);
//            //入库
//        } else {
//            log.error("【RocketMQ创建topic】:失败,失败原因：{}", data);
//        }
    }

    /**
     * 测试hutools工具类
     */
    @Test
    public void testHutool() {
        Hutool.printAllUtils();
    }

    @Test
    public void testIncrement(){
        int i=1;
        i=i++;
        System.out.println(i);

        System.out.println(Instant.now().toEpochMilli());
    }
}

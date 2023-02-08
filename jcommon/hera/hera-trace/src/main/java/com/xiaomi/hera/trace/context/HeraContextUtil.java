package com.xiaomi.hera.trace.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 读写HeraContext工具类，实现逻辑皆由探针完成
 * @Author dingtao
 * @Date 2022/3/14 4:21 下午
 */
public class HeraContextUtil {

    public static Map<String,String> getHeraContext(){
        return new HashMap<>();
    }

    public static String get(String key){
        return null;
    }

    public static boolean set(String key,String value){
        return false;
    }
}

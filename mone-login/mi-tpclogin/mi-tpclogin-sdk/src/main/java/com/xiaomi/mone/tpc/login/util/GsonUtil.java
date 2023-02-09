package com.xiaomi.mone.tpc.login.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 11:36
 */
public class GsonUtil {
    //不用创建对象,直接使用Gson.就可以调用方法
    private static Gson gson = null;
    //判断gson对象是否存在了,不存在则创建对象
    static {
        if (gson == null) {
            //gson = new Gson();
            //当使用GsonBuilder方式时属性为空的时候输出来的json字符串是有键值key的,显示形式是"key":null，而直接new出来的就没有"key":null的
            gson= new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        }
    }
    //无参的私有构造方法
    private GsonUtil() {
    }

    /**
     * 将对象转成json格式
     *
     * @param object
     * @return String
     */
    public static String gsonString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * 将json转成特定的cls的对象
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T gsonToBean(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            //传入json对象和对象类型,将json转成对象
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    /**
     * json字符串转成list
     *
     * @param gsonString
     * @param type
     * @return
     */
    public static <T> T gsonToBean(String gsonString, TypeToken type) {
        if (gson != null) {
            return gson.fromJson(gsonString, type.getType());
        }
        return null;
    }

}

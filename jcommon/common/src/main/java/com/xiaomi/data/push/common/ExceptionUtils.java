package com.xiaomi.data.push.common;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by zhangzhiyong on 19/06/2018.
 */
public abstract class ExceptionUtils {

    /**
     * 收集异常的msg+异常栈信息
     *
     * @param throwable
     * @return
     */
    public static String collectExceptionMsg(String id, Throwable throwable) {
        List<String> msg = Lists.newArrayList();
        msg.add(id);
        msg.add(throwable.getMessage());
        Stream.of(throwable.getStackTrace()).limit(10).forEach(it -> {
            msg.add(it.toString());
        });
        return new GsonBuilder().setPrettyPrinting().create().toJson(msg);
    }
}

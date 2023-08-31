package com.xiaomi.mone.app.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 16:58
 */
public class Constant {

    public static final String SUCCESS_MESSAGE = "success";

    public static Gson GSON = new GsonBuilder().create();

    public static String DEFAULT_OPERATOR = "system";

}

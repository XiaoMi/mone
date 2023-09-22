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

    public static String DEFAULT_REGISTER_REMOTE_TYPE = "deploymentIp";


    public static class URL {
        // tpc query app url
        public static String HERA_TPC_APP_DETAIL_URL = "/backend/node/flag/inner_list";
        // operator query app ip url
        public static String HERA_OPERATOR_ENV_URL = "/ips";
    }
}

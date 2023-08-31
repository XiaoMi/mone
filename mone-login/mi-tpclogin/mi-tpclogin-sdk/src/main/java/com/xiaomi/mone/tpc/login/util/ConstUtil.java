package com.xiaomi.mone.tpc.login.util;

public class ConstUtil {
    public static volatile String authTokenUrlVal = null;
    public final static String TPC_USER = "TPC_USER";
    public final static String AUTH_TOKEN = "TPC_TOKEN";
    public final static String authTokenUrl = "authTokenUrl";
    public final static String ignoreUrl = "IGNORE_URL";
    public final static String devMode = "devMode";
    public final static String innerAuth = "innerAuth";
    public static final String CAS_PUBLIC_KEY = "AEGIS_SDK_PUBLIC_KEY";
    public static final String SYS_SIGN = "sysSign";
    public static final String SYS_NAME = "sysName";
    public static final String USER_TOKEN = "userToken";
    public static final String REQ_TIME = "reqTime";
    public static final String DATA_SIGN = "dataSign";
    public static final String ACCOUNT = "account";
    public static final String TTL_MILLS = "ttlMills";
    public static final String USER_INFO_PATH = "userInfoPath";
    public final static String PUBLIC_KEY_FILTER_INIT_PARAM_KEY = "AEGIS_SDK_PUBLIC_KEY";
    public final static String HEADER_KEY_SIGN_VERIFY_IDENTITY = "X-Proxy-Midun";
    /**
     * 数据签名+用户数据header key
     */
    public final static String HEADER_KEY_SIGN_AND_USER_DATA = "x-proxy-userdetail";
    /**
     * 用户信息的request attribute key
     */
    public final static String REQUEST_ATTRIBUTE_USER_INFO_KEY = "user-info";

    public final static String hermesUrl = "hermesUrl";

    public final static String hermesProjectName = "hermesProjectName";

    public final static String openHermes = "openHermes";
    public final static String loginUrl = "loginUrl";
    public final static String logoutUrl = "logoutUrl";

}

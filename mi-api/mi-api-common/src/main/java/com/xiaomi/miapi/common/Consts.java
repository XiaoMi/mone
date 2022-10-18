package com.xiaomi.miapi.common;

import com.alibaba.nacos.api.config.annotation.NacosValue;

/**
 * @Description TODO
 * @Author zhenxing.dong
 * @Date 2021/7/31 23:46
 */
public final class Consts {

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_WORK = 2;
    public static final String SUCCESS_MSG = "ok";

    public static final int HTTP_API_TYPE = 1;
    public static final int DUBBO_API_TYPE = 3;
    public static final int GATEWAY_API_TYPE = 4;
    public static final int GRPC_API_TYPE = 5;

    public static final int FORM_DATA_TYPE = 0;
    public static final int JSON_DATA_TYPE = 1;
    public static final int RAW_DATA_TYPE = 2;


    public static final int REQ_EXP_JAVA_TYPE = 1;
    public static final int REQ_EXP_CURL_TYPE = 2;
    public static final int REQ_EXP_RAW_TYPE = 3;

    public static final int RSP_EXP_JSON_TYPE = 2;

    public static final int GATEWAY_ROUTE_TYPE_HTTP = 0;
    public static final int GATEWAY_ROUTE_TYPE_MI_DUBBO = 1;
    public static final int GATEWAY_ROUTE_TYPE_DUBBO = 4;

    public static final String PROJECT_NAME = "MiApi";

    private Consts() {
    }
    public static final int BY_API_PATH = 0;
    public static final int BY_API_NAME = 1;

    public static final int BY_DOCUMENT_TITLE = 0;
    public static final int BY_DOCUMENT_CONTENT = 1;

    public static final int GW_ALTER_TYPE_MANUAL = 1;
    public static final int GW_ALTER_TYPE_NORMAL = 0;

    public static final int DEFAULT_PAGE_SIZE = 15;
    public static final String REQUEST_URL_FORMAT = "%s/%s:%s";

    public static final String UPDATE_MOCK_DATA_URL_FORMAT = "%s:%s";

    @NacosValue(value = "mock.server.addr",autoRefreshed = true)
    public static final String MockUrlPrefix = "";

    @NacosValue(value = "mock.server.mock.addr",autoRefreshed = true)
    public static final String ProxyMockUrlPrefix = "";
    public static final String MockPrefix = "/dubbo/mock";
    public static final String GatewayMockPrefix = "/gateway/mock";
    public static final String HttpMockPrefix = "/http/mock";

    public static final String UPDATE_MOCK_URL = "/dubboApi/editMockData";
    public static final String GATEWAY_UPDATE_MOCK_URL = "/gatewayApi/editMockData";
    public static final String HTTP_UPDATE_MOCK_URL = "/httpApi/editMockData";
    public static final String ADD_PROXY_URL = "/http/addUrlProxy";
    public static final String ENABLE_MOCK_URL = "/api/enableApiMock";
    public static final String IMPORT_SWAGGER_FLAG = "swagger-import";

    public static final String reviewTmp = "{\n" +
            "  \"config\": {\n" +
            "    \"wide_screen_mode\": true\n" +
            "  },\n" +
            "  \"header\": {\n" +
            "    \"title\": {\n" +
            "      \"tag\": \"plain_text\",\n" +
            "      \"content\": \"mi-api 线上Dubbo接口测试审批\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"elements\": [\n" +
            "    {\n" +
            "      \"tag\": \"hr\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"div\",\n" +
            "      \"text\": {\n" +
            "        \"tag\": \"plain_text\",\n" +
            "        \"content\": \"服务名: ${serviceName}\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"div\",\n" +
            "      \"text\": {\n" +
            "        \"tag\": \"plain_text\",\n" +
            "        \"content\": \"分组: ${groupName}\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"div\",\n" +
            "      \"text\": {\n" +
            "        \"tag\": \"plain_text\",\n" +
            "        \"content\": \"版本：${versionName}\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"div\",\n" +
            "      \"text\": {\n" +
            "        \"tag\": \"plain_text\",\n" +
            "        \"content\": \"申请人：${operator}\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"action\",\n" +
            "      \"actions\": [\n" +
            "        {\n" +
            "          \"tag\": \"button\",\n" +
            "          \"text\": {\n" +
            "            \"tag\": \"plain_text\",\n" +
            "            \"content\": \"同意\"\n" +
            "          },\n" +
            "          \"type\": \"primary\",\n" +
            "          \"value\": {\n" +
            "            \"key\": \"agree\",\n" +
            "            \"type\": \"mi_api_dubbo_test\",\n" +
            "            \"rKey\": \"${rKey}\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"tag\": \"button\",\n" +
            "          \"text\": {\n" +
            "            \"tag\": \"plain_text\",\n" +
            "            \"content\": \"拒绝\"\n" +
            "          },\n" +
            "          \"type\": \"danger\",\n" +
            "          \"value\": {\n" +
            "            \"key\": \"refuse\",\n" +
            "            \"type\": \"mi_api_dubbo_test\",\n" +
            "            \"rKey\": \"${rKey}\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"hr\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"div\",\n" +
            "      \"text\": {\n" +
            "        \"tag\": \"lark_md\",\n" +
            "        \"content\": \"平台地址：<a>https://127.0.0.1:8080/#/</a>\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static String getServiceKey(String interfaceName,String version,String group) {
        return interfaceName + ":" + version+ ":" + group;
    }

    public static String genRecentlyProjectsKey(Integer userId){
        return "mi_api:recently10_projects_"+userId;
    }

    public static String genRecentlyApisKey(Integer userId){
        return "mi_api:recently10_apis_"+userId;
    }

}

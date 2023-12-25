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

package run.mone.mimeter.dashboard.bo.common;


public class Constants {

    public static final String MiOne_Tenant = "TEST";

    public static final int STATUS_ON = 1;
    public static final int STATUS_OFF = 2;

    public static final int DEGREE_WARN = 1;
    public static final int DEGREE_ERROR = 2;

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_WORK = 2;
    public static final int ROLE_GUEST = 3;

    public static final int HTTP_REQ_GET = 0;
    public static final int HTTP_REQ_POST = 1;

    public static final int MI_API_HTTP_REQ_GET = 1;
    public static final int MI_API_HTTP_REQ_POST = 0;

    public static final int REPORT_STATUS_RUNNING = 0;

    public static final int REPORT_STATUS_INACTIVE = 1;

    public static final int REPORT_STATUS_FINISH = 3;

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final int SINGLE_API_DEBUG = 0;
    public static final int SCENE_DEBUG = 1;
    public static final int SCENE_BENCH = 2;

    public static final int HTTP_API_TYPE = 1;
    public static final int DUBBO_API_TYPE = 3;
    public static final int GATEWAY_API_TYPE = 4;

    public static final String DEFAULT_EXPR_PREX = "params.toMap()";

    public static final String DEFAULT_EXPR_JSON_PREX = "params.json()";

    public static final String EXPR_INT_FLAG = "::int";

    public static final String EXPR_BOOLEAN_FLAG = "::boolean";

    public static final String EXPR_STRING_FLAG = "::string";

    public static final String EXPR_LIST_FLAG = "::list[";

    public static final int SCENE_TYPE_HTTP = 0;
    public static final int SCENE_TYPE_DUBBO = 1;


    public static final int NACOS_TYPE_ST = 0;
    public static final int NACOS_TYPE_OL = 1;

    public static final int SCENE_PARAM_DATA_TYPE_GLOBAL = 0;
    public static final int SCENE_PARAM_DATA_TYPE_LINK = 1;

    public static final String CONTENT_TYPE_APP_JSON= "application/json";
    public static final String CONTENT_TYPE_APP_FORM= "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_APP_FORM2= "x-www-form-urlencoded";

    public static final String SKIP_MI_DUN_USER_NAME = "mone-skip-mi-dun-username";

    public static final String PROJECT_NAME = "MiMeter";

    public static final String NACOS_ST = "staging";
    public static final String NACOS_CN_ONLINE = "online";

    public static final int CASE_TYPE_HTTP = 0;
    public static final int CASE_TYPE_RPCX = 1;

    public static final int DEFAULT_SCENE_GROUP = 30001;

    public static final Long DEFAULT_API_TIMEOUT = 6000L;

    public static final int SCENE_ENV_ST = 0;

    public static final int SCENE_ENV_OL = 1;

    public static final long ONE_YEAR_MS = 1000 * 60 * 60 * 24 * 365L;


    public static final String BENCH_BEGIN_MSG = "{\n" +
            "  \"config\": {\n" +
            "    \"wide_screen_mode\": true\n" +
            "  },\n" +
            "  \"header\": {\n" +
            "    \"title\": {\n" +
            "      \"tag\": \"plain_text\",\n" +
            "      \"content\": \"MiMeter压测平台消息通知\"\n" +
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
            "        \"content\": \"场景名: ${sceneName}\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"div\",\n" +
            "      \"text\": {\n" +
            "        \"tag\": \"plain_text\",\n" +
            "        \"content\": \"操作人: ${opUser}\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"div\",\n" +
            "      \"text\": {\n" +
            "        \"tag\": \"plain_text\",\n" +
            "        \"content\": \"压测时长：${benchTime}\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"div\",\n" +
            "      \"text\": {\n" +
            "        \"tag\": \"plain_text\",\n" +
            "        \"content\": \"压测量级：${totalRps}\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"hr\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"tag\": \"div\",\n" +
            "      \"text\": {\n" +
            "        \"tag\": \"lark_md\",\n" +
            "        \"content\": \"压测报告链接：<a>https://test.com/report-detail?reportId=${reportId}&isRunning=1</a>\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

}

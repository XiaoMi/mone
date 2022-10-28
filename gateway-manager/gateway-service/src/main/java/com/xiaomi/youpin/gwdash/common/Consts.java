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

package com.xiaomi.youpin.gwdash.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Consts {
    private Consts() {
    }

    public static final List<Integer> ValidRoles = Arrays.asList(
            Consts.ROLE_ADMIN,
            Consts.ROLE_WORK
    );

    public static final List<Integer> ValidBoolValue = Arrays.asList(
            Consts.BOOL_NO,
            Consts.BOOL_YES
    );

    public static final List<String> ValidHttpMethods = Arrays.asList(
            "get",
            "post"
    );

    public static final List<String> ValidContentTypes = Arrays.asList(
            "application/json",
            "application/x-www-form-urlencoded"
    );

    /**
     * 起容器的时候,需要额外的加一些内存,因为除了堆内存还需要一些其他内存占用
     */
    public static final long ContainerExtraMem = 512 * 1024 * 1024;

    public static final int DEFAULT_PAGE_SIZE = 10;

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_WORK = 2;
    public static final int ROLE_GUEST = 3;

    public static final int BOOL_NO = 0;
    public static final int BOOL_YES = 1;

    public static final int HTTP_SESSION_TIMEOUT = 3 * 60 * 60;

    public static final int STATUS_VALID = 0;
    public static final int STATUS_DELETED = 1;

    public static final String PLUGIN_OPT_TOKEN = "";

    public static final String HTTP_METHOD_GET = "get";

    public static final String HTTP_METHOD_POST = "post";

    public static final int DEFAULT_API_TIMEOUT = 1000; //毫秒

    public static final int FILTER_STATUS_NEW = 1;
    public static final int FILTER_STATUS_EFFECT = 2;
    public static final int FILTER_STATUS_DELETE = 3;

    //新建
    public static final int DOCKER_IMAGE_STATUS_NEW = 0;
    //审核通过
    public static final int DOCKER_IMAGE_STATUS_EFFECT = 2;
    //审核未通过
    public static final int DOCKER_IMAGE_STATUS_REJECT = 3;
    //docker build 完成
    public static final int DOCKER_IMAGE_STATUS_BUILD = 4;
    //删除
    public static final int DOCKER_IMAGE_STATUS_DELETE = 5;
    public static final String DOCKER_LOG_PATH_PREFIX = "/home/work/log/";

    public static final int FILTER_OFFLINE = 0;
    public static final int FILTER_ONLINE = 1;

    //group type
    public  static  final  int GROUP_TYPE_QUERY_ALL=1;
    public  static  final  int GROUP_TYPE_QUERY_CREATEDBYME=2;
    public  static  final  int GROUP_TYPE_QUERY_UPDATEDBYME=3;
    public  static  final  int GROUP_TYPE_QUERY_MyCOLLECTION=4;

    public static final int APP_SRC_DEFAULT = 0;
    public static final int APP_SRC_TIANGONG = 1;

    public static final int API_SRC_DEFAULT = 0;
    public static final int API_SRC_TIANGONG = 1;

    // public enum ReleaseStatus { UNKNOW, COMPILATION_RUNING, COMPILATION_SUCCESS, COMPILATION_FAIL, DEPLOYMENT_RUNING, DEPLOYMENT_SUCCESS, DEPLOYMENT_FAIL, DEPLOYMENT_SUCCESS_OFF }

    public enum DeploymentStatus { UNKNOW, DEPLOYING, DEPLOYED, UNDEPLOY };

    public enum DeploymentRecordStatus { UNKNOW, RUNING, SUCCESS, FAIL };

    public static final String APPLY_MACHINE_ENV_ONLINE = "online";

    public static final String APPLY_MACHINE_ENV_STAGING = "staging";

    public static long CPU_LIMIT = 8L;

    public static long MEM_LIMIT = 32768L;

    public static long INSTANCE_LIMIT = 15L;

    public static final String DOCKER_COUNT_KEY = "docker_count";

    public static final String QPS_COUNT_KEY = "qps_count";

    public static final String BILLING_TOP_TEN = "billing_top_ten";

    public static final String TENEMENT_DATA_ID = "gwdash.tenement.setting";

    /**
     * 不使用米盾而使用header中的userName获取账号信息的参数
     */
    public static final String SKIP_MI_DUN_USER_NAME = "mone-skip-mi-dun-username";

    /**
     * 米效redis缓存key的顶级前缀
     */
    public static final String MIONE_PREFIX = "mione:";
    /**
     * 自动化部署的在redis缓存的（二级）自动发布的相关的key前缀
     */
    public static final String AUTO_START_PREFIX = MIONE_PREFIX + "autoStart:";

    public static final String LOCK_KEY_PREFIX = MIONE_PREFIX + "lockKey:";

    public static final String GIT_ACCESS_TOKEN_CACHE = MIONE_PREFIX + "git-access-token-cache";

    public static final String USER_BATCH_LOCK_KEY_PREFIX = MIONE_PREFIX + "user-batch:";

    /**
     * 空格
     */
    public static final String BLANK = " ";

    /**
     * 可以修改nacosConfig的角色
     */
    public static final String[] canEditNacosConfigRoles = {"admin", "nacos admin"};

    /**
     * map按照value倒序排序
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

}

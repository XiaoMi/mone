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
import java.util.List;

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

    public static final String SESSION_ATTR_NAME = "name";
    public static final String CONST_CAS_ASSERTION = "_const_cas_assertion_";
    public static final String SESSION_ATTR_ROLE = "role";
    public static final String SESSION_ATTR_TOKEN = "token";
    public static final String SESSION_ATTR_UUID = "uuid";
    public static final String SESSION_ATTR_GID = "gid";
    public static final String SESSION_ATTR_USERNAME = "username";

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

    public static final String PLUGIN_OPT_TOKEN = "2dd64d4f-5e46-4148-bf83-9c9ba6470d7c";

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
    public static final String DOCKER_LOG_PATH_PREFIX = "xxxx/log/";

    public static final int FILTER_OFFLINE = 0;
    public static final int FILTER_ONLINE = 1;

    //group type
    public  static  final  int GROUP_TYPE_QUERY_ALL=1;
    public  static  final  int GROUP_TYPE_QUERY_CREATEDBYME=2;
    public  static  final  int GROUP_TYPE_QUERY_UPDATEDBYME=3;
    public  static  final  int GROUP_TYPE_QUERY_MyCOLLECTION=4;

    // public enum ReleaseStatus { UNKNOW, COMPILATION_RUNING, COMPILATION_SUCCESS, COMPILATION_FAIL, DEPLOYMENT_RUNING, DEPLOYMENT_SUCCESS, DEPLOYMENT_FAIL, DEPLOYMENT_SUCCESS_OFF }

    public enum DeploymentStatus { UNKNOW, DEPLOYING, DEPLOYED, UNDEPLOY };

    public enum DeploymentRecordStatus { UNKNOW, RUNING, SUCCESS, FAIL };

    public static final String APPLY_MACHINE_ENV_ONLINE = "online";

    public static final String APPLY_MACHINE_ENV_STAGING = "staging";

    public static long CPU_LIMIT = 8L;

    public static long MEM_LIMIT = 32768L;

    public static long INSTANCE_LIMIT = 10L;

    public static final String DOCKER_COUNT_KEY = "docker_count";

}

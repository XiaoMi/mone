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

import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BizUtils {
    private BizUtils() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BizUtils.class);

    /**
     * 用于校验新增账户参数
     * @param param
     * @return
     */
    public static CheckResult chkNewAccountParam(AccountParam param) {

        if (null == param) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "空的账号参数");

        }

        if (StringUtils.isBlank(param.getUsername()) ||
                StringUtils.isBlank(param.getName()) ||
                StringUtils.isBlank(param.getEmail()) ||
                StringUtils.isBlank(param.getPhone())) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的账号参数");
        }

        if (null == param.getRole() || !Consts.ValidRoles.contains(param.getRole())) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的账号角色参数");
        }

        return new CheckResult(true, CommonError.Success.getCode(), "");
    }

    public static CheckResult chkUpdateAccountParam(AccountParam param) {

        if (null == param) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "空的账号参数");

        }

        if (null == param.getId() || param.getId() <= 0) {
            return new CheckResult(false,
                    CommonError.InvalidIDParamError.getCode(), "无效的账号ID参数");
        }

        if (null == param.getRole() || !Consts.ValidRoles.contains(param.getRole())) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的账号角色参数");
        }

        return new CheckResult(true, CommonError.Success.getCode(), "");
    }

    public static long memToDockerMem (long mem) {
        return mem * 1048576;
    }

    /**
     * 用于校验新增ApiGroupInfo参数
     * @param param
     * @return
     */
    public static CheckResult chkNewApiGroupInfoParam(ApiGroupInfoParam param) {
        if (null ==  param) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "空的ApiGroupInfo参数");
        }

        if (StringUtils.isBlank(param.getName())) {
            return new CheckResult(false,
                    CommonError.InvalidApiGroupNameError.getCode(), "无效的ApiGroup名称");
        }

        if (StringUtils.isBlank(param.getDescription())) {
            return new CheckResult(false,
                    CommonError.InvalidApiGroupDescError.getCode(), "无效的ApiGroup描述信息");
        }

        if (StringUtils.isBlank(param.getBaseUrl())) {
            return new CheckResult(false,
                    CommonError.InvalidApiGroupBaseUrlError.getCode(), "无效的ApiGroup BaseUrl信息");
        }

        return new CheckResult(true, CommonError.Success.getCode(), "");
    }

    /**
     * 用于校验新建ApiInfo参数
     * @param param
     * @return
     */
    public static CheckResult chkNewApiInfoParam(ApiInfoParam param) {
        if (null ==  param) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "空的ApiInfo参数");
        }

        if (StringUtils.isBlank(param.getName())) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的Api名称");
        }
// 2019-09-16 17:16:19 黄江需求 描述字段改为可选
//        if (StringUtils.isBlank(param.getDescription())) {
//            return new CheckResult(false,
//                    CommonError.InvalidParamError.getCode(), "无效的Api描述");
//        }

        if (!RouteType.isValidRouteType(param.getRouteType())) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的route类型");
        }

        if (StringUtils.isBlank(param.getUrl())) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的路径");
        }

        if (RouteType.isDirect(param.getRouteType())) {
            return chkNewHttpApiParam(param);
        } else if (RouteType.isDubbo(param.getRouteType())) {
            return chkNewDubboApiParam(param);
        }

        return new CheckResult(true, CommonError.Success.getCode(), "");

    }

    /**
     * todo 用于校验更新Resource的参数
     * @param param
     * @return
     */
    public static CheckResult chkUpdateResourceParam(ResourceParam param) {
        if (null ==  param) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "空的resource参数");
        }

        if (StringUtils.isBlank(param.getIp())) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的参数");
        }

        return new CheckResult(true, CommonError.Success.getCode(), "");

    }

    public static CheckResult chkNewHttpApiParam(ApiInfoParam param) {
        if (StringUtils.isBlank(param.getPath())) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的目标路径信息");
        }

//        String url = param.getPath().toLowerCase();
//        if (!url.startsWith("http://") && !url.startsWith("https://")) {
//            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "无效的目标路径格式");
//        }

        return new CheckResult(true, CommonError.Success.getCode(), "");
    }

    public static CheckResult chkNewDubboApiParam(ApiInfoParam param) {
        if (StringUtils.isBlank(param.getServiceName())) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的服务名称信息");
        }

        if (StringUtils.isBlank(param.getMethodName())) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的方法名称信息");
        }

        return new CheckResult(true, CommonError.Success.getCode(), "");
    }

    public static CheckResult chkAgentUpdateParam(AgentUpdateParam param) {
        if (null == param) {
            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "空的Agent更新参数");
        }
        if (param.getId() == null) {
            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "无效的Agent更新参数");
        }

        return new CheckResult(true, CommonError.Success.getCode(), "");
    }

    /**
     * 用于检查ApiGroupInfo的更新参数
     * @param param
     * @return
     */
    public static CheckResult chkUpdateApiGroupParam(ApiGroupInfoUpdateParam param) {
        if (null ==  param) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "空的ApiGroupInfo更新参数");
        }

        if (param.getId() == null || param.getId() <= 0) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的ApiGroupInfo id参数");
        }

        if (StringUtils.isBlank(param.getBaseUrl())) {
            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "API前缀不可为空");
        }

        return new CheckResult(true, CommonError.Success.getCode(), "");
    }

    /**
     * 用于更新ApiInfo更新参数
     * @param param
     * @return
     */
    public static CheckResult chkUpdateApiInfoParam(ApiInfoUpdateParam param) {
        if (null ==  param) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "空的ApiInfo更新参数");
        }

        if (param.getId() == null || param.getId() <= 0) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的ApiInfo id参数");
        }

        // TODO add check logic for necessary fields

        return new CheckResult(true, CommonError.Success.getCode(), "");
    }


    public static CheckResult chkGitlabOptParam(GitlabOptParam param) {
        if (param == null) {
            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "无效的参数信息");
        }

        if (StringUtils.isBlank(param.getProjectId())) {
            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "无效的projectId");
        }

        if (StringUtils.isBlank(param.getToken())) {
            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "无效的token");
        }

        if (StringUtils.isBlank(param.getPath())) {
            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "无效的path");
        }

        if (StringUtils.isBlank(param.getBranch())) {
            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "无效的branch");
        }
        return new CheckResult(true, CommonError.Success.getCode(), "");
    }

    /**
     * 用于设置ApiInfo DAO 对象空字段的默认值
     * @param apiInfo
     */
    public static void setApiDefaults(ApiInfo apiInfo) {
            if (apiInfo == null) {
                return;
            }

            if (apiInfo.getMethodName() == null) {
                apiInfo.setMethodName("");
            }
            if (apiInfo.getServiceName() == null) {
                apiInfo.setServiceName("");
            }
            if (apiInfo.getServiceGroup() == null) {
                apiInfo.setServiceGroup("");
            }
            if (apiInfo.getServiceVersion() == null) {
                apiInfo.setServiceVersion("");
            }
            if (apiInfo.getParamTemplate() == null) {
                apiInfo.setParamTemplate("");
            }
            if (apiInfo.getPath() == null) {
                apiInfo.setPath("");
            }

            if (apiInfo.getContentType() == null) {
                apiInfo.setContentType("");
            }

            if (apiInfo.getStatus() == null) {
                apiInfo.setStatus(0);
            }

            if (apiInfo.getTimeout() == null || apiInfo.getTimeout() <= 0) {
                apiInfo.setTimeout(Consts.DEFAULT_API_TIMEOUT);
            }

            if (apiInfo.getInvokeLimit() == null || apiInfo.getInvokeLimit() <= 0) {
                apiInfo.setInvokeLimit(50);
            }

            if (apiInfo.getQpsLimit() == null || apiInfo.getQpsLimit() <= 0) {
                apiInfo.setQpsLimit(50);
            }

            if (apiInfo.getToken() == null) {
                apiInfo.setToken("");
            }

            if (StringUtils.isNotBlank(apiInfo.getUrl())) {
                apiInfo.setUrl(apiInfo.getUrl().trim());
            }

            if (StringUtils.isNotBlank(apiInfo.getPath())) {
                apiInfo.setPath(apiInfo.getPath().trim());
            }
    }

    public static CheckResult chkApiDebugParam(ApiDebugParam param) {
        if (null ==  param) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "空的API Debug参数");
        }

        if (param.getAid() == null || param.getAid() <= 0) {
            return new CheckResult(false,
                    CommonError.InvalidParamError.getCode(), "无效的API id参数");
        }

        if (StringUtils.isBlank(param.getHttpMethod()) && !Consts.ValidHttpMethods.contains(param.getHttpMethod())) {
            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "无效的http method");
        }

        if (StringUtils.isBlank(param.getUrl())) {
            return new CheckResult(false, CommonError.InvalidParamError.getCode(), "无效的URL");
        }

        return new CheckResult(true, CommonError.Success.getCode(), "");
    }

    public static boolean isSuperRole(SessionAccount account) {
        long count = account.getRoles().stream().filter(role -> "SuperRole".equals(role.getName())).count();
        if (count > 0) {
            return true;
        }
        return false;
    }

}

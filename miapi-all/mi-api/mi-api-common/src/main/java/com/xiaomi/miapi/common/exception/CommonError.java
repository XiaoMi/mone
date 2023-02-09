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

package com.xiaomi.miapi.common.exception;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * error enum
 */
public enum CommonError {

    Success(0, "success"),
    UnknownError(1, "unknown error"),

    InvalidParamError(100001, "无效的参数"),
    InvalidIDParamError(100002, "无效的id参数"),
    InvalidPageParamError(100003, "无效的列表请求参数"),
    InvalidProjectParamError(100004, "无效的用户名或密码"),
    InvalidApiGroupNameError(100005, "无效的ApiGroup名称"),
    InvalidApiGroupDescError(100006, "无效的ApiGroup描述信息"),
    NotAuthorizedProjectError(100007, "您无权创建或修改该项目的API"),
    NotAuthorizedGroupOptError(100008, "您无权创建或修改Team"),
    EmptyFileError(100009, "文件为空！"),
    NotAuthorizedPluginOptError(100010, "未经授权的操作！"),
    InvalidApiGroupBaseUrlError(100011, "无效的ApiGroup baseUrl信息"),
    UrlExistError(100012, "URL已经存在于数据库中，请更换"),
    CaseNotExist(100003, "测试用例不存在"),

    NewAccountError(200001, "创建账号失败"),
    UnAuthorized(200002, "无权限执行此操作"),
    NoProjectIdError(200003, "未指定项目id"),
    UpdateAccountError(200004, "更新账号失败"),
    DelAccountError(200005, "删除账号失败"),
    AccountNotFoundError(200006, "未找到给定账号信息"),
    AccountExistError(200007, "用户名已经存在"),
    JsonSerializeError(200008, "JSON序列化失败"),
    JsonDeSerializeError(200008, "网络异常,请刷新"),
    APIAlreadyExist(200009, "API 已经存在"),
    APIDoNotExist(200010, "API不存在"),
    GitlabOptError(200011, "gitlab操作失败"),
    FileTypeError(200012, "错误的文件类型"),
    DriftError(200013, "drift失败"),
    ProjectAlreadyExist(200014, "项目已存在"),
    UserIsAlreadyMember(200015, "该用户已经是项目成员"),
    DubboApiForIpPortNotFound(200016, "该服务尚未接入dubbo文档依赖"),
    ProjectDoNotExist(200017, "项目不存在"),
    RpcCallError(200018, "rpc调用失败"),
    GatewayAPIDoesNotExist(200019, "网关中不存在该API"),
    IndexDoNotExist(200020,"索引组不存在"),
    EorrorEnv(200021,"测试环境无法选用线上环境数据"),
    ProjectGroupAlreadyExist(200021, "项目组已存在"),
    APIUrlAlreadyExist(200022, "API 路径 已经存在"),
    MockDoNotAllowed(200023, "请先打开mock开关"),
    ConnectRefused(20024,"Connect refused"),
    MockExceptAlreadyExist(200025, "该期望已存在"),
    ErrorMockJsRule(200026, "mock js 数据格式不正确"),
    PleaseWait(20027,"尽请期待"),
    HttpApiForIpPortNotFound(200027, "该服务尚未接入http文档依赖"),
    ServiceMustRun(200028, "服务需要运行才可同步"),
    ApiMustBeLoaded(200029, "接口需要是自动加载方式才可同步"),
    ServiceMustGetGrpcConfig(200030, "服务需要接入Grpc配置"),
    ApplyPermissionPlease(200031, "请先申请权限"),
    WrongDubboEnv(200032, "请选择正确的环境"),

    DataSyxNotSupported(200033, "不支持的数据格式"),

    SidecarApiForIpPortNotFound(200034, "该服务尚未接入sidecar文档依赖")
    ;

    private final int code;
    private final String message;

    CommonError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

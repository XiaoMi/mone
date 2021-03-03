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

package com.xiaomi.youpin.gwdash.exception;

/**
 * Created by zhangzhiyong on 05/06/2018.
 * 错误定义
 */
public enum CommonError {

    Success(0, "success"),
    UnknownError(1, "unknown error"),

    // 参数问题
    InvalidParamError(100001, "无效的参数"),
    InvalidIDParamError(100002, "无效的id参数"),
    InvalidPageParamError(100003, "无效的列表请求参数"),
    InvalidUsernameOrPasswdError(100004, "无效的用户名或密码"),
    InvalidApiGroupNameError(100005, "无效的ApiGroup名称"),
    InvalidApiGroupDescError(100006, "无效的ApiGroup描述信息"),
    NotAuthorizedGroupError(100007, "您无权创建或修改该Team的API"),
    NotAuthorizedGroupOptError(100008, "您无权创建或修改Team"),
    EmptyFileError(100009, "文件为空！"),
    NotAuthorizedPluginOptError(100010, "未经授权的操作！"),
    InvalidApiGroupBaseUrlError(100011, "无效的ApiGroup baseUrl信息"),
    UrlExistError(100012, "URL已经存在于数据库中，请更换"),

    NewAccountError(200001, "创建账号失败"),
    UnAuthorized(200002, "无权限执行此操作"),
    BeanCopyError(200003, "执行属性拷贝失败"),
    UpdateAccountError(200004, "更新账号失败"),
    DelAccountError(200005, "删除账号失败"),
    AccountNotFoundError(200006, "未找到给定账号信息"),
    AccountExistError(200007, "用户名已经存在"),
    JsonSerializeError(200008, "JSON序列化失败"),
    JsonDeSerializeError(200008, "JSON反序列化失败"),
    APIServerError(200009, "API Server请求失败"),
    FileSaveError(200010, "文件保存失败"),
    GitlabOptError(200011, "gitlab操作失败"),
    FileTypeError(200012, "错误的文件类型"),
    DriftError(200013, "drift失败"),
    ReviewPassError(200014, "项目已审核完成");

    public int code;
    public String message;

    CommonError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

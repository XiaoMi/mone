package com.xiaomi.youpin.infra.rpc.errors;

/**
 * 通用错误码，只定义通用的错误码，不可以定义业务错误
 * 数据库、redis等系统错误，都认为是InternalError(500)，区分code没有意义，最终还是要依赖log去调查
 * 0或4开头的错误码都被自动标为可用，5开头的错误码都被自动标为不可用
 *
 * Created by daxiong on 2018/8/21.
 */
public final class GeneralCodes {
    // 成功
    public static ErrorCode OK = new ErrorCode(0);
    // 内部错误
    public static ErrorCode InternalError = new ErrorCode(500);
    // 系统繁忙
    public static ErrorCode ServerIsBuzy = new ErrorCode(50013);
    // 参数错误
    public static ErrorCode ParamError = new ErrorCode(400);
    // 未登录
    public static ErrorCode NotAuthorized = new ErrorCode(401);
    // 无权限
    public static ErrorCode Forbidden = new ErrorCode(403);
    // 找不到资源
    public static ErrorCode NotFound = new ErrorCode(404);
    // 被限流
    public static ErrorCode Throttled = new ErrorCode(418);
}

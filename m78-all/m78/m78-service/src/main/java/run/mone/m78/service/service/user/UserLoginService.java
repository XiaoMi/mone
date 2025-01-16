package run.mone.m78.service.service.user;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.SecureUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.api.enums.StatusEnum;
import run.mone.m78.service.bo.user.BizUserInfo;
import run.mone.m78.service.bo.user.CheckLoginReq;
import run.mone.m78.service.bo.user.GoogleUserLoginReq;
import run.mone.m78.service.bo.user.UserLoginReq;
import run.mone.m78.service.bo.user.UserLoginRes;
import run.mone.m78.service.dao.entity.UserLoginPo;
import run.mone.m78.service.dao.entity.table.UserLoginPoTableDef;
import run.mone.m78.service.dao.mapper.UserLoginMapper;
import run.mone.m78.service.service.google.GoogleIdTokenService;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2024/4/29 10:28
 */
@Service
@Slf4j
public class UserLoginService {

    @Autowired
    private Redis redis;

    @Resource
    private UserLoginMapper userLoginMapper;

    @Resource
    private GoogleIdTokenService googleIdTokenService;

    private static final int USERINFO_REDIS_TTL = 1000 * 60 * 60 * 24 * 7;

    /**
     * 用户登录方法
     *
     * @param userLoginReq 用户登录请求对象，包含用户名、密码和应用ID等信息
     * @return 登录结果，成功时返回包含用户名、token和应用ID的UserLoginRes对象，失败时返回错误信息
     */
    public Result<UserLoginRes> login(UserLoginReq userLoginReq) {
        String authKeyHash = SecureUtil.md5(userLoginReq.getPassword());
        // 构建查询条件
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(UserLoginPo::getUserName, userLoginReq.getUserName())
                .eq(UserLoginPo::getPassword, authKeyHash)
                .eq(UserLoginPo::getStatus, StatusEnum.NOT_DELETED.getCode())
                .eq(UserLoginPo::getAppId, userLoginReq.getAppId());

        // 查询用户信息
        List<UserLoginPo> userLoginPos = userLoginMapper.selectListByQuery(queryWrapper);

        // 检查是否找到匹配的用户
        if (CollectionUtils.isEmpty(userLoginPos)) {
            return Result.fail(STATUS_INTERNAL_ERROR, "用户名或密码错误");
        }

        // 更新用户的登录 token
        String token = UUID.fastUUID().toString();
        UserLoginPo userLoginPo = userLoginPos.get(userLoginPos.size() - 1);
        userLoginPo.setToken(token);
        userLoginPo.setLastLoginTime(System.currentTimeMillis());
        int updateCount = userLoginMapper.update(userLoginPo);

        // 检查更新是否成功
        if (updateCount == 1) {
            cacheUserInfo(userLoginPo);
            return Result.success(UserLoginRes.builder().userName(userLoginReq.getUserName()).token(token).appId(userLoginReq.getAppId()).build());
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "登录失败");
        }
    }

    /**
     * 用户登出操作
     *
     * @param userLoginReq 用户登录请求，包含用户名、token、应用ID等信息
     * @return 包含操作结果的Result对象，成功时返回"退出成功"，失败时返回错误信息
     */
    public Result<String> loginOut(UserLoginReq userLoginReq) {
        // 构建查询条件
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(UserLoginPo::getUserName, userLoginReq.getUserName())
                .eq(UserLoginPo::getToken, userLoginReq.getToken())
                .eq(UserLoginPo::getStatus, StatusEnum.NOT_DELETED.getCode())
                .eq(UserLoginPo::getAppId, userLoginReq.getAppId());
        // 查询用户信息
        List<UserLoginPo> userLoginPos = userLoginMapper.selectListByQuery(queryWrapper);
        // 检查用户是否登录
        if (CollectionUtils.isEmpty(userLoginPos)) {
            return Result.fail(STATUS_NOT_FOUND, "没有此账户");
        }
        UserLoginPo po = userLoginPos.get(0);
        //作用就是让老的token失效
        po.setToken(UUID.fastUUID().toString());
        userLoginMapper.update(po);
        removeUserInfoFromRedis(po);
        return Result.success("退出成功");
    }

    /**
     * 注册新用户
     *
     * @param userLoginReq 用户登录请求，包含用户名和应用ID等信息
     * @return 注册结果，成功返回"注册成功"，失败返回相应的错误信息
     */
    public Result<String> register(UserLoginReq userLoginReq) {
        // 检查用户是否已经注册
        if (isUserRegistered(userLoginReq.getUserName(), userLoginReq.getAppId())) {
            return Result.fail(STATUS_INTERNAL_ERROR, "用户已注册");
        }

        // 创建新用户并插入数据库
        UserLoginPo userLoginPo = createUserLoginPo(userLoginReq);
        int insertCount = userLoginMapper.insert(userLoginPo);
        if (insertCount == 1) {
            return Result.success("注册成功");
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "注册失败");
        }
    }

    /**
     * 检查用户登录请求中的用户名和令牌是否匹配
     *
     * @param req 用户登录请求，包含用户名和令牌
     * @return 如果用户名和令牌匹配，返回true；否则返回false
     */
    public boolean checkToken(UserLoginReq req) {
        String username = req.getUserName();
        String token = req.getToken();
        long count = userLoginMapper.selectCountByCondition(UserLoginPoTableDef.USER_LOGIN_PO.USER_NAME.eq(username).and(UserLoginPoTableDef.USER_LOGIN_PO.TOKEN.eq(token)));
        return count > 0;
    }

    /**
     * 检查用户是否已经注册
     */
    private boolean isUserRegistered(String userName, Integer appId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select()
                .eq(UserLoginPo::getAppId, appId)
                .eq(UserLoginPo::getUserName, userName)
                .eq(UserLoginPo::getStatus, StatusEnum.NOT_DELETED.getCode());
        return CollectionUtils.isNotEmpty(userLoginMapper.selectListByQuery(queryWrapper));
    }

    /**
     * 创建新用户对象
     */
    private UserLoginPo createUserLoginPo(UserLoginReq userLoginReq) {
        UserLoginPo userLoginPo = new UserLoginPo();
        userLoginPo.setUserName(userLoginReq.getUserName());
        userLoginPo.setPassword(SecureUtil.md5(userLoginReq.getPassword()));
        String token = SecureUtil.sha1(userLoginReq.getUserName());
        userLoginPo.setToken(token);
        userLoginPo.setStatus(StatusEnum.NOT_DELETED.getCode());
        userLoginPo.setCtime(System.currentTimeMillis());
        userLoginPo.setUtime(System.currentTimeMillis());
        userLoginPo.setAppId(userLoginReq.getAppId());
        return userLoginPo;
    }


    /**
     * 缓存用户登录信息到Redis
     *
     * @param userLoginPo 用户登录信息对象
     * @return 总是返回true，表示缓存操作成功
     */
    public boolean cacheUserInfo(UserLoginPo userLoginPo) {
        String key = getRedisKey(userLoginPo.getAppId(), userLoginPo.getToken());
        BizUserInfo bizUserInfo = BizUserInfo.builder()
                .userName(userLoginPo.getUserName())
                .lastLoginTime(userLoginPo.getLastLoginTime())
                .appId(userLoginPo.getAppId())
                .build();
        redis.set(key, bizUserInfo, USERINFO_REDIS_TTL);
        return true;
    }

    /**
     * 从Redis中移除用户信息
     *
     * @param userLoginPo 用户登录信息对象，包含AppId和Token
     * @return 总是返回true
     */
    public boolean removeUserInfoFromRedis(UserLoginPo userLoginPo) {
        String key = getRedisKey(userLoginPo.getAppId(), userLoginPo.getToken());
        redis.del(key);
        return true;
    }


    /**
     * 根据提供的登录请求信息验证用户的Token，并返回用户信息
     *
     * @param checkLoginReq 包含Token和AppId的登录请求信息
     * @return 包含用户信息的Result对象，如果未找到用户信息则返回失败的Result对象
     */
    public Result<BizUserInfo> authToken(CheckLoginReq checkLoginReq) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(checkLoginReq.getToken()) && checkLoginReq.getAppId() != null, "req is invalid");
        // 构建Redis Key
        String key = getRedisKey(checkLoginReq.getAppId(), checkLoginReq.getToken());
        // 从Redis获取用户信息
        BizUserInfo bizUserInfo = redis.get(key, BizUserInfo.class);
        // 检查是否找到用户信息
        if (bizUserInfo != null) {
            return Result.success(bizUserInfo);
        } else {
            return Result.fail(STATUS_NOT_FOUND, "未找到用户信息");
        }
    }


    /**
     * 实现google登录，判断userName是否存在，如果不存在，调用注册接口进行注册，然后再调用登录接口，返回结果
     *
     * @param userLoginReq
     * @return
     */
    public Result<UserLoginRes> googleLogin(GoogleUserLoginReq googleUserLoginReq) {
        // TODO : 换成公共的
        List<String> clientIds = new ArrayList<>();
        clientIds.add("555367256149-85uhnr84u107hsa2auc80t9q0kosbm29.apps.googleusercontent.com");
        clientIds.add("555367256149-0485sjlhrh63bbhu1aqrjjhudnvs15m1.apps.googleusercontent.com");

        Pair<String, String> result = googleIdTokenService.verifyGoogleIdToken(googleUserLoginReq.getJwtToken(), clientIds);
        if(result == null){
            return Result.fail(STATUS_INTERNAL_ERROR, "Google check valid failed");
        }
        String email = result.getKey();
        String userId = result.getValue();
        UserLoginReq userLoginReq = createUserLoginReq(email, googleUserLoginReq.getAppId(), userId);
        if (!isUserRegistered(email, googleUserLoginReq.getAppId())) {
            Result<String> registerResult = register(userLoginReq);
            if (GeneralCodes.OK.getCode() != registerResult.getCode()) {
                return Result.fail(STATUS_INTERNAL_ERROR, "注册失败");
            }
        }
        return login(userLoginReq);

    }

    private String getRedisKey(Integer appId, String token) {
        return "project:userToken:" + appId + ":" + token;
    }


    private UserLoginReq createUserLoginReq(String email, Integer appId, String password) {
        UserLoginReq userLoginReq = new UserLoginReq();
        userLoginReq.setUserName(email);
        userLoginReq.setAppId(appId);
        userLoginReq.setPassword(password);
        return userLoginReq;
    }

}

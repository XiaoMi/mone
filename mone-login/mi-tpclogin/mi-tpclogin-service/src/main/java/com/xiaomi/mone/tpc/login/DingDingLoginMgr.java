package com.xiaomi.mone.tpc.login;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.tpc.login.common.vo.AuthAccountVo;
import com.xiaomi.mone.tpc.login.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.login.common.vo.ResultVo;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DingDingLoginMgr extends LoginMgr {

    @NacosValue("${dingding.client_id:}")
    private String clientId;
    @NacosValue("${dingding.client_secret:''}")
    private String clientSecret;

    @NacosValue("${dingding.oauth.auth.url:''}")
    private String oauthAuthUrl;

    @NacosValue("${dingding.oauth.token.url:''}")
    private String oauthTokenUrl;

    @NacosValue("${dingding.oauth.user.url:''}")
    private String oauthUserUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 构建Auth2LoginInfo
     *
     * @return
     */
    @Override
    public AuthAccountVo buildAuth2LoginInfo(String pageUrl, String vcode, String state) throws Exception {
        AuthAccountVo info = new AuthAccountVo();
        info.setName("dingding");
        info.setDesc("dingding账号授权登陆");
        info.setUrl(this.buildAuthUrl(clientId, pageUrl, vcode, state));
        return info;
    }

    @Override
    public ResultVo<AuthUserVo> getUserVo(String code, String pageUrl, String vcode, String state) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("grantType", "authorization_code");
            map.put("clientId", clientId);
            map.put("clientSecret", clientSecret);
            map.put("code", code);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity<Map> entity = new HttpEntity<>(map, headers);
            ResponseEntity<Map> tokenResponseEntity = restTemplate.exchange(getTokenUrl(), HttpMethod.POST, entity, Map.class);
            log.info("oauth2 code to token request={}, response={}", map, tokenResponseEntity);
            if (tokenResponseEntity.getBody() == null || !tokenResponseEntity.getBody().containsKey("accessToken")) {
                return ResponseCode.OUTER_CALL_FAILED.build("access_token获取失败");
            }
            headers = new HttpHeaders();
            headers.add("x-acs-dingtalk-access-token", tokenResponseEntity.getBody().get("accessToken").toString());
            headers.add("Content-Type", "application/json");
            entity = new HttpEntity<>(headers);
            ResponseEntity<Map> responseEntity = restTemplate.exchange(getUserUrl(), HttpMethod.GET, entity, Map.class);
            log.info("userInfo.feishu={}", responseEntity);
            if (responseEntity.getBody() == null || responseEntity.getBody().get("unionId") == null) {
                log.error("dingding没有拿到open_id字段， responseEntity={}", responseEntity);
                return ResponseCode.NO_OPER_PERMISSION.build("用户信息[open_id]获取失败，请授权");
            }
            AuthUserVo userVo = new AuthUserVo();
            userVo.setExprTime(Integer.parseInt(tokenResponseEntity.getBody().get("expireIn").toString()));
            userVo.setUserType(UserTypeEnum.FEISHU_TYPE.getCode());
            userVo.setAccount(responseEntity.getBody().get("unionId").toString());
            userVo.setToken(TokenUtil.createToken(userVo.getExprTime(), userVo.getAccount(), userVo.getUserType()));
            if (responseEntity.getBody().get("unionId") != null) {
                userVo.setUnionId(responseEntity.getBody().get("unionId").toString());
            }
            if (responseEntity.getBody().get("openId") != null) {
                userVo.setOpenId(responseEntity.getBody().get("openId").toString());
            }
            if (responseEntity.getBody().get("nick") != null) {
                userVo.setName(responseEntity.getBody().get("nick").toString());
            }
            if (responseEntity.getBody().get("avatarUrl") != null) {
                userVo.setAvatarUrl(responseEntity.getBody().get("avatarUrl").toString());
            }
            return ResponseCode.SUCCESS.build(userVo);
        } catch (Throwable e) {
            log.error("dingding_oauth2_failed", e);
            return ResponseCode.UNKNOWN_ERROR.build("用户信息获取异常，请稍后重试");
        }
    }

    @Override
    public String getSource() {
        return "dingding";
    }

    @Override
    public String getAuthUrl() {
        return oauthAuthUrl + "?response_type=code&scope=openid&state=tpclogin&prompt=consent";
    }

    @Override
    public String getTokenUrl() {
        return oauthTokenUrl;
    }

    @Override
    public String getUserUrl() {
        return oauthUserUrl;
    }

}

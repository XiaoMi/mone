package com.xiaomi.mone.tpc.login;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.tpc.login.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.common.vo.AuthAccountVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class FeiShuLoginMgr extends LoginMgr {

    @NacosValue("${feishu.client_id:}")
    private String clientId;
    @NacosValue("${feishu.client_secret:''}")
    private String clientSecret;

    @NacosValue("${feishu.oauth.auth.url:''}")
    private String oauthAuthUrl;

    @NacosValue("${feishu.oauth.token.url:''}")
    private String oauthTokenUrl;

    @NacosValue("${feishu.oauth.user.url:''}")
    private String oauthUserUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 构建Auth2LoginInfo
     * @return
     */
    @Override
    public AuthAccountVo buildAuth2LoginInfo(String pageUrl, String vcode, String state) throws Exception {
        AuthAccountVo info = new AuthAccountVo();
        info.setName("feishu");
        info.setDesc("feishu账号授权登陆");
        info.setUrl(this.buildAuthUrl(clientId, pageUrl, vcode, state));
        return info;
    }

    @Override
    public AuthUserVo getUserVo(String code, String pageUrl, String vcode, String state) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("grant_type", "authorization_code");
            map.put("redirect_uri", getAuth2CallbackUrlFull(pageUrl, vcode, state));
            map.put("client_id", clientId);
            map.put("client_secret", clientSecret);
            map.put("code", code);
            Map responseMap = restTemplate.postForObject(getTokenUrl(), map, Map.class);
            log.info("oauth2 code to token request={}, response={}", map, responseMap);
            if (responseMap == null || !responseMap.containsKey("access_token")) {
                return null;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + responseMap.get("access_token"));
            HttpEntity<Map> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> responseEntity = restTemplate.exchange(getUserUrl(), HttpMethod.GET, entity, Map.class);
            log.info("userInfo.gatlab={}", responseEntity);
            String account = null;
            if (responseEntity.getBody().get("user_id") != null) {
                account = responseEntity.getBody().get("user_id").toString();
            }
            if (responseEntity.getBody().get("open_id") != null) {
                account = responseEntity.getBody().get("open_id").toString();
            }
            if (responseEntity.getBody() == null || StringUtils.isBlank(account)) {
                log.error("feishu没有拿到user_id字段， responseEntity={}", responseEntity);
                return null;
            }
            AuthUserVo userVo = new AuthUserVo();
            if (responseEntity.getBody().get("email") != null) {
                userVo.setEmail(responseEntity.getBody().get("email").toString());
            }
            userVo.setExprTime(3600 * 48);
            userVo.setUserType(UserTypeEnum.GITLAB_TYPE.getCode());
            userVo.setAccount(account);
            userVo.setToken(TokenUtil.createToken(userVo.getExprTime(), userVo.getAccount(), userVo.getUserType()));
            if (responseEntity.getBody().get("avatar_url") != null) {
                userVo.setAvatarUrl(responseEntity.getBody().get("avatar_url").toString());
            }
            if (responseEntity.getBody().get("name") != null) {
                userVo.setName(responseEntity.getBody().get("name").toString());
            }
            return userVo;
        } catch (Throwable e) {
            log.error("gitlab_oauth2_failed", e);
            return null;
        }
    }

    @Override
    public String getSource() {
        return "feishu";
    }

    @Override
    public String getAuthUrl() {
        return oauthAuthUrl + "?response_type=code";
    }

    @Override
    public String getTokenUrl() {
        return oauthTokenUrl;
    }

    @Override
    public String getUserUrl() {
        return oauthUserUrl;
    }

    @Override
    public String getEmailUrl() {
        return null;
    }
}

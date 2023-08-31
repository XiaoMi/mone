package com.xiaomi.mone.tpc.login;

import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.vo.AuthAccountVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class GitlabLoginMgr extends LoginMgr {

    @Value("${gitlab.client_id}")
    private String clientId;
    @Value("${gitlab.client_secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 构建Auth2LoginInfo
     * @return
     */
    @Override
    public AuthAccountVo buildAuth2LoginInfo(String pageUrl) throws Exception {
        AuthAccountVo info = new AuthAccountVo();
        info.setName("gitlab");
        info.setDesc("gitlab账号授权登陆");
        info.setUrl(this.buildAuthUrl(clientId, pageUrl));
        return info;
    }

    @Override
    public AuthUserVo getUserVo(String code, String pageUrl) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("grant_type", "authorization_code");
            map.put("redirect_uri", getAuth2CallbackUrlFull(pageUrl));
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
            if (responseEntity.getBody() == null || responseEntity.getBody().get("username") == null) {
                return null;
            }
            AuthUserVo userVo = new AuthUserVo();
            userVo.setExprTime(Integer.parseInt(responseMap.get("expires_in").toString()));
            userVo.setUserType(UserTypeEnum.GITLAB_TYPE.getCode());
            userVo.setAccount(responseEntity.getBody().get("username").toString());
            userVo.setToken(TokenUtil.createToken(userVo.getExprTime(), userVo.getAccount(), userVo.getUserType()));
            if (responseEntity.getBody().get("avatar_url") != null) {
                userVo.setAvatarUrl(responseEntity.getBody().get("avatar_url").toString());
            }
            if (responseEntity.getBody().get("name") != null) {
                userVo.setName(responseEntity.getBody().get("name").toString());
            }
            return userVo;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getSource() {
        return "gitlab";
    }

    @Override
    public String getAuthUrl() {
        return "https://gitlab.com/oauth/authorize?response_type=code&scope=api read_user";
    }

    @Override
    public String getTokenUrl() {
        return "https://gitlab.com/oauth/token";
    }

    @Override
    public String getUserUrl() {
        return "https://gitlab.com/api/v4/user";
    }
}

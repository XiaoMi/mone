package com.xiaomi.mone.tpc.login;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.tpc.login.common.vo.AuthAccountVo;
import com.xiaomi.mone.tpc.login.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.login.common.vo.ResultVo;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.util.ImgUtil;
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

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GithubLoginMgr extends LoginMgr {

    @NacosValue("${github.client_id:}")
    private String clientId;
    @NacosValue("${github.client_secret:''}")
    private String clientSecret;

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
        info.setName("github");
        info.setDesc("github账号授权登陆");
        info.setUrl(this.buildAuthUrl(clientId, pageUrl, vcode, state));
        info.setIcon(getLogoData());
        return info;
    }

    @Override
    public ResultVo<AuthUserVo> getUserVo(String code, String pageUrl, String vcode, String state) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("client_id", clientId);
            map.put("client_secret", clientSecret);
            map.put("code", code);
            Map responseMap = restTemplate.postForObject(getTokenUrl(), map, Map.class);
            log.info("oauth2 code to token request={}, response={}", map, responseMap);
            if (responseMap == null || !responseMap.containsKey("access_token")) {
                return ResponseCode.OUTER_CALL_FAILED.build("access_token获取失败");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + responseMap.get("access_token"));
            HttpEntity<Map> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> responseEntity = restTemplate.exchange(getUserUrl(), HttpMethod.GET, entity, Map.class);
            log.info("responseEntity.github={}", responseEntity);
            if (responseEntity.getBody() == null || responseEntity.getBody().get("email") == null) {
                return ResponseCode.NO_OPER_PERMISSION.build("公开资料[email]没有设置");
            }
            AuthUserVo userVo = new AuthUserVo();
            userVo.setExprTime(Integer.parseInt(responseMap.get("expires_in").toString()));
            userVo.setUserType(UserTypeEnum.GITHUB_TYPE.getCode());
            userVo.setAccount(responseEntity.getBody().get("email").toString());
            userVo.setToken(TokenUtil.createToken(userVo.getExprTime(), userVo.getAccount(), userVo.getUserType()));
            userVo.setEmail(responseEntity.getBody().get("email").toString());
            if (responseEntity.getBody().get("login") != null) {
                userVo.setUserId(responseEntity.getBody().get("login").toString());
            }
            if (responseEntity.getBody().get("avatar_url") != null) {
                userVo.setAvatarUrl(responseEntity.getBody().get("avatar_url").toString());
            }
            if (responseEntity.getBody().get("name") != null) {
                userVo.setName(responseEntity.getBody().get("name").toString());
            } else {
                userVo.setName(userVo.getUserId());
            }
            return ResponseCode.SUCCESS.build(userVo);
        } catch (Throwable e) {
            log.error("github_oauth2_failed", e);
            return ResponseCode.UNKNOWN_ERROR.build("用户信息获取异常，请稍后重试");
        }
    }

    @Override
    public String getSource() {
        return "github";
    }

    @Override
    public String getAuthUrl() {
        return "https://github.com/login/oauth/authorize?response_type=code&scope=read:user read:email";
    }

    @Override
    public String getTokenUrl() {
        return "https://github.com/login/oauth/access_token";
    }

    @Override
    public String getUserUrl() {
        return "https://api.github.com/user";
    }

}

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
     *
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
    public ResultVo<AuthUserVo> getUserVo(String code, String pageUrl, String vcode, String state) {
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
                return ResponseCode.OUTER_CALL_FAILED.build("access_token获取失败");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + responseMap.get("access_token"));
            HttpEntity<Map> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> responseEntity = restTemplate.exchange(getUserUrl(), HttpMethod.GET, entity, Map.class);
            log.info("userInfo.feishu={}", responseEntity);
            if (responseEntity.getBody() == null || !responseEntity.getBody().containsKey("user_id")) {
                log.error("feishu没有拿到user_id字段， responseEntity={}", responseEntity);
                return ResponseCode.NO_OPER_PERMISSION.build("用户信息[user_id]获取失败，请授权");
            }
            AuthUserVo userVo = new AuthUserVo();
            userVo.setExprTime(3600 * 48);
            userVo.setUserType(UserTypeEnum.FEISHU_TYPE.getCode());
            userVo.setAccount(responseEntity.getBody().get("user_id").toString());
            userVo.setToken(TokenUtil.createToken(userVo.getExprTime(), userVo.getAccount(), userVo.getUserType()));
            if (responseEntity.getBody().containsKey("tenant_key")) {
                userVo.setTenantKey(responseEntity.getBody().get("tenant_key").toString());
            }
            if (responseEntity.getBody().containsKey("union_id")) {
                userVo.setUnionId(responseEntity.getBody().get("union_id").toString());
            }
            if (responseEntity.getBody().containsKey("open_id")) {
                userVo.setOpenId(responseEntity.getBody().get("open_id").toString());
            }
            if (responseEntity.getBody().containsKey("user_id")) {
                userVo.setUserId(responseEntity.getBody().get("user_id").toString());
            }
            if (responseEntity.getBody().containsKey("name")) {
                userVo.setName(responseEntity.getBody().get("name").toString());
            }
            if (responseEntity.getBody().containsKey("en_name")) {
                userVo.setEnName(responseEntity.getBody().get("en_name").toString());
            }
            if (responseEntity.getBody().containsKey("picture")) {
                userVo.setAvatarUrl(responseEntity.getBody().get("picture").toString());
            }
            if (responseEntity.getBody().containsKey("email")) {
                userVo.setEmail(responseEntity.getBody().get("email").toString());
            }
            if (responseEntity.getBody().containsKey("employee_no")) {
                userVo.setEmployeeNo(responseEntity.getBody().get("employee_no").toString());
            }
            if (responseEntity.getBody().containsKey("mobile")) {
                userVo.setMobile(responseEntity.getBody().get("mobile").toString());
            }
            return ResponseCode.SUCCESS.build(userVo);
        } catch (Throwable e) {
            log.error("gitlab_oauth2_failed", e);
            return ResponseCode.UNKNOWN_ERROR.build("用户信息获取异常，请稍后重试");
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

}

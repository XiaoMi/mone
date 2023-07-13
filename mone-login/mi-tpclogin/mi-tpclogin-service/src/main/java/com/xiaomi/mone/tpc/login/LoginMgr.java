package com.xiaomi.mone.tpc.login;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.tpc.login.common.vo.AuthAccountVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class LoginMgr {

    private static final Map<String, LoginMgr> mgrMap = new ConcurrentHashMap<>();
    @NacosValue("${home.url:http://localhost:80}")
    private String homeUrl;

    public LoginMgr() {
        mgrMap.put(getSource(), this);
    }

    public abstract AuthAccountVo buildAuth2LoginInfo(String pageUrl, String vcode, String state) throws Exception;

    public abstract AuthUserVo getUserVo(String code, String pageUrl, String vcode, String state);

    public String buildAuthUrl(String clientId, String pageUrl, String vcode, String state) throws Exception {
        if (StringUtils.isBlank(clientId)) {
            return null;
        }
        StringBuilder auth2Url = new StringBuilder();
        auth2Url.append(getAuthUrl())
                .append("&redirect_uri=").append(URLEncoder.encode(getAuth2CallbackUrlFull(pageUrl, vcode, state), "UTF-8"))
                .append("&client_id=").append(clientId);
        return auth2Url.toString();
    }

    protected String getAuth2CallbackUrlFull(String pageUrl, String vcode, String state) throws Exception {
        StringBuilder auth2CallbackUrlFull = new StringBuilder();
        auth2CallbackUrlFull.append(homeUrl).append("/user-manage/login/code")
                .append("?source=").append(getSource())
                .append("&pageUrl=").append(URLEncoder.encode(pageUrl, "UTF-8"));
        if (StringUtils.isNotBlank(vcode)) {
            auth2CallbackUrlFull.append("&vcode=").append(vcode);
        }
        if (StringUtils.isNotBlank(state)) {
            auth2CallbackUrlFull.append("&state=").append(state);
        }
        return auth2CallbackUrlFull.toString();
    }

    public static List<AuthAccountVo> buildAuth2LoginInfos(String pageUrl, String vcode, String state) {
        List<AuthAccountVo> infos = new ArrayList<>();
        mgrMap.forEach((k, v) -> {
            try {
                infos.add(v.buildAuth2LoginInfo(pageUrl, vcode, state));
            } catch (Exception e) {
                log.info("构建{}的登陆信息异常", k, e);
            }
        });
        return infos;
    }

    public final static LoginMgr get(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return mgrMap.get(source);
    }

    public abstract String getSource();
    public abstract String getAuthUrl();
    public abstract String getTokenUrl();
    public abstract String getUserUrl();
    public abstract String getEmailUrl();

}

package com.xiaomi.mone.tpc.login;

import com.xiaomi.mone.tpc.common.vo.AuthAccountVo;
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
    @Value("${home.url}")
    private String homeUrl;

    public LoginMgr() {
        mgrMap.put(getSource(), this);
    }

    public abstract AuthAccountVo buildAuth2LoginInfo(String pageUrl) throws Exception;

    public abstract AuthUserVo getUserVo(String code, String pageUrl);

    public String buildAuthUrl(String clientId, String pageUrl) throws Exception {
        StringBuilder auth2Url = new StringBuilder();
        auth2Url.append(getAuthUrl())
                .append("&redirect_uri=").append(URLEncoder.encode(getAuth2CallbackUrlFull(pageUrl), "UTF-8"))
                .append("&client_id=").append(clientId);
        return auth2Url.toString();
    }

    protected String getAuth2CallbackUrlFull(String pageUrl) throws Exception {
        StringBuilder auth2CallbackUrlFull = new StringBuilder();
        auth2CallbackUrlFull.append(homeUrl).append("/user-manage/login/code")
                .append("?source=").append(getSource())
                .append("&pageUrl=").append(URLEncoder.encode(pageUrl, "UTF-8"));
        return auth2CallbackUrlFull.toString();
    }

    public static List<AuthAccountVo> buildAuth2LoginInfos(String pageUrl) {
        List<AuthAccountVo> infos = new ArrayList<>();
        mgrMap.forEach((k, v) -> {
            try {
                infos.add(v.buildAuth2LoginInfo(pageUrl));
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

}

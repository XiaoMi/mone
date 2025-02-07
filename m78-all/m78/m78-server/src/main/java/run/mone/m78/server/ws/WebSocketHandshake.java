/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.m78.server.ws;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.util.SignUtil;
import com.xiaomi.mone.tpc.login.util.SystemReqUtil;
import com.xiaomi.mone.tpc.login.util.TokenUtil;
import com.xiaomi.mone.tpc.login.vo.AuthTokenVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.ResultVo;
import com.xiaomi.mone.tpc.login.vo.UserInfoVO;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import run.mone.m78.common.Constant;
import run.mone.m78.service.bo.user.BizUserInfo;
import run.mone.m78.service.bo.user.CheckLoginReq;
import run.mone.m78.service.bo.user.UserLoginReq;
import run.mone.m78.service.common.SafeRun;
import run.mone.m78.service.context.ApplicationContextProvider;
import run.mone.m78.service.service.user.UserLoginService;
import run.mone.m78.service.service.z.ZService;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class WebSocketHandshake implements HandshakeInterceptor {


    private String[] publicKeys = null;
    private boolean isOpen;
    private UserLoginService userLoginService;

    public WebSocketHandshake(String aegisSdk, boolean isOpen) {
        this.publicKeys = aegisSdk.split("[,|，]");
        this.isOpen = isOpen;
    }

    public WebSocketHandshake(String aegisSdk, boolean isOpen, UserLoginService userLoginService) {
        this.publicKeys = aegisSdk.split("[,|，]");
        this.isOpen = isOpen;
        this.userLoginService = userLoginService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("websocket hand shake enter=======");
        HttpServletRequest servletRequest = getRequest(request);
        HttpServletResponse servletResponse = getResponse(response);
        AuthUserVo userInfo = null;
        // adjust http header
        String xAccount = servletRequest.getHeader("x-account");

        int userType = -99;
//        String xAccount = "dingtao";

        //允许通过参数传递x-account
        if (request.getURI().getPath().equals("/ws/bot/abc")) {
            UriComponents uriComponents = UriComponentsBuilder.fromUri(request.getURI()).build();
            MultiValueMap<String, String> queryParams = uriComponents.getQueryParams();
            xAccount = queryParams.getFirst("x-account");

            List<String> xTokenList = request.getHeaders().get("x-token");
            List<String> athenaTokenList = request.getHeaders().get("athena-token");
            List<String> userTypeList = request.getHeaders().get("athena-user-type");

            if (CollectionUtils.isEmpty(xTokenList) && CollectionUtils.isEmpty(athenaTokenList)) {
                log.error("/bot/abc websocket hand shake get user token is null !!!");
                return false;
            }

            if (!CollectionUtils.isEmpty(xTokenList)) {
                String token = xTokenList.get(0);
                UserLoginService service = ApplicationContextProvider.getBean(UserLoginService.class);
                UserLoginReq req = new UserLoginReq();
                req.setUserName(xAccount);
                req.setToken(token);
                boolean check = service.checkToken(req);
                if (!check) {
                    log.error("/bot/abc websocket hand shake get user token is error(x-token) !!!");
                } else {
                    userType = -98;
                }
            }

            if (!CollectionUtils.isEmpty(athenaTokenList)) {
                String token = athenaTokenList.get(0);
                ZService zService = ApplicationContextProvider.getBean(ZService.class);
                String username = zService.getUserName(token);
                if (StringUtils.isBlank(username)) {
                    log.error("/bot/abc websocket hand shake get user token is error(athena-token) !!!");
                } else {
                    xAccount = username;
                    userType = -97;
                    try {
                        if (!CollectionUtils.isNotEmpty(userTypeList)) {
                            Integer ut = Integer.valueOf(userTypeList.get(0));
                            if (ut < 0) {
                                userType = ut;
                            }
                        }
                    } catch (Throwable ignore) {

                    }
                }
            }
        }

        if (request.getURI().getPath().equals("/ws/bot/biz/abc") || request.getURI().getPath().equals("/ws/image/translate")) {
            List<String> cookies = Optional.ofNullable(request.getHeaders().get("Cookie")).orElse(request.getHeaders().get("cookie"));
            if (cookies != null && cookies.size() > 0) {
                String cookie = cookies.get(0);
                Map<String, String> cookieMap = Arrays.stream(cookie.split("; ")).map(c -> c.split("=")).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1], (v1, v2) -> v2));
                String m78AppId = cookieMap.get(Constant.M78_APP_ID);
                String m78Token = cookieMap.get(Constant.M78_TOKEN);
                if (StringUtils.isBlank(m78AppId) || StringUtils.isBlank(m78Token)) {
                    log.error("/ws/bot/biz/abc websocket hand shake get cookie is invalid !!!");
                    return false;
                }
                Result<BizUserInfo> authRst = userLoginService.authToken(CheckLoginReq.builder().appId(Integer.parseInt(m78AppId)).token(m78Token).build());
                if (authRst.getCode() != 0 || authRst.getData() == null) {
                    log.error("/ws/bot/biz/abc websocket hand shake authToken error is null !!!");
                    return false;
                }
                userInfo = new AuthUserVo();
                userInfo.setAccount(authRst.getData().getUserName());
                userInfo.setAttachments(ImmutableMap.of(Constant.M78_APP_ID, Integer.parseInt(m78AppId)));
                attributes.put("TPC_USER", userInfo);
                return true;
            } else {
                log.error("/ws/bot/biz/abc websocket hand shake get cookie is null !!!");
                return false;
            }
        }

        if (StringUtils.isNotEmpty(xAccount) || request.getURI().getPath().equals("/ws/multiModal/audio")) {
            userInfo = new AuthUserVo();
            userInfo.setAccount(xAccount);
            userInfo.setUserType(userType);
        } else {
            if (isOpen) {
                userInfo = getUserInfoOpen(servletRequest, servletResponse);
            } else {
                userInfo = getUserInfo(servletRequest, servletResponse);
            }
            if (userInfo == null) {
                log.error("websocket hand shake get user is null !!!");
                return false;
            }
        }
        attributes.put("TPC_USER", userInfo);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private HttpServletRequest getRequest(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest();
        } else {
            return null;
        }
    }

    private HttpServletResponse getResponse(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse) {
            ServletServerHttpResponse serverHttpResponse = (ServletServerHttpResponse) response;
            return serverHttpResponse.getServletResponse();
        } else {
            return null;
        }
    }

    private AuthUserVo getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse response) throws SignatureException {
        String verifyIdentitySignData = httpServletRequest.getHeader("X-Proxy-Midun");
        String url = httpServletRequest.getRequestURI();
        if (StringUtils.isEmpty(verifyIdentitySignData)) {
            log.error("没有标识身份的签名数据,url:{}", url);
            this.noAuthResponse(response);
        } else {
            String currentUsePublicKey = null;
            String verifyIdentityData = null;
            String[] var9 = this.publicKeys;
            int var10 = var9.length;

            for (int var11 = 0; var11 < var10; ++var11) {
                String key = var9[var11];
                verifyIdentityData = SignUtil.verifySignGetInfo(verifyIdentitySignData, key);
                if (StringUtils.isNotEmpty(verifyIdentityData)) {
                    currentUsePublicKey = key;
                    break;
                }
            }

            if (StringUtils.isEmpty(verifyIdentityData)) {
                log.error("检测身份,验签失败,url:{},signData:{}", url, verifyIdentitySignData);
                this.noAuthResponse(response);
            } else {
                log.info("账号登录,url:{}", url);
                String signAndUserSignData = httpServletRequest.getHeader("x-proxy-userdetail");
                if (StringUtils.isEmpty(signAndUserSignData)) {
                    log.info("确认请求，没有签名用户数据(bypass|静态资源)，url:{}", url);
                } else {
                    String userJson = SignUtil.verifySignGetInfo(signAndUserSignData, currentUsePublicKey);
                    if (StringUtils.isEmpty(userJson)) {
                        log.error("获取用户数据，验签失败,url:{},signData:{}", url, signAndUserSignData);
                        this.noAuthResponse(response);
                    } else {
                        Gson gson = (new GsonBuilder()).serializeNulls().create();
                        UserInfoVO userInfo = (UserInfoVO) gson.fromJson(userJson, UserInfoVO.class);
                        AuthUserVo authUserVo = new AuthUserVo();
                        authUserVo.setUserType(UserTypeEnum.CAS_TYPE.getCode());
                        authUserVo.setAccount(userInfo.getUser());
                        authUserVo.setName(userInfo.getDisplayName());
                        authUserVo.setEmail(userInfo.getEmail());
                        authUserVo.setAvatarUrl(userInfo.getAvatar());
                        authUserVo.setCasUid(userInfo.getuID());
                        authUserVo.setDepartmentName(userInfo.getDepartmentName());
//                        httpServletRequest.getSession().setAttribute("TPC_USER", authUserVo);
                        log.info("AuthCasFilter check success,url:{}", url);
                        return authUserVo;
                    }
                }
            }
        }
        return null;
    }

    private AuthUserVo getUserInfoOpen(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String uri = request.getRequestURI();
        AuthTokenVo authToken = TokenUtil.parseAuthToken(request);
        log.info("authToken={}", authToken);
        if (authToken == null) {
            log.info("request not login request_uri={}", uri);
            this.noAuthResponse(response);
        } else {
            ResultVo<AuthUserVo> resultVo = SystemReqUtil.authRequest(authToken.getAuthToken(), !authToken.isFromCookie());
            log.info("getResult={}", resultVo);
            if (resultVo != null && resultVo.success()) {
                if (!authToken.isFromCookie()) {
                    TokenUtil.setCookie(request, (AuthUserVo) resultVo.getData(), response);
                }
                return resultVo.getData();
            } else {
                log.info("request not login request_uri={}", uri);
                this.noAuthResponse(response);
            }
        }
        return null;
    }

    private void noAuthResponse(ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(401);
    }
}

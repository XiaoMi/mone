package run.mone.m78.server.ws;

import com.alibaba.fastjson.JSON;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import run.mone.m78.api.bo.multiModal.audio.AsrRecognizedError;
import run.mone.m78.api.bo.multiModal.audio.AsrRecognizedRes;
import run.mone.m78.common.URIParser;
import run.mone.m78.server.config.WebSocketConfig;
import run.mone.m78.service.asr.auth.RetailAuthService;
import run.mone.m78.service.common.GsonUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

/**
 * @author liuchuankang
 * @Type WebSocketASRHandshake.java
 * @Desc
 * @date 2024/9/6 16:46
 */
@Slf4j
public class WebSocketASRHandshake implements HandshakeInterceptor {

	private static final String AUTH_TYPE_RETAIL_FILTER = "retail-filter";
	private static final int DEFAULT_USER_TYPE = -99;

	private final String[] publicKeys;
	private final boolean isInternalOpen;
	private final boolean isExternalOpen;
	private final List<String> asrWsOutVendors;
	private final RetailAuthService retailAuthService;
	private WebSocketConfig config;

	public WebSocketASRHandshake(String aegisSdk, boolean isInternalOpen, boolean isExternalOpen, List<String> asrWsOutVendors, RetailAuthService retailAuthService, WebSocketConfig config) {
		this.publicKeys = aegisSdk.split("[,|，]");
		this.isInternalOpen = isInternalOpen;
		this.isExternalOpen = isExternalOpen;
		this.asrWsOutVendors = asrWsOutVendors;
		this.retailAuthService = retailAuthService;
		this.config = config;
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		HttpServletResponse servletResponse = getResponse(response);
		if (servletResponse == null) {
			return false;
		}

		String query = request.getURI().getQuery();
		log.info("websocketHandShakeEnter，query={}", query);
		URI uri = request.getURI();
		String vendor = URIParser.getQueryParamValue(uri, "vendor");
		String authType = URIParser.getQueryParamValue(uri, "authType");

		if (!asrWsOutVendors.contains(vendor)) {
			log.error("asr out not support，vendor={}", vendor);
			writeErrorResponse(servletResponse, AsrRecognizedError.ASR_PARAMS_ERROR, "fail");
			return false;
		}

		if (!isExternalOpen) {
			AuthUserVo authUserVo = requestFromM78(request, response, attributes);
			attributes.put("TPC_USER", authUserVo);
			return true;
		}

		if (StringUtils.isEmpty(authType)) {
			log.error("auth-type is error!!! authType={}", authType);
			writeErrorResponse(servletResponse, AsrRecognizedError.ASR_PARAMS_ERROR, "fail");
			return false;
		}

		AuthUserVo authUserVo = null;
		if (AUTH_TYPE_RETAIL_FILTER.equals(authType)) {
			authUserVo = retailAuthService.checkAuth(request);
			log.info("websocketHandShakeEnter，query={},authUser={}", query, GsonUtils.gson.toJson(authUserVo));
		}

		String authSwitch = this.config.getAuthSwitch();
		if (authUserVo == null && (!StringUtils.isEmpty(authSwitch) && authSwitch.equals("open"))) {
			log.error("authUserVo is null !!! query={}", query);
			writeErrorResponse(servletResponse, AsrRecognizedError.ASR_AUTH_CHECK_FAILED, "fail");
			return false;
		}
		return true;
	}

	private AuthUserVo requestFromM78(ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> attributes) throws Exception {
		HttpServletRequest servletRequest = getRequest(request);
		HttpServletResponse servletResponse = getResponse(response);
		if (servletRequest == null || servletResponse == null) {
			return null;
		}

		AuthUserVo userInfo = null;
		String xAccount = servletRequest.getHeader("x-account");

		if (StringUtils.isNotEmpty(xAccount) || request.getURI().getPath().equals("/ws/multiModal/audio")) {
			userInfo = new AuthUserVo();
			userInfo.setAccount(xAccount);
			userInfo.setUserType(DEFAULT_USER_TYPE);
		} else {
			if (isInternalOpen) {
				userInfo = getUserInfoOpen(servletRequest, servletResponse);
			} else {
				userInfo = getUserInfo(servletRequest, servletResponse);
			}
			if (userInfo == null) {
				log.error("websocket hand shake get user is null !!!");
				return null;
			}
		}
		return userInfo;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
		// No implementation needed
	}

	private HttpServletRequest getRequest(ServerHttpRequest request) {
		if (request instanceof ServletServerHttpRequest) {
			return ((ServletServerHttpRequest) request).getServletRequest();
		}
		return null;
	}

	private HttpServletResponse getResponse(ServerHttpResponse response) {
		if (response instanceof ServletServerHttpResponse) {
			return ((ServletServerHttpResponse) response).getServletResponse();
		}
		return null;
	}

	private AuthUserVo getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse response) throws SignatureException {
		String verifyIdentitySignData = httpServletRequest.getHeader("X-Proxy-Midun");
		String url = httpServletRequest.getRequestURI();
		if (StringUtils.isEmpty(verifyIdentitySignData)) {
			log.error("没有标识身份的签名数据,url:{}", url);
			noAuthResponse(response);
			return null;
		}

		String currentUsePublicKey = null;
		String verifyIdentityData = null;
		for (String key : publicKeys) {
			verifyIdentityData = SignUtil.verifySignGetInfo(verifyIdentitySignData, key);
			if (StringUtils.isNotEmpty(verifyIdentityData)) {
				currentUsePublicKey = key;
				break;
			}
		}

		if (StringUtils.isEmpty(verifyIdentityData)) {
			log.error("检测身份,验签失败,url:{},signData:{}", url, verifyIdentitySignData);
			noAuthResponse(response);
			return null;
		}

		log.info("账号登录,url:{}", url);
		String signAndUserSignData = httpServletRequest.getHeader("x-proxy-userdetail");
		if (StringUtils.isEmpty(signAndUserSignData)) {
			log.info("确认请求，没有签名用户数据(bypass|静态资源)，url:{}", url);
			return null;
		}

		String userJson = SignUtil.verifySignGetInfo(signAndUserSignData, currentUsePublicKey);
		if (StringUtils.isEmpty(userJson)) {
			log.error("获取用户数据，验签失败,url:{},signData:{}", url, signAndUserSignData);
			noAuthResponse(response);
			return null;
		}

		Gson gson = new GsonBuilder().serializeNulls().create();
		UserInfoVO userInfo = gson.fromJson(userJson, UserInfoVO.class);
		AuthUserVo authUserVo = new AuthUserVo();
		authUserVo.setUserType(UserTypeEnum.CAS_TYPE.getCode());
		authUserVo.setAccount(userInfo.getUser());
		authUserVo.setName(userInfo.getDisplayName());
		authUserVo.setEmail(userInfo.getEmail());
		authUserVo.setAvatarUrl(userInfo.getAvatar());
		authUserVo.setCasUid(userInfo.getuID());
		authUserVo.setDepartmentName(userInfo.getDepartmentName());
		log.info("AuthCasFilter check success,url:{}", url);
		return authUserVo;
	}

	private AuthUserVo getUserInfoOpen(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String uri = request.getRequestURI();
		AuthTokenVo authToken = TokenUtil.parseAuthToken(request);
		log.info("authToken={}", authToken);
		if (authToken == null) {
			log.info("request not login request_uri={}", uri);
			noAuthResponse(response);
			return null;
		}

		ResultVo<AuthUserVo> resultVo = SystemReqUtil.authRequest(authToken.getAuthToken(), !authToken.isFromCookie());
		log.info("getResult={}", resultVo);
		if (resultVo != null && resultVo.success()) {
			if (!authToken.isFromCookie()) {
				TokenUtil.setCookie(request, resultVo.getData(), response);
			}
			return resultVo.getData();
		} else {
			log.info("request not login request_uri={}", uri);
			noAuthResponse(response);
			return null;
		}
	}

	private void noAuthResponse(ServletResponse response) {
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setStatus(401);
	}

	private void writeErrorResponse(HttpServletResponse response, AsrRecognizedError error, String message) throws IOException {
		AsrRecognizedRes res = AsrRecognizedRes.builder().code(error.getCode()).message(message).build();
		response.getWriter().write(JSON.toJSONString(res));
	}
}

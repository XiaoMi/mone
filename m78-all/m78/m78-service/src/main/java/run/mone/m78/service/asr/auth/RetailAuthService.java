package run.mone.m78.service.asr.auth;

import com.alibaba.fastjson.JSON;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;
import run.mone.m78.common.URIParser;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuchuankang
 * @Type RetailAuth.java
 * @Desc
 * @date 2024/9/6 16:40
 */
@Slf4j
@DependsOn("botService")
@Service
public class RetailAuthService {

	@Value("${ref.loginAccount.group}")
	private String refLoginAccountGroup;

	private static final String ONLINE_TAG_VALUE = "1";

	private GenericService loginAccountProviderRpc;

	/**
	 * 检查用户的认证信息
	 *
	 * @param request 服务器HTTP请求
	 * @return 返回用户认证信息的对象，如果认证失败则返回null
	 */
	public AuthUserVo checkAuth(ServerHttpRequest request) {
		/*HttpHeaders headers = request.getHeaders();
		List<String> cookies = headers.get("Cookie");
		if (CollectionUtils.isEmpty(cookies)) {
			log.error("cookie不存在");
			return null;
		}
		String serviceToken = CookieHelper.extractToken(cookies.get(0), "serviceToken");
		if (serviceToken == null) {
			log.error("serviceToken不存在");
			return null;
		}
		List<String> hosts = headers.get("Host");
		if (CollectionUtils.isEmpty(hosts)) {
			log.error("host无法解析");
			return null;
		}*/
		//String onlineTag = CookieHelper.extractToken(cookies.get(0), "account_online_tag");
		//List<String> useDomainVersions = headers.get("USE-DOMAIN-VERSION");
		String serviceToken = URIParser.getQueryParamValue(request.getURI(), "serviceToken");

		AuthUserVo authUserVo = invokerDubboProvider(serviceToken, false, "2.0", null, null, null);

		return authUserVo;
	}

	/**
	 * 调用Dubbo服务获取用户信息
	 *
	 * @param serviceToken     服务令牌
	 * @param onlineTag        在线账户标识
	 * @param useDomainVersion 使用域名版本
	 * @param requestHeaders   请求头信息
	 * @param requestHost      请求主机
	 * @param requestBody      请求体
	 * @return 返回用户信息对象，如果获取失败则返回null
	 */
	public AuthUserVo invokerDubboProvider(String serviceToken, boolean onlineTag, String useDomainVersion, String requestHeaders,
										   String requestHost, String requestBody) {
		Map<String, Object> paraMap = new HashMap<>();
		try {
			String methodName = "getUserInfo";
			String[] paramTypes = {"com.xiaomi.cnzone.proretail.account.api.model.req.GetUserInfoReq"};
			paraMap.put("serviceToken", serviceToken);
			paraMap.put("onlineAccount", onlineTag);
			paraMap.put("useDomainVersion", useDomainVersion);
			paraMap.put("requestHeader", requestHeaders);
			paraMap.put("requestHost", requestHost);
			paraMap.put("requestBody", requestBody);
			Object[] paramValues = {paraMap};

			Map<String, Object> result = (Map<String, Object>) loginAccountProviderRpc.$invoke(methodName, paramTypes, paramValues);
			log.info("调用LoginAccountProvider.getUserInfo,result={}" + JSON.toJSONString(result));
			if (!result.get("code").equals(0)) {
				log.error("调用LoginAccountProvider.getUserInfo结果返回错误数据，result={}", JSON.toJSONString(result));
			} else {
				Map<String, Object> data = (Map<String, Object>) result.get("data");
				if (data != null) {
					String userId = (String) data.get("userId");
					if (StringUtils.isNotBlank(userId)) {
						AuthUserVo userInfo = new AuthUserVo();
						userInfo.setAccount((String) data.get("userId"));
						userInfo.setUserType(UserTypeEnum.CAS_TYPE.getCode());
						return userInfo;
					}
				}
			}
		} catch (Exception e) {
			log.error("调用LoginAccountProvider.getUserInfo异常，paraMap={}", JSON.toJSONString(paraMap));
		}
		return null;
	}

	/**
	 * 初始化方法，在Bean创建后执行
	 * <p>
	 * 该方法用于配置并获取登录账户服务的远程调用接口。
	 *
	 * @return 无返回值
	 */
	@PostConstruct
	public void init() {
		ApplicationConfig applicationConfig = ApplicationModel.getApplicationConfig();
		List<RegistryConfig> registryConfigs = ApplicationModel.getConfigManager().getDefaultRegistries();
		String interfaceName = "com.xiaomi.cnzone.proretail.account.api.provider.LoginAccountProvider";
		ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
		reference.setGeneric("true");
		reference.setApplication(applicationConfig);
		reference.setInterface(interfaceName);
		reference.setGroup(refLoginAccountGroup);
		reference.setVersion(null);
		reference.setTimeout(3000);
		reference.setRegistries(registryConfigs);
		this.loginAccountProviderRpc = reference.get();
	}


}

package com.xiaomi.youpin.gateway.netty.filter.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.xiaomi.youpin.gateway.common.HttpRequestUtils.getBasePath;


/**
 * 根据domain与请求API是否匹配来过滤
 */
@Slf4j
@Component
@FilterOrder(300)
public class DomainFilter extends RequestFilter {

    @Autowired
    private ConfigService configService;

    @Value("${mtop.group}")
    private String mtopGroupKey;

    private static Gson gson = new Gson();

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        String host = request.headers().get("Host");
        String uri = getBasePath(request);
        log.debug("DomainFilter.doFilter, request Host : {}", host);
        if (StringUtils.isEmpty(host)
                || (CollectionUtils.isNotEmpty(apiInfo.getDomains()) && !apiInfo.getDomains().contains(host))
                || !allowMtop(uri)) {
            return HttpResponseUtils.create(Result.fail(GeneralCodes.Forbidden, HttpResponseStatus.FORBIDDEN.reasonPhrase()));
        }
        return invoker.doInvoker(context, apiInfo, request);
    }

    private boolean allowMtop(String uri) {
        String mtopGroup = configService.getAllowMtopGroup();
        if (StringUtils.isEmpty(mtopGroup)) {
            return true;
        }
        Map<String, String> mtopMap = new Gson().fromJson(mtopGroup, new TypeToken<HashMap<String, String>>(){}.getType());
        if (!mtopMap.containsKey(mtopGroupKey)) {
            return true;
        }
        String mtopStr = mtopMap.get(mtopGroupKey);
        String[] mtopArr = mtopStr.split(";");
        return Arrays.asList(mtopArr).stream().anyMatch(it -> uri.startsWith(it));
    }
}

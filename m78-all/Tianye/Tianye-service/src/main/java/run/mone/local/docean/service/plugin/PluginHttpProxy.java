package run.mone.local.docean.service.plugin;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.xiaomi.data.push.client.HttpClientV5;
import run.mone.local.docean.fsm.bo.FlowReq;
import run.mone.local.docean.fsm.bo.InputData;
import run.mone.local.docean.fsm.bo.PluginInfo;
import run.mone.local.docean.fsm.flow.PluginFlow;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.GsonUtils;
import run.mone.local.docean.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author wmin
 * @date 2024/8/7
 */
@Slf4j
public class PluginHttpProxy {

    private PluginFlow pluginFlow;

    public PluginHttpProxy(PluginFlow pluginFlow) {
        this.pluginFlow = pluginFlow;
    }

    public String proxy(PluginInfo pluginInfo, FlowReq req){
        String urlStr = pluginInfo.getUrl();
        String methodStr = pluginInfo.getMethod();
        Map<String, String> headersMap = pluginInfo.getHeaders();

        JsonObject param = new JsonObject();
        Map<String, Object> paramMap = Maps.newHashMap();
        pluginFlow.getInputMap().entrySet()
                .stream()
                .filter(e -> e.getValue().isOriginalInput())
                .forEach(entry -> {
                    handleParams(req, entry, param, paramMap);
                });
        String reqUrl = "GET".equalsIgnoreCase(methodStr)
                ? HttpUtils.buildUrlWithParameters(urlStr, paramMap)
                : urlStr;
        //todo 页面设置或者plugin元数据带过来
        int timeout = 30 * 1000 * 10000;
        return doProxy(reqUrl, methodStr, headersMap, param, timeout);
    }

    private static void handleParams(FlowReq req, Map.Entry<String, InputData> entry, JsonObject param, Map<String, Object> paramMap) {
        // 特殊处理$$TY_USERNAME$$
        if (CommonConstants.TY_USERNAME_MARK.equals(entry.getKey())) {
            String userName = req.getUserName();
            param.addProperty(CommonConstants.TY_USERNAME_MARK, userName);
            paramMap.put(CommonConstants.TY_USERNAME_MARK, userName);
        } else {
            if(entry.getValue().getValue() != null) {
                param.add(entry.getKey(), entry.getValue().getValue());
                if (entry.getValue().getValue().isJsonPrimitive()) {
                    paramMap.put(entry.getKey(), entry.getValue().getValue().getAsJsonPrimitive().getAsString());
                }
            }
        }
    }

    private String doProxy(String reqUrl, String method, Map<String, String> headers, JsonObject param, int timeout) {
        if ("GET".equalsIgnoreCase(method.toUpperCase())) {
            return HttpClientV5.get(reqUrl, headers, timeout);
        }
        String body = GsonUtils.gson.toJson(param);
        log.info("plugin post body:{}", body);
        return HttpClientV5.post(reqUrl, body, headers, timeout);
    }
}

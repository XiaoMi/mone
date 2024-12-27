package run.mone.local.docean.fsm.flow;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.service.BotPluginService;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.GsonUtils;
import run.mone.local.docean.util.HttpUtils;

import java.util.Map;

/**
 * @author caobaoyu
 * @author goodjava@qq.com
 * @description: 调用外部 plugin
 * @date 2024-03-05 09:53
 */
@Slf4j
public class PluginFlow extends BotFlow {

    private static Gson gson = GsonUtils.gson;

    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        JsonObject res = applyPlugin(this.inputMap);
        storeResultsInReferenceData(context, res);
        return FlowRes.success(res);
    }

    @Override
    public String getFlowName() {
        return "plugin";
    }

    public JsonObject applyPlugin(Map<String, InputData> inputMap) {
        long pluginId = Long.parseLong(inputMap.get(CommonConstants.TY_PLUGIN_ID_MARK).getValue().getAsString());
        //从m78获取plugin信息
        PluginInfo pluginInfo = getPluginInfo(pluginId);
        String urlStr = pluginInfo.getUrl();
        String methodStr = pluginInfo.getMethod();
        Map<String, String> headersMap = pluginInfo.getHeaders();

        JsonObject param = new JsonObject();
        Map<String, Object> paramMap = Maps.newHashMap();
        inputMap.entrySet()
                .stream()
                .filter(e -> e.getValue().isOriginalInput()).forEach(entry -> {
                    param.add(entry.getKey(), entry.getValue().getValueByType());
                    if (entry.getValue().getValue().isJsonPrimitive()) {
                        paramMap.put(entry.getKey(), entry.getValue().getValue().getAsJsonPrimitive().getAsString());
                    }
                });
        String reqUrl = "GET".equalsIgnoreCase(methodStr)
                ? HttpUtils.buildUrlWithParameters(urlStr, paramMap)
                : urlStr;
        //todo 页面设置或者plugin元数据带过来
        int timeout = 30 * 1000 * 10000;
        String jsonStr = this.proxy(reqUrl, methodStr, headersMap, param, timeout);
        log.info("plugin proxy rst:{}", jsonStr);
        return GsonUtils.gson.fromJson(jsonStr, JsonObject.class);
    }

    private PluginInfo getPluginInfo(long pluginId) {
        BotPluginService botPluginService = Ioc.ins().getBean(BotPluginService.class);
        return botPluginService.getPluginInfoById(pluginId);
    }

    public String proxy(String reqUrl, String method, Map<String, String> headers, JsonObject param, int timeout) {
        if ("GET".equalsIgnoreCase(method.toUpperCase())) {
            return HttpClientV5.get(reqUrl, headers, timeout);
        }
        String body = gson.toJson(param);
        log.info("plugin post body:{}", body);
        return HttpClientV5.post(reqUrl, body, headers, timeout);
    }


}

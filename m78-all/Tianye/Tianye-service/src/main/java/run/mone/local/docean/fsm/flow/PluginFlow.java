package run.mone.local.docean.fsm.flow;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.service.BotPluginService;
import run.mone.local.docean.service.plugin.PluginDubboProxy;
import run.mone.local.docean.service.plugin.PluginHttpProxy;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.GsonUtils;
import run.mone.m78.api.bo.plugins.BotPluginDTO;

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
        JsonObject res = null;
        try {
            res = applyPlugin(this.inputMap, req, context);
        } catch (Throwable ex) {
            log.error("call plugin error:{}", ex);
            return FlowRes.failure("call plugin error:" + ex.getMessage());
        }
        storeResultsInReferenceData(context, res);
        return FlowRes.success(res);
    }

    @Override
    public String getFlowName() {
        return "plugin";
    }

    public JsonObject applyPlugin(Map<String, InputData> inputMap, FlowReq req, FlowContext context) {
        long pluginId = Long.parseLong(inputMap.get(CommonConstants.TY_PLUGIN_ID_MARK).getValue().getAsString());
        //从m78获取plugin信息
        BotPluginService botPluginService = Ioc.ins().getBean(BotPluginService.class);
        BotPluginDTO pluginDTO = botPluginService.getPluginDtoById(pluginId);

        if ("dubbo".equals(pluginDTO.getType())){
            String jsonStr = new PluginDubboProxy(this).proxy(pluginDTO, req, context);
            return GsonUtils.gson.fromJson(jsonStr, JsonObject.class);
        } else {
            PluginInfo pluginInfo = botPluginService.convertBotPluginDTOToPluginInfo(pluginDTO);
            String jsonStr = new PluginHttpProxy(this).proxy(pluginInfo, req);
            return GsonUtils.gson.fromJson(jsonStr, JsonObject.class);
        }
    }


}

package run.mone.local.docean.fsm.flow;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.context.TianyeContext;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.FlowContext;
import run.mone.local.docean.fsm.bo.FlowReq;
import run.mone.local.docean.fsm.bo.FlowRes;
import run.mone.local.docean.fsm.bo.InputData;
import run.mone.local.docean.handler.MessageProxy;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.GsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wmin
 * @date 2024/8/23
 */
@Slf4j
public class WorkChartFlow extends BotFlow {

    public static final String PROMPT_WORK_CHART_MERMAID_GEN = "work_chart_mermaid_gen";

    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        InputData input = this.inputMap.get(CommonConstants.TY_INPUT_MARK);
        Preconditions.checkArgument(null != input, "input can not be null");
        InputData charType = this.inputMap.get(CommonConstants.TY_WORK_CHART_TYPE_MARK);
        Preconditions.checkArgument(null != charType, "charType can not be null");

        //调用函数
        String res;
        try {
            res = postProxy(input.getValue().getAsString(), charType.getValue().getAsString(), getLLMModel());
        } catch (Exception e) {
            log.error("callFunction error,{}", e);
            return FlowRes.failure(e.getMessage());
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("output", res);
        storeResultsInReferenceData(context, jo);
        return FlowRes.success(null);
    }

    public String postProxy(String input, String chartType, String model) {
        log.info("postJsonProxy input:{},chartType:{}", input, chartType);
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("zzToken", getToken());
        List<String> params = new ArrayList<>();
        List<MessageProxy> msgs = new ArrayList<>();
        Map<String, Object> pm = new HashMap<>();
        MessageProxy message = new MessageProxy("user", null, PROMPT_WORK_CHART_MERMAID_GEN,
                ImmutableMap.of("input", input, "chartType", chartType));
        msgs.add(message);
        pm.put("messages", msgs);
        params.add(GsonUtils.gson.toJson(pm));
        body.put("params", params);
        body.put("temperature", getTemperature());
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        int timeout = getTimeout();
        String aiProxy = ((Config) Ioc.ins().getBean(Config.class)).get("ai.proxy", "");

        Stopwatch sw = Stopwatch.createStarted();
        String res = HttpClientV5.post(aiProxy + "/json3", GsonUtils.gson.toJson(body), header, timeout);
        log.info("postProxy res:{}, use time:{}s", res, sw.elapsed(TimeUnit.SECONDS));
        return res;
    }

    public String getLLMModel(){
        return inputMap.get(CommonConstants.TY_LLM_MODEL_MARK).getValue().getAsString();
    }

    public String getToken() {
        return TianyeContext.ins().getToken();
    }

    public Integer getTimeout() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_LLM_TIMEOUT_MARK, 550 * 1000, Integer.class);
    }

    public Double getTemperature() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_LLM_TEMPERATURE_MARK, 0.7, Double.class);
    }

    @Override
    public String getFlowName() {
        return "workChart";
    }

}

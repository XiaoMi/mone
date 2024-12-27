package run.mone.local.docean.fsm.flow;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.FlowContext;
import run.mone.local.docean.fsm.bo.FlowReq;
import run.mone.local.docean.fsm.bo.FlowRes;
import run.mone.local.docean.fsm.bo.InputData;
import run.mone.local.docean.tianye.common.CommonConstants;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 10:25
 * <p>
 * 处理Code的状态
 */
@Slf4j
@Data
public class CodeFlow extends BotFlow {

    private String code = "";

    @SneakyThrows
    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        loadCode();
        log.info("execute:{}", code);
        //调用函数
        JsonObject resObj;
        try {
            resObj = callFunction(this.inputMap);
        } catch (Exception e){
            log.error("callFunction error,{}", e);
            return FlowRes.failure(e.getMessage());
        }
        storeResultsInReferenceData(context, resObj);
        return FlowRes.success(null);
    }

    @Override
    public String getFlowName() {
        return "code";
    }

    private void loadCode() throws IOException {
        InputData inputCode = this.inputMap.get(CommonConstants.TY_CODE_INPUT_MARK);
        log.info("inputCode:{}", inputCode);
        code = inputCode.getValue().getAsString();
    }

    @NotNull
    private JsonObject callFunction(Map<String, InputData> inputMap) throws ScriptException, NoSuchMethodException {
        return new JsonObject();
    }
}

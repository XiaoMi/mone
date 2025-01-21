package run.mone.local.docean.fsm.flow;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.Ioc;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.local.docean.context.CodeContext;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.service.GroovyService;
import run.mone.local.docean.tianye.common.CommonConstants;

import javax.script.ScriptException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import com.xiaomi.data.push.rpc.common.Pair;

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

    private StringBuilder codeLog = new StringBuilder();

    @SneakyThrows
    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        loadCode();
        log.info("execute:{}", code);
        //调用函数
        JsonObject resObj;
        try {
            resObj = callFunction(this.inputMap, req, context);
        } catch (Exception e) {
            log.error("callFunction error,{}", e);
            return FlowRes.failure(e.getMessage());
        }
        if (StringUtils.isNotBlank(codeLog)){
            this.outputMap.put(CommonConstants.TY_CODE_LOG_MARK, OutputData.builder().build());
            resObj.addProperty(CommonConstants.TY_CODE_LOG_MARK, codeLog.toString());
        }
        storeResultsInReferenceData(context, resObj);

        FlowRes res = parseFlowResponse(resObj);
        if (res != null) {
            return res;
        }

        return FlowRes.success(null);
    }

    @Nullable
    private static FlowRes parseFlowResponse(JsonObject resObj) {
        if (resObj.has("_goto_")) {
            int flowId = resObj.get("_goto_").getAsInt();
            FlowRes res = FlowRes.success(null);
            res.setCode(FlowRes.GOTO);
            res.setAttachement(ImmutableMap.of("_goto_", flowId));
            return res;
        }
        return null;
    }

    @Override
    public String getFlowName() {
        return "code";
    }

    private void loadCode() throws IOException {
        // HINT: uncomment this for test only
        // code = Files.readString(Paths.get("/Users/zhangzhiyong/IdeaProjects/ai/Tianye/Tianye-server/src/test/resources/code.txt"));
        InputData inputCode = this.inputMap.get(CommonConstants.TY_CODE_INPUT_MARK);
        log.info("inputCode:{}", inputCode);
        code = inputCode.getValue().getAsString();
    }

    @NotNull
    private JsonObject callFunction(Map<String, InputData> inputMap, FlowReq flowReq, FlowContext flowContext) throws ScriptException, NoSuchMethodException {
        GroovyService groovyService = Ioc.ins().getBean(GroovyService.class);
        JsonObject req = new JsonObject();
        inputMap.entrySet()
                .stream()
                .filter(e -> e.getValue().isOriginalInput())
                .forEach(e -> req.add(e.getValue().getName(), e.getValue().getValue()));
        // 特殊处理$$TY_USERNAME$$
        req.addProperty(CommonConstants.TY_USERNAME_MARK, flowReq.getUserName());
        log.info("callFunction req:{}", req);

        Pair<Logger,ByteArrayOutputStream> pair = getLogger();
        CodeContext context = CodeContext.builder()
                .messageList(flowContext.getQuestionQueue())
                .memory(flowContext.getMemory())
                .meta(flowContext.getMeta())
                .logger(pair.getObject1())
                .build();

        //处理code返回的信息
        consumeMessageQueue(context, flowReq.getM78RpcAddr());

        JsonObject scriptRst = callGroovyScript(groovyService, req, context);
        flowContext.setMeta(context.getMeta());
        codeLog.append(pair.getObject2().toString());
        log.info("codeLog:{}", pair.getObject2().toString());
        return scriptRst;
    }

    private JsonObject callGroovyScript(GroovyService groovyService, JsonObject req, CodeContext context) throws ScriptException, NoSuchMethodException {
        try {
            JsonElement res = (JsonElement) groovyService.invoke(code, "execute", Maps.newHashMap(), req, context);
            log.info("callFunction re:{}", res);
            return (JsonObject) res;
        } finally {
            context.getQueue().add("quit!");
        }
    }

    private void consumeMessageQueue(CodeContext context, String m78RpcAddr) {
        new Thread(() -> {
            for (; ; ) {
                String message = null;
                try {
                    message = context.getQueue().take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (null == message || message.equals("quit!")) {
                    break;
                }
                log.info("receive message:{}", message);
                Map<String, String> meta = new HashMap<>(ImmutableMap.of("message", message, "from", "bot"));
                //通知到用户
                getSyncFlowStatusServices().syncM78FlowStatus(SyncFlowStatus.builder().flowRecordId(this.getFlowRecordId()).endFlowStatus(-99).build(), this.getFlowRecordId(), false, System.currentTimeMillis(), meta, "FLOW_EXECUTE_MESSAGE", m78RpcAddr);
            }
            log.info("message thread exit");
        }).start();
    }

    private Pair<Logger,ByteArrayOutputStream> getLogger(){
        Logger logger = Logger.getLogger("codeFlow");
        // 创建一个ByteArrayOutputStream来捕获日志输出
        ByteArrayOutputStream logOutput = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(logOutput);

        // 创建一个自定义的日志处理器
        Handler customHandler = new java.util.logging.ConsoleHandler() {
            @Override
            public void publish(LogRecord record) {
                printStream.println(record.getLevel() + " " + record.getMessage());
            }

            @Override
            public void flush() {
                printStream.flush();
            }
        };
        customHandler.setLevel(Level.ALL); // 设置处理器的日志级别
        // 将自定义处理器添加到Logger
        logger.addHandler(customHandler);
        return new Pair<>(logger, logOutput);
    }
}

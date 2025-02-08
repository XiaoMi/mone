package run.mone.m78.server.ws.biz;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.server.ws.WsSessionHolder;
import run.mone.m78.server.ws.biz.bo.VisionMsg;
import run.mone.m78.server.ws.biz.bo.VisionReq;
import run.mone.m78.server.ws.biz.bo.VisionResponse;
import run.mone.m78.service.bo.BaseMessage;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.service.base.ProxyAiService;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/5/18 15:39
 * 处理多模态的
 */
@Service
@Slf4j
public class VisionHandlerService {


    /**
     * 处理多模态的调用请求（例如对某个图片的解释）
     *
     * @param req 包含视觉消息的请求列表
     * @param context 业务上下文，包含请求和用户信息
     */
	//多模态的调用(比如对某个图片的解释)
    public void handle(List<VisionMsg> req, BizContext context) {
        log.info("req:{}", req);
        VisionReq visionReq = VisionReq.builder().model("claude3").temperature(0.2f).msgs(req).build();
        JsonObject res = ProxyAiService.vision(GsonUtils.gson.toJson(visionReq));
        String reqId = "";
        if (context.getReq().has("id")) {
            reqId = context.getReq().get("id").getAsString();
        }
        WsSessionHolder.INSTANCE.sendMessage(context.getUser(), VisionResponse.builder().messageType("vision").id(reqId).msg(res.get("data").getAsString()).build());
    }




}

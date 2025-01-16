package run.mone.m78.server.controller;

import com.alibaba.fastjson.JSON;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.multiModal.WanxTaskNotifyReq;
import run.mone.m78.api.bo.multiModal.audio.OfflineAsrReqDTO;
import run.mone.m78.api.bo.multiModal.audio.SentenceRecognitionRes;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.ws.FlowRecordSessionHolder;
import run.mone.m78.server.ws.StreamTypeEnum;
import run.mone.m78.service.asr.tencent.TencentAsrBaseService;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.exceptions.ExCodes;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.multiModal.AsrSentenceRecognitionService;
import run.mone.m78.service.service.multiModal.MultiModalHistoryService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;


/**
 * @author wmin
 * @date 2024/7/26
 */
@Slf4j
@RestController
@RequestMapping(value = "/open-apis/v1/multiModal")
public class MultiModalOpenAPIController {

    @Resource
    private MultiModalHistoryService modalHistoryService;

    @Resource
    private AsrSentenceRecognitionService sentenceRecognitionService;

    @PostMapping("/notifyByTaskId")
    @ResponseBody
    public Result<Boolean> notifyByTaskId(HttpServletRequest request, @RequestBody WanxTaskNotifyReq notifyReq) {
        log.info("notifyByTaskId req:{}", notifyReq);
        //根据validateReq校验入参
        Pair<Boolean, String> checkReq = notifyReq.validateReq();
        if (checkReq.getKey()){
            return modalHistoryService.notifyByTaskId(notifyReq);
        }
        return Result.fail(ExCodes.STATUS_BAD_REQUEST, "req is invalid");
    }

    // asr语音识别,一句话识别
    @PostMapping("/sentenceRecognition")
    @ResponseBody
    public SentenceRecognitionRes sentenceRecognition(HttpServletRequest request, @RequestBody OfflineAsrReqDTO req) {
        log.info("sentenceRecognition req:{}", JSON.toJSONString(req));
        return sentenceRecognitionService.sentenceRecognition(req);
    }

}

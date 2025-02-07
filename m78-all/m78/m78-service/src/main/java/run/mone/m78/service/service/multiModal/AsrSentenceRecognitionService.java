package run.mone.m78.service.service.multiModal;

import com.alibaba.fastjson.JSON;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.asr.v20190614.models.SentenceRecognitionRequest;
import com.tencentcloudapi.asr.v20190614.models.SentenceRecognitionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.multiModal.audio.*;
import run.mone.m78.common.Constant;
import run.mone.m78.service.asr.tencent.TencentAsrBaseService;
import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Service

/**
 * AsrSentenceRecognitionService类提供了离线语音识别的服务。
 * 该类依赖于TencentAsrBaseService和AudioAsrCostService来处理语音识别请求和计费。
 * 主要功能包括接收包含音频数据和格式的请求对象，调用腾讯语音识别接口进行处理，并返回识别结果。
 * 该类还负责处理请求参数的校验和异常处理。
 */

public class AsrSentenceRecognitionService {

    private static final String SENTENCE_SUFFIX = "_sentence";

    @Resource
    private TencentAsrBaseService asrBaseService;

    @Resource
    private AudioAsrCostService asrCostService;

    /**
     * 进行离线语音识别
     *
     * @param req 包含音频数据和格式的请求对象
     * @return 语音识别结果，包括状态码、消息、请求ID和识别结果
     */
    public SentenceRecognitionRes sentenceRecognition(OfflineAsrReqDTO req) {
        // 生成uuid，方便问题排查
        String uuid = UUID.randomUUID().toString();
        String message = "";
        log.info("sentenceRecognition uuid:{} req:{}", uuid, JSON.toJSON(req));
        String account = asrBaseService.getAccountBySentenceFrom(req.getFrom());
        if (account == null) {
            message = "from业务线错误";
        } else if (req.getAudioData().isEmpty() || req.getFormat().isEmpty()) {
            message = "缺少audioData或者format参数";
        }

        // 校验失败
        if (!message.isEmpty()) {
            return SentenceRecognitionRes.builder()
                    .code(AsrRecognizedError.ASR_PARAMS_ERROR.getCode())
                    .message(message)
                    .requestId(uuid).build();
        }

        Integer code = 0;
        String recognitionResult = "";
        OfflineAsrQueryResDTO.OfflineAsrRecognizedData data = null;

        try {
            AsrClient client = asrBaseService.getTencentAsrClient(account);
            SentenceRecognitionRequest asrReq = new SentenceRecognitionRequest();
            asrReq.setEngSerViceType(TencentAsrBaseService.ENGINE_8KZH);
            asrReq.setSourceType(1L);
            asrReq.setVoiceFormat(req.getFormat());
            asrReq.setData(req.getAudioData());


            // 通过client对象调用想要访问的接口，需要传入请求对象
            SentenceRecognitionResponse resp = client.SentenceRecognition(asrReq);
            log.info("Tencent sentence ASR request success, request_id:{} tencent_requestId: {}", uuid, resp.getRequestId());
            // 一句话识别计费
            String productLine = req.getFrom() + SENTENCE_SUFFIX;
            asrCostService.saveOrUpdateUsedCount(Constant.TENCENT_PLATFORM, productLine);

            recognitionResult = resp.getResult();
        } catch (Exception e) {
            log.error("Tencent offline ASR request failed, request_id:{}", uuid, e);
            code = AsrRecognizedError.ASR_RECOGNIZED_ERROR.getCode();
            message = e.getMessage();
        }

        return SentenceRecognitionRes.builder()
                .code(code)
                .message(message)
                .requestId(uuid)
                .result(recognitionResult).build();
    }

}

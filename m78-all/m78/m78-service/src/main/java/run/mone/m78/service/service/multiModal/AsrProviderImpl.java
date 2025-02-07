package run.mone.m78.service.service.multiModal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.asr.v20190614.models.CreateRecTaskRequest;
import com.tencentcloudapi.asr.v20190614.models.CreateRecTaskResponse;
import com.tencentcloudapi.asr.v20190614.models.DescribeTaskStatusRequest;
import com.tencentcloudapi.asr.v20190614.models.DescribeTaskStatusResponse;

import com.xiaomi.data.push.redis.Redis;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import run.mone.m78.common.Constant;
import run.mone.m78.api.AsrProvider;
import run.mone.m78.api.bo.multiModal.audio.*;
import run.mone.m78.api.constant.CommonConstant;
import run.mone.m78.service.asr.tencent.TencentAsrBaseService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@DubboService(timeout = CommonConstant.DEF_DUBBO_TIMEOUT, group = "${dubbo.group}", version = "1.0")
public class AsrProviderImpl implements AsrProvider {

    @Autowired
    private Redis redis;

    @Resource
    private AudioAsrCostService audioAsrCostService;

    @Resource
    private TencentAsrBaseService asrBaseService;

    private static final String PREFIX_REDIS = "asr_offline_";

    private static final String OFFLINE_SUFFIX = "_offline";


    // 声道数, 16k仅支持单声道
    private static final Long SINGLE_CHANNEL_NUM = 1L;
    private static final Long DOUBLE_CHANNEL_NUM = 2L;

    // 识别结果返回格式,增加词粒度的详细识别结果，并输出口语转书面语转写结果，
    // 该结果去除语气词、重复词、精简冗余表达，并修正发言人口误，实现口语转书面语的效果，
    // 适用于线上、线下会议直接总结为书面会议纪要的场景，仅支持8k_zh/16k_zh引擎
    private static final Long RES_TEXT_FORMAT = 5L;

    // 任务失败
    private static final int TASK_FAILED = 3;
    // 任务成功
    private static final int TASK_SUCCESS = 2;
    // 任务进行中
    private static final int TASK_RUNNING = 1;
    // 任务等待
    private static final int TASK_WAITING = 0;

    private static final int OFFILNE_KEY_EXP = 1000 * 60 * 60 * 24;



    @Override
    public OfflineAsrResDTO audioRecognizeOffline(OfflineAsrReqDTO req)  {
        // 生成uuid，方便问题排查
        String uuid = UUID.randomUUID().toString();
        log.info("audioRecognizeOffline req:{},request_id:{}", JSON.toJSONString(req),uuid);
        String message = "";
        String account = asrBaseService.getAccountByOfflineFrom(req.getFrom());
        if (account == null) {
            message = "from is not allow, please check the from param";
        } else if (req.getAudioUrl() == null || req.getIsPhoneScene() == null || req.getChannelNum() == null) {
            message = "required params is missing";
        }
        // 校验失败
        if (!message.isEmpty()) {
            return OfflineAsrResDTO.builder()
                    .code(AsrRecognizedError.ASR_OFFLINE_SUBMIT_ERROR.getCode())
                    .message(message)
                    .requestId(uuid).build();
        }

        try {
            AsrClient client = asrBaseService.getTencentAsrClient(account);
            CreateRecTaskRequest asrReq = new CreateRecTaskRequest();

            Long channelNum = req.getChannelNum().equals(SINGLE_CHANNEL_NUM) ? SINGLE_CHANNEL_NUM : DOUBLE_CHANNEL_NUM;
            if (req.getIsPhoneScene()) {
                // 设置引擎模型类型
                asrReq.setEngineModelType(TencentAsrBaseService.ENGINE_8KZH);
            } else {
                // 设置引擎模型类型
                asrReq.setEngineModelType(TencentAsrBaseService.ENGINE_16KZH);
                // 只支持单声道，进行改写
                channelNum = SINGLE_CHANNEL_NUM;
            }

            // 设置声道数
            asrReq.setChannelNum(channelNum);
            // 单声道，需要设置说话人分离
            if (channelNum.equals(SINGLE_CHANNEL_NUM)) {
                // 设置开启说话人分离
                asrReq.setSpeakerDiarization(1L);
                // 可设置说话人数量
                // 不设置，默认是2人
                Long speakerNumber = req.getSpeakerNumber() != null ? req.getSpeakerNumber() : 2L;
                asrReq.setSpeakerNumber(speakerNumber);
            }

            // 设置识别结果返回格式
            asrReq.setResTextFormat(RES_TEXT_FORMAT);
            // 当前仅支持url方式
            asrReq.setSourceType(0L);
            asrReq.setUrl(req.getAudioUrl());
            if (asrBaseService.getHotWordId() != null) {
                asrReq.setHotwordId(asrBaseService.getHotWordId());
            }

            // 通过client对象调用想要访问的接口，需要传入请求对象
            CreateRecTaskResponse resp = client.CreateRecTask(asrReq);
            log.info("Tencent offline ASR request success, request_id:{} tencent_requestId: {}, taskId: {}", uuid, resp.getRequestId(), resp.getData().getTaskId());

            Long asrTaskId = resp.getData().getTaskId();
            if (asrTaskId > 0) {
                // 记入redis，暂存，24h.
                String offlineKey = getRedisKey(asrTaskId);
                redis.setV2(offlineKey, generateValue(req.getFrom(), account), OFFILNE_KEY_EXP);
                // 返回成功
                return OfflineAsrResDTO.builder()
                        .code(0)
                        .message("")
                        .taskId(asrTaskId)
                        .requestId(uuid).build();
            }
        } catch (Exception e) {
            log.error("Tencent offline ASR request failed, request_id:{}", uuid, e);
            message = e.getMessage();
        }
        return OfflineAsrResDTO.builder()
                .code(AsrRecognizedError.ASR_OFFLINE_SUBMIT_ERROR.getCode())
                .message(message)
                .requestId(uuid).build();
    }

    @Override
    public OfflineAsrQueryResDTO queryAudioRecognizeRes(OfflineAsrQueryReqDTO req) {
        log.info("queryAudioRecognizeRes req:{}", JSON.toJSONString(req));
        if (req.getTaskId() == null || req.getTaskId() < 1L) {
            return OfflineAsrQueryResDTO.builder()
                    .code(AsrRecognizedError.ASR_OFFLINE_TASK_FAILED.getCode())
                    .message("taskId is empty").build();
        }

        Integer code = 0;
        String message = "";
        OfflineAsrQueryResDTO.OfflineAsrRecognizedData data = null;

        try {
            // 获取account，如果获取不到account，会抛异常，返回失败
            String account = getAccountFromTaskId(req.getTaskId());
            AsrClient client = asrBaseService.getTencentAsrClient(account);
            DescribeTaskStatusRequest asrReq = new DescribeTaskStatusRequest();
            asrReq.setTaskId(req.getTaskId());
            // 获取task状态
            DescribeTaskStatusResponse resp = client.DescribeTaskStatus(asrReq);
            Long status = resp.getData().getStatus();

            switch (status.intValue()) {
                case TASK_SUCCESS:
                    data = OfflineAsrQueryResDTO.OfflineAsrRecognizedData.builder()
                            .audioDuration(resp.getData().getAudioDuration())
                            .result(resp.getData().getResult()).build();
                    // 计费
                    asrTaskBillCount(req.getTaskId(), resp.getData().getAudioDuration());
                    break;
                case TASK_FAILED:
                    code = AsrRecognizedError.ASR_OFFLINE_TASK_FAILED.getCode();
                    message = resp.getData().getErrorMsg();
                    log.error("Tencent offline ASR query failed, taskId:{}, res:{}", req.getTaskId(), new Gson().toJson(resp));
                    break;
                case TASK_RUNNING:
                    code = AsrRecognizedError.ASR_OFFLINE_TASK_RUNNING.getCode();
                    message = resp.getData().getStatusStr();
                    break;
                case TASK_WAITING:
                    code = AsrRecognizedError.ASR_OFFLINE_TASK_WAITING.getCode();
                    message = resp.getData().getStatusStr();
                    break;
                default:
                    // 处理其他未预料的状态
                    code = AsrRecognizedError.ASR_OFFLINE_TASK_FAILED.getCode();
                    message= "tencent unknow error";
                    break;
            }

        }catch (Exception e) {
            code =  AsrRecognizedError.ASR_OFFLINE_TASK_FAILED.getCode();
            message = "request tencent error";
            log.error("Tencent offline ASR query failed, taskId:{}", req.getTaskId(), e);
        }

        return OfflineAsrQueryResDTO.builder()
                .code(code)
                .message(message)
                .data(data).build();
    }

    public String getRedisKey(Long taskId) {
        return PREFIX_REDIS + Long.toString(taskId);
    }


    public String generateValue(String from, String account) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", from);
        jsonObject.put("isCount", false);
        jsonObject.put("time", System.currentTimeMillis());
        jsonObject.put("account", account);

        return jsonObject.toString();
    }

    public String getAccountFromTaskId(Long taskId) {
        String redisValue = redis.get(getRedisKey(taskId));
        // taskId对应的
        if (redisValue.isEmpty()) {
            log.error("Tencent offline ASR query failed, taskId have expired or not valid taskId");
            throw new RuntimeException("taskId have expired or not valid taskId");
        }

        JSONObject jsonObject = JSON.parseObject(redisValue);
        if (!jsonObject.containsKey("account") || jsonObject.get("account") == null) {
            log.error("Tencent offline ASR query failed, taskId not have account info");
            throw new RuntimeException("taskId not have account info");
        }

        return jsonObject.get("account").toString();
    }

    public void asrTaskBillCount(Long taskId, Float duration) {
        try {
            String offlineKey = getRedisKey(taskId);
            String redisValue = redis.get(offlineKey);
            if (redisValue.isEmpty()) {
                return;
            }

            JSONObject jsonObject = JSON.parseObject(redisValue);
            if (jsonObject.getBoolean("isCount")) {
                // 已经计费，忽略
                return;
            }

            // from增加_offline，更新db
            String productLine = jsonObject.getString("from") + OFFLINE_SUFFIX;
            Long durationMill = (long) (duration * 1000);
            audioAsrCostService.saveOrUpdateUsedTime(Constant.TENCENT_PLATFORM, productLine, durationMill);

            // 获取第一次写入redis时间
            Long recordTime = jsonObject.getLong("time");
            int newExp = OFFILNE_KEY_EXP - (int) (System.currentTimeMillis() - recordTime);

            // 更新redis
            jsonObject.put("isCount", true);
            // 24h过期时间，剩余的时间作为过期时间。

            redis.setV2(offlineKey, jsonObject.toString(), newExp);
        } catch (Exception e) {
            log.error("Tencent offline ASR billcount failed", e);
        }
    }
}

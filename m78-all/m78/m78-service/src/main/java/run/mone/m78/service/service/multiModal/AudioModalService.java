package run.mone.m78.service.service.multiModal;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.multiModal.audio.AsrPlatformEnum;
import run.mone.m78.api.bo.multiModal.audio.AudioToTextCallProxyRes;
import run.mone.m78.api.bo.multiModal.audio.AudioToTextReq;
import run.mone.m78.api.enums.ImageTypeEnum;
import run.mone.m78.api.enums.MultiModalCmdTypeEnum;
import run.mone.m78.service.dao.entity.M78MultiModalHistoryPo;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.service.base.ProxyAiService;
import run.mone.m78.service.service.fileserver.RemoteFileService;
import run.mone.m78.service.utils.NetUtils;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import static run.mone.m78.api.constant.CommonConstant.TMP_DIR;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;

@Service
@Slf4j

/**
 * AudioModalService类提供了与音频处理相关的服务。
 *
 * 该类包含以下主要功能：
 * 1. 获取所有ASR（自动语音识别）平台的列表。
 * 2. 将音频文件转换为文本，并处理相关的请求和响应。
 *
 * 该类依赖于MultiModalHistoryService和RemoteFileService来处理历史记录和文件操作。
 * 通过Spring的@Service注解标记为服务类，并使用@Slf4j进行日志记录。
 *
 * 配置属性包括回调URL和服务器端口，用于在音频转换过程中进行回调通知。
 */

public class AudioModalService {

    @Resource
    private MultiModalHistoryService modalHistoryService;

    @Autowired
    private RemoteFileService fileService;

    @Value("${multiModal.notifyByTaskId.uri}")
    private String callbackUrl;

    @Value("${server.port}")
    private String httpPort;

    /**
     * 获取所有ASR平台的列表
     *
     * @return 包含所有ASR平台名称的结果列表
     */
    public Result<List<String>> getAllAsrPlatformList() {
        return Result.success(AsrPlatformEnum.getAll());
    }

    /**
     * 将音频文件转换为文本
     *
     * @param audioToTextReq 包含音频文件和ASR平台信息的请求对象
     * @param userName       用户名
     * @return 包含转换结果的Result对象
     */
    public Result<String> audioToText(AudioToTextReq audioToTextReq, String userName) {
        if (!AsrPlatformEnum.have(audioToTextReq.getAsrPlatform())) {
            return Result.fail(STATUS_INTERNAL_ERROR, "asrplatform not support");
        }

        String localIp = NetUtils.getLocalHost();
        String multiModalNotifyByTaskIdUrl = "http://" + localIp + ":" + httpPort + callbackUrl;

        Path filePath = null;
        try {
            filePath = Paths.get(TMP_DIR + "/" + UUIDUtil.generateType1UUID() + audioToTextReq.getAudioFile().getOriginalFilename());
            Files.write(filePath, audioToTextReq.getAudioFile().getBytes());
        } catch (Exception e) {
            log.error("write audio file error", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }

        JsonObject jsonObject = null;
        try {
            jsonObject = ProxyAiService.multiModalAudio(filePath.toString(), multiModalNotifyByTaskIdUrl, audioToTextReq.getAsrPlatform());
            Gson gson = new Gson();
            AudioToTextCallProxyRes audioToTextCallProxyRes = gson.fromJson(jsonObject, AudioToTextCallProxyRes.class);

            if (audioToTextCallProxyRes.getCode() != 0
                    || audioToTextCallProxyRes.getAsrPlatformTaskId().isEmpty()
                    || audioToTextCallProxyRes.getModalTaskId().isEmpty()) {
                return Result.fail(STATUS_INTERNAL_ERROR, audioToTextCallProxyRes.getMsg());
            }

            // todo 替换
            //  upload file
            // 等m78 fds上传，再替换
            String base64Str = Base64.getEncoder().encodeToString(audioToTextReq.getAudioFile().getBytes());
            String fileName = audioToTextReq.getAudioFile().getOriginalFilename();
            int lastIndex = fileName.lastIndexOf(".");
            String extension = "wav";
            if (lastIndex > 0) {
                extension = fileName.substring(lastIndex + 1);
            }
//            String uploadUrl = fileService.uploadImageFileByBase64(ImageTypeEnum.AVATAR, "audio", base64Str, extension);
            String uploadUrl = "";

            AudioToTextReq req = AudioToTextReq.builder()
                    .audioFileUrl(uploadUrl).asrPlatform(audioToTextReq.getAsrPlatform())
                    .audioFileUrl(uploadUrl)
                    .asrTaskId(audioToTextCallProxyRes.getAsrPlatformTaskId()).build();

            M78MultiModalHistoryPo po = M78MultiModalHistoryPo.builder()
                    .aiModel(audioToTextReq.getAsrPlatform())
                    .userName(userName)
                    .type(MultiModalCmdTypeEnum.AUDIO_TO_TEXT.getCode())
                    .setting(req)
                    .taskId(jsonObject.get("modalTaskId").getAsString())
                    .runStatus(0)
                    .ctime(System.currentTimeMillis() / 1000).build();

            log.info("execute asr recognition save rst:{}", modalHistoryService.insert(po, false));
        } catch (Exception e) {
            log.error("request ai-proxy failed, res: {}, error", jsonObject, e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }

        return Result.success("success");
    }


}
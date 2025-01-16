package run.mone.m78.api.bo.multiModal.audio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import run.mone.m78.api.bo.multiModal.image.BaseReq;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class AudioToTextReq implements Serializable {

    // 此处详见z-ai-proxy AsrPlatform 枚举
    // tencent_asr/ali_asr等
    private String asrPlatform;

    private String audioFileUrl;

    private String asrTaskId;

    private MultipartFile audioFile;
}
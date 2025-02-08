package run.mone.m78.service.asr.xiaoai.DTO;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liuchuankang
 * @Type AudioMessageRespDTO.java
 * @Desc
 * @date 2024/8/30 09:53
 */
@Builder
@Data
public class AudioMessageRespDTO implements Serializable {
	private String data;
	private Boolean canSendAudio;
	private Boolean isFinal;
}

package run.mone.m78.service.asr.xiaoai.DTO;

import lombok.Data;

import java.io.Serializable;
@Data
public class XiaoAiFramePayloadDTO implements Serializable {
    private String vendor ;
    private Boolean is_final;
    private String query;
    private Integer gender;
    private String pinyin;
    private Integer audio_duration;
    private Integer packet_id;
    private Boolean vad_end;
    private XiaoAiFrameVadInfo vad_info;
}

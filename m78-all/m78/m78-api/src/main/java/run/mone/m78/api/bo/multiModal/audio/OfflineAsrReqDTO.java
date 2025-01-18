package run.mone.m78.api.bo.multiModal.audio;

import lombok.Data;

import java.io.Serializable;

@Data
public class OfflineAsrReqDTO implements Serializable {

    // 是否是电话场景，true: 是
    private Boolean isPhoneScene;

    // 音频url
    private String audioUrl;
    // 音频文件声道数，取值为1，和 2
    private Long channelNum;

    // 是否角色分离, true:表示要分离，false: 不需要分离，默认true.
    private Boolean speakerDiarization;

    // 说话人数量, 如果不设置，默认使用2
    private Integer speakerNumber;

    // 表示上游业务线，由m78侧分配给业务方
    private String from;

    // 音频的数据格式
    private String format;

    // 音频数据
    private String audioData;
}

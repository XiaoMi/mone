package run.mone.m78.service.asr.xiaoai.DTO;

import lombok.Data;

import java.io.Serializable;
@Data
public class XiaoAiFrameDTO implements Serializable {
    private XiaoAiFrameHeaderDTO header;

    private XiaoAiFramePayloadDTO payload;

}

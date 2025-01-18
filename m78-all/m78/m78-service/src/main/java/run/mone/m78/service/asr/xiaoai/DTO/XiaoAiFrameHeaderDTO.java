package run.mone.m78.service.asr.xiaoai.DTO;

import lombok.Data;

import java.io.Serializable;
@Data
public class XiaoAiFrameHeaderDTO implements Serializable {
    private String namespace;
    private String name;
    private String id;
}

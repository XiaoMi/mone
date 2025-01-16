package run.mone.m78.service.asr.xiaoai.DTO;

import lombok.Data;

import java.io.Serializable;
@Data
public class XiaoAiFrameVadInfo implements Serializable {
    private Integer vadBeginPoint;
    private Integer currentPoint;
    private Integer vadEndPoint;
    private Boolean isOneSentenceResult;
    private String serValue ;
}

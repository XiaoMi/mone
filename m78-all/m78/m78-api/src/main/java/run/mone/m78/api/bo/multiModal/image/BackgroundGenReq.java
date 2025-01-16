package run.mone.m78.api.bo.multiModal.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BackgroundGenReq extends BaseReq implements Serializable {

    private String cmd;

    @Builder.Default
    private double temperature = 0.2;

    private String input;

    private int num;
    private int noiseLevel;
    private float refPromptWeight;
    private String sceneType;
    private String backgroundEdge;
    private String foregroundEdge;

    private String negativeInput;
    private String baseImageUrl;
    private String refImageUrl;
    private String title;
    private String subTitle;

    private String zzToken;

}

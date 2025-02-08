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
public class TextAndImageReq extends BaseReq implements Serializable {

    private String cmd;

    @Builder.Default
    private double temperature = 0.2;

    private String input;

    private int num;

    private String maskImageUrl;
    private String baseImageUrl;
    private String negativeInput;

    private Integer imageHeight;
    private Integer imageWidth;

    private String zzToken;
}

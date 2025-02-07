package run.mone.m78.api.bo.multiModal.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SketchToImageReq extends BaseReq implements Serializable {

    private String cmd;

    @Builder.Default
    private double temperature = 0.2;


    private String input;

    private int num;
    private String size;
    private String style;
    private Boolean sketchExtraction;
    private List<List<Integer>> sketchColor;
    private String baseImageUrl;

    private String zzToken;

}

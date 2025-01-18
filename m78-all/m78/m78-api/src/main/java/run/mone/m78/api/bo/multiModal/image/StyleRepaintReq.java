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
public class StyleRepaintReq extends BaseReq implements Serializable {

    private String cmd;

    @Builder.Default
    private double temperature = 0.2;


    private String input;

    private String baseImageUrl;
    private String refImageUrl;
    private int num;
    private Integer styleIndex;

    private String zzToken;
}

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
public class ImageUnderstandingReq extends BaseReq implements Serializable {

    private String cmd;

    @Builder.Default
    private double temperature = 0.2;

    private List<String> imageUrls;

    private String input;

}

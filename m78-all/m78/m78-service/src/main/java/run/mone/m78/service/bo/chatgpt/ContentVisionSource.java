package run.mone.m78.service.bo.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentVisionSource implements Serializable {

    @Builder.Default
    private String type="base64";

    private String media_type;

    private String data;

}

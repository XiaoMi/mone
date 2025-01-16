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
public class ContentVision implements Serializable {

    private String type;

    private String text;

    private ContentVisionSource source;

}

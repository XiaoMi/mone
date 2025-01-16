package run.mone.m78.gateway.bo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PartBO {

    private String title;
    private String answer;
    private String sourceType;
    private String from;
    private String contentLink;
}

package run.mone.mcp.idea.composer.handler.biz;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComposerImagePo {
    private String imageBase64;
    private String imageType;
}

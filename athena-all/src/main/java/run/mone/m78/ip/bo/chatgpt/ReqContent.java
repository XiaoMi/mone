package run.mone.m78.ip.bo.chatgpt;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/11/24 13:53
 */
@Data
@Builder
public class ReqContent {

    @Builder.Default
    private String type = "text";

    private String text;

    private ImageUrl image_url;
}

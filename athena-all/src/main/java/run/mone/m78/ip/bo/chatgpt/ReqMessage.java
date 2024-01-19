package run.mone.m78.ip.bo.chatgpt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/11/24 13:52
 */
@Data
@Builder
public class ReqMessage {

    @Builder.Default
    private String role = "user";

    private List<ReqContent> content;
}

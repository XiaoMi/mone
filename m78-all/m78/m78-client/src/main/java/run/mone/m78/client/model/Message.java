package run.mone.m78.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2024/9/10 12:59
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String role;

    private String content;

}

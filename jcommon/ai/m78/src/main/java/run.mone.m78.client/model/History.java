package run.mone.m78.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/9/10 12:58
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {

    private List<Message> messages;


}

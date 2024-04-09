package run.mone.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/3/26 16:11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatCompletion {

    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

}

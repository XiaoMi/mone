package run.mone.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/3/26 16:00
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestBodyContent {

    private String model;
    private List<Message> messages;
    private double temperature;

}

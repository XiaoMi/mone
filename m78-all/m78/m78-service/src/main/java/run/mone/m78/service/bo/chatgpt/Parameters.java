package run.mone.m78.service.bo.chatgpt;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/7/10 13:47
 */
@Data
@Builder
public class Parameters implements Serializable {

    private String type;

    private Map<String,Value> properties;

    private List<String> required;

}

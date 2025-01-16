package run.mone.m78.service.bo.chatgpt;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/7/10 13:48
 */
@Data
@Builder
public class Value implements Serializable {

    private String type;

    private String description;
}

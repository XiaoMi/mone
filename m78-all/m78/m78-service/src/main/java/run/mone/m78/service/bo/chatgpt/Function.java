package run.mone.m78.service.bo.chatgpt;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/7/10 13:45
 */
@Builder
@Data
public class Function implements Serializable {


    private String name;

    private String description;

    private Parameters parameters;


}

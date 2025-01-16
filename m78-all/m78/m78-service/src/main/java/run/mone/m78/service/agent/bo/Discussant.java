package run.mone.m78.service.agent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/9/11 15:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Discussant implements Serializable {

    private String name;
    private String role;
    private String model;
    private String initialView;
    private String botId;
}

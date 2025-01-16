package run.mone.m78.service.agent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2024/2/23 14:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agent {

    private String name;

    private String role;

    private String address;

    private long time;

}

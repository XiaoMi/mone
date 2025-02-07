package run.mone.m78.service.agent.state;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/12/23 14:59
 */
@Data
@Builder
public class Snapshot implements Serializable {

    //用json序列化过来
    private String stateContext;

}

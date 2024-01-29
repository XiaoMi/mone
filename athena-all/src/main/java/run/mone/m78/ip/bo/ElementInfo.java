package run.mone.m78.ip.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/29 13:05
 */
@Data
@Builder
public class ElementInfo implements Serializable {

    private String name;

}

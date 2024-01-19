package run.mone.ultraman.listener.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2023/7/14 14:54
 */
@Data
public class AthenaMessage implements Serializable {

    private String type;

    private String name;

    private Consumer consumer;


}

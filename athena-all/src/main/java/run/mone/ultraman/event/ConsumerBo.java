package run.mone.ultraman.event;

import lombok.Data;

import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2023/6/24 21:49
 */
@Data
public class ConsumerBo {

    private String type;

    private Consumer consumer;

}

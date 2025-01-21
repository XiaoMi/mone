package run.mone.local.docean.context;

import lombok.Builder;
import lombok.Data;
import java.util.logging.Logger;
import run.mone.local.docean.fsm.MemoryData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author goodjava@qq.com
 * @date 2024/3/27 15:07
 */
@Data
@Builder
public class CodeContext {

    private Logger logger;

    //code执行返回的结果
    @Builder.Default
    private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

    //用户调用过来的message
    private LinkedBlockingQueue<String> messageList;

    //记忆体
    private List<MemoryData> memory;

    //用于跨节点传递数据
    private Map<String, String> meta;

}

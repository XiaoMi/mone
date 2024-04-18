package run.mone.local.docean.fsm.flow;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 2024/2/29 11:54
 */
@Slf4j
public class PreconditionFlow extends BotFlow {


    /**
     * 执行流程操作的方法。
     * 根据传入的请求和上下文，通过一系列条件判断来决定执行的逻辑。
     *
     * @param req     流程请求对象
     * @param context 流程上下文
     * @return 返回操作结果，包含布尔值表示操作是否成功
     */
    @Override
    public FlowRes<Boolean> execute(FlowReq req, FlowContext context) {
        return FlowRes.success("");
    }

    @Override
    public String getFlowName() {
        return "precondition";
    }

}

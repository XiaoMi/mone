package run.mone.local.docean.util.template.function;

import org.apache.dubbo.common.utils.CollectionUtils;
import org.beetl.core.Context;
import org.beetl.core.Function;
import run.mone.local.docean.fsm.MemoryData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/4/24 22:33
 */
public class MemoryFunction implements Function {

    public static final String name = "memory";

    private List<MemoryData> memory;

    public MemoryFunction(List<MemoryData> memory) {
        this.memory = memory;
    }

    @Override
    public Object call(Object[] paras, Context ctx) {
        if (CollectionUtils.isEmpty(this.memory)) {
            return "";
        }
        return this.memory.stream().map(it -> it.getRole() + ":" + it.getMessage()).collect(Collectors.joining("\n"));
    }
}

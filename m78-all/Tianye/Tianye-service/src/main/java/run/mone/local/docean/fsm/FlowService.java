package run.mone.local.docean.fsm;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.ClassFinder;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/3/5 10:57
 */
@Service
@Data
public class FlowService {

    public static Map<String,Class> flowMap = Maps.newHashMap();

    public void init() {
        ClassFinder classFinder = new ClassFinder();
        Set<String> set = classFinder.findClassSet("run.mone.local.docean.fsm.flow");
        List<Class> list = set.stream().map(it -> ReflectUtils.classForName(it)).collect(Collectors.toList());
        list.stream().filter(it-> BotFlow.class.isAssignableFrom(it)).forEach(it->{
            BotFlow botFlow = (BotFlow) ReflectUtils.getInstance(it);
            String name = botFlow.getFlowName();
            Class clazz = botFlow.getClass();
            flowMap.put(name,clazz);
        });
    }

}

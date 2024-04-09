package run.mone.local.docean.test;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import run.mone.local.docean.fsm.bo.Condition;
import run.mone.local.docean.fsm.bo.Operator;
import run.mone.local.docean.protobuf.Bo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 2024/3/4 10:30
 */
public class ConditionTest {


    @Test
    public void test1() {
        Condition condition = new Condition("a", Operator.EQUALS, "a");
        Condition condition2 = new Condition("a", Operator.EQUALS, "b");
        Condition condition3 = new Condition("a", Operator.EQUALS, "b");

        List<Condition> conditions = Lists.newArrayList(condition, condition2, condition3);

        List<Boolean> isOrRelationshipList = Lists.newArrayList(false, false);

        AtomicInteger index = new AtomicInteger(0);
        Condition combinedCondition = conditions.stream()
                .reduce((previousCondition, currentCondition) -> {
                    boolean isOrRelationship = isOrRelationshipList.get(index.getAndIncrement());
                    return isOrRelationship ? previousCondition.or(currentCondition) : previousCondition.and(currentCondition);
                }).orElse(null); // orElse(null) is used to handle the case where the conditions list might be empty

        boolean result = false;
        if (combinedCondition != null) {
            result = combinedCondition.evaluate();
        }
        System.out.println(result);


    }
}

package com.xiaomi.youpin.codecheck.code.impl.set;

import com.sun.source.tree.VariableTree;
import com.xiaomi.youpin.codecheck.code.impl.VariableCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

public class LongVariableAvoidNoneL extends VariableCheck {
    private static final String DESC = "When assigning an initial value to a variable of type long, use a capital L at the end of the value";
    private static final String CHINA_DESC = "long类型变量赋初值时，数值后使用大写L结尾，不能不写";

    @Override
    public Pair<Integer, CheckResult> _check(VariableTree variableTree) {
        String name = variableTree.getName().toString();
        String type = variableTree.getType().toString();
        if (type.equals("long")) {
            if (!variableTree.getInitializer().toString().endsWith("L")) {
                return Pair.of(CheckResult.WARN, CheckResult.getWarnRes("name: " + name, DESC, CHINA_DESC));
            }
        }
        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("name: " + name, "", ""));
    }
}

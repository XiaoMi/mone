package com.xiaomi.youpin.codecheck.code.impl.naming;

import com.sun.source.tree.VariableTree;
import com.xiaomi.youpin.codecheck.code.impl.VariableCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

/**
 * [Mandatory] All names should not start or end with an underline or a dollar sign.
 * 代码中的命名均不能以下划线或美元符号开始，也不能以下划线或美元符号结束
 */
public class AvoidStartWithDollarAndUnderLineNamingRule extends VariableCheck {
    private static final String DOLLAR = "$";
    private static final String UNDERSCORE = "_";
    private static final String DESC = "All variables should not start or end with an underline or a dollar sign";
    private static final String CHINA_DESC = "代码中的命名均不能以下划线或美元符号开始，也不能以下划线或美元符号结束";


    @Override
    public Pair<Integer, CheckResult> _check(VariableTree variableTree) {
        String name = variableTree.getName().toString();
        if (name.startsWith(DOLLAR)
                || name.endsWith(DOLLAR)
                || name.startsWith(UNDERSCORE)
                || name.endsWith(UNDERSCORE)) {
            return Pair.of(CheckResult.WARN, CheckResult.getWarnRes("name: " + name, DESC, CHINA_DESC));
        }

        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("name: " + name,"", ""));
    }
}

package com.xiaomi.youpin.codecheck.code.impl.flowcontrol;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.StatementTree;
import com.sun.tools.javac.tree.JCTree;
import com.xiaomi.youpin.codecheck.code.impl.MethodCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;

/**
 * In a switch block, a default statement must be present, even if it is empty.
 */
public class SwitchStatementRule extends MethodCheck {
    private static final String DESC = "In a switch block, a default statement must be present, even if it is empty";
    private static final String CHINA_DESC = "在一个switch块内，都必须包含一个default语句并且放在最后，即使它什么代码也没有";


    @Override
    public Pair<Integer, CheckResult> _check(MethodTree methodTree) {
        BlockTree body = methodTree.getBody();
        if (body == null) {
            return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("", "", ""));
        }

        List<? extends StatementTree> stats = body.getStatements();
        Optional<? extends JCTree.JCSwitch> switchStatOptional = (Optional<? extends JCTree.JCSwitch>) stats.stream().filter(it -> it instanceof JCTree.JCSwitch).findFirst();
        if (switchStatOptional.isPresent()) {
            JCTree.JCSwitch jcSwitch = switchStatOptional.get();

            //在一个switch块内，都必须包含一个default语句并且放在最后，即使它什么代码也没有
            List<JCTree.JCCase> caseList= jcSwitch.getCases();
            if (caseList.stream().noneMatch(it -> it.getExpression() == null)) {
                return Pair.of(CheckResult.WARN, CheckResult.getWarnRes("switch" + jcSwitch.getExpression(), DESC, CHINA_DESC));
            }
        }

        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("", "", ""));
    }

}

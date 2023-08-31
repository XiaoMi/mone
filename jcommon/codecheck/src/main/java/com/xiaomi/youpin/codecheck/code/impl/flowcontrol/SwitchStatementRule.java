/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

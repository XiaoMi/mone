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

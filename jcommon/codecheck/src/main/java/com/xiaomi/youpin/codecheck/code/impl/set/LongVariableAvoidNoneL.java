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

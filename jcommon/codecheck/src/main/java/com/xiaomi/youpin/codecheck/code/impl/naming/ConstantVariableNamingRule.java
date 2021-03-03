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

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.regex.Pattern;

public class ConstantVariableNamingRule extends VariableCheck {
    private static final String DESC = "Constant names are all capitalized, and words are separated by underscores";
    private static final String CHINA_DESC = "常量命名全部大写，单词间用下划线隔开";
    private static final Pattern PATTERN = Pattern.compile("([A-Z]+[_]?)+");

    @Override
    public Pair<Integer, CheckResult> _check(VariableTree variableTree) {
        String name = variableTree.getName().toString();
        Set<Modifier> set = variableTree.getModifiers().getFlags();
        if (set.contains(Modifier.FINAL)) {
            if(!PATTERN.matcher(name).matches()) {
                return Pair.of(CheckResult.WARN, CheckResult.getWarnRes("name: " + name, DESC, CHINA_DESC));
            }
        }

        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("name: " + name,"", ""));
    }
}

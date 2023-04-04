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

public class VariableNamingShouldBeLowerCamelRule extends VariableCheck {
    private static final String DESC = "Variable names should be named using the lowerCamelCase ";
    private static final String CHINA_DESC = "Variable names should be named using the lowerCamelCase";
    private static final Pattern PATTERN = Pattern.compile("^([a-z][a-z0-9]?)+([A-Z]([a-z0-9])+)?$");

    @Override
    public Pair<Integer, CheckResult> _check(VariableTree variableTree) {
        String variableName = variableTree.getName().toString();
        Set<Modifier> set = variableTree.getModifiers().getFlags();
        if (variableName == null || variableName.equals("")) {
            return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("variable name is null or empty", "", ""));
        }
        // 常量除外
        if (!set.contains(Modifier.FINAL) && !PATTERN.matcher(variableName).matches()) {
            return Pair.of(CheckResult.WARN, CheckResult.getWarnRes("variable name: " + variableName, DESC, CHINA_DESC));
        }
        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("name: " + variableName,"", ""));
    }
}

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

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ModifiersTree;
import com.xiaomi.youpin.codecheck.code.impl.ClassCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

import javax.lang.model.element.Modifier;
import java.util.HashSet;
import java.util.Set;

import static javax.lang.model.element.Modifier.ABSTRACT;

public class AbstractClassNamingRule extends ClassCheck {
    private static final String DESC = "Abstract class naming should start with Abstract or Base";
    private static final String CHINA_DESC = "抽象类命名使用Abstract或Base开头";

    @Override
    public Pair<Integer, CheckResult> _check(ClassTree classTree) {
        String className = classTree.getSimpleName().toString();
        ModifiersTree classModify = classTree.getModifiers();
        Set<Modifier> set = classModify.getFlags();

        if (className == null || className.equals("")) {
            return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("class name is null or empty", "", ""));

        }
        if (set.contains(ABSTRACT)) {
            if(!className.startsWith("Abstract")&&!className.startsWith("Base"))
                return Pair.of(CheckResult.WARN, CheckResult.getWarnRes("class name: " + className, DESC, CHINA_DESC));
        }
        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("class name: " + className, "", ""));
    }
}

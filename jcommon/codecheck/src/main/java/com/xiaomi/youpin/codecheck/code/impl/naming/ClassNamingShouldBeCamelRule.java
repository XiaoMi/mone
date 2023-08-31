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
import com.xiaomi.youpin.codecheck.code.impl.ClassCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.regex.Pattern;

/**
 * Class names should be nouns in UpperCamelCase except domain models: DO, BO, DTO, VO, etc.
 * 类名使用UpperCamelCase风格，但以下情形例外:DO/BO/DTO/VO/AO/PO/UID等
 */
public class ClassNamingShouldBeCamelRule extends ClassCheck {
    private static final Pattern PATTERN
            = Pattern.compile("^I?([A-Z][a-z0-9]+)+(([A-Z])|(DO|DTO|VO|DAO|BO|DAOImpl|YunOS|AO|PO))?$");
    private static final String DESC = "Class names should be nouns in UpperCamelCase except domain models: DO, BO, DTO, VO, etc.";
    private static final String CHINA_DESC = "类名使用UpperCamelCase风格，但以下情形例外:DO/BO/DTO/VO/AO/PO/UID等";

    @Override
    public Pair<Integer, CheckResult> _check(ClassTree classTree) {
        String className = classTree.getSimpleName().toString();
        if (className == null || className.equals("")) {
            return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("class name is null or empty", "", ""));

        }
        if (!PATTERN.matcher(className).matches()) {
            return Pair.of(CheckResult.WARN, CheckResult.getWarnRes("class name: " + className, DESC, CHINA_DESC));
        }

        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("class name: " + className, "", ""));
    }

}

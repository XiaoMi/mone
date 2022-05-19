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

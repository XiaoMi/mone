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
    private static final String CHINA_DESC = "变量名应使用lowerCamelCase风格";
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

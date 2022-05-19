package com.xiaomi.youpin.codecheck.code.impl.naming;

import com.sun.source.tree.VariableTree;
import com.xiaomi.youpin.codecheck.code.impl.VariableCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author goodjava@qq.com
 */
public class ConstantVariableNamingRule extends VariableCheck {
    private static final String DESC = "Constant names are all capitalized, and words are separated by underscores";
    private static final String CHINA_DESC = "常量命名全部大写，单词间用下划线隔开";
    private static final Pattern PATTERN = Pattern.compile("([A-Z|_])+");

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

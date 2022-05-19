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

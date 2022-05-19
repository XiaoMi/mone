package com.xiaomi.youpin.codecheck.code.impl;

import com.sun.source.tree.ClassTree;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

public abstract class ClassCheck {

    public Pair<Integer, CheckResult> check(ClassTree classTree) {
        try {
            return _check(classTree);
        } catch (Throwable throwable) {
            return Pair.of(CheckResult.INFO, null);
        }
    }

    public abstract Pair<Integer, CheckResult> _check(ClassTree classTree);
}

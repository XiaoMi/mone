package com.xiaomi.youpin.codecheck.code.impl;

import com.sun.source.tree.VariableTree;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

public abstract class VariableCheck {

    protected static final Pair<Integer, CheckResult> success = Pair.of(CheckResult.INFO, CheckResult.getInfoRes("", "", ""));


    public Pair<Integer, CheckResult> check(VariableTree variableTree) {
        try {
            return _check(variableTree);
        } catch (Throwable throwable) {
            return Pair.of(CheckResult.INFO, null);
        }
    }

    public abstract Pair<Integer, CheckResult> _check(VariableTree variableTree);
}

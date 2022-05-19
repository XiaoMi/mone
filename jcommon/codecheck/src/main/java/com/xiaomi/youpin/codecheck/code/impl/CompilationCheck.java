package com.xiaomi.youpin.codecheck.code.impl;

import com.sun.source.tree.CompilationUnitTree;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

public abstract class CompilationCheck {
    public Pair<Integer, CheckResult> check(CompilationUnitTree compilationUnitTree) {
        try {
            return _check(compilationUnitTree);
        } catch (Throwable throwable) {
            return Pair.of(CheckResult.INFO, null);
        }
    }

    public abstract Pair<Integer, CheckResult> _check(CompilationUnitTree compilationUnitTree);
}

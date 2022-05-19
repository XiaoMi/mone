package com.xiaomi.youpin.codecheck.code.impl;

import com.sun.source.tree.MethodTree;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author goodjava@qq.com
 */
public abstract class MethodCheck {

    protected static final Pair<Integer, CheckResult> success = Pair.of(CheckResult.INFO, CheckResult.getInfoRes("", "", ""));


    public Pair<Integer, CheckResult> check(MethodTree methodTree) {
        try {
            return _check(methodTree);
        } catch (Throwable throwable) {
            return Pair.of(CheckResult.INFO, null);
        }
    }

    public abstract Pair<Integer, CheckResult> _check(MethodTree methodTree);
}

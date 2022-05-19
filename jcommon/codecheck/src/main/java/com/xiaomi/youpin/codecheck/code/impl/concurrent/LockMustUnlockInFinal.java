package com.xiaomi.youpin.codecheck.code.impl.concurrent;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.MethodTree;
import com.xiaomi.youpin.codecheck.code.impl.MethodCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

public class LockMustUnlockInFinal extends MethodCheck {

    @Override
    public Pair<Integer, CheckResult> _check(MethodTree methodTree) {
        BlockTree body = methodTree.getBody();
        if (body == null) {
            return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("", "", ""));
        }



        return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("", "", ""));
    }
}

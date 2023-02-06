package com.xiaomi.youpin.infra.rpc.errors;

import lombok.Getter;
import lombok.var;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by daxiong on 2018/8/21.
 */
public class ErrorScope {
    @Getter
    private int scopeId;

    private static Set<Integer> scopes = new TreeSet<Integer>();

    private ErrorScope(int scopeId) {
        this.scopeId = scopeId;
    }

    public static ErrorScope createOnce(int scopeId) {
        if (scopeId < 0 || scopeId >= 1000) {
            throw new IllegalArgumentException("Bad scope range:" + scopeId);
        }
        if (scopes.contains(scopeId)) {
            throw new IllegalArgumentException("Duplicate scope id:" + scopeId);
        }
        var scope = new ErrorScope(scopeId);
        scopes.add(scopeId);
        return scope;
    }
}

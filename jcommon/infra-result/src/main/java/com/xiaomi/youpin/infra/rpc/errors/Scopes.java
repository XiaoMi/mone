package com.xiaomi.youpin.infra.rpc.errors;

import com.xiaomi.youpin.infra.rpc.errors.ErrorScope;

/**
 * Created by daxiong on 2018/8/21.
 */
public final class Scopes {
    private Scopes() {
    }

    // mone
    public static ErrorScope SCOPE_MONE = ErrorScope.createOnce(40); //mone

}

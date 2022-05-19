package com.xiaomi.data.push.rpc.common;

import com.xiaomi.data.push.rpc.netty.ResponseFuture;

/**
 * @author goodjava@qq.com
 */
public interface InvokeCallback {

    void operationComplete(final ResponseFuture responseFuture);

}

package com.xiaomi.data.push.rpc.exception;

/**
 * @author goodjava@qq.com
 */
public class RemotingConnectException extends RemotingException {

    private static final long serialVersionUID = -5565366231695911316L;


    public RemotingConnectException(String addr) {
        this(addr, null);
    }


    public RemotingConnectException(String addr, Throwable cause) {
        super("connect to <" + addr + "> failed", cause);
    }

}

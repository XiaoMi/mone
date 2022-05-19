package com.xiaomi.data.push.rpc.exception;

public class RemotingTooMuchRequestException extends RemotingException {
    private static final long serialVersionUID = 4326919581254519654L;


    public RemotingTooMuchRequestException(String message) {
        super(message);
    }
}

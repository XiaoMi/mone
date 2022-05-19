package com.xiaomi.data.push.rpc.protocol;


import com.xiaomi.data.push.rpc.exception.RemotingCommandException;

/**
 * @author goodjava@qq.com
 */
public interface CommandCustomHeader {

    void checkFields() throws RemotingCommandException;

}

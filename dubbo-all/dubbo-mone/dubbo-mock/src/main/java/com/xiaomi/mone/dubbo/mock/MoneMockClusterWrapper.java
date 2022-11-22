package com.xiaomi.mone.dubbo.mock;

import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Cluster;
import org.apache.dubbo.rpc.cluster.Directory;

public class MoneMockClusterWrapper implements Cluster {

    private Cluster cluster;

    public MoneMockClusterWrapper(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        //防御性前置判断
        if(MockConfig.INSTANCE.isMockEnable()) {
            return new MoneMockInvoker<>(directory, this.cluster.join(directory));
        }

        return this.cluster.join(directory);
    }

}

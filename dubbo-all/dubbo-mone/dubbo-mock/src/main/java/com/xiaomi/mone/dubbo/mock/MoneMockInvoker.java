package com.xiaomi.mone.dubbo.mock;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Directory;

public class MoneMockInvoker<T> implements Invoker<T> {

    private final Directory<T> directory;

    private final Invoker<T> invoker;

    public MoneMockInvoker(Directory<T> directory, Invoker<T> invoker) {
        this.directory = directory;
        this.invoker = invoker;
    }

    @Override
    public Class<T> getInterface() {
        return directory.getInterface();
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        return MoneMockHandler.invoke(invoker, invocation, invoker.getInterface().getCanonicalName(), invocation.getMethodName());
    }

    @Override
    public URL getUrl() {
        return directory.getUrl();
    }

    @Override
    public boolean isAvailable() {
        //这边返回true 让check=true的情况也通过
        return true;
    }

    @Override
    public void destroy() {
        this.invoker.destroy();
    }
}

package com.xiaomi.youpin.tesla.rcurve.proxy.control;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/14 21:47
 */
public interface IControl {

    Object call(ControlCallable call,Invoker invoker);

}

package com.xiaomi.mone.buddy.agent;

import net.bytebuddy.asm.Advice;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/1 11:30
 */
public class ExecuteAdvic {


//    @Advice.OnMethodEnter
//    public static void onMethodEnter(@Advice.This Object obj, @Advice.Origin Method method, @Advice.Argument(value = 0, readOnly = false) Runnable task, @Advice.Local("context") AgentContext context) {
//        System.out.println("^^");
//        task = new MyRunnable(task);
//        context = new AgentContext();
//        context.setClazz(obj.getClass().getName());
//        context.setMethodName(method.getName());
//        context.setBeginTime(System.currentTimeMillis());
//    }

    @Advice.OnMethodEnter
    public static void onMethodEnter() {
        System.out.println("^^");
    }

    @Advice.OnMethodExit
    public static void onMethodExit(@Advice.Return Object res, @Advice.Local("context") AgentContext context) {
        context.setEndTime(System.currentTimeMillis());
        context.setRes(res);
        System.out.println("context:" + context);
    }



}

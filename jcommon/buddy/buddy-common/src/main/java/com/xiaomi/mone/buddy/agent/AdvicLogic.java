package com.xiaomi.mone.buddy.agent;

import com.xiaomi.mone.buddy.agent.common.CallableWrapper;
import com.xiaomi.mone.buddy.agent.common.RunnableWrapper;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/26 14:47
 */
public class AdvicLogic {

//    @RuntimeType
//    @Advice.OnMethodEnter
//    public static void onMethodEnter(@Advice.This Object obj, @Advice.Origin Method method, @Advice.AllArguments Object[] args, @Advice.Local("context") AgentContext context) {
//        try {
//            System.out.println("advic");
////            System.out.println("advic enter:" + Arrays.toString(args));
////            context = new AgentContext();
////            context.setClazz(obj.getClass().getName());
////            context.setMethodName(method.getName());
////            context.setBeginTime(System.currentTimeMillis());
////            context.setParams(args);
//        } catch (Throwable ex) {
//            ex.printStackTrace();
//        }
//    }

    //    @Advice.OnMethodEnter
//    public static void onMethodEnter(@Advice.AllArguments(readOnly = false,typing = Assigner.Typing.DYNAMIC) Object[] args) {
    public static void onMethodEnter(@Advice.Origin Method method, @Advice.AllArguments Object[] args) {
        try {
            System.out.println("advic:" + Arrays.toString(args) + ",method:" + method.getName());
//            args[0] = "huhu";
//            List<String> list = (List<String>) args[1];
//            args[2] = new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("123");
//                }
//            };
//            list.add("zzy");
//            System.out.println("advic enter:" + Arrays.toString(args));
//            context = new AgentContext();
//            context.setClazz(obj.getClass().getName());
//            context.setMethodName(method.getName());
//            context.setBeginTime(System.currentTimeMillis());
//            context.setParams(args);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    //    @Advice.OnMethodEnter
    public static void onMethodEnter2(@Advice.Argument(value = 0, readOnly = false) String str) {
        System.out.println("method enter str:" + str);
        str = "new";
    }

    @Advice.OnMethodEnter
    public static void onMethodEnter3(@Advice.Argument(value = 0, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object obj) {
        System.out.println("method enter");
        if (obj instanceof Runnable) {
            final Runnable r = (Runnable) obj;
            obj = new RunnableWrapper(r);
        }

        if (obj instanceof Callable) {
            final Callable c = (Callable) obj;
            obj = new CallableWrapper<>(c);
        }
    }


//    @RuntimeType
//    @Advice.OnMethodExit
//    public static void onMethodExit(@Advice.Return Object res, @Advice.Local("context") AgentContext context) {
////        context.setEndTime(System.currentTimeMillis());
////        context.setRes(res);
////        System.out.println("context:" + context);
//        System.out.println("end");
//    }


}

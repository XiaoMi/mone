package com.xiaomi.mone.buddy.agent;

import com.xiaomi.mone.buddy.agent.interceptor.JedisInterceptor;
import com.xiaomi.mone.buddy.agent.interceptor.NutzInterceptor;
import com.xiaomi.mone.buddy.agent.interceptor.OkHttpInterceptor;
import com.xiaomi.mone.buddy.agent.interceptor.SecurityInterceptor;
import com.xiaomi.mone.buddy.agent.interceptor.TimeInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;


/**
 * @Author goodjava@qq.com
 * @Date 2021/7/26 09:48
 */
public class BuddyAgent {

    public static AgentBuilder.Listener listener = new DefaultListener();

    public static void premain(String arg, Instrumentation inst) {
        System.out.println("agent arg:" + arg);
//        service();
        String packageStr = arg;
//        AgentBuilder.Transformer securityTransformer = (builder, typeDescription, classLoader, module) -> builder
//                .method(ElementMatchers.isAnnotatedWith(Secured.class))
//                .intercept(MethodDelegation.to(SecurityInterceptor.class));
//
//        AgentBuilder.Transformer timeTransformer = (builder, typeDescription, classLoader, module) -> builder
//                .method(ElementMatchers.any())
//                .intercept(MethodDelegation.to(TimeInterceptor.class));
//
//        AgentBuilder.Transformer codeTransformer = (builder, typeDescription, classLoader, module) -> builder
//                .method(ElementMatchers.named("test"))
                //构造方法
//                .constructor(ElementMatchers.any())
//                .intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(CodeInterceptor.class)));
                //构造方法
//                .intercept(
//                        MethodDelegation.withDefaultConfiguration()
//                                .withBinders(Morph.Binder.install(IDemoService.class))
//                                .to(new CodeInterceptor()));

//        .intercept(MethodDelegation.to(CodeInterceptor.class));

//        new AgentBuilder.Default()
//                .type(ElementMatchers.nameStartsWith("com.xiaomi.mone.demo.service.DemoService"))
//                .transform(codeTransformer)
//                .with(listener)
//                .installOn(inst);
//

        //jedis
        AgentBuilder.Transformer jedisTransformer = (builder, typeDescription, classLoader, module) -> builder
                .method(ElementMatchers.isPublic().and(ElementMatchers.named("get").or(ElementMatchers.named("set"))))
                .intercept(MethodDelegation.to(JedisInterceptor.class));

//        new AgentBuilder.Default()
//                .type(ElementMatchers.nameStartsWith("redis.clients.jedis.Jedis"))
//                .transform(jedisTransformer)
//                .with(listener)
//                .installOn(inst);
        //jedis


        //nutz dao
        AgentBuilder.Transformer nutzTransformer = (builder, typeDescription, classLoader, module) -> builder
                .method(ElementMatchers.isPublic().and(ElementMatchers.named("exec")))
                .intercept(MethodDelegation.to(NutzInterceptor.class));

//        new AgentBuilder.Default()
//                .type(ElementMatchers.nameStartsWith("org.nutz.dao.impl.sql.run.NutDaoExecutor"))
//                .transform(nutzTransformer)
//                .with(listener)
//                .installOn(inst);
        //nutz dao


        //okhttp
        AgentBuilder.Transformer okhttpTransformer = (builder, typeDescription, classLoader, module) -> builder
                .method(ElementMatchers.isPublic().and(ElementMatchers.named("execute")))
                .intercept(MethodDelegation.to(OkHttpInterceptor.class));

//        new AgentBuilder.Default()
//                .type(ElementMatchers.nameStartsWith("okhttp3.internal.connection.RealCall"))
//                .transform(okhttpTransformer)
//                .with(listener)
//                .installOn(inst);
        //okhttp


        //runnable
//        AgentBuilder.Transformer runnableTransformer = (builder, typeDescription, classLoader, module) -> builder.visit(Advice.to(ExecuteAdvic.class).on(ElementMatchers.isPublic().and(ElementMatchers.isConstructor()).and(ElementMatchers.takesArguments(1)).and(ElementMatchers.takesArgument(0,Runnable.class))));

//        AgentBuilder.Transformer runnableTransformer = (builder, typeDescription, classLoader, module) -> builder.visit(Advice.to(ExecuteAdvic.class).on(ElementMatchers.isPublic()));
//
//
//        new AgentBuilder.Default()
//                .type(ElementMatchers.nameStartsWith("com.xiaomi"))
//                .transform(runnableTransformer)
//                .with(listener)
//                .installOn(inst);
        //runnable


//        new AgentBuilder.Default()
//                .type(ElementMatchers.nameStartsWith(packageStr))
//                .transform(securityTransformer)
//                .with(listener)
//                .installOn(inst);
//
//        new AgentBuilder.Default()
//                .type(ElementMatchers.nameStartsWith(packageStr))
//                .transform(timeTransformer)
//                .with(listener)
//                .installOn(inst);


        //advice
//        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, module) -> builder.visit(Advice.to(AdvicLogic.class).on(ElementMatchers.isAnnotatedWith(Secured.class)));

        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, module) -> builder.visit(Advice.to(AdvicLogic.class).on(ElementMatchers.named("submit")));

        new AgentBuilder.Default()
                .type(ElementMatchers.nameStartsWith("com.xiaomi.mone.demo.service.DemoService"))
                .transform(transformer)
                .with(listener)
                .installOn(inst);
        //advice


    }

    private static void service() {
//        new Thread(()->{
//            while (true) {
//                System.out.println("agent run");
//                try {
//                    TimeUnit.SECONDS.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }


}

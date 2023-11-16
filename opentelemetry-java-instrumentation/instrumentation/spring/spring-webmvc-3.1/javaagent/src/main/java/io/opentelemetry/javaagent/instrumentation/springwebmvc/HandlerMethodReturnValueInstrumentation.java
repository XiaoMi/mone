package io.opentelemetry.javaagent.instrumentation.springwebmvc;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.lang.reflect.Field;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static io.opentelemetry.javaagent.extension.matcher.ClassLoaderMatcher.hasClassesNamed;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;

@SuppressWarnings({"SystemOut","CatchAndPrintStackTrace"})
public class HandlerMethodReturnValueInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("org.springframework.web.method.support.HandlerMethodReturnValueHandler");
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return implementsInterface(named("org.springframework.web.method.support.HandlerMethodReturnValueHandler"));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(isPublic())
                        .and(named("handleReturnValue")),
                HandlerMethodReturnValueInstrumentation.class.getName() + "$HandleReturnValueAdvice");
    }

    @SuppressWarnings("unused")
    public static class HandleReturnValueAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void nameResourceAndStartSpan(
                @Advice.Argument(0) Object returnValue,
                @Advice.Argument(3) NativeWebRequest request) {
            if(returnValue != null && "run.mone.common.Result".equals(returnValue.getClass().getName())){
                if(request instanceof ServletWebRequest){
                    ServletWebRequest servletRequst = (ServletWebRequest)request;
                    Class<?> returnValueClass = returnValue.getClass();
                    try {
                        Field codeFiled = returnValueClass.getDeclaredField("code");
                        codeFiled.setAccessible(true);
                        Object code = codeFiled.get(returnValue);
                        servletRequst.getResponse().addHeader("X-BUSSINESS-CODE",String.valueOf(code));
                        Field messageFiled = returnValueClass.getDeclaredField("message");
                        messageFiled.setAccessible(true);
                        Object message = messageFiled.get(returnValue);
                        servletRequst.getResponse().addHeader("X-BUSSINESS-MESSAGE",String.valueOf(message));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}

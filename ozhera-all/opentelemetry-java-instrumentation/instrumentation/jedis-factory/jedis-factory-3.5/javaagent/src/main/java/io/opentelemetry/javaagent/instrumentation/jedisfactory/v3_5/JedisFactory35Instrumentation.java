package io.opentelemetry.javaagent.instrumentation.jedisfactory.v3_5;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.instrumentation.jdbc.driver.DriverRequest;
import io.opentelemetry.javaagent.instrumentation.jdbc.driver.DriverSingletons;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import redis.clients.jedis.HostAndPort;

import java.util.concurrent.atomic.AtomicReference;

import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;

@SuppressWarnings({"SystemOut","CatchAndPrintStackTrace","CatchingUnchecked"})
public class JedisFactory35Instrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        ElementMatcher.Junction<TypeDescription> matcher =
        named("redis.clients.jedis.JedisFactory").and(
                new ElementMatcher<TypeDescription>() {
                    @Override
                    public boolean matches(TypeDescription target) {
                        try {
                            FieldList<FieldDescription.InDefinedShape> declaredFields = target.getDeclaredFields();
                            for(FieldDescription.InDefinedShape field : declaredFields){
                                if("hostAndPort".equals(field.getName())){
                                    return true;
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
        return matcher;
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isPublic().
                        and(named("makeObject")),
                this.getClass().getName()+"$MakeObjectAdvice35");
    }

    public static class MakeObjectAdvice35 {

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void stopSpan(
                @Advice.FieldValue("hostAndPort") AtomicReference<HostAndPort> hostAndPort,
                @Advice.FieldValue("password") String password) {
            DriverRequest request = new DriverRequest();
            HostAndPort hp = hostAndPort.get();
            request.setType("redis");
            request.setDomainPort(hp.getHost()+":"+hp.getPort());
            request.setPassword(password);
            Context parentContext = currentContext();
            Context context = DriverSingletons.instrumenter().start(parentContext, request);
            Scope scope = context.makeCurrent();
            if (scope != null) {
                scope.close();
                DriverSingletons.instrumenter().end(context, request, null, null);
            }
        }
    }
}

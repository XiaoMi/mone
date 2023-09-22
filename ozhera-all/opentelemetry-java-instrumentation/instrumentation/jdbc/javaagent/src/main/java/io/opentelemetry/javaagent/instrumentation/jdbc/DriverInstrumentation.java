/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jdbc;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static io.opentelemetry.javaagent.extension.matcher.ClassLoaderMatcher.hasClassesNamed;
import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;

import java.sql.Connection;
import java.util.Properties;

import io.opentelemetry.javaagent.instrumentation.jdbc.driver.DriverRequest;
import io.opentelemetry.javaagent.instrumentation.jdbc.driver.DriverSingletons;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class DriverInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("java.sql.Driver");
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return implementsInterface(named("java.sql.Driver"));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                nameStartsWith("connect")
                        .and(takesArgument(0, String.class))
                        .and(takesArgument(1, Properties.class))
                        .and(returns(named("java.sql.Connection"))),
                DriverInstrumentation.class.getName() + "$DriverAdvice");
    }

    @SuppressWarnings({"unused","SystemOut"})
    public static class DriverAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void onEnter(
                @Advice.Argument(0) String url,
                @Advice.Argument(1) Properties props,
                @Advice.Local("otelRequest") DriverRequest request,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {

            Context parentContext = currentContext();
            request = new DriverRequest();
            request.setType("db");
            if (url != null) {
                // jdbc:mysql://localhost:3306/testdb?connectTime=1000&wait_timeout=3600&...
                String urlNoProtocol = url.split("//")[1];
                String[] ipAndPortArr = urlNoProtocol.split("/");
                request.setDomainPort(ipAndPortArr[0]);
                if(ipAndPortArr[1].contains("?")){
                    request.setDataBaseName(ipAndPortArr[1].split("\\?")[0]);
                }else{
                    request.setDataBaseName(ipAndPortArr[1]);
                }
            }
            if (props != null) {
                request.setUserName(props.getProperty("user"));
                request.setPassword(props.getProperty("password"));
            }
            context = DriverSingletons.instrumenter().start(parentContext, request);
            scope = context.makeCurrent();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void addDbInfo(
                @Advice.Thrown Throwable throwable,
                @Advice.Argument(0) String url,
                @Advice.Argument(1) Properties props,
                @Advice.Local("otelRequest") DriverRequest request,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope,
                @Advice.Return Connection connection) {
            if (scope != null) {
                scope.close();
                DriverSingletons.instrumenter().end(context, request, null, null);
            }

            if (connection == null) {
                // Exception was probably thrown.
                return;
            }
            DbInfo dbInfo = JdbcConnectionUrlParser.parse(url, props);
            JdbcMaps.connectionInfo.put(connection, dbInfo);
        }
    }
}

/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.tooling;

import static io.opentelemetry.javaagent.bootstrap.AgentInitializer.isJavaBefore9;
import static io.opentelemetry.javaagent.tooling.SafeServiceLoader.loadOrdered;
import static io.opentelemetry.javaagent.tooling.Utils.getResourceName;
import static net.bytebuddy.matcher.ElementMatchers.any;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import io.opentelemetry.instrumentation.api.config.Config;
import io.opentelemetry.javaagent.bootstrap.AgentClassLoader;
import io.opentelemetry.javaagent.extension.AgentExtension;
import io.opentelemetry.javaagent.extension.AgentListener;
import io.opentelemetry.javaagent.extension.ignore.IgnoredTypesConfigurer;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.instrumentation.api.internal.BootstrapPackagePrefixesHolder;
import io.opentelemetry.javaagent.spi.BootstrapPackagesProvider;
import io.opentelemetry.javaagent.tooling.config.ConfigInitializer;
import io.opentelemetry.javaagent.tooling.context.FieldBackedProvider;
import io.opentelemetry.javaagent.tooling.ignore.IgnoredClassLoadersMatcher;
import io.opentelemetry.javaagent.tooling.ignore.IgnoredTypesBuilderImpl;
import io.opentelemetry.javaagent.tooling.ignore.IgnoredTypesMatcher;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.opentelemetry.sdk.common.EnvOrJvmProperties;
import io.opentelemetry.sdk.common.HeraJavaagentConfig;
import io.opentelemetry.sdk.common.SystemCommon;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

@SuppressWarnings({"CatchAndPrintStackTrace", "SystemOut"})
public class AgentInstaller {

    private static final Logger log;

    private static final String JAVAAGENT_ENABLED_CONFIG = "otel.javaagent.enabled";


    private static final String HERA_JAVAAGENT_CONFIG_DATA_ID = "hera_javaagent_config";
    private static final String HERA_JAVAAGENT_CONFIG_GROUP = "DEFAULT_GROUP";
    private static final int HERA_JAVAAGENT_CONFIG_TIME_OUT = 2000;

    // This property may be set to force synchronous AgentListener#afterAgent() execution: the
    // condition for delaying the AgentListener initialization is pretty broad and in case it covers
    // too much javaagent users can file a bug, force sync execution by setting this property to true
    // and continue using the javaagent
    private static final String FORCE_SYNCHRONOUS_AGENT_LISTENERS_CONFIG =
            "otel.javaagent.experimental.force-synchronous-agent-listeners";

    private static final Map<String, List<Runnable>> CLASS_LOAD_CALLBACKS = new HashMap<>();
    private static volatile Instrumentation instrumentation;

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    static {
        LoggingConfigurer.configureLogger();
        log = LoggerFactory.getLogger(AgentInstaller.class);

        addByteBuddyRawSetting();
        BootstrapPackagePrefixesHolder.setBoostrapPackagePrefixes(loadBootstrapPackagePrefixes());
        // this needs to be done as early as possible - before the first Config.get() call
        ConfigInitializer.initialize();
        // ensure java.lang.reflect.Proxy is loaded, as transformation code uses it internally
        // loading java.lang.reflect.Proxy after the bytebuddy transformer is set up causes
        // the internal-proxy instrumentation module to transform it, and then the bytebuddy
        // transformation code also tries to load it, which leads to a ClassCircularityError
        // loading java.lang.reflect.Proxy early here still allows it to be retransformed by the
        // internal-proxy instrumentation module after the bytebuddy transformer is set up
        Proxy.class.getName();

        // caffeine can trigger first access of ForkJoinPool under transform(), which leads ForkJoinPool
        // not to get transformed itself.
        // loading it early here still allows it to be retransformed as part of agent installation below
        ForkJoinPool.class.getName();

        // caffeine uses AtomicReferenceArray, ensure it is loaded to avoid ClassCircularityError during
        // transform.
        AtomicReferenceArray.class.getName();
    }

    public static void installBytebuddyAgent(Instrumentation inst) {
        logVersionInfo();
        Config config = Config.get();
        if (config.getBoolean(JAVAAGENT_ENABLED_CONFIG, true)) {
            // set env or jvm properties
            setEnvAndJvmProperties(config.getString(EnvOrJvmProperties.JVM_OTEL_NACOS_ADDRESS.getKey(), "nacos.hera-namespace:80"));

            List<AgentListener> agentListeners = loadOrdered(AgentListener.class);
            installBytebuddyAgent(inst, agentListeners);
        } else {
            log.debug("Tracing is disabled, not installing instrumentations.");
        }
    }

    /**
     * Install the core bytebuddy agent along with all implementations of {@link
     * InstrumentationModule}.
     *
     * @param inst Java Instrumentation used to install bytebuddy
     * @return the agent's class transformer
     */
    public static ResettableClassFileTransformer installBytebuddyAgent(
            Instrumentation inst, Iterable<AgentListener> agentListeners) {

        Config config = Config.get();
        runBeforeAgentListeners(agentListeners, config);

        instrumentation = inst;

        FieldBackedProvider.resetContextMatchers();

        AgentBuilder agentBuilder =
                new AgentBuilder.Default()
                        .disableClassFormatChanges()
                        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                        .with(new RedefinitionDiscoveryStrategy())
                        .with(AgentBuilder.DescriptionStrategy.Default.POOL_ONLY)
                        .with(AgentTooling.poolStrategy())
                        .with(new ClassLoadListener())
                        .with(AgentTooling.locationStrategy());
        // FIXME: we cannot enable it yet due to BB/JVM bug, see
        // https://github.com/raphw/byte-buddy/issues/558
        // .with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)

        agentBuilder = configureIgnoredTypes(config, agentBuilder);

        if (log.isDebugEnabled()) {
            agentBuilder =
                    agentBuilder
                            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                            .with(new RedefinitionDiscoveryStrategy())
                            .with(new RedefinitionLoggingListener())
                            .with(new TransformLoggingListener());
        }

        int numberOfLoadedExtensions = 0;
        for (AgentExtension agentExtension : loadOrdered(AgentExtension.class)) {
            log.debug(
                    "Loading extension {} [class {}]",
                    agentExtension.extensionName(),
                    agentExtension.getClass().getName());
            try {
                agentBuilder = agentExtension.extend(agentBuilder);
                numberOfLoadedExtensions++;
            } catch (Exception | LinkageError e) {
                log.error(
                        "Unable to load extension {} [class {}]",
                        agentExtension.extensionName(),
                        agentExtension.getClass().getName(),
                        e);
            }
        }
        log.debug("Installed {} extension(s)", numberOfLoadedExtensions);

        ResettableClassFileTransformer resettableClassFileTransformer = agentBuilder.installOn(inst);
        runAfterAgentListeners(agentListeners, config);
        return resettableClassFileTransformer;
    }

    private static void runBeforeAgentListeners(
            Iterable<AgentListener> agentListeners, Config config) {
        for (AgentListener agentListener : agentListeners) {
            agentListener.beforeAgent(config);
        }
    }

    private static AgentBuilder configureIgnoredTypes(Config config, AgentBuilder agentBuilder) {
        IgnoredTypesBuilderImpl builder = new IgnoredTypesBuilderImpl();
        for (IgnoredTypesConfigurer configurer : loadOrdered(IgnoredTypesConfigurer.class)) {
            configurer.configure(config, builder);
        }

        return agentBuilder
                .ignore(any(), new IgnoredClassLoadersMatcher(builder.buildIgnoredClassLoadersTrie()))
                .or(new IgnoredTypesMatcher(builder.buildIgnoredTypesTrie()));
    }

    private static void runAfterAgentListeners(
            Iterable<AgentListener> agentListeners, Config config) {
        // java.util.logging.LogManager maintains a final static LogManager, which is created during
        // class initialization. Some AgentListener implementations may use JRE bootstrap classes
        // which touch this class (e.g. JFR classes or some MBeans).
        // It is worth noting that starting from Java 9 (JEP 264) Java platform classes no longer use
        // JUL directly, but instead they use a new System.Logger interface, so the LogManager issue
        // applies mainly to Java 8.
        // This means applications which require a custom LogManager may not have a chance to set the
        // global LogManager if one of those AgentListeners runs first: it will incorrectly
        // set the global LogManager to the default JVM one in cases where the instrumented application
        // sets the LogManager system property or when the custom LogManager class is not on the system
        // classpath.
        // Our solution is to delay the initialization of AgentListeners when we detect a custom
        // log manager being used.
        // Once we see the LogManager class loading, it's safe to run AgentListener#afterAgent() because
        // the application is already setting the global LogManager and AgentListener won't be able
        // to touch it due to classloader locking.
        boolean shouldForceSynchronousAgentListenersCalls =
                Config.get().getBoolean(FORCE_SYNCHRONOUS_AGENT_LISTENERS_CONFIG, false);
        if (!shouldForceSynchronousAgentListenersCalls
                && isJavaBefore9()
                && isAppUsingCustomLogManager()) {
            log.debug("Custom JUL LogManager detected: delaying AgentListener#afterAgent() calls");
            registerClassLoadCallback(
                    "java.util.logging.LogManager", new DelayedAfterAgentCallback(config, agentListeners));
        } else {
            for (AgentListener agentListener : agentListeners) {
                agentListener.afterAgent(config);
            }
        }
    }

    private static void addByteBuddyRawSetting() {
        String savedPropertyValue = System.getProperty(TypeDefinition.RAW_TYPES_PROPERTY);
        try {
            System.setProperty(TypeDefinition.RAW_TYPES_PROPERTY, "true");
            boolean rawTypes = TypeDescription.AbstractBase.RAW_TYPES;
            if (!rawTypes) {
                log.debug("Too late to enable {}", TypeDefinition.RAW_TYPES_PROPERTY);
            }
        } finally {
            if (savedPropertyValue == null) {
                System.clearProperty(TypeDefinition.RAW_TYPES_PROPERTY);
            } else {
                System.setProperty(TypeDefinition.RAW_TYPES_PROPERTY, savedPropertyValue);
            }
        }
    }

    private static List<String> loadBootstrapPackagePrefixes() {
        List<String> bootstrapPackages = new ArrayList<>(Constants.BOOTSTRAP_PACKAGE_PREFIXES);
        Iterable<BootstrapPackagesProvider> bootstrapPackagesProviders =
                SafeServiceLoader.load(BootstrapPackagesProvider.class);
        for (BootstrapPackagesProvider provider : bootstrapPackagesProviders) {
            List<String> packagePrefixes = provider.getPackagePrefixes();
            log.debug(
                    "Loaded bootstrap package prefixes from {}: {}",
                    provider.getClass().getName(),
                    packagePrefixes);
            bootstrapPackages.addAll(packagePrefixes);
        }
        return bootstrapPackages;
    }

    static class RedefinitionLoggingListener implements AgentBuilder.RedefinitionStrategy.Listener {

        private static final Logger log = LoggerFactory.getLogger(RedefinitionLoggingListener.class);

        @Override
        public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
        }

        @Override
        public Iterable<? extends List<Class<?>>> onError(
                int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
            if (log.isDebugEnabled()) {
                log.debug("Exception while retransforming {} classes: {}", batch.size(), batch, throwable);
            }
            return Collections.emptyList();
        }

        @Override
        public void onComplete(
                int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
        }
    }

    static class TransformLoggingListener implements AgentBuilder.Listener {

        private static final TransformSafeLogger log =
                TransformSafeLogger.getLogger(TransformLoggingListener.class);

        @Override
        public void onError(
                String typeName,
                ClassLoader classLoader,
                JavaModule module,
                boolean loaded,
                Throwable throwable) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Failed to handle {} for transformation on classloader {}",
                        typeName,
                        classLoader,
                        throwable);
            }
        }

        @Override
        public void onTransformation(
                TypeDescription typeDescription,
                ClassLoader classLoader,
                JavaModule module,
                boolean loaded,
                DynamicType dynamicType) {
            log.debug("Transformed {} -- {}", typeDescription.getName(), classLoader);
        }

        @Override
        public void onIgnored(
                TypeDescription typeDescription,
                ClassLoader classLoader,
                JavaModule module,
                boolean loaded) {
        }

        @Override
        public void onComplete(
                String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }

        @Override
        public void onDiscovery(
                String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }
    }

    /**
     * Register a callback to run when a class is loading.
     *
     * <p>Caveats:
     *
     * <ul>
     *   <li>This callback will be invoked by a jvm class transformer.
     *   <li>Classes filtered out by {@link AgentInstaller}'s skip list will not be matched.
     * </ul>
     *
     * @param className name of the class to match against
     * @param callback  runnable to invoke when class name matches
     */
    public static void registerClassLoadCallback(String className, Runnable callback) {
        synchronized (CLASS_LOAD_CALLBACKS) {
            List<Runnable> callbacks =
                    CLASS_LOAD_CALLBACKS.computeIfAbsent(className, k -> new ArrayList<>());
            callbacks.add(callback);
        }
    }

    private static class DelayedAfterAgentCallback implements Runnable {
        private final Iterable<AgentListener> agentListeners;
        private final Config config;

        private DelayedAfterAgentCallback(Config config, Iterable<AgentListener> agentListeners) {
            this.agentListeners = agentListeners;
            this.config = config;
        }

        @Override
        public void run() {
            /*
             * This callback is called from within bytecode transformer. This can be a problem if callback tries
             * to load classes being transformed. To avoid this we start a thread here that calls the callback.
             * This seems to resolve this problem.
             */
            Thread thread = new Thread(this::runAgentListeners);
            thread.setName("delayed-agent-listeners");
            thread.setDaemon(true);
            thread.start();
        }

        private void runAgentListeners() {
            for (AgentListener agentListener : agentListeners) {
                try {
                    agentListener.afterAgent(config);
                } catch (RuntimeException e) {
                    log.error("Failed to execute {}", agentListener.getClass().getName(), e);
                }
            }
        }
    }

    private static class ClassLoadListener implements AgentBuilder.Listener {
        @Override
        public void onDiscovery(
                String typeName, ClassLoader classLoader, JavaModule javaModule, boolean b) {
        }

        @Override
        public void onTransformation(
                TypeDescription typeDescription,
                ClassLoader classLoader,
                JavaModule javaModule,
                boolean b,
                DynamicType dynamicType) {
        }

        @Override
        public void onIgnored(
                TypeDescription typeDescription,
                ClassLoader classLoader,
                JavaModule javaModule,
                boolean b) {
        }

        @Override
        public void onError(
                String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {
        }

        @Override
        public void onComplete(
                String typeName, ClassLoader classLoader, JavaModule javaModule, boolean b) {
            synchronized (CLASS_LOAD_CALLBACKS) {
                List<Runnable> callbacks = CLASS_LOAD_CALLBACKS.get(typeName);
                if (callbacks != null) {
                    for (Runnable callback : callbacks) {
                        callback.run();
                    }
                }
            }
        }
    }

    private static class RedefinitionDiscoveryStrategy
            implements AgentBuilder.RedefinitionStrategy.DiscoveryStrategy {
        private static final AgentBuilder.RedefinitionStrategy.DiscoveryStrategy delegate =
                AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Reiterating.INSTANCE;

        @Override
        public Iterable<Iterable<Class<?>>> resolve(Instrumentation instrumentation) {
            // filter out our agent classes and injected helper classes
            return () ->
                    streamOf(delegate.resolve(instrumentation))
                            .map(RedefinitionDiscoveryStrategy::filterClasses)
                            .iterator();
        }

        private static Iterable<Class<?>> filterClasses(Iterable<Class<?>> classes) {
            return () -> streamOf(classes).filter(c -> !isIgnored(c)).iterator();
        }

        private static <T> Stream<T> streamOf(Iterable<T> iterable) {
            return StreamSupport.stream(iterable.spliterator(), false);
        }

        private static boolean isIgnored(Class<?> c) {
            ClassLoader cl = c.getClassLoader();
            if (cl instanceof AgentClassLoader || cl instanceof ExtensionClassLoader) {
                return true;
            }

            return HelperInjector.isInjectedClass(c);
        }
    }

    /**
     * Detect if the instrumented application is using a custom JUL LogManager.
     */
    private static boolean isAppUsingCustomLogManager() {
        String jbossHome = System.getenv("JBOSS_HOME");
        if (jbossHome != null) {
            log.debug("Found JBoss: {}; assuming app is using custom LogManager", jbossHome);
            // JBoss/Wildfly is known to set a custom log manager after startup.
            // Originally we were checking for the presence of a jboss class,
            // but it seems some non-jboss applications have jboss classes on the classpath.
            // This would cause AgentListener#afterAgent() calls to be delayed indefinitely.
            // Checking for an environment variable required by jboss instead.
            return true;
        }

        String customLogManager = System.getProperty("java.util.logging.manager");
        if (customLogManager != null) {
            log.debug(
                    "Detected custom LogManager configuration: java.util.logging.manager={}",
                    customLogManager);
            boolean onSysClasspath =
                    ClassLoader.getSystemResource(getResourceName(customLogManager)) != null;
            log.debug(
                    "Class {} is on system classpath: {}delaying AgentInstaller#afterAgent()",
                    customLogManager,
                    onSysClasspath ? "not " : "");
            // Some applications set java.util.logging.manager but never actually initialize the logger.
            // Check to see if the configured manager is on the system classpath.
            // If so, it should be safe to initialize AgentInstaller which will setup the log manager:
            // LogManager tries to load the implementation first using system CL, then falls back to
            // current context CL
            return !onSysClasspath;
        }

        return false;
    }


    /**
     * Check if environment variables and JVM parameters are set, if not, use the corresponding configuration in Nacos and set them back.
     */
    private static void setEnvAndJvmProperties(String nacosAddr) {
        Map<String, String> byNacos = getByNacos(nacosAddr);
        if (byNacos != null && byNacos.size() > 0) {
            for (HeraJavaagentConfig config : EnvOrJvmProperties.INIT_ENV_JVM_LIST) {
                String propertiesKey = config.getKey();
                String envOrProperties = SystemCommon.getEnvOrProperties(propertiesKey);
                if (envOrProperties == null) {
                    envOrProperties = byNacos.get(propertiesKey);
                    if(envOrProperties != null) {
                        System.setProperty(propertiesKey, envOrProperties);
                    }
                }
            }
        }
        // set env to JVM operation
        setJvmToEnv();
        // set default values for env or properties
        setDefaultEnv();
    }

    private static void setJvmToEnv() {
        // set project env id
        if(SystemCommon.getEnvOrProperties(EnvOrJvmProperties.ENV_MIONE_PROJECT_ENV_ID.getKey()) == null){
            System.setProperty(EnvOrJvmProperties.ENV_MIONE_PROJECT_ENV_ID.getKey(), SystemCommon.getEnvOrProperties(EnvOrJvmProperties.JVM_OTEL_MIONE_PROJECT_ENV_ID.getKey()));
        }
        // set project env name
        if(SystemCommon.getEnvOrProperties(EnvOrJvmProperties.ENV_MIONE_PROJECT_ENV_NAME.getKey()) == null){
            System.setProperty(EnvOrJvmProperties.ENV_MIONE_PROJECT_ENV_NAME.getKey(), SystemCommon.getEnvOrProperties(EnvOrJvmProperties.JVM_OTEL_MIONE_PROJECT_ENV_NAME.getKey()));
        }
        // set project name
        if(SystemCommon.getEnvOrProperties(EnvOrJvmProperties.MIONE_PROJECT_NAME.getKey()) == null){
            String service = SystemCommon.getEnvOrProperties(EnvOrJvmProperties.JVM_OTEL_RESOURCE_ATTRIBUTES.getKey());
            if (service != null && service.contains("=")) {
                System.setProperty(EnvOrJvmProperties.MIONE_PROJECT_NAME.getKey(), service.split("=")[1]);
            }
        }
    }

    private static void setDefaultEnv(){
        for(HeraJavaagentConfig config : EnvOrJvmProperties.INIT_ENV_JVM_LIST){
            if(SystemCommon.getEnvOrProperties(config.getKey()) == null && config.getDefaultValue() != null){
                System.setProperty(config.getKey(), config.getDefaultValue());
            }
        }
    }

    @Nullable
    private static Map<String, String> getByNacos(String nacosAddr) {
        try {
            ConfigService configService = ConfigFactory.createConfigService(nacosAddr);
            String config = configService.getConfig(HERA_JAVAAGENT_CONFIG_DATA_ID, HERA_JAVAAGENT_CONFIG_GROUP, HERA_JAVAAGENT_CONFIG_TIME_OUT);
            return formatConfig(config);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @Nullable
    private static Map<String, String> formatConfig(String nacosConfig) {
        if (nacosConfig == null || nacosConfig.equals("")) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        String[] entries = nacosConfig.split(System.lineSeparator());
        for (String entry : entries) {
            if (entry != null && entry.contains("=")) {
                String[] kv = entry.split("=", 2);
                result.put(kv[0], kv[1]);
            }
        }
        return result;
    }


    private static void logVersionInfo() {
        VersionLogger.logAllVersions();
        log.debug(
                "{} loaded on {}", AgentInstaller.class.getName(), AgentInstaller.class.getClassLoader());
    }

    private AgentInstaller() {
    }
}

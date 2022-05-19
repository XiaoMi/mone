package com.xiaomi.youpin.docean.plugin.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.MethodUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.common.Pair;
import com.xiaomi.youpin.docean.exception.DoceanException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class SentinelInterceptor extends EnhanceInterceptor {


    private static final String KEY = "sentinel";

    @Override
    public void before(AopContext context,Method originMethod, Object[] args) {
        super.before(context,originMethod, args);

        Sentinel annotation = originMethod.getAnnotation(Sentinel.class);
        String resourceName = this.getResourceName(annotation.value(), originMethod);
        EntryType entryType = annotation.entryType();
        int resourceType = annotation.resourceType();
        Entry entry = null;
        Object obj = null;
        try {
            entry = SphU.entry(resourceName, resourceType, entryType, args);
        } catch (BlockException ex) {
            try {
                obj = handleBlockException(originMethod, args, annotation, ex);
            } catch (Throwable throwable) {
                throw new DoceanException(throwable);
            }
        } catch (Throwable ex) {
            Class<? extends Throwable>[] exceptionsToIgnore = annotation.exceptionsToIgnore();
            // The ignore list will be checked first.
            if (exceptionsToIgnore.length > 0 && exceptionBelongsTo(ex, exceptionsToIgnore)) {
                throw new RuntimeException("block");
            }
            if (exceptionBelongsTo(ex, annotation.exceptionsToTrace())) {
                traceException(ex);
                try {
                    obj = handleFallback(originMethod, args, annotation, ex);
                } catch (Throwable throwable) {
                    throw new DoceanException(throwable);
                }
            }else{
                // No fallback function can handle the exception, so throw it out.
                throw new RuntimeException("block");
            }
        }

        if (entry!=null) {
            context.put(KEY,Pair.of(entry, args));
        }else{
            Object[] result = new Object[1];
            result[0] = obj;
            context.put(KEY,Pair.of(entry, result));
        }
    }
    
    private void traceException(Throwable ex) {
        Tracer.trace(ex);
    }

    private boolean exceptionBelongsTo(Throwable ex, Class<? extends Throwable>[] exceptions) {
        if (exceptions == null) {
            return false;
        }
        for (Class<? extends Throwable> exceptionClass : exceptions) {
            if (exceptionClass.isAssignableFrom(ex.getClass())) {
                return true;
            }
        }
        return false;
    }

    private Object handleBlockException(Method originMethod, Object[] originArgs, Sentinel annotation, BlockException ex)
            throws Throwable {

        // Execute block handler if configured.
        Method blockHandlerMethod = extractBlockHandlerMethod(originMethod, annotation.blockHandler(),
                annotation.blockHandlerClass());
        if (blockHandlerMethod != null) {
            // Construct args.
            Object[] args = Arrays.copyOf(originArgs, originArgs.length + 1);
            args[args.length - 1] = ex;
            try {
                if (isStatic(blockHandlerMethod)) {
                    return blockHandlerMethod.invoke(null, args);
                }
                return blockHandlerMethod.invoke(originMethod.getDeclaringClass().newInstance(), args);
            } catch (InvocationTargetException e) {
                // throw the actual exception
                throw e.getTargetException();
            }
        }

        // If no block handler is present, then go to fallback.
        return handleFallback(originMethod, originArgs, annotation, ex);
    }
    
    private Object handleFallback(Method originMethod, Object[] originArgs, Sentinel annotation, Throwable ex)
            throws Throwable {
        return handleFallback(originMethod, originArgs, annotation.fallback(), annotation.defaultFallback(), annotation.fallbackClass(), ex);
    }

    private Object handleFallback(Method originMethod, Object[] originArgs, String fallback, String defaultFallback,
                                    Class<?>[] fallbackClass, Throwable ex) throws Throwable {

        // Execute fallback function if configured.
        Method fallbackMethod = extractFallbackMethod(originMethod, fallback, fallbackClass);
        if (fallbackMethod != null) {
            // Construct args.
            int paramCount = fallbackMethod.getParameterTypes().length;
            Object[] args;
            if (paramCount == originArgs.length) {
                args = originArgs;
            } else {
                args = Arrays.copyOf(originArgs, originArgs.length + 1);
                args[args.length - 1] = ex;
            }

            try {
                if (isStatic(fallbackMethod)) {
                    return fallbackMethod.invoke(null, args);
                }
                return fallbackMethod.invoke(originMethod.getDeclaringClass().newInstance(), args).toString();
            } catch (InvocationTargetException e) {
                // throw the actual exception
                throw e.getTargetException();
            }
        }
        // If fallback is absent, we'll try the defaultFallback if provided.
        return handleDefaultFallback(originMethod, defaultFallback, fallbackClass, ex);
    }

    private Object handleDefaultFallback(Method originMethod, String defaultFallback,
                                           Class<?>[] fallbackClass, Throwable ex) throws Throwable {
        // Execute the default fallback function if configured.
        Method fallbackMethod = extractDefaultFallbackMethod(originMethod, defaultFallback, fallbackClass);
        if (fallbackMethod != null) {
            // Construct args.
            Object[] args = fallbackMethod.getParameterTypes().length == 0 ? new Object[0] : new Object[] {ex};
            try {
                if (isStatic(fallbackMethod)) {
                    return fallbackMethod.invoke(null, args);
                }
                return fallbackMethod.invoke(originMethod.getDeclaringClass().newInstance(), args);
            } catch (InvocationTargetException e) {
                // throw the actual exception
                throw e.getTargetException();
            }
        }

        // If no any fallback is present, then directly throw the exception.
        throw ex;
    }

    private Method extractDefaultFallbackMethod(Method originMethod, String defaultFallback,
                                                Class<?>[] locationClass) {
        if (StringUtil.isBlank(defaultFallback)) {
            return null;
        }
        boolean mustStatic = locationClass != null && locationClass.length >= 1;
        Class<?> clazz = mustStatic ? locationClass[0] : originMethod.getDeclaringClass();

        MethodWrapper m = ResourceMetadataRegistry.lookupDefaultFallback(clazz, defaultFallback);
        if (m == null) {
            // First time, resolve the default fallback.
            Class<?> originReturnType = originMethod.getReturnType();
            // Default fallback allows two kinds of parameter list.
            // One is empty parameter list.
            Class<?>[] defaultParamTypes = new Class<?>[0];
            // The other is a single parameter {@link Throwable} to get relevant exception info.
            Class<?>[] paramTypeWithException = new Class<?>[] {Throwable.class};
            // We first find the default fallback with empty parameter list.
            Method method = findMethod(mustStatic, clazz, defaultFallback, originReturnType, defaultParamTypes);
            // If default fallback with empty params is absent, we then try to find the other one.
            if (method == null) {
                method = findMethod(mustStatic, clazz, defaultFallback, originReturnType, paramTypeWithException);
            }
            // Cache the method instance.
            ResourceMetadataRegistry.updateDefaultFallbackFor(clazz, defaultFallback, method);
            return method;
        }
        if (!m.isPresent()) {
            return null;
        }
        return m.getMethod();
    }


    private Method extractFallbackMethod(Method originMethod, String fallbackName, Class<?>[] locationClass) {
        if (StringUtil.isBlank(fallbackName)) {
            return null;
        }
        boolean mustStatic = locationClass != null && locationClass.length >= 1;
        Class<?> clazz = mustStatic ? locationClass[0] : originMethod.getDeclaringClass();
        MethodWrapper m = ResourceMetadataRegistry.lookupFallback(clazz, fallbackName);
        if (m == null) {
            // First time, resolve the fallback.
            Method method = resolveFallbackInternal(originMethod, fallbackName, clazz, mustStatic);
            // Cache the method instance.
            ResourceMetadataRegistry.updateFallbackFor(clazz, fallbackName, method);
            return method;
        }
        if (!m.isPresent()) {
            return null;
        }
        return m.getMethod();
    }

    private Method resolveFallbackInternal(Method originMethod, /*@NonNull*/ String name, Class<?> clazz,
                                           boolean mustStatic) {
        // Fallback function allows two kinds of parameter list.
        Class<?>[] defaultParamTypes = originMethod.getParameterTypes();
        Class<?>[] paramTypesWithException = Arrays.copyOf(defaultParamTypes, defaultParamTypes.length + 1);
        paramTypesWithException[paramTypesWithException.length - 1] = Throwable.class;
        // We first find the fallback matching the signature of origin method.
        Method method = findMethod(mustStatic, clazz, name, originMethod.getReturnType(), defaultParamTypes);
        // If fallback matching the origin method is absent, we then try to find the other one.
        if (method == null) {
            method = findMethod(mustStatic, clazz, name, originMethod.getReturnType(), paramTypesWithException);
        }
        return method;
    }
    
    private Method extractBlockHandlerMethod(Method originMethod, String name, Class<?>[] locationClass) {
        if (StringUtil.isBlank(name)) {
            return null;
        }

        boolean mustStatic = locationClass != null && locationClass.length >= 1;
        Class<?> clazz;
        if (mustStatic) {
            clazz = locationClass[0];
        } else {
            // By default current class.
            clazz = originMethod.getDeclaringClass();
        }
        MethodWrapper m = ResourceMetadataRegistry.lookupBlockHandler(clazz, name);
        if (m == null) {
            // First time, resolve the block handler.
            Method method = resolveBlockHandlerInternal(originMethod, name, clazz, mustStatic);
            // Cache the method instance.
            ResourceMetadataRegistry.updateBlockHandlerFor(clazz, name, method);
            return method;
        }
        if (!m.isPresent()) {
            return null;
        }
        return m.getMethod();
    }

    private Method resolveBlockHandlerInternal(Method originMethod, /*@NonNull*/ String name, Class<?> clazz,
                                               boolean mustStatic) {
        Class<?>[] originList = originMethod.getParameterTypes();
        Class<?>[] parameterTypes = Arrays.copyOf(originList, originList.length + 1);
        parameterTypes[parameterTypes.length - 1] = BlockException.class;
        return findMethod(mustStatic, clazz, name, originMethod.getReturnType(), parameterTypes);
    }

    private Method findMethod(boolean mustStatic, Class<?> clazz, String name, Class<?> returnType,
                              Class<?>... parameterTypes) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (name.equals(method.getName()) && checkStatic(mustStatic, method)
                    && returnType.isAssignableFrom(method.getReturnType())
                    && Arrays.equals(parameterTypes, method.getParameterTypes())) {

                RecordLog.info("Resolved method [{0}] in class [{1}]", name, clazz.getCanonicalName());
                return method;
            }
        }
        // Current class not found, find in the super classes recursively.
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !Object.class.equals(superClass)) {
            return findMethod(mustStatic, superClass, name, returnType, parameterTypes);
        } else {
            String methodType = mustStatic ? " static" : "";
            RecordLog.warn("Cannot find{0} method [{1}] in class [{2}] with parameters {3}",
                    methodType, name, clazz.getCanonicalName(), Arrays.toString(parameterTypes));
            return null;
        }
    }

    private boolean checkStatic(boolean mustStatic, Method method) {
        return !mustStatic || isStatic(method);
    }

    private boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }


    @Override
    public Object after(AopContext context, Method method, Object res) {
        Pair<Entry, Object[]> enryPair = context.get(KEY);
        if (enryPair.getKey() != null) {
            enryPair.getKey().exit(1, enryPair.getValue());
            return res;
        } else {
            return enryPair.getValue()[0];
        }
    }

    private String getResourceName(String resourceName, Method method) {
        return StringUtil.isNotBlank(resourceName) ? resourceName : MethodUtil.resolveMethodName(method);
    }


}
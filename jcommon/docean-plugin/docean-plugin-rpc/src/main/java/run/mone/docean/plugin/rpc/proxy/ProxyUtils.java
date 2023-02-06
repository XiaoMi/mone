package run.mone.docean.plugin.rpc.proxy;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.SneakyThrows;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import run.mone.docean.plugin.rpc.interceptor.RpcCallMethodInterceptor;
import run.mone.docean.plugin.rpc.processor.ExceptionProcessor;
import run.mone.docean.plugin.rpc.processor.ResultProcessor;

import java.lang.reflect.Field;

/**
 * @author goodjava@qq.com
 * @date 2023/1/8
 */
public abstract class ProxyUtils {


    @SneakyThrows
    public static void setField(Object bean, Field field, Object value) {
        if (ProxyUtils.isAopProxy(bean)) {
            Object obj = ProxyUtils.unwrapProxy(bean);
            field.setAccessible(true);
            field.set(obj, value);
        } else {
            field.setAccessible(true);
            field.set(bean, value);
        }
    }


    public static boolean isAopProxy(Object bean) {
        return AopUtils.isAopProxy(bean);
    }


    public static Object unwrapProxy(Object proxy) {
        if (proxy instanceof Advised) {
            try {
                Object target = ((Advised) proxy).getTargetSource().getTarget();
                return target;
            } catch (Exception e) {
            }
        }
        return proxy;
    }


    /**
     * 代理调用
     *
     * @param ioc
     * @param config
     * @param clazz
     * @param alias
     * @param <T>
     * @return
     */
    public static <T> T proxy(RpcReferenceBo reference, Ioc ioc, Config config, Class clazz, String alias) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        reference.setServiceName(clazz.getName());
        RpcCallMethodInterceptor interceptor = new RpcCallMethodInterceptor(ioc, config, reference, alias);
        interceptor.setExceptionProcessor(new ExceptionProcessor());
        interceptor.setResultProcessor(new ResultProcessor());
        enhancer.setCallback(interceptor);
        Object service = enhancer.create();
        return (T) service;
    }



}

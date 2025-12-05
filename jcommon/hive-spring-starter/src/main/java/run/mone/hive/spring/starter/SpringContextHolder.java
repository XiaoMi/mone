package run.mone.hive.spring.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring ApplicationContext 持有器
 * 用于在静态方法中获取 Spring Bean
 *
 * @author goodjava@qq.com
 */
@Slf4j
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        log.info("SpringContextHolder initialized");
    }

    /**
     * 获取 ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据类型获取 Bean
     *
     * @param clazz Bean 类型
     * @return Bean 实例，如果未找到或 ApplicationContext 未初始化则返回 null
     */
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            log.debug("ApplicationContext is not initialized yet");
            return null;
        }
        try {
            return applicationContext.getBean(clazz);
        } catch (BeansException e) {
            log.debug("Bean not found for class: {}", clazz.getName());
            return null;
        }
    }

    /**
     * 根据名称获取 Bean
     *
     * @param name Bean 名称
     * @return Bean 实例，如果未找到或 ApplicationContext 未初始化则返回 null
     */
    public static Object getBean(String name) {
        if (applicationContext == null) {
            log.debug("ApplicationContext is not initialized yet");
            return null;
        }
        try {
            return applicationContext.getBean(name);
        } catch (BeansException e) {
            log.debug("Bean not found for name: {}", name);
            return null;
        }
    }

    /**
     * 根据名称和类型获取 Bean
     *
     * @param name  Bean 名称
     * @param clazz Bean 类型
     * @return Bean 实例，如果未找到或 ApplicationContext 未初始化则返回 null
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        if (applicationContext == null) {
            log.debug("ApplicationContext is not initialized yet");
            return null;
        }
        try {
            return applicationContext.getBean(name, clazz);
        } catch (BeansException e) {
            log.debug("Bean not found for name: {} and class: {}", name, clazz.getName());
            return null;
        }
    }

    /**
     * 检查 ApplicationContext 是否已初始化
     */
    public static boolean isInitialized() {
        return applicationContext != null;
    }
}

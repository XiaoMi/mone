package run.mone.hive.mcp.service.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * ApplicationContext静态持有者
 * 用于在非Spring管理的类中获取Spring容器中的Bean
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;
        log.info("ApplicationContext已设置到ApplicationContextHolder中");
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext实例
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据名称获取Bean
     *
     * @param name Bean名称
     * @return Bean实例
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 根据类型获取Bean
     *
     * @param clazz Bean类型
     * @param <T>   泛型类型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称和类型获取Bean
     *
     * @param name  Bean名称
     * @param clazz Bean类型
     * @param <T>   泛型类型
     * @return Bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 检查ApplicationContext是否已初始化
     *
     * @return true如果已初始化，false否则
     */
    public static boolean isInitialized() {
        return applicationContext != null;
    }
}

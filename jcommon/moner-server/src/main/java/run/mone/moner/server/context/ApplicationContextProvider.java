package run.mone.moner.server.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * @author goodjava@qq.com
 * @date 2024/2/23 14:42
 */
@Service
public class ApplicationContextProvider implements ApplicationListener<ContextRefreshedEvent> {

    private static ApplicationContext context;


    /**
     * 当应用程序上下文刷新事件发生时，设置ApplicationContextProvider的上下文
     *
     * @param event 应用程序上下文刷新事件
     */
	@Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContextProvider.context = event.getApplicationContext();
    }

    /**
     * 根据给定的类从Spring上下文中获取对应的Bean实例
     *
     * @param <T> Bean的类型
     * @param beanClass 要获取的Bean的类
     * @return 对应类型的Bean实例
     */
	public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }


}

package org.apache.dubbo.config.spring.beans.factory.config;

import org.apache.dubbo.config.AbstractConfig;

import com.alibaba.spring.beans.factory.config.GenericBeanPostProcessorAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.core.PriorityOrdered;


/**
 * @Author zhangping17
 * @Date 2022/2/23 15:33
 */
public class DubboConfigEarlyRegistrationPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    public static final String BEAN_NAME = "dubboConfigEarlyRegistrationPostProcessor";

    private static final Log logger = LogFactory.getLog(DubboConfigEarlyRegistrationPostProcessor.class.getName());

    private DefaultListableBeanFactory beanFactory;

    private DubboConfigEarlyInitializationPostProcessor configEarlyInitializationPostProcessor =
            new DubboConfigEarlyInitializationPostProcessor();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.beanFactory = unwrap(registry);
        registryConfigEarlyInitializationPostProcessor(beanFactory);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.beanFactory == null) {
            // try again if postProcessBeanDefinitionRegistry method does not effect.
            this.beanFactory = unwrap(beanFactory);
            registryConfigEarlyInitializationPostProcessor(this.beanFactory);
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    private void registryConfigEarlyInitializationPostProcessor(DefaultListableBeanFactory beanFactory) {
        if (beanFactory != null) {
            // Register DubboConfigEarlyInitializationPostProcessor
            beanFactory.addBeanPostProcessor(configEarlyInitializationPostProcessor);
            if (logger.isInfoEnabled()) {
                logger.info("DubboConfigEarlyInitializationPostProcessor has bean registered");
            }
        }
    }

    private DefaultListableBeanFactory unwrap(Object registry) {
        if (registry instanceof DefaultListableBeanFactory) {
            return (DefaultListableBeanFactory) registry;
        }
        return null;
    }

    class DubboConfigEarlyInitializationPostProcessor extends GenericBeanPostProcessorAdapter<AbstractConfig> {

        private boolean registeredCommonAnnotationBeanPostProcessor = false;

        @Override
        protected void processBeforeInitialization(AbstractConfig config, String beanName) throws BeansException {
            if (beanFactory == null) {
                if (logger.isErrorEnabled()) {
                    logger.error("Current Processor is not running in Spring container, next action will be skipped!");
                }
                return;
            }
            // If CommonAnnotationBeanPostProcessor is already registered,  the method addIntoConfigManager()
            // will be invoked in Bean life cycle.
            if (!hasRegisteredCommonAnnotationBeanPostProcessor()) {
                if (logger.isWarnEnabled()) {
                    logger.warn("CommonAnnotationBeanPostProcessor is not registered yet, " +
                            "the method addIntoConfigManager() will be invoked directly");
                }
                config.addIntoConfigManager();
            }
        }

        private boolean hasRegisteredCommonAnnotationBeanPostProcessor() {
            if (registeredCommonAnnotationBeanPostProcessor) {
                return true;
            }
            for (BeanPostProcessor beanPostProcessor : beanFactory.getBeanPostProcessors()) {
                if (CommonAnnotationBeanPostProcessor.class.equals(beanPostProcessor.getClass())) {
                    this.registeredCommonAnnotationBeanPostProcessor = true;
                    return true;
                }
            }
            return false;
        }
    }
}

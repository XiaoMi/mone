package run.mone.junit;

import com.xiaomi.youpin.docean.Ioc;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.Resource;
import java.util.Arrays;


/**
 * @author goodjava@qq.com
 * @date 2024/3/3 09:14
 */
public class DoceanExtension implements  BeforeAllCallback,BeforeEachCallback {

    private Ioc container;


    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        // 获取测试类
        Class<?> testClass = extensionContext.getRequiredTestClass();
        DoceanConfiguration iocConfig = testClass.getAnnotation(DoceanConfiguration.class);
        if (iocConfig == null) {
            throw new IllegalStateException("Missing @IocConfiguration on test class " + testClass.getName());
        }
        // 从注解中获取包名
        String[] basePackage = iocConfig.basePackage();
        // 初始化IoC容器
        this.container = Ioc.ins().init(basePackage);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Object testInstance = extensionContext.getRequiredTestInstance();
        Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Resource.class))
                .forEach(field -> {
                    Object bean = container.getBean(field.getType());
                    if (bean != null) {
                        field.setAccessible(true);
                        try {
                            field.set(testInstance, bean);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }
}

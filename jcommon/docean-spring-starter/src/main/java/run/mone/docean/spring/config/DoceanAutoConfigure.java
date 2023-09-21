package run.mone.docean.spring.config;

import com.google.common.base.Splitter;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.Safe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.docean.spring.extension.Extensions;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/9/19 13:46
 */
@Configuration
@Slf4j
public class DoceanAutoConfigure {

    private Ioc ioc;

    @Resource
    private ApplicationContext ac;

    @Value("${extensions:}")
    private String extensionsConfig;

    public static Map<String, String> extensionMap = new HashMap<>();


    @PostConstruct
    public void initConfig() {
        List<String> packageList = new ArrayList<>();
        Splitter.on(",").splitToList(extensionsConfig).forEach(it -> {
            List<String> list = Splitter.on(":").splitToList(extensionsConfig);
            extensionMap.put(list.get(0), list.get(1));
            packageList.add(list.get(2));
        });
        packageList.add("run.mone.docean.plugin.spring");
        ioc = Ioc.ins().name("extension").setContextFunction(name -> {
            if (ac.containsBean(name)) {
                return ac.getBean(name);
            }
            return Safe.callAndLog(() -> ac.getBean(Class.forName(name)), null);
        }).init(packageList.toArray(new String[0]));
    }


    @Bean
    @ConditionalOnMissingBean
    public Extensions extensions() {
        Extensions extensions = new Extensions(ioc);
        return extensions;
    }

}

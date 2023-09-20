package run.mone.docean.spring.extension;

import com.xiaomi.youpin.docean.Ioc;
import run.mone.docean.spring.config.DoceanAutoConfigure;

/**
 * @author goodjava@qq.com
 * @date 2023/9/19 14:20
 */
public class Extensions {

    private Ioc ioc;

    public Extensions(Ioc ioc) {
        this.ioc = ioc;
    }

    public <T> T get(String name) {
        String key = DoceanAutoConfigure.extensionMap.get(name);
        return ioc.getBean(key);
    }


}

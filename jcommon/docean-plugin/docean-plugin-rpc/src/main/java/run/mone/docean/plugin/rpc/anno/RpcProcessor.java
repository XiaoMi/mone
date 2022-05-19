package run.mone.docean.plugin.rpc.anno;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcProcessor {

    int cmd();

}

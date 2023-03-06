package run.mone.hera.operator.test;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2022/6/14 15:56
 */
public class SidecarTest {

    @Test
    public void testSidecar() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass().getResourceAsStream("/xxx/deployment/redis_deployment.yaml");
        Deployment deployment = yaml.loadAs(inputStream, Deployment.class);
        List<Container> containers = deployment.getSpec().getTemplate().getSpec().getContainers();
        InputStream sidecarIs = this.getClass().getResourceAsStream("/xxx/sidecar/tools.yaml");
        Container container = yaml.loadAs(sidecarIs, Container.class);
        containers.add(container);
        System.out.println(deployment);
    }


    @Test
    public void saa() {
        Yaml yaml = new Yaml();
        Container container = new Container();
        container.setName("abc");
        System.out.println(yaml.dump(container));
    }


}

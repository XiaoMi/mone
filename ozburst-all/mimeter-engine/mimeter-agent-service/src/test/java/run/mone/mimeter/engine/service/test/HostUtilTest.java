package run.mone.mimeter.engine.service.test;

import org.junit.Test;
import run.mone.mimeter.engine.agent.bo.hosts.HostBo;
import run.mone.mimeter.engine.service.HostsService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dongzhenxing
 */
public class HostUtilTest {

    @Test
    public void test() throws InterruptedException {
        System.out.println(System.getProperty("os.name"));
    }

    @Test
    public void testUpdateHosts() {
        List<HostBo> hostBoList = new ArrayList<>();
        HostBo hostBo = new HostBo();
        hostBo.setIp("127.0.0.1");
        hostBo.setDomain("test.com");

        HostBo hostBo2 = new HostBo();
        hostBo2.setIp("127.0.0.2");
        hostBo2.setDomain("test2.com");
        hostBoList.add(hostBo);
        hostBoList.add(hostBo2);
        HostsService.updateHostConfig(hostBoList);
    }

    @Test
    public void testDelHosts() {
        List<HostBo> hostBoList = new ArrayList<>();
        HostBo hostBo = new HostBo();
        hostBo.setDomain("test.com");

        HostBo hostBo2 = new HostBo();
        hostBo2.setDomain("test2.com");
        hostBoList.add(hostBo);
        hostBoList.add(hostBo2);
        HostsService.deleteDomainsConfig(hostBoList);
    }


}

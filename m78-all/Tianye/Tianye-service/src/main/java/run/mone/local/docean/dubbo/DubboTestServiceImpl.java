package run.mone.local.docean.dubbo;

import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.local.docean.api.service.DubboTestService;

//@Service(group = "dev", version = "1.0", interfaceClass = DubboTestService.class)
@Slf4j
public class DubboTestServiceImpl implements DubboTestService {

    private static final Logger logger = LoggerFactory.getLogger(DubboTestServiceImpl.class);

    @Override
    public String test() {
        return "test: " + System.currentTimeMillis();
    }
}

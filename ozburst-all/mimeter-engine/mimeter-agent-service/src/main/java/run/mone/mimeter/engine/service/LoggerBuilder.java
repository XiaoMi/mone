package run.mone.mimeter.engine.service;

import com.xiaomi.sautumn.api.bo.ResourceKey;
import com.xiaomi.sautumn.api.bo.ResourceType;
import com.xiaomi.sautumn.api.service.ResourceService;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.Data;
import org.slf4j.Logger;

import javax.annotation.Resource;

@Component
@Data
public class LoggerBuilder {

    @Resource
    private ResourceService resourceService;

    private Logger logger;

    public void init(){
        ResourceKey customLogKey = new ResourceKey("customLogKey", ResourceType.customLog);
        logger = resourceService.getResource(customLogKey);
    }

}

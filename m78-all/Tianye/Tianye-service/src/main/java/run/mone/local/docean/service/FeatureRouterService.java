package run.mone.local.docean.service;

import com.google.common.base.Preconditions;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;
import run.mone.m78.api.FeatureRouterProvider;
import run.mone.m78.api.bo.feature.router.FeatureRouterDTO;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/1/24 16:40
 */
@Slf4j
@Service
public class FeatureRouterService {

    @Reference(interfaceClass = FeatureRouterProvider.class, group = "${dubbo.group}", version = "${dubbo.version}", timeout = 30000, check = false)
    private FeatureRouterProvider featureRouterProvider;

    public FeatureRouterDTO getFeatureRouterDetail(Long id) {
        Preconditions.checkArgument(id != null, "须传递id");
        FeatureRouterDTO featureRouterDetailById = featureRouterProvider.getFeatureRouterDetailById(id);
        log.info("getFeatureRouterDetail, id:{}, res:{}", id, featureRouterDetailById);
        return featureRouterDetailById;
    }
}

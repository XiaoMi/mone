package run.mone.m78.api;

import org.apache.commons.lang3.tuple.Pair;
import run.mone.m78.api.bo.feature.router.FeatureRouterDTO;
import run.mone.m78.api.bo.feature.router.FeatureRouterReq;

import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/29/24 11:11 AM
 */
public interface FeatureRouterProvider {

    Pair<Long, List<FeatureRouterDTO>> listAllFeatureRouter(FeatureRouterReq req);

    FeatureRouterDTO getFeatureRouterDetailById(Long id);
}

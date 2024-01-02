package run.mone.mimeter.dashboard.service.openservice;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.faas.func.api.MimeterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import run.mone.mimeter.dashboard.bo.scene.SceneTaskAppsBo;
import run.mone.mimeter.dashboard.common.util.Util;
import run.mone.mimeter.dashboard.service.MonitorInfoService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@DubboService(group = "${dubbo.group}", version = "${dubbo.version}")
@Slf4j
public class MonitorInfoServiceImpl implements MonitorInfoService {

    private static final Gson gson = Util.getGson();
    @DubboReference(check = false,group = "${ref.hera.service.group}",timeout = 3000)
    private MimeterService mimeterService;


    @Override
    public List<String> getAppListByReportID(Integer sceneId, String reportId) {
        List<String> appList = new ArrayList<>();

        String flag = sceneId + "_" + reportId;
        com.xiaomi.youpin.infra.rpc.Result res;
        try {
            res = mimeterService.getApps(flag);
        } catch (Exception e) {
            return appList;
        }
        if (res.getCode() == 0 && res.getData() != null) {
            //succ
            SceneTaskAppsBo sceneTaskAppsBo = gson.fromJson(res.getData().toString(), new TypeToken<SceneTaskAppsBo>() {
            }.getType());
            List<SceneTaskAppsBo.LinkTaskAppsBo> linkTaskAppsBos = sceneTaskAppsBo.getSerialLinks();
            linkTaskAppsBos.forEach(linkTaskAppsBo -> {
                List<List<String>> apps =linkTaskAppsBo.getApis().stream().map(SceneTaskAppsBo.ApiTaskAppsBo::getApps).collect(Collectors.toList());
                apps.forEach(appList::addAll);
            });
//            List<SceneTaskAppsBo.ApiTaskAppsBo> apiTaskAppsBos = linkTaskAppsBos.stream().map(SceneTaskAppsBo.LinkTaskAppsBo::getApis);
        }
        return appList;
    }
}

package run.mone.mimeter.dashboard.service;

import com.alibaba.nacos.api.exception.NacosException;
import run.mone.mimeter.dashboard.bo.DubboService;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scene.DefaultSceneInfo;
import run.mone.mimeter.dashboard.bo.scene.DubboApiInfoDTO;
import run.mone.mimeter.dashboard.bo.scene.HttpApiInfoDTO;
import run.mone.mimeter.dashboard.bo.sceneapi.GetApiDetailReq;
import run.mone.mimeter.dashboard.bo.sceneapi.SceneApiInfoItemBasic;

import java.util.List;

public interface SceneApiService {
    Result<Object> searchApiFromMiApi(String keyword, Integer apiProtocol);

    Result<Object> getApiDetailFromMiApi(GetApiDetailReq getApiDetailReq);

    Result<DefaultSceneInfo> getSceneBasicInfoFromApiID(GetApiDetailReq req);

    boolean newHttpSceneApis(List<HttpApiInfoDTO> httpApiInfoDTOS, int sceneId,int serialLinkId);

    boolean updateHttpSceneApi(HttpApiInfoDTO httpApiInfoDTO);

    boolean updateDubboSceneApi(DubboApiInfoDTO dubboApiInfoDTO);

    boolean newDubboSceneApis(List<DubboApiInfoDTO> dubboApiInfoDTOS, int sceneId,int serialLinkId);

    boolean deleteSceneApisBySceneId(int sceneId);

    boolean deleteSceneApisBySerialLinkId(int serialLinkId);

    Result<List<DubboService>> loadDubboApiServices(String keyword, String env);

    Result<List<String>> getServiceMethod(String serviceName, String env) throws NacosException;

    List<SceneApiInfoItemBasic> getBasicInfosByIds(List<Integer> apiIdList);

    Result<String> getApiUrlById(Integer apiId);

}

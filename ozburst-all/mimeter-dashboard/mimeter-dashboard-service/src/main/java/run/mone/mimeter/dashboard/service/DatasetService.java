package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.dataset.*;
import run.mone.mimeter.dashboard.pojo.Dataset;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public interface DatasetService {

    /**
     * sla
     */
    Result<Integer> newDataset(DatasetDto param);

    Result<List<Integer>> multiNewDataset(List<DatasetDto> param);

    Result<Boolean> updateDataset(DatasetDto param);

    Result<Boolean> multiUpdateDataset(List<DatasetDto> param);

    Result<DatasetList> getDatasetList(GetDatasetListReq req);

    Result<List<Dataset>> getDatasetListByIds(List<Integer> datasetIds);

    Result<DatasetDto> getDatasetById(int id);

    Result<Boolean> delDataset(int id);

    Result<Boolean> multiDelDataset(List<Integer> ids);

    Result<Integer> bindScene(int datasetId, int sceneId);

    Result<Boolean> bindDataSetsByScene(List<Integer> datasetIds, Set<Integer> refDatasetIds, int sceneId);

    Result<Integer> unbindScene(int datasetId, int sceneId);

    Result<PreviewFileRes> filePreview(int id);

    Result<List<ParamData>> getParamDataByIds(List<Integer> ids);

    Result<List<ParamData>> getParamDataBySceneId(Integer sceneId);

    List<Integer> getDatasetIdsBySceneId(Integer sceneId);

    Result<SceneFileDetailRes> sceneFileDetail(Integer sceneId);

    TreeMap<String,List<String>> getParamDataMap(List<DatasetLinesReq> reqList);

    boolean syncTenant();

}

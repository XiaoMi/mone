package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.dataset.DatasetLineNum;
import run.mone.mimeter.dashboard.bo.dataset.DatasetLinesReq;

import java.util.List;
import java.util.TreeMap;

public interface DatasetInfoSercice {

    Result<List<DatasetLineNum>> getLineNumBySceneId(Integer sceneID);

    Result<TreeMap<String,List<String>>> getDatasetMap(List<DatasetLinesReq> linesReqs);

    Result<Boolean> syncTenant();

    Result<Boolean> updateDatasetTenant(Integer datasetId,String tenant);

}

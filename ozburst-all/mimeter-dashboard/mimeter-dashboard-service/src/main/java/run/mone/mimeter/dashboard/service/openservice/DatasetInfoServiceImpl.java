package run.mone.mimeter.dashboard.service.openservice;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.dataset.DatasetLineNum;
import run.mone.mimeter.dashboard.bo.dataset.DatasetLinesReq;
import run.mone.mimeter.dashboard.mapper.DatasetMapper;
import run.mone.mimeter.dashboard.pojo.Dataset;
import run.mone.mimeter.dashboard.service.DatasetInfoSercice;
import run.mone.mimeter.dashboard.service.DatasetService;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@DubboService(group = "${dubbo.group}",version = "${dubbo.version}",timeout = 10000)
public class DatasetInfoServiceImpl implements DatasetInfoSercice {

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private DatasetMapper datasetMapper;

    private static final int MAX_DUBBO_DATA = 8*1024;

    @Override
    public Result<List<DatasetLineNum>> getLineNumBySceneId(Integer sceneID) {
        List<DatasetLineNum> datasetLineNums = new ArrayList<>();

        List<Integer> datasetsIds = datasetService.getDatasetIdsBySceneId(sceneID);

        if (datasetsIds == null || datasetsIds.size() == 0) {
            return Result.success(datasetLineNums);
        }
        List<Dataset> datasets = datasetService.getDatasetListByIds(datasetsIds).getData();
        datasets.forEach(dataset -> {
            DatasetLineNum datasetLineNum = new DatasetLineNum();
            datasetLineNum.setDatasetId(dataset.getId());
            datasetLineNum.setFileName(dataset.getFileName());
            datasetLineNum.setFileUrl(dataset.getFileUrl());
            datasetLineNum.setFileKsKey(dataset.getFileKsKey());
            datasetLineNum.setFileRaw(dataset.getFileRows());
            datasetLineNum.setDefaultParamName(dataset.getDefaultParamName());
            datasetLineNum.setIgnoreFirstLine(dataset.getIgnoreFirstRow() == 1);
            datasetLineNums.add(datasetLineNum);
        });
        return Result.success(datasetLineNums);
    }

    @Override
    public Result<TreeMap<String, List<String>>> getDatasetMap(List<DatasetLinesReq> linesReqs) {
        TreeMap<String, List<String>> dataRes = datasetService.getParamDataMap(linesReqs);
//        if (dataRes.toString().getBytes().length >= MAX_DUBBO_DATA){
//            return Result.fail(CommonError.DubboDataTooLong);
//        }
        return Result.success(dataRes);
    }

    @Override
    public Result<Boolean> syncTenant() {
        return Result.success(datasetService.syncTenant());
    }

    @Override
    public Result<Boolean> updateDatasetTenant(Integer datasetId, String tenant) {
        Dataset dataset = datasetMapper.selectByPrimaryKey(datasetId);
        dataset.setTenant(tenant);
        datasetMapper.updateByPrimaryKey(dataset);
        return Result.success(true);
    }
}

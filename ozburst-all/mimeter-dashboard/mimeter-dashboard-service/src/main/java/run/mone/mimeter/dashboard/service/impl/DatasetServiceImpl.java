package run.mone.mimeter.dashboard.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.tpc.api.service.UserOrgFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.dataset.*;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.mapper.DatasetMapper;
import run.mone.mimeter.dashboard.mapper.DatasetSceneRelationMapper;
import run.mone.mimeter.dashboard.pojo.*;
import run.mone.mimeter.dashboard.service.DatasetService;
import run.mone.mimeter.dashboard.service.SceneService;

import java.util.*;
import java.util.stream.Collectors;

import static run.mone.mimeter.dashboard.bo.common.Constants.DEFAULT_PAGE_SIZE;
import static run.mone.mimeter.dashboard.service.impl.UploadService.PreviewFileRowNum;

@Service
@Slf4j
public class DatasetServiceImpl implements DatasetService {

    private static final Gson gson = new Gson();

    @Autowired
    DatasetMapper datasetMapper;

    @Autowired
    DatasetSceneRelationMapper datasetSceneRelationMapper;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private SceneService sceneService;

    @DubboReference(registry = "stRegistry",check = false,group = "staging-open",version = "1.0")
    private UserOrgFacade userOrgFacade;

    @Override
    public Result<Integer> newDataset(DatasetDto param) {
        Pair<Integer, String> checkRes = checkParam(param);
        if (checkRes.getKey() != 0) {
            return Result.fail(CommonError.InvalidParamError.code, checkRes.getValue());
        }

        Dataset dataset = toDataset(param);
        //添加租户信息
        if (param.getTenant() != null) {
            dataset.setTenant(param.getTenant());
        }
        datasetMapper.insert(dataset);
        return Result.success(dataset.getId());
    }

    @Override
    public Result<List<Integer>> multiNewDataset(List<DatasetDto> param) {
        for (DatasetDto perParam : param) {
            Pair<Integer, String> checkRes = checkParam(perParam);
            if (checkRes.getKey() != 0) {
                return Result.fail(CommonError.InvalidParamError.code, checkRes.getValue());
            }
        }
        List<Dataset> datasets = param.stream().map(it -> {
            Dataset dataset = toDataset(it);
            //添加租户信息
            if (it.getTenant() != null) {
                dataset.setTenant(it.getTenant());
            }
            return dataset;
        }).collect(Collectors.toList());
        datasetMapper.batchInsert(datasets);
        return Result.success(datasets.stream().map(Dataset::getId).collect(Collectors.toList()));
    }

    @Override
    public Result<Boolean> updateDataset(DatasetDto param) {
        Pair<Integer, String> checkRes = checkParam(param);
        if (checkRes.getKey() != 0) {
            return Result.fail(CommonError.InvalidParamError.code, checkRes.getValue());
        }

        Dataset dataset = toDataset(param);
        //兼容，早期的数据源没有租户信息，这里在更新的时候补充进去
//        if (param.getTenant() != null && dataset.getTenant() == null) {
//            dataset.setTenant(param.getTenant());
//        }
        datasetMapper.updateByPrimaryKeyWithBLOBs(dataset);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> multiUpdateDataset(List<DatasetDto> param) {
        for (DatasetDto perParam : param) {
            Pair<Integer, String> checkRes = checkParam(perParam);
            if (checkRes.getKey() != 0) {
                return Result.fail(CommonError.InvalidParamError.code, checkRes.getValue());
            }
        }

        param.forEach(it -> {
            Dataset dataset = toDataset(it);
            //兼容，早期的数据源没有租户信息，这里在更新的时候补充进去
            if (it.getTenant() != null && dataset.getTenant() == null) {
                dataset.setTenant(it.getTenant());
            }
            datasetMapper.updateByPrimaryKeyWithBLOBs(dataset);
        });
        return Result.success(true);
    }

    @Override
    public Result<DatasetList> getDatasetList(GetDatasetListReq req) {

        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(DEFAULT_PAGE_SIZE);
        }
        int offset = (req.getPage() - 1) * req.getPageSize();

        DatasetExample totalDatasetExample = new DatasetExample();
        DatasetExample.Criteria totalCriteria = totalDatasetExample.createCriteria();
        DatasetExample datasetExample = new DatasetExample();

        DatasetExample.Criteria criteria = datasetExample.createCriteria();

        if (StringUtils.isNotEmpty(req.getDatasetName())) {
            totalCriteria.andNameLike("%" + req.getDatasetName() + "%");
            criteria.andNameLike("%" + req.getDatasetName() + "%");
        }
        if (StringUtils.isNotEmpty(req.getCreator())) {
            totalCriteria.andCreatorLike("%" + req.getCreator() + "%");
            criteria.andCreatorLike("%" + req.getCreator() + "%");
        }
        if (req.getType() != null && req.getType() > 0) {
            totalCriteria.andTypeEqualTo(req.getType());
            criteria.andTypeEqualTo(req.getType());
        }

        datasetExample.setOrderByClause("id desc limit " + req.getPageSize() + " offset " + offset);

        DatasetList datasetList = new DatasetList();
        datasetList.setPage(req.getPage());
        datasetList.setPageSize(req.getPageSize());
        List<Dataset> datasets = datasetMapper.selectByExampleWithBLOBs(datasetExample);
        if (datasets == null || datasets.size() == 0) {
            return Result.success(datasetList);
        }
        List<DatasetDto> datasetDtos = datasets.stream().map(this::toDatasetDto).collect(Collectors.toList());

        //添加引用场景字段
        List<Integer> datasetIds = datasetDtos.stream().map(DatasetDto::getId).collect(Collectors.toList());
        Map<Integer, List<Integer>> datasetIdToSceneIds = getSceneIdsByDatasetIds(datasetIds);
        Set<Integer> sceneIdSet = new HashSet<>();
        datasetIdToSceneIds.forEach((key, value) -> sceneIdSet.addAll(value));
        if (sceneIdSet.size() > 0) {
            List<SceneInfo> scenes = sceneService.getSceneInfoByIds(new ArrayList<>(sceneIdSet)).getData();
            Map<Integer, String> sceneIdName = scenes.stream().collect(Collectors.toMap(SceneInfo::getId, SceneInfo::getName));
            datasetDtos.forEach(it -> {
                Integer datasetId = it.getId();
                List<Integer> sceneIds = datasetIdToSceneIds.get(datasetId);
                if (sceneIds != null && sceneIds.size() > 0) {
                    Map<Integer, String> perSceneIdName = sceneIds.stream().filter(it1 -> sceneIdName.get(it1) != null)
                            .collect(Collectors.toMap(it1 -> it1, sceneIdName::get));

                    it.setBindScenes(perSceneIdName);
                }
            });
        }

        datasetList.setList(datasetDtos);
        datasetList.setTotal(datasetMapper.countByExample(totalDatasetExample));

        return Result.success(datasetList);
    }

    @Override
    public Result<List<Dataset>> getDatasetListByIds(List<Integer> datasetIds) {
        List<Dataset> datasetList = new ArrayList<>();
        if (datasetIds != null && datasetIds.size() != 0) {
            DatasetExample example = new DatasetExample();
            example.createCriteria().andIdIn(datasetIds);
            datasetList = datasetMapper.selectByExampleWithBLOBs(example);
        }
        return Result.success(datasetList);
    }

    @Override
    public Result<DatasetDto> getDatasetById(int id) {
        Dataset dataset = datasetMapper.selectByPrimaryKey(id);
        DatasetDto datasetDto = toDatasetDto(dataset);
        return Result.success(datasetDto);
    }

    @Override
    public Result<Boolean> delDataset(int id) {
        DatasetSceneRelationExample relationExample = new DatasetSceneRelationExample();
        DatasetSceneRelationExample.Criteria criteria = relationExample.createCriteria();
        criteria.andDatasetIdEqualTo(id);
        List<DatasetSceneRelation> relations = datasetSceneRelationMapper.selectByExample(relationExample);
        if (relations != null && relations.size() > 0) {
            //多校验一步，看一下关联的场景到底还存不存在
            List<SceneInfo> scenes = sceneService.getSceneInfoByIds(relations.stream().map(DatasetSceneRelation::getSceneId).collect(Collectors.toList())).getData();
            if (scenes != null && scenes.size() > 0) {
                return Result.fail(CommonError.DeleteFileBindingSceneError);
            }
        }

        datasetMapper.deleteByPrimaryKey(id);
        datasetSceneRelationMapper.deleteByExample(relationExample);

        return Result.success(true);
    }

    @Override
    public Result<Boolean> multiDelDataset(List<Integer> ids) {
        DatasetSceneRelationExample relationExample = new DatasetSceneRelationExample();
        DatasetSceneRelationExample.Criteria criteria = relationExample.createCriteria();
        criteria.andDatasetIdIn(ids);
        List<DatasetSceneRelation> relations = datasetSceneRelationMapper.selectByExample(relationExample);
        if (relations != null && relations.size() > 0) {
            //多校验一步，看一下关联的场景到底还存不存在
            List<SceneInfo> scenes = sceneService.getSceneInfoByIds(relations.stream().map(DatasetSceneRelation::getSceneId).collect(Collectors.toList())).getData();
            if (scenes != null && scenes.size() > 0) {
                return Result.fail(CommonError.DeleteFileBindingSceneError);
            }
        }

        DatasetExample example = new DatasetExample();
        example.createCriteria().andIdIn(ids);
        datasetMapper.deleteByExample(example);
        datasetSceneRelationMapper.deleteByExample(relationExample);

        return Result.success(true);
    }

    @Override
    public Result<Integer> bindScene(int datasetId, int sceneId) {
        long now = System.currentTimeMillis();
        DatasetSceneRelation datasetSceneRelation = new DatasetSceneRelation();
        datasetSceneRelation.setDatasetId(datasetId);
        datasetSceneRelation.setSceneId(sceneId);
        datasetSceneRelation.setCtime(now);
        datasetSceneRelation.setUtime(now);
        datasetSceneRelation.setEnable(false);
        datasetSceneRelationMapper.insert(datasetSceneRelation);
        return Result.success(datasetSceneRelation.getId());
    }

    @Override
    public Result<Boolean> bindDataSetsByScene(List<Integer> datasetIds, Set<Integer> refDatasetIds, int sceneId) {
        List<Integer> toBeAdded = new ArrayList<>();
        List<Integer> toBeRemoved = new ArrayList<>();
        List<Integer> toBeUpdated = new ArrayList<>();

        DatasetSceneRelationExample example = new DatasetSceneRelationExample();
        example.createCriteria().andSceneIdEqualTo(sceneId);
        List<DatasetSceneRelation> relations = datasetSceneRelationMapper.selectByExample(example);
        List<Integer> olds = relations.stream().map(DatasetSceneRelation::getDatasetId).collect(Collectors.toList());


        datasetIds.forEach(newId -> {
            if (!olds.contains(newId)) {
                toBeAdded.add(newId);
            }
        });
        olds.forEach(oldId -> {
            if (!datasetIds.contains(oldId)) {
                toBeRemoved.add(oldId);
            } else {
                toBeUpdated.add(oldId);
            }
        });

        if (toBeUpdated.size() != 0) {
            toBeUpdated.forEach(uId -> {
                DatasetSceneRelationExample relationExample = new DatasetSceneRelationExample();
                relationExample.createCriteria().andSceneIdEqualTo(sceneId).andDatasetIdEqualTo(uId);
                List<DatasetSceneRelation> relationList = datasetSceneRelationMapper.selectByExample(relationExample);
                if (relationList != null && relationList.size() != 0) {
                    DatasetSceneRelation relation = relationList.get(0);
                    if (refDatasetIds != null) {
                        relation.setEnable(refDatasetIds.contains(uId));
                    } else {
                        relation.setEnable(false);
                    }
                    datasetSceneRelationMapper.updateByPrimaryKey(relation);
                }
            });
        }

        if (toBeRemoved.size() != 0) {
            DatasetSceneRelationExample rmExp = new DatasetSceneRelationExample();
            rmExp.createCriteria().andDatasetIdIn(toBeRemoved).andSceneIdEqualTo(sceneId);
            datasetSceneRelationMapper.deleteByExample(rmExp);
        }

        long now = System.currentTimeMillis();
        if (toBeAdded.size() != 0) {
            List<DatasetSceneRelation> relationList = toBeAdded.stream().map(it -> {
                DatasetSceneRelation datasetSceneRelation = new DatasetSceneRelation();
                datasetSceneRelation.setDatasetId(it);
                datasetSceneRelation.setSceneId(sceneId);
                datasetSceneRelation.setCtime(now);
                datasetSceneRelation.setUtime(now);
                if (refDatasetIds != null) {
                    datasetSceneRelation.setEnable(refDatasetIds.contains(it));
                } else {
                    datasetSceneRelation.setEnable(false);
                }
                return datasetSceneRelation;
            }).collect(Collectors.toList());
            datasetSceneRelationMapper.batchInsert(relationList);
        }


        return Result.success(true);
    }

    @Override
    public Result<Integer> unbindScene(int datasetId, int sceneId) {
        DatasetSceneRelationExample relationExample = new DatasetSceneRelationExample();
        DatasetSceneRelationExample.Criteria criteria = relationExample.createCriteria();
        criteria.andDatasetIdEqualTo(datasetId).andSceneIdEqualTo(sceneId);
        List<DatasetSceneRelation> relationList = datasetSceneRelationMapper.selectByExample(relationExample);
        if (relationList != null && relationList.size() != 0) {
            if (relationList.get(0).getEnable()) {
                return Result.fail(CommonError.UnbindRefDataset);
            }
            int res = datasetSceneRelationMapper.deleteByPrimaryKey(relationList.get(0).getId());
            return Result.success(res);
        }
        return Result.fail(CommonError.InvalidParamError);
    }

    @Override
    public Result<PreviewFileRes> filePreview(int id) {
        Dataset dataset = datasetMapper.selectByPrimaryKey(id);
        List<String> lines = uploadService.loadFileByRange(dataset, 0, PreviewFileRowNum).getData();

        PreviewFileRes previewFileRes = new PreviewFileRes();
        previewFileRes.setPreviewFileRows(lines);
        previewFileRes.setFileRows(dataset.getFileRows());
        previewFileRes.setFileName(dataset.getFileName());

        return Result.success(previewFileRes);
    }

    @Override
    public Result<List<ParamData>> getParamDataByIds(List<Integer> ids) {
        List<ParamData> paramDatas = new ArrayList<>();
        if (ids == null || ids.size() == 0) {
            return Result.success(paramDatas);
        }

        DatasetExample datasetExample = new DatasetExample();
        DatasetExample.Criteria criteria = datasetExample.createCriteria();
        criteria.andIdIn(ids);
        List<Dataset> list = datasetMapper.selectByExampleWithBLOBs(datasetExample);

        if (list == null || list.size() == 0) {
            return Result.success(paramDatas);
        }

        list.stream().filter(it -> StringUtils.isNotEmpty(it.getDefaultParamName())).forEach(it -> {
            List<String> params = Arrays.asList(it.getDefaultParamName().split(","));
            for (int i = 0; i < params.size(); i++) {

                ParamData paramData = new ParamData();
                paramData.setColumnIndex(i + 1);
                paramData.setDatasetId(it.getId());
                paramData.setDatasetName(it.getName());
                paramData.setParamName(params.get(i));
                paramDatas.add(paramData);
            }
        });

        return Result.success(paramDatas);
    }

    @Override
    public Result<List<ParamData>> getParamDataBySceneId(Integer sceneId) {
        List<Integer> datasetsIds = getDatasetIdsBySceneId(sceneId);
        return getParamDataByIds(datasetsIds);
    }

    @Override
    public List<Integer> getDatasetIdsBySceneId(Integer sceneId) {
        DatasetSceneRelationExample relationExample = new DatasetSceneRelationExample();
        DatasetSceneRelationExample.Criteria criteria = relationExample.createCriteria();
        criteria.andSceneIdEqualTo(sceneId);
        List<DatasetSceneRelation> relations = datasetSceneRelationMapper.selectByExample(relationExample);

        return relations.stream().map(DatasetSceneRelation::getDatasetId).collect(Collectors.toList());
    }

    @Override
    public Result<SceneFileDetailRes> sceneFileDetail(Integer sceneId) {
        SceneFileDetailRes sceneFileDetailRes = new SceneFileDetailRes();

        List<Integer> datasetIds = getDatasetIdsBySceneId(sceneId);
        if (datasetIds == null || datasetIds.size() == 0) {
            return Result.success(sceneFileDetailRes);
        }

        List<Dataset> datasets = getDatasetListByIds(datasetIds).getData();
        List<DatasetDto> datasetDtos = datasets.stream().map(this::toDatasetDto).collect(Collectors.toList());
        List<ParamData> paramDatas = getParamDataByIds(datasetIds).getData();

        sceneFileDetailRes.setDatasetLists(datasetDtos);
        sceneFileDetailRes.setParamDataList(paramDatas);

        return Result.success(sceneFileDetailRes);
    }

    @Override
    public TreeMap<String, List<String>> getParamDataMap(List<DatasetLinesReq> reqList) {
        TreeMap<String, List<String>> dataRes = new TreeMap<>();
        List<Integer> datasetIds = reqList.stream().map(DatasetLinesReq::getDatasetId).collect(Collectors.toList());
        List<Dataset> datasets = getDatasetListByIds(datasetIds).getData();
        Map<Integer, Dataset> datasetMap = datasets.stream().collect(Collectors.toMap(Dataset::getId, it -> it));
        reqList.forEach(req -> {
            List<String> paramNames = Arrays.asList(req.getDefaultParamName().split(","));
            Dataset dataset = datasetMap.get(req.getDatasetId());
            List<String[]> lines = null;
            try {
                lines = uploadService.loadStringArrByRange(dataset, req.getFrom(), req.getTo()).getData();
            } catch (Exception e) {
                log.error("loadStringArrByRange error,datasetId:{},fileUrl:{},ksKey:{},paramNames:{},from:{},to:{},error:{}",req.getDatasetId(),req.getFileUrl(),req.getFileKsKey(),req.getDefaultParamName(),req.getFrom(),req.getTo(),e.getMessage());
            }
            paramNames.forEach(paramName -> dataRes.put(paramName, new ArrayList<>()));
            lines.forEach(line -> {
                for (int i = 0; i < paramNames.size(); i++) {
                    dataRes.get(paramNames.get(i)).add(line[i]);
                }
            });
        });
        log.info("get getParamDataMap dataRes size:{}",dataRes.size());
        return dataRes;
    }

    private Pair<Integer, String> checkParam(DatasetDto datasetDto) {
        List<Integer> datasetTypes = DatasetTypeEnum.getDatasetTypes();
        if (datasetDto.getType() == null) {
            return Pair.of(-1, "数据源类型必填");
        }
        if (datasetDto.getIgnoreFirstRow() == null) {
            datasetDto.setIgnoreFirstRow(0);
        }

        int type = datasetDto.getType();
        if (!datasetTypes.contains(type)) {
            return Pair.of(-1, "不存在的数据源类型");
        }
        if (datasetDto.getType() == DatasetTypeEnum.FileUpload.typeCode) {
            if (StringUtils.isEmpty(datasetDto.getDefaultParamName())) {
                return Pair.of(-1, "文件类型数据源参数名必填");
            }
            if (StringUtils.isEmpty(datasetDto.getDefaultParamName())) {
                return Pair.of(-1, "文件参数名不允许为空");
            } else {
                String[] paramNameArr = datasetDto.getDefaultParamName().split(",");
                if (Arrays.asList(paramNameArr).stream().anyMatch(it -> StringUtils.isEmpty(it))) {
                    return Pair.of(-1, "文件参数名不允许为空");
                }
            }
        }

        return Pair.of(0, "success");
    }

    private List<Integer> getSceneIdsByDatasetId(Integer datasetId) {
        DatasetSceneRelationExample relationExample = new DatasetSceneRelationExample();
        DatasetSceneRelationExample.Criteria criteria = relationExample.createCriteria();
        criteria.andDatasetIdEqualTo(datasetId);
        List<DatasetSceneRelation> relations = datasetSceneRelationMapper.selectByExample(relationExample);

        return relations.stream().map(DatasetSceneRelation::getSceneId).collect(Collectors.toList());
    }

    private Map<Integer, List<Integer>> getSceneIdsByDatasetIds(List<Integer> ids) {
        DatasetSceneRelationExample relationExample = new DatasetSceneRelationExample();
        DatasetSceneRelationExample.Criteria criteria = relationExample.createCriteria();
        criteria.andDatasetIdIn(ids);
        List<DatasetSceneRelation> relations = datasetSceneRelationMapper.selectByExample(relationExample);

        Map<Integer, List<Integer>> map = new HashMap<>();
        ids.stream().forEach(it -> map.put(it, new ArrayList<>()));
        relations.stream().forEach(it -> {
            map.get(it.getDatasetId()).add(it.getSceneId());
        });

        return map;
    }

    private Dataset toDataset(DatasetDto param) {
        Dataset dataset = new Dataset();
        BeanUtils.copyProperties(param, dataset);
        if (param.getPreviewFileRows() != null && param.getPreviewFileRows().size() != 0) {
            dataset.setPreviewFileRows(gson.toJson(param.getPreviewFileRows()));
        }
        if (param.getHeader() != null && param.getHeader().size() != 0) {
            dataset.setHeader(gson.toJson(param.getHeader()));
        }
        long now = System.currentTimeMillis();
        if (dataset.getCtime() == null || dataset.getCtime() == 0) {
            dataset.setCtime(now);
        }
        dataset.setUtime(now);

        return dataset;
    }

    private DatasetDto toDatasetDto(Dataset dataset) {
        DatasetDto datasetDto = new DatasetDto();
        BeanUtils.copyProperties(dataset, datasetDto);
        if (StringUtils.isNotEmpty(dataset.getPreviewFileRows())) {
            datasetDto.setPreviewFileRows(gson.fromJson(dataset.getPreviewFileRows(), new TypeToken<List<String>>() {
            }.getType()));
        }
        if (StringUtils.isNotEmpty(dataset.getHeader())) {
            datasetDto.setHeader(gson.fromJson(dataset.getHeader(), new TypeToken<Map<String, String>>() {
            }.getType()));
        }
        return datasetDto;
    }

    @Override
    public boolean syncTenant() {
//        DatasetExample datasetExample = new DatasetExample();
//        datasetExample.createCriteria().andIdIsNotNull();
//        List<Dataset> datasetList = datasetMapper.selectByExample(datasetExample);
//        datasetList.forEach(dataset -> {
//            NullParam param  = new NullParam();
//            param.setAccount(dataset.getUpdater());
//            param.setUserType(0);
//            OrgInfoVo orgInfoVo = userOrgFacade.getOrgByAccount(param).getData();
//            dataset.setTenant(orgInfoVo.getIdPath());
//            datasetMapper.updateByPrimaryKey(dataset);
//        });
        return true;
    }
}

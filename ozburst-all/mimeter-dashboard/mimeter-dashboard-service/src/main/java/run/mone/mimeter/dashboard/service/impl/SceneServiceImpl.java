package run.mone.mimeter.dashboard.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import run.mone.mimeter.dashboard.bo.agent.AgentDTO;
import run.mone.mimeter.dashboard.bo.common.Constants;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.dataset.SceneParamData;
import run.mone.mimeter.dashboard.bo.operationlog.OperationLogDto;
import run.mone.mimeter.dashboard.bo.operationlog.PerOperation;
import run.mone.mimeter.dashboard.bo.scene.*;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.sceneapi.ApiTrafficInfo;
import run.mone.mimeter.dashboard.bo.sceneapi.ApiX5Info;
import run.mone.mimeter.dashboard.bo.sceneapi.CheckPointInfoDTO;
import run.mone.mimeter.dashboard.bo.sceneapi.SceneApiOutputParam;
import run.mone.mimeter.dashboard.bo.scenegroup.GetSceneGroupListReq;
import run.mone.mimeter.dashboard.bo.scenegroup.GroupSceneDTO;
import run.mone.mimeter.dashboard.bo.scenegroup.SceneGroupList;
import run.mone.mimeter.dashboard.bo.sla.SlaDto;
import run.mone.mimeter.dashboard.common.TaskStatus;
import run.mone.mimeter.dashboard.common.util.Util;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.exception.CommonException;
import run.mone.mimeter.dashboard.mapper.*;
import run.mone.mimeter.dashboard.pojo.*;
import run.mone.mimeter.dashboard.service.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static run.mone.mimeter.dashboard.bo.common.Constants.*;
import static run.mone.mimeter.dashboard.bo.operationlog.OperationLogTypeEnum.NEW_OPERATION;
import static run.mone.mimeter.dashboard.bo.operationlog.OperationLogTypeEnum.UPDATE_OPERATION;
import static run.mone.mimeter.dashboard.bo.operationlog.OperationTypeEnum.COPY_SNAPSHOT;
import static run.mone.mimeter.dashboard.bo.operationlog.OperationTypeEnum.VIEW_SNAPSHOT;
import static run.mone.mimeter.dashboard.bo.snapshot.SnapshotTypeEnum.SCENE_SNAPSHOT;

@Service
@Slf4j
public class SceneServiceImpl implements SceneService {

    @Autowired
    private SceneInfoMapper sceneInfoMapper;

    @Autowired
    private SceneSnapshotService sceneSnapshotService;

    @Autowired
    private SlaService slaService;

    @Autowired
    private SceneApiService sceneApiService;

    @Autowired
    private SceneApiInfoMapper sceneApiInfoMapper;

    @Autowired
    private SerialLinkMapper serialLinkMapper;

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private SceneGroupMapper sceneGroupMapper;
    /**
     * 操作记录
     */
    @Autowired
    private OperationLogMapper logMapper;

    @Autowired
    private AgentInfoMapper agentInfoMapper;

    private static final Gson gson = Util.getGson();

    private static final Integer SCENE_REQ_TIMEOUT = 1000;

    /**
     * 创建压测场景
     * 1、创建场景实体插表
     * 2、json保存接口施压配置
     * 3、数据集处理
     * 4、分类型处理api接口结构，创建链路，插表
     * 5、创建sla，插表
     * 6、数据集处理
     * 7、操作记录和场景快照
     * ...
     *
     * @param createSceneReq 创建场景所需数据
     * @param opUser         创建人
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<SceneDTO> newScene(CreateSceneDTO createSceneReq, String opUser) {

        Pair<Integer, String> checkRes = checkSceneParam(createSceneReq);
        if (checkRes.getKey() != 0) {
            throw new CommonException(CommonError.InvalidParamError.code, checkRes.getValue());
        }
        SceneInfoExample oldExp = new SceneInfoExample();
        oldExp.createCriteria().andNameEqualTo(createSceneReq.getName());
        List<SceneInfo> olds = sceneInfoMapper.selectByExample(oldExp);
        if (olds != null && olds.size() != 0) {
            return Result.fail(CommonError.SceneAlreadyExist);
        }
        SceneInfo sceneInfo;
        try {
            sceneInfo = new SceneInfo();
            BeanUtils.copyProperties(createSceneReq, sceneInfo);
            sceneInfo.setCreator(opUser);
            sceneInfo.setUpdator(opUser);
            sceneInfo.setSceneGroupId(createSceneReq.getGroupID());
            long now = System.currentTimeMillis();
            sceneInfo.setCtime(now);
            sceneInfo.setUtime(now);
            sceneInfo.setSceneStatus(TaskStatus.Init.code);
            if (createSceneReq.getGlobalHeaderList() != null && createSceneReq.getGlobalHeaderList().size() != 0) {
                sceneInfo.setGlobalHeader(gson.toJson(createSceneReq.getGlobalHeaderList()));
            } else {
                sceneInfo.setGlobalHeader("[]");
            }
            if (createSceneReq.getGroupID() == null) {
                sceneInfo.setSceneGroupId(0);
            } else {
                sceneInfo.setSceneGroupId(createSceneReq.getGroupID());
            }
            //施压配置中的接口数据配置
            sceneInfo.setApiBenchInfos(gson.toJson(createSceneReq.getApiBenchInfos()));

            //插入sla配置
            if (Objects.nonNull(createSceneReq.getSlaDto())) {
                sceneInfo.setSla(gson.toJson(createSceneReq.getSlaDto()));
            }

            //自定义成功状态码
            if (createSceneReq.getSuccessCode() != null) {
                sceneInfo.setSuccessCode(createSceneReq.getSuccessCode());
            }

            //检查施压机配置
            if (createSceneReq.getAgentIdList() != null && createSceneReq.getAgentIdList().size() != 0) {
                List<AgentDTO> agentDTOList = new ArrayList<>();
                AgentInfoExample example = new AgentInfoExample();
                example.createCriteria().andIdIn(createSceneReq.getAgentIdList());

                List<AgentInfo> agentInfos = agentInfoMapper.selectByExample(example);
                agentInfos.forEach(agentInfo -> {
                    AgentDTO agentDTO = new AgentDTO();
                    BeanUtils.copyProperties(agentInfo, agentDTO);
                    agentDTOList.add(agentDTO);
                });
                sceneInfo.setAgentList(gson.toJson(agentDTOList));
            } else {
                sceneInfo.setAgentList("[]");
            }

            //汽车部tsp接口鉴权
            if (createSceneReq.getGlobalTspAuth() != null) {
                sceneInfo.setGlobalTspAuth(gson.toJson(createSceneReq.getGlobalTspAuth()));
            } else {
                sceneInfo.setGlobalTspAuth(gson.toJson(new TspAuthInfo(false)));
            }

            if (createSceneReq.getRefDatasetIds() != null) {
                sceneInfo.setRefDatasetIds(gson.toJson(createSceneReq.getRefDatasetIds()));
            }

            //插入租户信息
            if (createSceneReq.getTenant() != null) {
                sceneInfo.setTenant(createSceneReq.getTenant());
            }

            //有效接口数
            AtomicInteger apiNum = new AtomicInteger();
            if (Constants.SCENE_TYPE_HTTP == createSceneReq.getSceneType()) {
                createSceneReq.getSerialLinkDTOs().forEach(serialLinkDTO -> apiNum.addAndGet(serialLinkDTO.getHttpApiInfoDTOList().size()));
            } else if (Constants.SCENE_TYPE_DUBBO == createSceneReq.getSceneType()) {
                createSceneReq.getSerialLinkDTOs().forEach(serialLinkDTO -> apiNum.addAndGet(serialLinkDTO.getDubboApiInfoDTOList().size()));
            }
            sceneInfo.setApinum(apiNum.get());
            //场景来源 控制台创建或openapi创建
            sceneInfo.setSceneSource(createSceneReq.getSceneSource());

            //场景负责人
            List<String> personsInCharge = new ArrayList<>();
            personsInCharge.add(opUser);
            sceneInfo.setPersonInCharge(gson.toJson(personsInCharge));

            //压测次数
            sceneInfo.setBenchCount(0);

            //上次压测时间
            sceneInfo.setLastBenchTime(0L);

            sceneInfoMapper.insert(sceneInfo);

            //插入场景api
            if (Constants.SCENE_TYPE_HTTP == createSceneReq.getSceneType()) {
                createSceneReq.getSerialLinkDTOs().forEach(serialLinkDTO -> {
                    //创建串联链路
                    SerialLink serialLink = new SerialLink();
                    serialLink.setSceneId(sceneInfo.getId());
                    serialLink.setName(serialLinkDTO.getSerialLinkName());
                    serialLink.setEnable(serialLinkDTO.getEnable());
                    serialLinkMapper.insert(serialLink);

                    serialLinkDTO.setSerialLinkID(serialLink.getId());
                    //http（||网关）类场景
                    sceneApiService.newHttpSceneApis(serialLinkDTO.getHttpApiInfoDTOList(), sceneInfo.getId(), serialLink.getId());
                });

            } else if (Constants.SCENE_TYPE_DUBBO == createSceneReq.getSceneType()) {
                createSceneReq.getSerialLinkDTOs().forEach(serialLinkDTO -> {
                    //创建串联链路
                    SerialLink serialLink = new SerialLink();
                    serialLink.setSceneId(sceneInfo.getId());
                    serialLink.setName(serialLinkDTO.getSerialLinkName());
                    serialLink.setEnable(serialLinkDTO.getEnable());
                    serialLinkMapper.insert(serialLink);
                    serialLinkDTO.setSerialLinkID(serialLink.getId());
                    //dubbo场景
                    sceneApiService.newDubboSceneApis(serialLinkDTO.getDubboApiInfoDTOList(), sceneInfo.getId(), serialLink.getId());
                });
            } else {
                return Result.fail(CommonError.InvalidParamError);
            }

            createSceneReq.setId(sceneInfo.getId());
            //绑定数据集
            datasetService.bindDataSetsByScene(createSceneReq.getDatasetIds(), new HashSet<>(createSceneReq.getRefDatasetIds().values()), sceneInfo.getId());
            this.postOperationLog(createSceneReq, true, opUser);
        } catch (Exception e) {
            log.error("new scene failed,error:{}", e.getMessage());
            throw e;
        }
        return Result.success(transferToDetailSceneDTO(sceneInfo, false));
    }

    /**
     * 删场景
     * 删场景绑定的接口
     *
     * @param sceneID
     * @param opUser
     * @return
     */
    @Override
    @Transactional
    public Result<Boolean> delScene(Integer sceneID, String opUser) {
        if (sceneInfoMapper.deleteByPrimaryKey(sceneID) > 0) {
            SceneApiInfoExample example = new SceneApiInfoExample();
            example.createCriteria().andSceneIdEqualTo(sceneID);
            sceneApiInfoMapper.deleteByExample(example);
            return Result.success(true);
        }
        return Result.fail(CommonError.UnknownError);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> editScene(EditSceneDTO editSceneReq, String opUser) {
        try {
            SceneInfo sceneInfo = sceneInfoMapper.selectByPrimaryKey(editSceneReq.getId());
            String oTenant = sceneInfo.getTenant();
            BeanUtils.copyProperties(editSceneReq, sceneInfo);
            sceneInfo.setTenant(oTenant);
            sceneInfo.setId(editSceneReq.getId());
            sceneInfo.setRpsRate(editSceneReq.getRpsRate());
            sceneInfo.setSceneGroupId(editSceneReq.getGroupID());
            long now = System.currentTimeMillis();
            sceneInfo.setUtime(now);
            //施压配置中的接口数据配置
            sceneInfo.setApiBenchInfos(gson.toJson(editSceneReq.getApiBenchInfos()));
            sceneInfo.setGlobalHeader(gson.toJson(editSceneReq.getGlobalHeaderList()));
            sceneInfo.setUpdator(opUser);

            if (editSceneReq.getGroupID() == null) {
                sceneInfo.setSceneGroupId(Constants.DEFAULT_SCENE_GROUP);
            }
            if (editSceneReq.getSceneEnv() == null) {
                sceneInfo.setSceneEnv(SCENE_ENV_ST);
            }
            if (editSceneReq.getIncreasePercent() == null) {
                sceneInfo.setIncreasePercent(0);
            }
            //插入sla配置
            if (Objects.nonNull(editSceneReq.getSlaDto())) {
                sceneInfo.setSla(gson.toJson(editSceneReq.getSlaDto()));
            }
            //检查施压机配置
            if (editSceneReq.getAgentIdList() != null && editSceneReq.getAgentIdList().size() != 0) {
                List<AgentDTO> agentDTOList = new ArrayList<>();
                AgentInfoExample example = new AgentInfoExample();
                example.createCriteria().andIdIn(editSceneReq.getAgentIdList());

                List<AgentInfo> agentInfos = agentInfoMapper.selectByExample(example);
                AtomicBoolean ok = new AtomicBoolean(true);
                agentInfos.forEach(agentInfo -> {
                    if (!agentInfo.getEnable()) {
                        ok.set(false);
                    } else {
                        AgentDTO agentDTO = new AgentDTO();
                        BeanUtils.copyProperties(agentInfo, agentDTO);
                        agentDTOList.add(agentDTO);
                    }
                });
                if (!ok.get()) {
                    return Result.fail(CommonError.UnableBenchAgent);
                }
                sceneInfo.setAgentList(gson.toJson(agentDTOList));
            } else {
                sceneInfo.setAgentList("[]");
            }

            //汽车部tsp接口鉴权
            if (editSceneReq.getGlobalTspAuth() != null) {
                sceneInfo.setGlobalTspAuth(gson.toJson(editSceneReq.getGlobalTspAuth()));
            } else {
                sceneInfo.setGlobalTspAuth(gson.toJson(new TspAuthInfo(false)));
            }

            if (editSceneReq.getSuccessCode() != null) {
                sceneInfo.setSuccessCode(editSceneReq.getSuccessCode());
            }

            if (editSceneReq.getRefDatasetIds() != null) {
                sceneInfo.setRefDatasetIds(gson.toJson(editSceneReq.getRefDatasetIds()));
            }

            //兼容，早期的场景没有租户信息，这里在更新的时候补充进去
            if (sceneInfo.getTenant() == null) {
                sceneInfo.setTenant(editSceneReq.getTenant());
            }

            //有效接口数
            AtomicInteger apiNum = new AtomicInteger();
            if (Constants.SCENE_TYPE_HTTP == editSceneReq.getSceneType()) {
                editSceneReq.getSerialLinkDTOs().forEach(serialLinkDTO -> apiNum.addAndGet(serialLinkDTO.getHttpApiInfoDTOList().size()));
            } else if (Constants.SCENE_TYPE_DUBBO == editSceneReq.getSceneType()) {
                editSceneReq.getSerialLinkDTOs().forEach(serialLinkDTO -> apiNum.addAndGet(serialLinkDTO.getDubboApiInfoDTOList().size()));
            }
            sceneInfo.setApinum(apiNum.get());
            //场景来源 控制台创建或openapi创建
            sceneInfo.setSceneSource(editSceneReq.getSceneSource());

            //场景负责人
            List<String> bindUsers = new ArrayList<>(editSceneReq.getPersonsInCharge().size());
            editSceneReq.getPersonsInCharge().forEach(user -> {
                if (user.contains("@")) {
                    user = user.substring(0, user.indexOf("@"));
                }
                bindUsers.add(user);
            });
            sceneInfo.setPersonInCharge(gson.toJson(bindUsers));

            sceneInfoMapper.updateByPrimaryKeyWithBLOBs(sceneInfo);

            SerialLinkExample linkExample = new SerialLinkExample();
            linkExample.createCriteria().andSceneIdEqualTo(editSceneReq.getId());
            //原有链路
            List<Integer> oldLinkIds = serialLinkMapper.selectByExample(linkExample).stream().map(SerialLink::getId).collect(Collectors.toList());

            removeOldLinks(oldLinkIds, editSceneReq);
            //更新场景下api列表
            if (Constants.SCENE_TYPE_HTTP == editSceneReq.getSceneType()) {
                //http（||网关）类场景
                editSceneReq.getSerialLinkDTOs().forEach(serialLinkDTO -> {
                    SerialLink serialLink = serialLinkMapper.selectByPrimaryKey(serialLinkDTO.getSerialLinkID());
                    if (serialLink == null) {
                        //新增链路
                        //创建串联链路
                        serialLink = new SerialLink();
                        serialLink.setSceneId(sceneInfo.getId());
                        serialLink.setName(serialLinkDTO.getSerialLinkName());
                        if (serialLinkDTO.getEnable() == null) {
                            serialLink.setEnable(false);
                        } else {
                            serialLink.setEnable(serialLinkDTO.getEnable());
                        }
                        serialLinkMapper.insert(serialLink);

                        serialLinkDTO.setSerialLinkID(serialLink.getId());
                        //http（||网关）类场景
                        sceneApiService.newHttpSceneApis(serialLinkDTO.getHttpApiInfoDTOList(), sceneInfo.getId(), serialLink.getId());
                    } else {
                        //更新链路
                        serialLink.setName(serialLinkDTO.getSerialLinkName());
                        if (serialLinkDTO.getEnable() == null) {
                            serialLink.setEnable(false);
                        } else {
                            serialLink.setEnable(serialLinkDTO.getEnable());
                        }
                        //更新链路信息
                        serialLinkMapper.updateByPrimaryKey(serialLink);

                        //更新链路中的接口信息
                        List<HttpApiInfoDTO> apisToBeAdded = new ArrayList<>();
                        //新接口id集
                        List<Integer> newApiIds = serialLinkDTO.getHttpApiInfoDTOList().stream().map(HttpApiInfoDTO::getApiID).collect(Collectors.toList());
                        //删除旧的
                        SceneApiInfoExample apiInfoExample = new SceneApiInfoExample();
                        apiInfoExample.createCriteria().andSceneIdEqualTo(sceneInfo.getId()).andSerialLinkIdEqualTo(serialLink.getId());

                        //原接口id集
                        List<Integer> oldApiIds = sceneApiInfoMapper.selectByExample(apiInfoExample).stream().map(SceneApiInfo::getId).collect(Collectors.toList());
                        oldApiIds.forEach(oldId -> {
                            if (!newApiIds.contains(oldId)) {
                                //删除
                                sceneApiInfoMapper.deleteByPrimaryKey(oldId);
                            }
                        });

                        //更新原有接口
                        serialLinkDTO.getHttpApiInfoDTOList().forEach(httpApiDTO -> {
                            if (httpApiDTO.getApiID() != null) {
                                sceneApiService.updateHttpSceneApi(httpApiDTO);
                            } else {
                                apisToBeAdded.add(httpApiDTO);
                            }
                        });

                        //添加新的接口
                        if (apisToBeAdded.size() != 0) {
                            sceneApiService.newHttpSceneApis(apisToBeAdded, sceneInfo.getId(), serialLink.getId());
                        }
                    }
                });

            } else if (Constants.SCENE_TYPE_DUBBO == editSceneReq.getSceneType()) {
                //dubbo场景
                editSceneReq.getSerialLinkDTOs().forEach(serialLinkDTO -> {
                    SerialLink serialLink = serialLinkMapper.selectByPrimaryKey(serialLinkDTO.getSerialLinkID());
                    if (serialLink == null) {
                        //创建串联链路
                        serialLink = new SerialLink();
                        serialLink.setSceneId(sceneInfo.getId());
                        serialLink.setName(serialLinkDTO.getSerialLinkName());

                        if (serialLinkDTO.getEnable() == null) {
                            serialLink.setEnable(false);
                        } else {
                            serialLink.setEnable(serialLinkDTO.getEnable());
                        }
                        serialLinkMapper.insert(serialLink);
                        //dubbo场景
                        sceneApiService.newDubboSceneApis(serialLinkDTO.getDubboApiInfoDTOList(), sceneInfo.getId(), serialLink.getId());
                    } else {
                        serialLink.setName(serialLinkDTO.getSerialLinkName());
                        serialLink.setEnable(serialLinkDTO.getEnable());
                        //更新链路信息
                        serialLinkMapper.updateByPrimaryKey(serialLink);
                        List<DubboApiInfoDTO> apisToBeAdded = new ArrayList<>();
                        //新接口id集
                        List<Integer> newApiIds = serialLinkDTO.getDubboApiInfoDTOList().stream().map(DubboApiInfoDTO::getApiID).collect(Collectors.toList());

                        //删除旧的
                        SceneApiInfoExample apiInfoExample = new SceneApiInfoExample();
                        apiInfoExample.createCriteria().andSceneIdEqualTo(sceneInfo.getId()).andSerialLinkIdEqualTo(serialLink.getId());

                        //原接口id集
                        List<Integer> oldApiIds = sceneApiInfoMapper.selectByExample(apiInfoExample).stream().map(SceneApiInfo::getId).collect(Collectors.toList());
                        oldApiIds.forEach(oldId -> {
                            if (!newApiIds.contains(oldId)) {
                                //删除
                                sceneApiInfoMapper.deleteByPrimaryKey(oldId);
                            }
                        });

                        serialLinkDTO.getDubboApiInfoDTOList().forEach(dubboApiDTO -> {
                            if (dubboApiDTO.getApiID() != null) {
                                sceneApiService.updateDubboSceneApi(dubboApiDTO);
                            } else {
                                apisToBeAdded.add(dubboApiDTO);
                            }
                        });

                        if (apisToBeAdded.size() != 0) {
                            sceneApiService.newDubboSceneApis(apisToBeAdded, sceneInfo.getId(), serialLink.getId());
                        }
                    }
                });
            } else {
                return Result.fail(CommonError.InvalidParamError);
            }
            //绑定数据集
            datasetService.bindDataSetsByScene(editSceneReq.getDatasetIds(), new HashSet<>(editSceneReq.getRefDatasetIds().values()), sceneInfo.getId());
            this.postOperationLog(editSceneReq, false, opUser);
        } catch (Exception e) {
            log.error("edit scene failed,error:{}", e.getMessage());
            throw e;
        }
        return Result.success(true);
    }

    private void removeOldLinks(List<Integer> oldLinkIds, EditSceneDTO editSceneDTO) {
        List<Integer> curLinkIds = editSceneDTO.getSerialLinkDTOs().stream().
                map(SerialLinkDTO::getSerialLinkID).filter(Objects::nonNull).collect(Collectors.toList());
        List<Integer> idToBeRemoved = new ArrayList<>();

        oldLinkIds.forEach(oldLinkId -> {
            if (!curLinkIds.contains(oldLinkId)) {
                idToBeRemoved.add(oldLinkId);
            }
        });
        if (idToBeRemoved.size() == 0) {
            return;
        }
        //删除接口
        SceneApiInfoExample example = new SceneApiInfoExample();
        example.createCriteria().andSceneIdEqualTo(editSceneDTO.getId()).andSerialLinkIdIn(idToBeRemoved);
        sceneApiInfoMapper.deleteByExample(example);

        //删除链路
        SerialLinkExample linkExample = new SerialLinkExample();
        linkExample.createCriteria().andIdIn(idToBeRemoved);
        serialLinkMapper.deleteByExample(linkExample);
    }

    @Override
    public Result<SceneDTO> getSceneByID(Integer sceneID, boolean engine) {
        SceneInfo sceneInfo = sceneInfoMapper.selectByPrimaryKey(sceneID);
        if (sceneInfo == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        return Result.success(transferToDetailSceneDTO(sceneInfo, engine));
    }

    @Override
    public Result<List<SceneInfo>> getSceneInfoByIds(List<Integer> sceneIds) {
        checkArgument(sceneIds != null && !sceneIds.isEmpty(),
                "[SceneService]getSceneInfoByIds empty input");
        SceneInfoExample example = new SceneInfoExample();
        example.createCriteria().andIdIn(sceneIds);
        return Result.success(sceneInfoMapper.selectByExample(example));
    }

    @Override
    public Result<SceneList> getSceneList(GetSceneListReq req) {
        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(DEFAULT_PAGE_SIZE);
        }
        if (req.getKeyword() == null) {
            req.setKeyword("");
        }
        SceneList sceneList = new SceneList();

        int offset = (req.getPage() - 1) * req.getPageSize();

        sceneList.setPage(req.getPage());
        sceneList.setPageSize(req.getPageSize());

        SceneInfoExample totalExp = new SceneInfoExample();
        SceneInfoExample.Criteria totalExpCriteria = totalExp.createCriteria();
        SceneInfoExample sceneInfoExample = new SceneInfoExample();
        SceneInfoExample.Criteria sceneInfoCriteria = sceneInfoExample.createCriteria();

        if (req.getKeyword() != null && !req.getKeyword().isEmpty()) {
            sceneInfoExample.or(sceneInfoCriteria.andNameLike("%" + req.getKeyword() + "%"));
            totalExp.or(totalExpCriteria.andNameLike("%" + req.getKeyword() + "%"));

            sceneInfoExample.or(sceneInfoExample.createCriteria().andCreatorLike("%" + req.getKeyword() + "%"));
            totalExp.or(sceneInfoExample.createCriteria().andCreatorLike("%" + req.getKeyword() + "%"));
        }
        //场景状态
        if (req.getStatus() != null) {
            sceneInfoCriteria.andSceneStatusEqualTo(req.getStatus());
            totalExpCriteria.andSceneStatusEqualTo(req.getStatus());
        }

        sceneInfoExample.setOrderByClause("id desc limit " + req.getPageSize() + " offset " + offset);

        List<SceneInfo> infoList = sceneInfoMapper.selectByExample(sceneInfoExample);
        if (infoList == null || infoList.size() == 0) {
            return Result.success(sceneList);
        }

        List<BasicSceneDTO> basicSceneDTOS = infoList.stream().map(this::transferToBasicSceneDTO).collect(Collectors.toList());

        sceneList.setList(basicSceneDTOS);
        sceneList.setTotal(sceneInfoMapper.countByExample(totalExp));

        return Result.success(sceneList);
    }

    @Override
    public Result<List<SceneDTO>> getSceneListByIds(GetSceneListByIdsReq req) {
        List<SceneDTO> sceneDTOList = new ArrayList<>();
        req.getSceneIdList().forEach(id -> sceneDTOList.add(getSceneByID(id, false).getData()));
        return Result.success(sceneDTOList);
    }

    @Override
    public Result<SceneGroupList> getSceneListByGroup(GetSceneGroupListReq req) {

        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(DEFAULT_PAGE_SIZE);
        }
        if (req.getKeyword() == null) {
            req.setKeyword("");
        }

        int offset = (req.getPage() - 1) * req.getPageSize();


        SceneGroupList groupList = new SceneGroupList();
        groupList.setPage(req.getPage());
        groupList.setPageSize(req.getPageSize());

        //总数
        SceneGroupExample totalExp = new SceneGroupExample();
        SceneGroupExample.Criteria totalExpCriteria = totalExp.createCriteria();

        SceneGroupExample sceneGroupExample = new SceneGroupExample();
        SceneGroupExample.Criteria sceneGroupCriteria = sceneGroupExample.createCriteria();
        //租户筛选
//        if (req.getTenant() != null && !req.getTenant().equals(MiOne_Tenant)) {
//            sceneGroupCriteria.andTenantEqualTo(req.getTenant());
//            totalExpCriteria.andTenantEqualTo(req.getTenant());
//        }

        if (req.getKeyword() != null) {
            sceneGroupExample.or(sceneGroupCriteria.andCreatorLike("%" + req.getKeyword() + "%"));
            sceneGroupExample.or(sceneGroupExample.createCriteria().andGroupNameLike("%" + req.getKeyword() + "%"));
            totalExp.or(totalExpCriteria.andCreatorLike("%" + req.getKeyword() + "%"));
            totalExp.or(totalExp.createCriteria().andGroupNameLike("%" + req.getKeyword() + "%"));
        }

        sceneGroupExample.setOrderByClause("id desc limit " + req.getPageSize() + " offset " + offset);

        groupList.setTotal(sceneGroupMapper.countByExample(totalExp));

        List<SceneGroup> groupInfoList = sceneGroupMapper.selectByExample(sceneGroupExample);
        if (groupInfoList == null || groupInfoList.size() == 0) {
            return Result.success(groupList);
        }

        List<GroupSceneDTO> list = new ArrayList<>();
        groupInfoList.forEach(groupInfo -> {
            GroupSceneDTO groupSceneDTO = new GroupSceneDTO();
            groupSceneDTO.setSceneGroupID(groupInfo.getId());
            groupSceneDTO.setGroupName(groupInfo.getGroupName());
            groupSceneDTO.setGroupDesc(groupInfo.getGroupDesc());
            groupSceneDTO.setCtime(groupInfo.getCtime());

            SceneInfoExample sceneInfoExample = new SceneInfoExample();
            sceneInfoExample.createCriteria().andSceneGroupIdEqualTo(groupInfo.getId());

            List<SceneInfo> sceneInfos = sceneInfoMapper.selectByExample(sceneInfoExample);
            groupSceneDTO.setList(sceneInfos.stream().map(this::transferToBasicSceneDTO).collect(Collectors.toList()));

            list.add(groupSceneDTO);
        });
        groupList.setList(list);
        return Result.success(groupList);
    }

    @Override
    public Result<SceneList> getSceneListByKeyword(GetSceneListReq req) {
        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(DEFAULT_PAGE_SIZE);
        }
        if (req.getKeyword() == null) {
            req.setKeyword("");
        }
        SceneList sceneList = new SceneList();

        int offset = (req.getPage() - 1) * req.getPageSize();

        sceneList.setPage(req.getPage());
        sceneList.setPageSize(req.getPageSize());

        SceneInfoExample totalExp = new SceneInfoExample();
        totalExp.createCriteria();

        SceneInfoExample sceneInfoExample = new SceneInfoExample();
        sceneInfoExample.or(sceneInfoExample.createCriteria().andNameLike("%" + req.getKeyword() + "%"));
        sceneInfoExample.or(sceneInfoExample.createCriteria().andCreatorLike("%" + req.getKeyword() + "%"));

        sceneInfoExample.or(sceneInfoExample.createCriteria().andSceneTypeEqualTo(req.getSceneType()));

        sceneInfoExample.setOrderByClause("id desc limit " + req.getPageSize() + " offset " + offset);

        List<SceneInfo> infoList = sceneInfoMapper.selectByExample(sceneInfoExample);
        if (infoList == null || infoList.size() == 0) {
            return Result.success(sceneList);
        }

        List<BasicSceneDTO> basicSceneDTOS = infoList.stream().map(this::transferToBasicSceneDTO).collect(Collectors.toList());

        sceneList.setList(basicSceneDTOS);
        sceneList.setTotal(sceneInfoMapper.countByExample(totalExp));

        return Result.success(sceneList);
    }

    public List<SerialLinkDTO> getSerialLinksByIds(List<Integer> serialIds) {
        if (serialIds == null || serialIds.isEmpty()) {
            return new ArrayList<>();
        }
        SerialLinkExample example = new SerialLinkExample();
        example.createCriteria().andIdIn(serialIds);
        List<SerialLink> poList = this.serialLinkMapper.selectByExample(example);

        return poList.stream().map((po) -> SerialLinkDTO.builder()
                .serialLinkID(po.getId())
                .serialLinkName(po.getName())
                .build()).collect(Collectors.toList());
    }

    private BasicSceneDTO transferToBasicSceneDTO(SceneInfo sceneInfo) {
        BasicSceneDTO basicSceneDTO = new BasicSceneDTO();
        basicSceneDTO.setId(sceneInfo.getId());
        basicSceneDTO.setName(sceneInfo.getName());
        basicSceneDTO.setRemark(sceneInfo.getRemark());
        basicSceneDTO.setBenchTime(sceneInfo.getBenchTime());
        basicSceneDTO.setSceneType(sceneInfo.getSceneType());
        basicSceneDTO.setStatus(sceneInfo.getSceneStatus());
        basicSceneDTO.setCurReportId(sceneInfo.getCurReportId());
        basicSceneDTO.setRpsRate(sceneInfo.getRpsRate());
        basicSceneDTO.setCreator(sceneInfo.getCreator());
        basicSceneDTO.setUTime(sceneInfo.getUtime());
        basicSceneDTO.setCTime(sceneInfo.getCtime());
        return basicSceneDTO;
    }

    private SceneDTO transferToDetailSceneDTO(SceneInfo sceneInfo, boolean engine) {

        SceneDTO sceneDTO = new SceneDTO();
        BeanUtils.copyProperties(sceneInfo, sceneDTO);

        sceneDTO.setGroupID(sceneInfo.getSceneGroupId());

        sceneDTO.setRpsRate(sceneInfo.getRpsRate());
        sceneDTO.setId(sceneInfo.getId());
        sceneDTO.setSceneSource(sceneInfo.getSceneSource());
        sceneDTO.setCTime(sceneInfo.getCtime());
        sceneDTO.setUTime(sceneInfo.getUtime());
        sceneDTO.setCreator(sceneInfo.getCreator());
        sceneDTO.setUpdater(sceneInfo.getUpdator());
        sceneDTO.setBenchCount(sceneInfo.getBenchCount());
        sceneDTO.setApiNum(sceneInfo.getApinum());
        sceneDTO.setLastBenchTime(sceneInfo.getLastBenchTime());

        BenchCalendar benchCalendar;
        if (sceneInfo.getBenchCalendar() == null || sceneInfo.getBenchCalendar().isEmpty()) {
            benchCalendar = new BenchCalendar();
            benchCalendar.setBenchDateList(new ArrayList<>());
        } else {
            benchCalendar = gson.fromJson(sceneInfo.getBenchCalendar(), BenchCalendar.class);
        }
        List<BenchDate> tmpList = new ArrayList<>();
        //过滤一年前的记录
        benchCalendar.getBenchDateList().forEach(benchDate -> {
            if (System.currentTimeMillis() - benchDate.getTimestamp() <= ONE_YEAR_MS) {
                tmpList.add(benchDate);
            }
        });
        benchCalendar.setBenchDateList(tmpList);
        //压测日历
        sceneDTO.setBenchCalendar(benchCalendar);

        if (sceneInfo.getSla() == null || sceneInfo.getSla().equals("")) {
            sceneDTO.setUseSla(0);
        } else {
            sceneDTO.setUseSla(1);
        }

        //责任人
        if (sceneInfo.getPersonInCharge() != null) {
            if (sceneInfo.getPersonInCharge().equals("")) {
                sceneDTO.setPersonsInCharge(new ArrayList<>());
            } else {
                sceneDTO.setPersonsInCharge(gson.fromJson(sceneInfo.getPersonInCharge(), new TypeToken<List<String>>() {
                }.getType()));
            }
        }

        //全局请求头
        if (sceneInfo.getGlobalHeader() != null) {
            if (sceneInfo.getGlobalHeader().equals("")) {
                sceneDTO.setGlobalHeaderList(new ArrayList<>());
            } else {
                List<GlobalHeader> globalHeaders = gson.fromJson(sceneInfo.getGlobalHeader(), new TypeToken<List<GlobalHeader>>() {
                }.getType());
                sceneDTO.setGlobalHeaderList(globalHeaders);
            }
        }

        //汽车部tsp鉴权消息
        if (sceneInfo.getGlobalTspAuth() != null) {
            if (sceneInfo.getGlobalTspAuth().equals("")) {
                sceneDTO.setGlobalTspAuth(new TspAuthInfo(false));
            } else {
                TspAuthInfo globalTspAuth = gson.fromJson(sceneInfo.getGlobalTspAuth(), new TypeToken<TspAuthInfo>() {
                }.getType());
                sceneDTO.setGlobalTspAuth(globalTspAuth);
            }
        }

        SerialLinkExample example = new SerialLinkExample();
        example.createCriteria().andSceneIdEqualTo(sceneInfo.getId());
        //该场景的串联链路集
        List<SerialLink> serialLinkList = serialLinkMapper.selectByExample(example);
        List<SerialLinkDTO> serialLinkDTOS = new ArrayList<>(serialLinkList.size());

        serialLinkList.forEach(serialLink -> {
            SceneApiInfoExample example1 = new SceneApiInfoExample();
            example1.createCriteria().andSceneIdEqualTo(sceneInfo.getId()).andSerialLinkIdEqualTo(serialLink.getId());
            List<SceneApiInfo> apiInfoList = sceneApiInfoMapper.selectByExampleWithBLOBs(example1);

            //串联链路信息
            SerialLinkDTO serialLinkDTO = new SerialLinkDTO();
            serialLinkDTO.setSerialLinkID(serialLink.getId());
            serialLinkDTO.setSerialLinkName(serialLink.getName());
            serialLinkDTO.setEnable(serialLink.getEnable());
            //获取处理压测接口信息
            if (sceneInfo.getSceneType() == Constants.SCENE_TYPE_HTTP) {
                List<HttpApiInfoDTO> apiDTOList = new ArrayList<>();
                apiInfoList.forEach(api -> {
                    HttpApiInfoDTO httpApiInfoDTO = new HttpApiInfoDTO();
                    BeanUtils.copyProperties(api, httpApiInfoDTO);
                    httpApiInfoDTO.setApiID(api.getId());
                    httpApiInfoDTO.setApiProtocol(Constants.HTTP_API_TYPE);
                    httpApiInfoDTO.setApiRequestType(api.getRequestMethod());
                    httpApiInfoDTO.setHeaderInfo(api.getApiHeader());
                    if (api.getContentType() == null || api.getContentType().equals("")) {
                        //get
                        httpApiInfoDTO.setRequestInfo(api.getRequestParamInfo());
                    } else if (api.getContentType().equals(Constants.CONTENT_TYPE_APP_FORM) || api.getContentType().equals(CONTENT_TYPE_APP_FORM2)) {
                        httpApiInfoDTO.setRequestInfo(api.getRequestParamInfo());
                    } else if (api.getContentType().equals(CONTENT_TYPE_APP_JSON)) {
                        httpApiInfoDTO.setRequestInfoRaw(api.getRequestBody());
                    }
                    //输出参数定义
                    List<SceneApiOutputParam> outputParams = gson.fromJson(api.getOutputParamInfo(), new TypeToken<List<SceneApiOutputParam>>() {
                    }.getType());

                    httpApiInfoDTO.setOutputParamInfos(outputParams);


                    //汽车部tsp鉴权消息
                    if (api.getApiTspAuth() != null) {
                        if (api.getApiTspAuth().equals("")) {
                            httpApiInfoDTO.setApiTspAuth(new TspAuthInfo(false));
                        } else {
                            TspAuthInfo apiTspAuth = gson.fromJson(api.getApiTspAuth(), new TypeToken<TspAuthInfo>() {
                            }.getType());
                            httpApiInfoDTO.setApiTspAuth(apiTspAuth);
                        }
                    }

                    //接口是否使用录制流量的配置信息
                    if (api.getApiTrafficInfo() != null) {
                        if (api.getApiTrafficInfo().equals("")) {
                            httpApiInfoDTO.setApiTrafficInfo(new ApiTrafficInfo(false));
                        } else {
                            ApiTrafficInfo apiTrafficInfo = gson.fromJson(api.getApiTrafficInfo(), new TypeToken<ApiTrafficInfo>() {
                            }.getType());
                            httpApiInfoDTO.setApiTrafficInfo(apiTrafficInfo);
                        }
                    }

                    //接口是否启用x5鉴权
                    if (api.getApiX5Info() != null) {
                        if (api.getApiX5Info().equals("")) {
                            httpApiInfoDTO.setApiX5Info(new ApiX5Info(false));
                        } else {
                            ApiX5Info apiX5Info = gson.fromJson(api.getApiX5Info(), new TypeToken<ApiX5Info>() {
                            }.getType());
                            httpApiInfoDTO.setApiX5Info(apiX5Info);
                        }
                    }

                    //检查点
                    if (api.getCheckPoint() != null && !api.getCheckPoint().isEmpty()) {
                        List<CheckPointInfo> checkPointInfoList = gson.fromJson(api.getCheckPoint(), new TypeToken<List<CheckPointInfo>>() {
                        }.getType());

                        List<CheckPointInfoDTO> checkPointInfoDTOs = new ArrayList<>(checkPointInfoList.size());

                        checkPointInfoList.forEach(checkPointInfo -> {
                            CheckPointInfoDTO checkPointInfoDTO = new CheckPointInfoDTO();
                            BeanUtils.copyProperties(checkPointInfo, checkPointInfoDTO);
                            checkPointInfoDTOs.add(checkPointInfoDTO);
                        });
                        httpApiInfoDTO.setCheckPointInfoList(checkPointInfoDTOs);
                    }
                    //过滤条件
                    if (api.getFilterCondition() != null && !api.getFilterCondition().isEmpty()) {
                        List<CheckPointInfo> filterConditions = gson.fromJson(api.getFilterCondition(), new TypeToken<List<CheckPointInfo>>() {
                        }.getType());
                        if (filterConditions != null) {
                            List<CheckPointInfoDTO> filterConditionList = new ArrayList<>(filterConditions.size());
                            filterConditions.forEach(filterCondition -> {
                                CheckPointInfoDTO checkPointInfoDTO = new CheckPointInfoDTO();
                                BeanUtils.copyProperties(filterCondition, checkPointInfoDTO);
                                filterConditionList.add(checkPointInfoDTO);
                            });
                            httpApiInfoDTO.setFilterCondition(filterConditionList);
                        }
                    }
                    apiDTOList.add(httpApiInfoDTO);
                });

                serialLinkDTO.setHttpApiInfoDTOList(apiDTOList.stream().sorted().collect(Collectors.toList()));
                serialLinkDTOS.add(serialLinkDTO);

            } else if (sceneInfo.getSceneType() == SCENE_TYPE_DUBBO) {
                List<DubboApiInfoDTO> apiDTOList = new ArrayList<>();
                apiInfoList.forEach(api -> {
                    DubboApiInfoDTO dubboApiInfoDTO = new DubboApiInfoDTO();
                    BeanUtils.copyProperties(api, dubboApiInfoDTO);
                    dubboApiInfoDTO.setApiID(api.getId());
                    dubboApiInfoDTO.setApiProtocol(Constants.DUBBO_API_TYPE);
                    dubboApiInfoDTO.setGroup(api.getDubboGroup());
                    dubboApiInfoDTO.setVersion(api.getDubboVersion());
                    //dubbo接口的nacos环境
                    if (api.getNacosType() == Constants.NACOS_TYPE_ST) {
                        dubboApiInfoDTO.setDubboEnv("staging");
                    } else if (api.getNacosType() == Constants.NACOS_TYPE_OL) {
                        dubboApiInfoDTO.setDubboEnv("online");
                    }
                    dubboApiInfoDTO.setAttachments(api.getApiHeader());

                    //参数类型列表
                    List<String> paramTypeList = gson.fromJson(api.getParamTypeList(), new TypeToken<List<String>>() {
                    }.getType());
                    dubboApiInfoDTO.setRequestParamTypeList(paramTypeList);

                    //参数体 json
                    dubboApiInfoDTO.setRequestBody(api.getDubboParamJson());

                    //输出参数定义
                    List<SceneApiOutputParam> outputParams = gson.fromJson(api.getOutputParamInfo(), new TypeToken<List<SceneApiOutputParam>>() {
                    }.getType());
                    if (!engine) {
                        for (SceneApiOutputParam output :
                                outputParams) {
                            output.setParseExpr(output.getParseExpr().substring(DEFAULT_EXPR_PREX.length()));
                        }
                    }
                    dubboApiInfoDTO.setOutputParamInfos(outputParams);
                    //检查点
                    if (api.getCheckPoint() != null && !api.getCheckPoint().isEmpty()) {
                        List<CheckPointInfo> checkPointInfoList = gson.fromJson(api.getCheckPoint(), new TypeToken<List<CheckPointInfo>>() {
                        }.getType());

                        List<CheckPointInfoDTO> checkPointInfoDTOs = new ArrayList<>(checkPointInfoList.size());

                        checkPointInfoList.forEach(checkPointInfo -> {
                            CheckPointInfoDTO checkPointInfoDTO = new CheckPointInfoDTO();
                            BeanUtils.copyProperties(checkPointInfo, checkPointInfoDTO);
                            checkPointInfoDTOs.add(checkPointInfoDTO);
                        });
                        dubboApiInfoDTO.setCheckPointInfoList(checkPointInfoDTOs);
                    }
                    //过滤条件
                    if (api.getFilterCondition() != null && !api.getFilterCondition().isEmpty()) {
                        List<CheckPointInfo> filterConditions = gson.fromJson(api.getFilterCondition(), new TypeToken<List<CheckPointInfo>>() {
                        }.getType());
                        if (filterConditions != null) {
                            List<CheckPointInfoDTO> filterConditionList = new ArrayList<>(filterConditions.size());

                            filterConditions.forEach(filterCondition -> {
                                CheckPointInfoDTO checkPointInfoDTO = new CheckPointInfoDTO();
                                BeanUtils.copyProperties(filterCondition, checkPointInfoDTO);
                                filterConditionList.add(checkPointInfoDTO);
                            });
                            dubboApiInfoDTO.setFilterCondition(filterConditionList);
                        }
                    }

                    apiDTOList.add(dubboApiInfoDTO);
                });
                serialLinkDTO.setDubboApiInfoDTOList(apiDTOList.stream().sorted().collect(Collectors.toList()));
                serialLinkDTOS.add(serialLinkDTO);
            }
        });
        sceneDTO.setSerialLinkDTOs(serialLinkDTOS);

        //转换接口压测量信息
        List<ApiBenchInfo> apiBenchInfoList = new ArrayList<>();
        if (Objects.nonNull(sceneInfo.getApiBenchInfos())) {
            apiBenchInfoList = gson.fromJson(sceneInfo.getApiBenchInfos(), new TypeToken<List<ApiBenchInfo>>() {
            }.getType());
        }
        sceneDTO.setApiBenchInfos(apiBenchInfoList);

        if (sceneInfo.getSla() != null) {
            SlaDto slaDto = gson.fromJson(sceneInfo.getSla(), new TypeToken<SlaDto>() {
            }.getType());
            sceneDTO.setSlaDto(slaDto);
        }

        if (sceneInfo.getAgentList() != null) {
            List<AgentDTO> agentDTOList = gson.fromJson(sceneInfo.getAgentList(), new TypeToken<List<AgentDTO>>() {
            }.getType());

            sceneDTO.setAgentIdList(agentDTOList.stream().map(AgentDTO::getId).collect(Collectors.toList()));
            sceneDTO.setAgentDTOList(agentDTOList);
        } else {
            sceneDTO.setAgentDTOList(new ArrayList<>());
        }

        sceneDTO.setDatasetIds(datasetService.getDatasetIdsBySceneId(sceneInfo.getId()));
        if (sceneInfo.getRefDatasetIds() != null) {
            Map<String, Integer> refDatasetIds = gson.fromJson(sceneInfo.getRefDatasetIds(), new TypeToken<Map<String, Integer>>() {
            }.getType());
            sceneDTO.setRefDatasetIds(refDatasetIds);
        }
        return sceneDTO;
    }

    @Override
    public Result<SceneParamData> getSceneParamData(Integer sceneId) {
        SceneParamData sceneParamData = new SceneParamData();
        sceneParamData.setParamDataType(Constants.SCENE_PARAM_DATA_TYPE_GLOBAL);
        sceneParamData.setParamDataList(datasetService.getParamDataBySceneId(sceneId).getData());
        return Result.success(sceneParamData);
    }

    private void postOperationLog(SceneDTO sceneDTO, boolean create, String opUser) {
        String snapshotId = this.sceneSnapshotService.createSnapshot(SceneSnapshotBo.builder()
                .sceneId((long) sceneDTO.getId())
                .type(SCENE_SNAPSHOT.typeCode)
                .createBy(opUser)
                .scene(gson.toJson(sceneDTO))
                .build()).getData();

        OperationLogDto opRecord = new OperationLogDto();
        opRecord.setSceneId(sceneDTO.getId());
        opRecord.setCreateBy(opUser);
        opRecord.setType(create ? NEW_OPERATION.typeCode : UPDATE_OPERATION.typeCode);
        opRecord.setContent(create ? "创建场景" : "更新场景");

        List<PerOperation> supportOperations = new ArrayList<>();
        PerOperation perOperationViewSnapshot = new PerOperation(VIEW_SNAPSHOT.typeCname, VIEW_SNAPSHOT.typeName, snapshotId);
        PerOperation perOperationCopySnapshot = new PerOperation(COPY_SNAPSHOT.typeCname, COPY_SNAPSHOT.typeName, snapshotId);
        supportOperations.add(perOperationViewSnapshot);
        supportOperations.add(perOperationCopySnapshot);
        opRecord.setSupportOperation(supportOperations);
        operationLogService.newOperationLog(opRecord);
    }

    private Pair<Integer, String> checkSceneParam(SceneDTO sceneDTO) {
        if (sceneDTO.getRequestTimeout() == null) {
            sceneDTO.setRequestTimeout(SCENE_REQ_TIMEOUT);
        }
        if (sceneDTO.getGroupID() == null) {
            sceneDTO.setGroupID(Constants.DEFAULT_SCENE_GROUP);
        }
        if (sceneDTO.getSceneEnv() == null) {
            return Pair.of(-1, "场景环境必传");
        }
        if (sceneDTO.getSceneType() == null) {
            return Pair.of(-1, "场景类型必传");
        }
        if (sceneDTO.getLogRate() == null) {
            return Pair.of(-1, "日志采样率必传");
        }
//        if (sceneDTO.getLogRate() >= 100) {
//            return Pair.of(-1, "日志采样率不能超过100");
//        }
        if (sceneDTO.getBenchTime() == null) {
            return Pair.of(-1, "施压时间必传");
        }
        if (sceneDTO.getIncreasePercent() == null) {
            //默认值
            sceneDTO.setIncreasePercent(0);
        }
        return Pair.of(0, "success");
    }

}

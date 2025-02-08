package run.mone.m78.service.service.knowledge;

import com.google.common.base.Preconditions;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.ExceptionHelper;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import run.mone.ai.z.dto.*;
import run.mone.ai.z.service.KnowledgeBaseService;
import run.mone.knowledge.api.KnowledgeBaseBlockProvider;
import run.mone.knowledge.api.KnowledgeBaseFileProvider;
import run.mone.knowledge.api.KnowledgeBaseProvider;
import run.mone.knowledge.api.dto.*;
import run.mone.m78.api.bo.knowledge.KnowledgeBaseFilesParam;
import run.mone.m78.api.bo.knowledge.KnowledgeBo;
import run.mone.m78.api.enums.UserRoleEnum;
import run.mone.m78.service.bo.knowledge.KnowledgeCreateV2Req;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78KnowledgeBase;
import run.mone.m78.service.dao.mapper.M78KnowledgeBaseMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import run.mone.m78.service.dto.knowledge.KnowledgeBaseFileResDto;
import run.mone.m78.service.dto.knowledge.KnowledgeBaseResDto;
import run.mone.m78.service.service.workspace.WorkspaceService;
import run.mone.m78.service.service.gray.GrayService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static run.mone.m78.service.exceptions.ExCodes.STATUS_BAD_REQUEST;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;

/**
 * @author wmin
 * @date 2024/1/29
 */
@Component
@Slf4j
public class KnowledgeService extends ServiceImpl<M78KnowledgeBaseMapper, M78KnowledgeBase> {

    //todo timeout
    @DubboReference(interfaceClass = KnowledgeBaseService.class, check = false, group = "${ref.ai.z.service.group}", version = "${ref.ai.z.service.version}", timeout = 10000)
    private KnowledgeBaseService zknowledgeBaseService;

    @DubboReference(interfaceClass = KnowledgeBaseBlockProvider.class, check = false, group = "${ref.knowledge.service.group}", version = "${ref.knowledge.service.version}", timeout = 10000)
    private KnowledgeBaseBlockProvider knowledgeBaseBlockProvider;

    @DubboReference(interfaceClass = KnowledgeBaseFileProvider.class, check = false, group = "${ref.knowledge.service.group}", version = "${ref.knowledge.service.version}", timeout = 10000, retries = 0)
    private KnowledgeBaseFileProvider knowledgeBaseFileProvider;

    @DubboReference(interfaceClass = KnowledgeBaseProvider.class, check = false, group = "${ref.knowledge.service.group}", version = "${ref.knowledge.service.version}", timeout = 10000)
    private KnowledgeBaseProvider knowledgeBaseProvider;

    @Autowired
    private M78KnowledgeBaseMapper knowledgeBaseMapper;

    @Autowired
    private GrayService grayService;

    @Value("${fileserver.address}")
    private String fileserverAddress;

    @Value("${fileserver.token:123456}")
    private String fileServerToken;

    private static Random random = new Random();
    @Autowired
    private WorkspaceService workspaceService;

    public Result<ZKnowledgeBaseDTO> create(String name, String userName) {
        ZKnowledgeBaseDTO zKnowledgeBaseDTO = new ZKnowledgeBaseDTO();
        zKnowledgeBaseDTO.setCreator(userName);
        zKnowledgeBaseDTO.setName(name);
        return zknowledgeBaseService.create(zKnowledgeBaseDTO);
    }

    public Result<KnowledgeBaseResDto> getSingleKnowledge(long id, String userName) {
        log.info("KnowledgeService.getSingleKnowledge id:{} userName:{}", id, userName);
        try {
            M78KnowledgeBase knowledgeBaseByIdFromDB = getKnowledgeBaseByIdFromDB(id);
            if (knowledgeBaseByIdFromDB == null) {
                log.error("知识库不存在或无权限");
                return Result.fail(STATUS_BAD_REQUEST, "知识库不存在或无权限");
            }
            KnowledgeBaseResDto res = new KnowledgeBaseResDto();
            BeanUtils.copyProperties(knowledgeBaseByIdFromDB, res);

            SessionAccount account = new SessionAccount();
            account.setUsername(userName);
            account.setUserType(1);
            Integer userWorkspaceRole = workspaceService.getWorkspaceRole(account, knowledgeBaseByIdFromDB.getWorkSpaceId());
            if (userWorkspaceRole > UserRoleEnum.UN_KNOW.getCode()) {
                res.setSelf(true);
            }

            return Result.success(res);

        } catch (Exception e) {
            log.error("KnowledgeService.getSingleKnowledge error", e);
            return Result.fromException(new Exception("获取知识库失败"));
        }
    }

    @Transactional
    public Result<Long> createKnowledgeBase(KnowledgeCreateV2Req req, String userName) {
        if (grayService.createKnowledgeGray2V2(req.getWorkSpaceId())) {
            return this.createKnowledgeBaseV2(req, userName);
        }
        try {
            log.info("KnowledgeService.createKnowledgeBase req:{} userName:{}", req, userName);
            ZKnowledgeBaseDTO zKnowledgeBaseDTO = new ZKnowledgeBaseDTO();
            zKnowledgeBaseDTO.setCreator(userName);
            zKnowledgeBaseDTO.setName(req.getName());
            zKnowledgeBaseDTO.setAuth(req.getAuth());
            zKnowledgeBaseDTO.setRemark(req.getRemark());
            zKnowledgeBaseDTO.setLabels(transfer2LabelMap(req.getLabels()));
            zKnowledgeBaseDTO.setType(req.getType());
            Result<ZKnowledgeBaseDTO> zKnowledgeBaseDTOResult = zknowledgeBaseService.create(zKnowledgeBaseDTO);
            if (zKnowledgeBaseDTOResult.getCode() != 0 || zKnowledgeBaseDTOResult.getData() == null) {
                log.error("创建知识库失败: {}", zKnowledgeBaseDTOResult.getMessage());
                return Result.fromException(new Exception("创建知识库失败: " + zKnowledgeBaseDTOResult.getMessage()));
            }

            //插库
            boolean dbRes = transferAndInsertToDB(zKnowledgeBaseDTOResult.getData(), req);
            if (!dbRes) {
                // 插入失败，再调接口远程删除掉
                zknowledgeBaseService.delete(zKnowledgeBaseDTOResult.getData().getId(), userName);
                log.error("插入数据库失败");
                return Result.fromException(new Exception("插入数据库失败"));
            }
            return Result.success(zKnowledgeBaseDTOResult.getData().getId());
        } catch (Exception e) {
            log.error("KnowledgeService.createKnowledgeBase error", e);
            return Result.fromException(new Exception(e));
        }
    }

    @Transactional
    public Result<Long> createKnowledgeBaseV2(KnowledgeCreateV2Req req, String userName) {
        try {
            log.info("KnowledgeService.createKnowledgeBase req:{} userName:{}", req, userName);
            //迁移到private_knowledge
            KnowledgeBaseDto knowledgeBaseDto = new KnowledgeBaseDto();
            knowledgeBaseDto.setCreator(userName);
            knowledgeBaseDto.setName(req.getName());
            knowledgeBaseDto.setAuth(req.getAuth());
            knowledgeBaseDto.setRemark(req.getRemark());
            knowledgeBaseDto.setLabels(transfer2LabelMap(req.getLabels()));
            knowledgeBaseDto.setType(req.getType());
            knowledgeBaseDto.setVersion(GrayService.KNOWLEDGE_VERSION_V2);
            Result<KnowledgeBaseDto> knowledgeBaseDtoResult = knowledgeBaseProvider.insertKnowledgeBase(knowledgeBaseDto);
            if (knowledgeBaseDtoResult.getCode() != 0 || knowledgeBaseDtoResult.getData() == null) {
                log.error("创建知识库失败: {}", knowledgeBaseDtoResult.getMessage());
                return Result.fromException(new Exception("创建知识库失败: " + knowledgeBaseDtoResult.getMessage()));
            }

            //插库
            boolean dbRes = transferAndInsertToDB(knowledgeBaseDtoResult.getData(), req, GrayService.KNOWLEDGE_VERSION_V2);
            if (!dbRes) {
                // 插入失败，再调接口远程删除掉
                knowledgeBaseProvider.deleteKnowledgeBase(knowledgeBaseDtoResult.getData().getId(), userName);
                log.error("插入数据库失败");
                return Result.fromException(new Exception("插入数据库失败"));
            }
            return Result.success(knowledgeBaseDtoResult.getData().getId());
        } catch (Exception e) {
            log.error("KnowledgeService.createKnowledgeBase error", e);
            return Result.fromException(new Exception(e));
        }
    }


    public Result<Boolean> editKnowledgeBase(KnowledgeCreateV2Req req, String userName) {
        try {
            log.info("KnowledgeService.editKnowledgeBase req:{} userName:{}", req, userName);
            if (req.getId() == null || req.getId() <= 0) {
                log.error("id非法");
                return Result.fromException(new Exception("id非法"));
            }
            // 查库
            M78KnowledgeBase knowledgeBaseByIdFromDB = getKnowledgeBaseByIdFromDB(req.getId());
            if (knowledgeBaseByIdFromDB == null || !knowledgeBaseByIdFromDB.getCreator().equals(userName)) {
                log.error("知识库不存在或无权限");
                return Result.fail(STATUS_BAD_REQUEST, "知识库不存在或无权限");
            }
            if (grayService.gray2Knowledge(knowledgeBaseByIdFromDB.getKnowledgeBaseId())) {
                return editKnowledgeBaseV2(req, userName);
            }
            // 查远程
            Result<ZKnowledgeBaseDTO> knowledgeBaseRemoteRes = zknowledgeBaseService.getKnowledgeBase(knowledgeBaseByIdFromDB.getKnowledgeBaseId(),
                    knowledgeBaseByIdFromDB.getKnowledgeBaseName());
            if (knowledgeBaseRemoteRes.getCode() != 0 || knowledgeBaseRemoteRes.getData() == null) {
                log.error("知识库远程不存在或无权限");
                return Result.fail(STATUS_BAD_REQUEST, "知识库远程不存在或无权限");
            }

            // 更新远程，只能更新部分字段(name auth remark label可改)
            ZKnowledgeBaseDTO remoteData = knowledgeBaseRemoteRes.getData();
            ZKnowledgeBaseDTO remoteDataBack = new ZKnowledgeBaseDTO();
            BeanUtils.copyProperties(remoteData, remoteDataBack);

            if (!StringUtils.isEmpty(req.getRemark())) {
                remoteData.setRemark(req.getRemark());
                knowledgeBaseByIdFromDB.setRemark(req.getRemark());
            }
            if (!StringUtils.isEmpty(req.getName())) {
                remoteData.setName(req.getName());
                knowledgeBaseByIdFromDB.setKnowledgeBaseName(req.getName());
            }
            if (req.getAuth() != null) {
                remoteData.setAuth(req.getAuth());
                knowledgeBaseByIdFromDB.setAuth(req.getAuth());
            }
            if (req.getLabels() != null) {
                remoteData.setLabels(transfer2LabelMap(req.getLabels()));
                knowledgeBaseByIdFromDB.setLabels(req.getLabels());
            }
            if (req.getAvatarUrl() != null && StringUtils.isNotBlank(req.getAvatarUrl())) {
                knowledgeBaseByIdFromDB.setAvatarUrl(req.getAvatarUrl());
            }

            remoteData.setOperator(userName);
            Result<Boolean> remoteEditRes = zknowledgeBaseService.edit(remoteData);
            if (remoteEditRes.getCode() != 0 || !remoteEditRes.getData()) {
                log.info("知识库远程更新失败");
                return Result.fromException(new Exception("知识库远程更新失败"));
            }
            //更新本地
            boolean upRes = updateKnowledgeBase(knowledgeBaseByIdFromDB);
            if (!upRes) {
                // 本地更新失败 回撤？
                zknowledgeBaseService.edit(remoteDataBack);
                return Result.success(false);
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("KnowledgeService.editKnowledgeBase error", e);
            return Result.fromException(new Exception(e));
        }
    }

    public Result<List<ZKnowledgeBaseFilesDTO>> embeddingFileKnowledge(List<KnowledgeBaseFilesParam> knowledgeBaseFilesParams, String userName) {
        log.info("KnowledgeService.embeddingFileKnowledge knowledgeBaseFilesParams:{}", knowledgeBaseFilesParams);
        if (CollectionUtils.isEmpty(knowledgeBaseFilesParams)) {
            return Result.fail(GeneralCodes.ParamError, "knowledgeBaseFilesParams is empty");
        }
        try {
            Long knowledgeBaseId = knowledgeBaseFilesParams.getFirst().getKnowledgeBaseId();
            if (grayService.gray2Knowledge(knowledgeBaseId)) {
                // embedding for private-knowledge
                List<KnowledgeBaseFilesDTO> knowledgeBaseFilesList = knowledgeBaseFilesParams.stream().map(i -> {
                    KnowledgeBaseFilesDTO knowledgeBaseFilesDTO = new KnowledgeBaseFilesDTO();
                    knowledgeBaseFilesDTO.setCreator(userName);
                    knowledgeBaseFilesDTO.setFilePath(StringUtils.isNotBlank(i.getUrlPath()) ? i.getUrlPath() : i.getFilePath());
                    knowledgeBaseFilesDTO.setFileName(i.getFileName());
                    knowledgeBaseFilesDTO.setKnowledgeBaseId(i.getKnowledgeBaseId());
                    knowledgeBaseFilesDTO.setUrlPath(i.getUrlPath());
                    knowledgeBaseFilesDTO.setSeparator(i.getSeparator());
                    return knowledgeBaseFilesDTO;
                }).toList();
                KnowledgeBaseFilesDTO[] array = knowledgeBaseFilesList.toArray(new KnowledgeBaseFilesDTO[0]);
                Result<List<KnowledgeBaseFilesDTO>> embeddingResult = knowledgeBaseFileProvider.embedding(array, userName);
                if (embeddingResult == null || embeddingResult.getCode() != GeneralCodes.OK.getCode()) {
                    log.error("embeddingFileKnowledge for private-knowledge fail");
                    return Result.fail(GeneralCodes.InternalError, "embeddingFileKnowledge for private-knowledge fail");
                }
                List<ZKnowledgeBaseFilesDTO> resList = new ArrayList<>();
                embeddingResult.getData().forEach(it -> {
                    ZKnowledgeBaseFilesDTO res = new ZKnowledgeBaseFilesDTO();
                    BeanUtils.copyProperties(it, res);
                    resList.add(res);
                });
                return Result.success(resList);
            } else {
                // embedding for Z
                List<ZKnowledgeBaseFilesDTO> zKnowledgeBaseFilesList = knowledgeBaseFilesParams.stream().map(i -> {
                    ZKnowledgeBaseFilesDTO zDto = new ZKnowledgeBaseFilesDTO();
                    zDto.setCreator(userName);
                    zDto.setFilePath(i.getFilePath());
                    zDto.setFileName(i.getFileName());
                    zDto.setKnowledgeBaseId(i.getKnowledgeBaseId());
                    zDto.setSeparator(i.getSeparator());
                    return zDto;
                }).collect(Collectors.toList());
                return zknowledgeBaseService.embeddingFileKnowledge(zKnowledgeBaseFilesList, userName, false);
            }
        } catch (Exception e) {
            log.error("KnowledgeService.embeddingFileKnowledge error", e);
            return Result.fromException(new Exception(e));
        }
    }

    public Result<ZKnowledgeBaseDTO> getKnowledgeBase(Long knowledgeBaseId, String userName) {
        Result<ZKnowledgeBaseDTO> rst = zknowledgeBaseService.getKnowledgeBase(knowledgeBaseId, userName);
        log.info("getKnowledgeBase knowledgeBaseId:{},rst:{}", knowledgeBaseId, rst);
        return rst;
    }

    public Result<List<ZKnowledgeBaseDTO>> listMyKnowledgeBase(String userName) {
        Result<List<ZKnowledgeBaseDTO>> rst = zknowledgeBaseService.listMyKnowledgeBase(userName);
        log.info("listMyKnowledgeBase userName:{},rst:{}", userName, rst);
        return rst;
    }

    public Result<Page<KnowledgeBaseResDto>> listMyKnowledgeBaseV2(String userName, int pageNum, int pageSize, String name, String creator, Long workSpaceId) {
        // 查库，查remote，如果remote有，库没有 则把remote插库，返回列表。
        try {
           /* Result<List<ZKnowledgeBaseDTO>> rst = zknowledgeBaseService.listMyKnowledgeBase(userName);
            log.info("listMyKnowledgeBaseV2 userName:{},rst:{}", userName, rst.getCode());
            List<Long> m78KnowledgeBaseIds = listMyKnowledgeBaseIdFromDB(userName);
            if (rst.getData() != null && !rst.getData().isEmpty()) {
                rst.getData().forEach(remote -> {
                    //如果库没有，插库
                    if (!m78KnowledgeBaseIds.contains(remote.getId())) {
                        // 0~9随机默认头像
                        int randomNumber = random.nextInt(10);
                        boolean innerSaveRes = transferAndInsertToDB(remote, String.valueOf(randomNumber));
                        log.info("listMyKnowledgeBaseV2 transferAndInsertToDB remote:{},innerSaveRes:{}", remote, innerSaveRes);
                    }
                });
            }*/
            if (workSpaceId == null || workSpaceId == 0) {
                log.error("listMyKnowledgeBaseV2 workSpaceId is null or 0");
                return Result.fromException(new Exception("workSpaceId is null or 0"));
            }
            QueryWrapper wrapper = QueryWrapper.create();
            wrapper = wrapper.eq("work_space_id", workSpaceId);
            Page<KnowledgeBaseResDto> res = new Page<>();
            if (!StringUtils.isEmpty(name)) {
                wrapper = wrapper.like("knowledge_base_name", name);
            }
            if (!StringUtils.isEmpty(creator)) {
                wrapper = wrapper.like("creator", creator);
            }
            wrapper.eq("deleted", 0);
            wrapper.orderBy("create_time", false);
            Page<M78KnowledgeBase> page = super.page(Page.of(pageNum, pageSize), wrapper);
            List<M78KnowledgeBase> list = page.getRecords().stream().toList();
            List<KnowledgeBaseResDto> resList = new ArrayList<>();
            list.forEach(it -> {
                KnowledgeBaseResDto oneRes = new KnowledgeBaseResDto();
                BeanUtils.copyProperties(it, oneRes);
                if (userName.equals(it.getCreator())) {
                    oneRes.setSelf(true);
                }
                resList.add(oneRes);
            });
            res.setRecords(resList);
            res.setPageNumber(page.getPageNumber());
            res.setPageSize(page.getPageSize());
            res.setTotalPage(page.getTotalPage());
            res.setTotalRow(page.getTotalRow());
            return Result.success(res);
        } catch (Exception e) {
            log.error("listMyKnowledgeBaseV2 error", e);
            return Result.fromException(new Exception(e));
        }
    }

    public Result<Boolean> deleteKnowledge(long id, String userName) {
        try {
            log.info("deleteKnowledge userName:{},req:{}", userName, id);
            // 查db
            M78KnowledgeBase knowledgeBaseByIdFromDB = getKnowledgeBaseByIdFromDB(id);
            if (knowledgeBaseByIdFromDB == null || knowledgeBaseByIdFromDB.getDeleted() == 1) {
                log.error("知识库不存在或状态异常");
                return Result.fromException(new Exception("知识库不存在或状态异常"));
            }
            if (grayService.gray2Knowledge(knowledgeBaseByIdFromDB.getKnowledgeBaseId())) {
                // 查看知识库下是否有未删除的文件
                Result<List<KnowledgeBaseFileDto>> listResult = knowledgeBaseProvider
                        .queryFileList(knowledgeBaseByIdFromDB.getKnowledgeBaseId(), null, userName);
                if (listResult == null || listResult.getCode() != 0) {
                    log.error("知识库下文件查询失败");
                    return Result.fromException(new Exception("知识库下文件查询失败"));
                }
                List<KnowledgeBaseFileDto> data = listResult.getData();
                if (data != null && !data.isEmpty()) {
                    return Result.fail(STATUS_INTERNAL_ERROR, "知识库下存在未删除的文件，无法删除");
                }
                // 删除远程
                Result<Boolean> deleteRemote = knowledgeBaseProvider.deleteKnowledgeBase(knowledgeBaseByIdFromDB.getKnowledgeBaseId(), userName);
                if (deleteRemote == null || deleteRemote.getCode() != 0 || !deleteRemote.getData()) {
                    log.error("远程删除知识库失败");
                    return Result.fromException(new Exception("远程删除知识库失败"));
                }
            } else {
                // 查看知识库下是否有未删除的文件
                Result<List<ZKnowledgeBaseFilesDTO>> listResult = zknowledgeBaseService
                        .listKnowledgeBaseFiles(knowledgeBaseByIdFromDB.getKnowledgeBaseId(), null, userName);
                if (listResult == null || listResult.getCode() != 0) {
                    log.error("知识库下文件查询失败");
                    return Result.fromException(new Exception("知识库下文件查询失败"));
                }
                List<ZKnowledgeBaseFilesDTO> data = listResult.getData();
                if (data != null && !data.isEmpty()) {
                    return Result.fail(STATUS_INTERNAL_ERROR, "知识库下存在未删除的文件，无法删除");
                }
                // 删除远程
                Result<Boolean> deleteRemote = zknowledgeBaseService.delete(knowledgeBaseByIdFromDB.getKnowledgeBaseId(), userName);
                if (deleteRemote == null || deleteRemote.getCode() != 0 || !deleteRemote.getData()) {
                    log.error("远程删除知识库失败");
                    return Result.fromException(new Exception("远程删除知识库失败"));
                }
            }

            // 更新db
            knowledgeBaseByIdFromDB.setDeleted(1);
            boolean dbRes = updateKnowledgeBase(knowledgeBaseByIdFromDB);
            return Result.success(dbRes);
        } catch (Exception e) {
            log.error("deleteKnowledge error", e);
            return Result.fromException(new Exception(e));
        }
    }

    public Result<List<ZKnowledgeRes>> searchKnowledge(Long knowledgeId, String queryText, String userName) {
        log.info("searchKnowledge knowledgeId:{},queryText:{},userName:{}", knowledgeId, queryText, userName);
        try {
            // 判断知识库灰度，调用private—knowledg
            if (grayService.gray2Knowledge(knowledgeId)) {
                return Result.success(querySimilarKnowledgeV2(knowledgeId, queryText, userName));
            }
            ZKnowledgeReq req = new ZKnowledgeReq();
            req.setKnowledgeBaseId(knowledgeId);
            req.setUserName(userName);
            req.setQueryText(queryText);
            req.setLimit(1);
            Result<List<ZKnowledgeRes>> listResult = zknowledgeBaseService.querySimilarKnowledge(req);
            if (listResult.getCode() != 0 || listResult.getData() == null) {
                log.info("searchKnowledge error,userName: {},queryText: {},listResult:{}", userName, queryText, listResult);
                return Result.fail(STATUS_INTERNAL_ERROR, "查询知识库失败，请检查是否有文件上传");
            }
            return Result.success(listResult.getData());
        } catch (Exception e) {
            log.error("searchKnowledge error", e);
            return Result.fromException(new Exception(e));
        }
    }


    // 调用KnowledgeBaseBlockProvider.querySimilarKnowledge相似度搜素
    public List<ZKnowledgeRes> querySimilarKnowledgeV2(Long knowledgeId, String queryText, String userName) {
        log.info("querySimilarKnowledgeFromBlockProvider knowledgeId:{}, queryText:{}, userName:{}", knowledgeId, queryText, userName);
        try {
            KnowledgeReq req = new KnowledgeReq();
            req.setKnowledgeBaseId(knowledgeId);
            req.setUserName(userName);
            req.setQueryText(queryText);
            req.setLimit(1);
            Result<List<KnowledgeResp>> listResult = knowledgeBaseBlockProvider.querySimilarKnowledge(req);
            // 将List<KnowledgeResp> 转化为 List<ZKnowledgeRes>
            return listResult.getData().stream().map(knowledgeResp -> {
                ZKnowledgeRes zKnowledgeRes = new ZKnowledgeRes();
                zKnowledgeRes.setKnowledgeBaseId(knowledgeResp.getKnowledgeBaseId());
                zKnowledgeRes.setContent(knowledgeResp.getContent());
                zKnowledgeRes.setDistance(knowledgeResp.getDistance());
                // TODO  blockId 需要改成string
                zKnowledgeRes.setVector(knowledgeResp.getVector().stream().mapToDouble(Double::doubleValue).toArray());
                return zKnowledgeRes;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("querySimilarKnowledgeFromBlockProvider error", e);
            throw new RuntimeException("querySimilarKnowledgeFromBlockProvider error");
        }
    }

    public Result<Void> deleteKnowledgeFile(KnowledgeBaseFilesParam param, String name) {
        log.info("deleteKnowledgeFile param:{},name:{}", param, name);
        try {
            List<Long> fieldIdList = new ArrayList<>();
            fieldIdList.add(param.getFileId());
            Result<Void> voidResult;
            if (grayService.gray2Knowledge(param.getKnowledgeBaseId())) {
                voidResult = knowledgeBaseFileProvider.deleteFile(param.getKnowledgeBaseId(), param.getFileId(), name);
            } else {
                voidResult = zknowledgeBaseService.deleteFileInKnowledgeBase(param.getKnowledgeBaseId(), fieldIdList, name, false);
            }

            return voidResult;
        } catch (Exception e) {
            log.error("deleteKnowledgeFile error", e);
            return Result.fromException(new Exception(e));
        }
    }

    public Result<Map<String, String>> uploadKnowledgeFile(Long knowledgeBaseId, String account) {
        log.info("uploadKnowledgeFile knowledgeBaseId:{},account:{}", knowledgeBaseId, account);
        try {
            Map<String, String> ossPolicy = zknowledgeBaseService.getOSSPolicy(knowledgeBaseId, account);
            log.info("remote getOSSPolicy res: {}", ossPolicy);
            return Result.success(ossPolicy);
        } catch (Exception e) {
            log.error("uploadKnowledgeFile error", e);
            return null;
        }
    }

    public Result<String> uploadKnowledgeFileV2(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }
            log.warn("upload file:{}", file.getName());
            AuthUserVo user = UserUtil.getUser();
            String account = user.getAccount();
            Preconditions.checkArgument(null != account);

            // Check for file type and size constraints
//            String contentType = file.getContentType();
            long size = file.getSize();
//            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
//                throw new IllegalArgumentException("Invalid file type. Only JPEG and PNG are allowed.");
//            }
            if (size > (40 * 1024 * 1024)) { // 40MB limit
                throw new IllegalArgumentException("File size exceeds the permissible limit of 40MB.");
            }
            String originalFilename = file.getOriginalFilename();
            String fileName = finalFileName(account, normalizeFileName(originalFilename));
            log.info("originalFilename:{} -> fileName:{}", originalFilename, fileName);
            HttpClientV2.upload(this.fileserverAddress + "/upload?name=" + fileName + "&token=" + this.fileServerToken, file.getBytes());
            String url = this.fileserverAddress + "/download?name=" + fileName + "&token=" + this.fileServerToken;

            return Result.success(fileName);
        } catch (IllegalArgumentException e) {
            log.error("fileServerUploadFile IllegalArgumentException:", e);
            return Result.fromException(ExceptionHelper.create(GeneralCodes.ParamError, e.getMessage()));
        } catch (Throwable e) {
            log.error("fileServerUploadFile Throwable:", e);
            return Result.fromException(ExceptionHelper.create(GeneralCodes.InternalError, e.getMessage()));

        }
    }

    private String finalFileName(String account, String fileName) {
        return account + "_j_" + fileName;
    }

    public String normalizeFileName(String fileName) {
        String fileServerName = "";

        String type = fileName.substring(fileName.lastIndexOf("."));
        String input = fileName;
        if (StringUtils.isBlank(type)) {
            type = "";
        } else {
            input = fileName.substring(0, fileName.lastIndexOf("."));
        }

        // 正则表达式匹配字母、数字和下划线
        Pattern pattern = Pattern.compile("\\w");
        Matcher matcher = pattern.matcher(input);

        StringBuilder alphaNumericUnderscore = new StringBuilder();
        StringBuilder others = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (matcher.reset(String.valueOf(c)).matches()) {
                alphaNumericUnderscore.append(c);
            } else {
                others.append(c);
            }
        }

        String left = alphaNumericUnderscore.toString();
        String right = others.toString();

        fileServerName = (null != left ? left : "") + (null != right ? right.hashCode() : "");

        return fileServerName + type;
    }

    public Result<List<ZKnowledgeBaseFileBlockDTO>> listKnowledgeFileBlock(@RequestParam Long knowledgeId, @RequestParam Long knowledgeFileId, String userName) {
        log.info("listKnowledgeFileBlock knowledgeId:{},knowledgeFileId:{},userName:{}", knowledgeId, knowledgeFileId, userName);
        try {
            //灰度
            if (grayService.gray2Knowledge(knowledgeId)) {
                Result<List<KnowledgeBaseFileBlockDTO>> res = knowledgeBaseBlockProvider.listKnowledgeBaseFileBlocks(knowledgeFileId, knowledgeId, userName);
                if (res.getData() == null) {
                    return Result.success(new ArrayList<>());
                }
                List<ZKnowledgeBaseFileBlockDTO> listResult = res.getData().stream().map(
                        block -> {
                            ZKnowledgeBaseFileBlockDTO blockDTO = new ZKnowledgeBaseFileBlockDTO();
                            BeanUtils.copyProperties(block, blockDTO);
                            return blockDTO;
                        }
                ).collect(Collectors.toList());
                return Result.success(listResult);
            } else {
                Result<List<ZKnowledgeBaseFileBlockDTO>> listResult = zknowledgeBaseService.listKnowledgeBaseFileBlocks(knowledgeId, knowledgeFileId, userName, false);
                return Result.success(listResult.getData());
            }

        } catch (Exception e) {
            log.error("listKnowledgeFileBlock error", e);
            return Result.fromException(new Exception(e));
        }
    }

    public Result<Boolean> deleteKnowledgeFileBlock(long knowledgeId, long knowledgeFileId, String knowledgeFileBlockId, String userName) {
        log.info("deleteKnowledgeFileBlock knowledgeId: {},knowledgeFileId: {} knowledgeFileBlockId:{},userName:{}",
                knowledgeId, knowledgeFileId, knowledgeFileBlockId, userName);
        try {
            if (grayService.gray2Knowledge(knowledgeId)) {
                // 删除private-knowledge
                return knowledgeBaseBlockProvider.deleteBlock(knowledgeId, userName, knowledgeFileId, knowledgeFileBlockId);
            } else {
                // 删除 Z
                return zknowledgeBaseService.deleteKnowledgeBaseFileBlock(knowledgeId, userName, knowledgeFileId, knowledgeFileBlockId, false);
            }
        } catch (Exception e) {
            log.error("deleteKnowledgeFileBlock error", e);
            return Result.fromException(new Exception(e));
        }
    }

    public Result<ZKnowledgeBaseFileBlockDTO> addOrUpdateKnowledgeBaseFileBlock(long knowledgeId, long knowledgeFileId, String userName, String blockId, String blockContent) {
        log.info("addOrUpdateKnowledgeBaseFileBlock knowledgeId: {},knowledgeFileId: {} userName:{}", knowledgeId, knowledgeFileId, userName);
        try {
            if (grayService.gray2Knowledge(knowledgeId)) {
                // 写private-knowledge
                KnowledgeBaseFileBlockDTO knowledgeBaseFileBlockDTO = new KnowledgeBaseFileBlockDTO();
                knowledgeBaseFileBlockDTO.setFileId(knowledgeFileId);
                knowledgeBaseFileBlockDTO.setId(knowledgeId);
                knowledgeBaseFileBlockDTO.setBlockId(blockId);
                knowledgeBaseFileBlockDTO.setBlockContent(blockContent);
                Result<KnowledgeBaseFileBlockDTO> knowledgeBaseFileBlockDTOResult = knowledgeBaseBlockProvider.addOrUpdateKnowledgeBaseFileBlock(knowledgeId, userName, knowledgeBaseFileBlockDTO);
                if (knowledgeBaseFileBlockDTOResult == null || knowledgeBaseFileBlockDTOResult.getCode() != GeneralCodes.OK.getCode()) {
                    log.error("addOrUpdateKnowledgeBaseFileBlock for private-knowledge fail");
                    return Result.fromException(new Exception("addOrUpdateKnowledgeBaseFileBlock for private-knowledge fail"));
                }
                ZKnowledgeBaseFileBlockDTO res = new ZKnowledgeBaseFileBlockDTO();
                KnowledgeBaseFileBlockDTO data = knowledgeBaseFileBlockDTOResult.getData();
                BeanUtils.copyProperties(data, res);
                return Result.success(res);
            } else {
                // 写Z
                ZKnowledgeBaseFileBlockDTO zKnowledgeBaseFileBlockDTO = new ZKnowledgeBaseFileBlockDTO();
                // 填充zKnowledgeBaseFileBlockDTO
                zKnowledgeBaseFileBlockDTO.setFileId(knowledgeFileId);
                zKnowledgeBaseFileBlockDTO.setId(knowledgeId);
                zKnowledgeBaseFileBlockDTO.setBlockId(blockId);
                zKnowledgeBaseFileBlockDTO.setBlockContent(blockContent);
                Result<ZKnowledgeBaseFileBlockDTO> zKnowledgeBaseFileBlockDTOResult = zknowledgeBaseService.addOrUpdateKnowledgeBaseFileBlock(knowledgeId, userName, zKnowledgeBaseFileBlockDTO, false);
                log.info("addOrUpdateKnowledgeBaseFileBlock for Z success");
                return zKnowledgeBaseFileBlockDTOResult;
            }
        } catch (Exception e) {
            log.error("addOrUpdateKnowledgeBaseFileBlock error", e);
            return Result.fromException(new Exception(e));
        }
    }


    public List<KnowledgeBo> listKnowledgeBase(List<Long> knowledgeBaseIdList) {
        List<KnowledgeBo> res = new ArrayList<>();
        try {
            log.info("listKnowledgeBase.knowledgeBaseIdList: {}", knowledgeBaseIdList);
            knowledgeBaseIdList.forEach(it -> {

                M78KnowledgeBase oneRes = super.getOne(QueryWrapper.create().eq("knowledge_base_id", it).eq("deleted", 0));
                KnowledgeBo build = KnowledgeBo.builder().knowledgeName(oneRes.getKnowledgeBaseName()).creator(oneRes.getCreator())
                        .gmtCreate(Date.from(oneRes.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()))
                        .gmtModified(Date.from(oneRes.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant()))
                        .remark(oneRes.getRemark())
                        .avatarUrl(oneRes.getAvatarUrl())
                        .knowledgeBaseId(oneRes.getKnowledgeBaseId())
                        .version(oneRes.getVersion()).build();
                res.add(build);
            });
            //Result<List<ZKnowledgeBaseDTO>> knowledgeBaseByIds = zknowledgeBaseService.getKnowledgeBaseByIds(knowledgeBaseIdList);
            return res;
        } catch (Exception e) {
            log.error("listKnowledgeBase error", e);
            return res;
        }
    }

    public Result<List<KnowledgeBaseFileResDto>> listKnowledgeBaseFiles(Long workspaceId, Long knowledgeBaseId, List<Long> fileIdList, String userName) {
        List<KnowledgeBaseFileResDto> resList = new ArrayList<>();
        if (grayService.gray2Knowledge(knowledgeBaseId)) {
            Result<List<KnowledgeBaseFileDto>> listResult = knowledgeBaseProvider.queryFileList(knowledgeBaseId, fileIdList, userName);
            listResult.getData().forEach(it -> {
                KnowledgeBaseFileResDto res = new KnowledgeBaseFileResDto();
                BeanUtils.copyProperties(it, res);
                resList.add(res);
            });
        } else {
            Result<List<ZKnowledgeBaseFilesDTO>> listResult = zknowledgeBaseService.listKnowledgeBaseFiles(knowledgeBaseId, fileIdList, userName);
            listResult.getData().forEach(it -> {
                KnowledgeBaseFileResDto res = new KnowledgeBaseFileResDto();
                BeanUtils.copyProperties(it, res);
                resList.add(res);
            });
        }

        if (!CollectionUtils.isEmpty(resList)) {
            boolean isWorkspaceIdNull = Objects.isNull(workspaceId);
            Integer workspaceRole = null;

            if (!isWorkspaceIdNull) {
                SessionAccount account = new SessionAccount();
                account.setUsername(userName);
                account.setUserType(0);
                workspaceRole = workspaceService.getWorkspaceRole(account, workspaceId);
            }

            for (KnowledgeBaseFileResDto it : resList) {
                boolean isUserNameEqualCreator = userName.equals(it.getCreator());
                if (isWorkspaceIdNull) {
                    it.setSelf(isUserNameEqualCreator);
                } else {
                    if (workspaceRole != null && workspaceRole > UserRoleEnum.UN_KNOW.getCode()) {
                        it.setSelf(true);
                    }
                }
            }
        }
        return Result.success(resList);
    }


    public Result<List<KnowledgeBaseFileResDto>> listKnowledgeBaseFiles(Long knowledgeBaseId, List<Long> fileIdList, String userName) {
        return listKnowledgeBaseFiles(null, knowledgeBaseId, fileIdList, userName);
    }


    public Result<List<KnowledgeBaseFileResDto>> listKnowledgeBaseFilesWithContent(Long knowledgeBaseId, List<Long> fileIdList, String userName) {
        Result<List<ZKnowledgeBaseFilesDTO>> listResult = zknowledgeBaseService.listKnowledgeBaseFilesWithContent(knowledgeBaseId, fileIdList, userName);
        List<KnowledgeBaseFileResDto> resList = new ArrayList<>();
        listResult.getData().forEach(it -> {
            KnowledgeBaseFileResDto res = new KnowledgeBaseFileResDto();
            BeanUtils.copyProperties(it, res);
            if (userName.equals(it.getCreator())) {
                res.setSelf(true);
            }
            resList.add(res);
        });
        return Result.success(resList);
    }

    public Result<ZKnowledgeBaseAnswer> summaryInKnowledgeBase(ZKnowledgeReq req) {
        return zknowledgeBaseService.summaryInKnowledgeBase(req);
    }

    public Result<List<ZKnowledgeRes>> querySimilarKnowledge(ZKnowledgeReq req) {
        if (grayService.gray2Knowledge(req.getKnowledgeBaseId())) {
            KnowledgeReq knowledgeReq = new KnowledgeReq();
            BeanUtils.copyProperties(req, knowledgeReq);
            Result<List<KnowledgeResp>> listResult = knowledgeBaseBlockProvider.querySimilarKnowledge(knowledgeReq);
            return Result.success(listResult.getData().stream().map(it -> {
                ZKnowledgeRes res = new ZKnowledgeRes();
                BeanUtils.copyProperties(it, res);
                return res;
            }).toList());
        }
        Result<List<ZKnowledgeRes>> rst = zknowledgeBaseService.querySimilarKnowledge(req);
        log.info("querySimilarKnowledge req:{},rst:{}", req, rst);
        return rst;
    }

    public Result<List<ZKnowledgeBaseFilesDTO>> uploadFileAndEmbeddings(MultipartFile file, Long knowledgeId, String account) {
        Result<String> uploadedRes = uploadKnowledgeFileV2(file);
        if (uploadedRes.getCode() != 0) {
            return Result.fail(STATUS_BAD_REQUEST, uploadedRes.getMessage());
        }
        KnowledgeBaseFilesParam param = new KnowledgeBaseFilesParam();
        param.setFilePath(uploadedRes.getData());
        param.setFileName(file.getOriginalFilename());
        param.setKnowledgeBaseId(knowledgeId);
        return embeddingFileKnowledge(List.of(param), account);
    }


    private List<M78KnowledgeBase> listMyKnowledgeBaseFromDB(String userName) {
        return super.list(QueryWrapper.create().eq("deleted", 0)
                .eq("creator", userName)).stream().toList();
    }

    private List<Long> listMyKnowledgeBaseIdFromDB(String userName) {
        return super.list(QueryWrapper.create().eq("deleted", 0)
                .eq("creator", userName)).stream().map(M78KnowledgeBase::getKnowledgeBaseId).toList();
    }

    private M78KnowledgeBase getKnowledgeBaseByIdFromDB(Long id) {
        M78KnowledgeBase byId = super.getById(id);
        if (byId.getDeleted() == 1) {
            // 删除状态过滤
            return null;
        }
        return byId;
    }

    private boolean updateKnowledgeBase(M78KnowledgeBase knowledgeBase) {
        return super.updateById(knowledgeBase);
    }

    private boolean transferAndInsertToDB(ZKnowledgeBaseDTO remote, KnowledgeCreateV2Req req) {
        String avatarUrl = req.getAvatarUrl();
        Long workSpaceId = req.getWorkSpaceId();
        M78KnowledgeBase knowledgeBase = new M78KnowledgeBase();
        if (remote.getAuth() != null) {
            knowledgeBase.setAuth(remote.getAuth());
        } else {
            knowledgeBase.setAuth(0);
        }
        if (remote.getCreator() != null) {
            knowledgeBase.setCreator(remote.getCreator());
        }
        knowledgeBase.setKnowledgeBaseId(remote.getId());
        if (remote.getType() != null) {
            knowledgeBase.setType(remote.getType());
        } else {
            knowledgeBase.setType("normal");
        }
        if (remote.getStatus() != null) {
            knowledgeBase.setStatus(remote.getStatus());
        }
        knowledgeBase.setRemark(remote.getRemark());
        if (avatarUrl != null && !StringUtils.isEmpty(avatarUrl)) {
            knowledgeBase.setAvatarUrl(avatarUrl);
        } else {
            // 替换成默认头像
            int randomNumber = random.nextInt(10);
            knowledgeBase.setAvatarUrl(String.valueOf(randomNumber));
        }
        knowledgeBase.setWorkSpaceId(workSpaceId);
        knowledgeBase.setKnowledgeBaseName(remote.getName());
        knowledgeBase.setUpdater(remote.getCreator());
        knowledgeBase.setCreateTime(LocalDateTime.now());
        knowledgeBase.setUpdateTime(LocalDateTime.now());
        knowledgeBase.setDeleted(0);

        log.info("transferToDB: {}", knowledgeBase);
        return super.save(knowledgeBase);
    }

    private boolean transferAndInsertToDB(KnowledgeBaseDto remote, KnowledgeCreateV2Req req, Integer version) {
        String avatarUrl = req.getAvatarUrl();
        Long workSpaceId = req.getWorkSpaceId();
        M78KnowledgeBase knowledgeBase = new M78KnowledgeBase();
        if (remote.getAuth() != null) {
            knowledgeBase.setAuth(remote.getAuth());
        } else {
            knowledgeBase.setAuth(0);
        }
        if (remote.getCreator() != null) {
            knowledgeBase.setCreator(remote.getCreator());
        }
        knowledgeBase.setKnowledgeBaseId(remote.getId());
        if (remote.getType() != null) {
            knowledgeBase.setType(remote.getType());
        } else {
            knowledgeBase.setType("normal");
        }
        if (remote.getStatus() != null) {
            knowledgeBase.setStatus(remote.getStatus());
        }
        knowledgeBase.setRemark(remote.getRemark());
        if (avatarUrl != null && !StringUtils.isEmpty(avatarUrl)) {
            knowledgeBase.setAvatarUrl(avatarUrl);
        } else {
            // 替换成默认头像
            int randomNumber = random.nextInt(10);
            knowledgeBase.setAvatarUrl(String.valueOf(randomNumber));
        }
        knowledgeBase.setWorkSpaceId(workSpaceId);
        knowledgeBase.setKnowledgeBaseName(remote.getName());
        knowledgeBase.setUpdater(remote.getCreator());
        knowledgeBase.setCreateTime(LocalDateTime.now());
        knowledgeBase.setUpdateTime(LocalDateTime.now());
        if (version != null) {
            knowledgeBase.setVersion(version);
        }
        knowledgeBase.setDeleted(0);

        log.info("transferToDB: {}", knowledgeBase);
        return super.save(knowledgeBase);
    }

    private Map<String, String> transfer2LabelMap(String labels) {
        //将labels字符串转换成Map
        Map<String, String> labelMap = new HashMap<>();

        if (StringUtils.isNotBlank(labels)) {
            // 假设标签格式为 "key1:value1,key2:value2"
            String[] labelArray = labels.split(",");
            for (String label : labelArray) {
                String[] keyValue = label.split(":");
                if (keyValue.length == 2) {
                    labelMap.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return labelMap;
    }

    public Result<Boolean> editKnowledgeBaseV2(KnowledgeCreateV2Req req, String userName) {
        try {
            log.info("KnowledgeService.editKnowledgeBaseV2 req:{} userName:{}", req, userName);
            if (req.getId() == null || req.getId() <= 0) {
                log.error("id非法");
                return Result.fromException(new Exception("id非法"));
            }
            // 查库
            M78KnowledgeBase knowledgeBaseByIdFromDB = getKnowledgeBaseByIdFromDB(req.getId());
            if (knowledgeBaseByIdFromDB == null || !knowledgeBaseByIdFromDB.getCreator().equals(userName)) {
                log.error("知识库不存在或无权限");
                return Result.fail(STATUS_BAD_REQUEST, "知识库不存在或无权限");
            }
            // 查远程
            Result<KnowledgeBaseDto> knowledgeBaseRemoteRes = knowledgeBaseProvider.getKnowledgeBase(knowledgeBaseByIdFromDB.getKnowledgeBaseId(),
                    knowledgeBaseByIdFromDB.getKnowledgeBaseName());
            if (knowledgeBaseRemoteRes.getCode() != 0 || knowledgeBaseRemoteRes.getData() == null) {
                log.error("知识库远程不存在或无权限");
                return Result.fail(STATUS_BAD_REQUEST, "知识库远程不存在或无权限");
            }

            // 更新远程，只能更新部分字段(name auth remark label可改)
            KnowledgeBaseDto remoteData = knowledgeBaseRemoteRes.getData();
            KnowledgeBaseDto remoteDataBack = new KnowledgeBaseDto();
            BeanUtils.copyProperties(remoteData, remoteDataBack);

            if (!StringUtils.isEmpty(req.getRemark())) {
                remoteData.setRemark(req.getRemark());
                knowledgeBaseByIdFromDB.setRemark(req.getRemark());
            }
            if (!StringUtils.isEmpty(req.getName())) {
                remoteData.setName(req.getName());
                knowledgeBaseByIdFromDB.setKnowledgeBaseName(req.getName());
            }
            if (req.getAuth() != null) {
                remoteData.setAuth(req.getAuth());
                knowledgeBaseByIdFromDB.setAuth(req.getAuth());
            }
            if (req.getLabels() != null) {
                remoteData.setLabels(transfer2LabelMap(req.getLabels()));
                knowledgeBaseByIdFromDB.setLabels(req.getLabels());
            }
            if (req.getAvatarUrl() != null && StringUtils.isNotBlank(req.getAvatarUrl())) {
                knowledgeBaseByIdFromDB.setAvatarUrl(req.getAvatarUrl());
            }

            remoteData.setOperator(userName);
            Result<Boolean> remoteEditRes = knowledgeBaseProvider.editKnowledgeBase(remoteData);
            if (remoteEditRes.getCode() != 0 || !remoteEditRes.getData()) {
                log.info("知识库远程更新失败");
                return Result.fromException(new Exception("知识库远程更新失败"));
            }
            //更新本地
            boolean upRes = updateKnowledgeBase(knowledgeBaseByIdFromDB);
            if (!upRes) {
                // 本地更新失败 回撤？
                knowledgeBaseProvider.editKnowledgeBase(remoteDataBack);
                return Result.success(false);
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("KnowledgeService.editKnowledgeBaseV2 error", e);
            return Result.fromException(new Exception(e));
        }
    }
}

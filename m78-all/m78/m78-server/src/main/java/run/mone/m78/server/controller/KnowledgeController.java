package run.mone.m78.server.controller;

import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.multipart.MultipartFile;
import run.mone.ai.z.dto.ZKnowledgeBaseDTO;
import run.mone.ai.z.dto.ZKnowledgeBaseFileBlockDTO;
import run.mone.ai.z.dto.ZKnowledgeBaseFilesDTO;
import run.mone.ai.z.dto.ZKnowledgeRes;
import run.mone.m78.api.bo.knowledge.KnowledgeBaseBlockDTO;
import run.mone.m78.api.bo.knowledge.KnowledgeBaseFilesParam;
import run.mone.m78.api.enums.UserRoleEnum;
import run.mone.m78.server.config.auth.RoleControl;
import run.mone.m78.service.bo.knowledge.KnowledgeCreateV2Req;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dto.knowledge.KnowledgeBaseFileResDto;
import run.mone.m78.service.dto.knowledge.KnowledgeBaseResDto;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.gray.GrayService;
import run.mone.m78.service.service.knowledge.KnowledgeService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_BAD_REQUEST;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

/**
 * @author wmin
 * @date 2024/1/29
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/knowledge")
public class KnowledgeController {

    @Autowired
    private LoginService loginService;

    @Resource
    private KnowledgeService knowledgeService;

    @Resource
    private GrayService grayService;

    /**
     * 创建知识库
     */
    @PostMapping(value = "/create")
    public Result<ZKnowledgeBaseDTO> create(@RequestParam String name, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.create(name, account.getUsername());
    }

    // 获取单个知识库信息
    @GetMapping(value = "/getSingleKnowledgeBase")
    public Result<KnowledgeBaseResDto> getSingleKnowledgeBase(@RequestParam Long id, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.getSingleKnowledge(id, account.getUsername());
    }

    // 创建知识库(落库版本)
    @PostMapping(value = "/createKnowledgeBase")
    @RoleControl(role = UserRoleEnum.USER)
    public Result<Long> createKnowledge(@RequestBody KnowledgeCreateV2Req req, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.createKnowledgeBase(req, account.getUsername());
    }

    // 编辑知识库元信息
    @PostMapping(value = "/editKnowledgeBase")
    @RoleControl(role = UserRoleEnum.USER)
    public Result<Boolean> editKnowledgeBase(@RequestBody KnowledgeCreateV2Req req, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.editKnowledgeBase(req, account.getUsername());
    }

    /**
     * 我的知识库列表
     */
    @RequestMapping(value = "/myList", method = RequestMethod.GET)
    public Result<List<ZKnowledgeBaseDTO>> getList(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.listMyKnowledgeBase(account.getUsername());
    }

    // 知识库列表(落库版本)
    @RequestMapping(value = "/listKnowledgeBase", method = RequestMethod.GET)
    public Result<Page<KnowledgeBaseResDto>> listKnowledgeBase(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1")
    int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, String knowledgeBaseName, String creator
            , @RequestParam(value = "workSpaceId") Long workSpaceId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.listMyKnowledgeBaseV2(account.getUsername(), pageNum, pageSize, knowledgeBaseName, creator, workSpaceId);
    }

    // 单个知识库下的知识列表
    @RequestMapping(value = "/file/myList", method = RequestMethod.GET)
    public Result<List<KnowledgeBaseFileResDto>> getFileList(@RequestParam Long knowledgeBaseId, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.listKnowledgeBaseFiles(knowledgeBaseId, null, account.getUsername());
    }

    @RequestMapping(value = "/file/myList2", method = RequestMethod.GET)
    public Result<List<KnowledgeBaseFileResDto>> getFileList2(@RequestParam Long knowledgeBaseId, @RequestParam Long workspaceId, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.listKnowledgeBaseFiles(workspaceId, knowledgeBaseId, null, account.getUsername());
    }

    // 解析知识
    @PostMapping(value = "/embedding")
    public Result<List<ZKnowledgeBaseFilesDTO>> embeddingFileKnowledge(@RequestBody List<KnowledgeBaseFilesParam> knowledgeBaseFilesParams, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.embeddingFileKnowledge(knowledgeBaseFilesParams, account.getUsername());
    }

    //删除知识库
    @PostMapping(value = "/deleteKnowledgeBase")
    public Result<Boolean> deleteKnowledgeBase(@RequestParam Long id, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (id == null || id <= 0) {
            return Result.fromException(new Exception("参数错误"));
        }
        return knowledgeService.deleteKnowledge(id, account.getUsername());
    }

    // 检索知识
    @GetMapping(value = "/searchKnowledge")
    public Result<List<ZKnowledgeRes>> searchKnowledge(HttpServletRequest request, @RequestParam Long knowledgeId, @RequestParam String queryText) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.searchKnowledge(knowledgeId, queryText, account.getUsername());
    }

    // 知识上传
    @GetMapping(value = "/uploadKnowledgeFile")
    public Result<Map<String, String>> uploadKnowledgeFile(@RequestParam Long knowledgeId, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.uploadKnowledgeFile(knowledgeId, account.getUsername());
    }

    // 删除单个知识库下的知识文件
    @PostMapping(value = "/deleteKnowledgeFile")
    public Result<Void> deleteKnowledgeFile(@RequestBody KnowledgeBaseFilesParam param, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.deleteKnowledgeFile(param, account.getUsername());
    }

    // 删除单个知识库下的知识里的block
    @PostMapping(value = "/deleteKnowledgeFileBlock")
    public Result<Boolean> deleteKnowledgeFileBlock(@RequestParam Long knowledgeId, @RequestParam Long knowledgeFileId,
                                                    @RequestParam String knowledgeFileBlockId, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.deleteKnowledgeFileBlock(knowledgeId, knowledgeFileId, knowledgeFileBlockId, account.getUsername());
    }

    //单个知识库下面知识的block列表
    @GetMapping(value = "/listKnowledgeFileBlock")
    public Result<List<KnowledgeBaseBlockDTO>> listKnowledgeFileBlock(@RequestParam Long knowledgeId,
                                                                           @RequestParam Long knowledgeFileId, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        Result<List<ZKnowledgeBaseFileBlockDTO>> listResult = knowledgeService.listKnowledgeFileBlock(knowledgeId, knowledgeFileId, account.getUsername());
        if(listResult == null || listResult.getData() == null) {
            return Result.success(new ArrayList<>());
        }
        List<KnowledgeBaseBlockDTO> result;
        boolean gray = grayService.gray2Knowledge(knowledgeId);
        result = listResult.getData().stream().map(item -> {
                KnowledgeBaseBlockDTO knowledgeBaseBlockDTO = new KnowledgeBaseBlockDTO();
                BeanUtils.copyProperties(item, knowledgeBaseBlockDTO);
                if(gray) {
                    knowledgeBaseBlockDTO.setId(item.getBlockId());
                } else {
                    knowledgeBaseBlockDTO.setId(item.getId().toString());
                }
                return knowledgeBaseBlockDTO;
            }).toList();

        return Result.success(result);
    }

    //添加或更新知识文件里的block
    @PostMapping(value = "addOrUpdateKnowledgeBaseFileBlock")
    public Result<ZKnowledgeBaseFileBlockDTO> addOrUpdateKnowledgeBaseFileBlock(@RequestParam Long knowledgeId,
                                                                                @RequestParam Long knowledgeFileId, @RequestParam(required = false) String blockId,
                                                                                @RequestParam(required = false) String blockContent, HttpServletRequest request) {

        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.addOrUpdateKnowledgeBaseFileBlock(knowledgeId, knowledgeFileId, account.getUsername(), blockId, blockContent);
    }

    // 知识上传
    @PostMapping(value = "/uploadKnowledgeFileV2")
    public Result<String> uploadKnowledgeFileV2(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return knowledgeService.uploadKnowledgeFileV2(file);
    }


    /**
     * 上传知识库文件并进行解析（会删除知识库下原有的同名文件和向量数据）
     *
     * @param request
     * @param file
     * @return
     */
    @PostMapping(value = "/uploadKnowledgeFileAndEmbeddings")
    public Result<String> uploadKnowledgeFileAndEmbeddings(HttpServletRequest request, MultipartFile file, Long knowledgeBaseId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        Result<List<KnowledgeBaseFileResDto>> listResult = knowledgeService.listKnowledgeBaseFiles(knowledgeBaseId, null, account.getUsername());
        if (listResult.getCode() != 0) {
            return Result.fail(STATUS_BAD_REQUEST, "查询知识库文件失败");
        }
        List<KnowledgeBaseFileResDto> data = listResult.getData();
        List<KnowledgeBaseFileResDto> sameNameFile = data.stream().filter(item -> item.getFileName().equals(file.getOriginalFilename())).toList();

        if (CollectionUtils.isEmpty(data) || CollectionUtils.isEmpty(sameNameFile)) {
            Result<List<ZKnowledgeBaseFilesDTO>> embeddings = knowledgeService.uploadFileAndEmbeddings(file, knowledgeBaseId, account.getUsername());
            if (embeddings.getCode() == 0) {
                return Result.success("success");
            } else {
                return Result.fail(STATUS_BAD_REQUEST, embeddings.getMessage());
            }
        }

        KnowledgeBaseFileResDto knowledgeBaseFileResDto = sameNameFile.getFirst();
        KnowledgeBaseFilesParam deleteParam = new KnowledgeBaseFilesParam();
        deleteParam.setFileId(knowledgeBaseFileResDto.getId());
        deleteParam.setKnowledgeBaseId(knowledgeBaseId);
        Result<Void> voidResult = knowledgeService.deleteKnowledgeFile(deleteParam, account.getUsername());
        if (voidResult.getCode() != 0) {
            return Result.fail(STATUS_BAD_REQUEST, voidResult.getMessage());
        }

        Result<List<ZKnowledgeBaseFilesDTO>> uploadFileAndEmbeddings = knowledgeService.uploadFileAndEmbeddings(file, knowledgeBaseId, account.getUsername());
        if (uploadFileAndEmbeddings.getCode() == 0) {
            return Result.success("success");
        } else {
            return Result.fail(STATUS_BAD_REQUEST, uploadFileAndEmbeddings.getMessage());
        }
    }

}

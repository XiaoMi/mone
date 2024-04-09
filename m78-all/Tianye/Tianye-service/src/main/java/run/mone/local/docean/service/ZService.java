package run.mone.local.docean.service;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import run.mone.ai.z.dto.ZKnowledgeBaseDTO;
import run.mone.ai.z.dto.ZKnowledgeBaseFileBlockDTO;
import run.mone.ai.z.dto.ZKnowledgeBaseFilesDTO;
import run.mone.ai.z.dto.ZKnowledgeReq;
import run.mone.ai.z.service.KnowledgeBaseService;
import run.mone.ai.z.service.ZDubboService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZService {

    @Reference(interfaceClass = ZDubboService.class, group = "staging", version = "1.0", check = false)
    private ZDubboService zDubboService;

    @Reference(interfaceClass = KnowledgeBaseService.class, group = "staging", version = "1.0", timeout = 30000, check = false)
    private KnowledgeBaseService knowledgeBaseService;

    /**
     * 根据token获取用户名
     *
     * @param token
     * @return
     */
    public String getUserByToken(String token) {
        return zDubboService.getUserName(token);
    }

    public String getKnowledgeBaseSummaryAnswer(ZKnowledgeReq req) {
        return knowledgeBaseService.summaryInKnowledgeBase(req).getData().getAnswer();
    }


    public String getKnowledgeBaseFilesContentConcatenated(Long knowledgeBaseId, String account) {
        if (null == knowledgeBaseId) {
            return "";
        }
        Result<List<ZKnowledgeBaseFilesDTO>> rst = knowledgeBaseService.listKnowledgeBaseFilesWithContent(knowledgeBaseId, Lists.newArrayList(), account);
        if (rst.getCode() != 0 || CollectionUtils.isEmpty(rst.getData())){
            log.error("listKnowledgeBaseFilesWithContent rst:{}", rst);
            return "";
        }
        return rst.getData().stream().map(it -> it.getFileContent()).collect(Collectors.joining("\n"));
    }


    /**
     * 获取用户知识库id
     *
     * @param userName
     * @return
     */
    public Long getKnowledgeIdByUserName(String userName, Map<String, String> lebels) {
        Result<List<ZKnowledgeBaseDTO>> result = knowledgeBaseService.listMyKnowledgeBase(userName, lebels);
        if (result.getData() == null || result.getData().isEmpty()) {
            return null;
        } else {
            return result.getData().get(0).getId();
        }
    }

    public Long getPromptIdByUserName(String userName) {
        List<Long> ids = zDubboService.getPromptIdsByUserWithPrivate(userName);
        if (ids == null || ids.isEmpty()) {
            return null;
        } else {
            return ids.get(0);
        }
    }

    public Long getPromptIdByUserNameAndLabel(String userName, String label) {
        List<Long> ids = zDubboService.getPromptByUserWithTagAndLabel(userName, label);
        if (ids == null || ids.isEmpty()) {
            return null;
        } else {
            return ids.get(0);
        }
    }

    public List<ZKnowledgeBaseDTO> getKnowledgeList(String username) {
        Map<String, String> lebels = new HashMap<>();
//        lebels.put("private", "true");
        return knowledgeBaseService.listMyKnowledgeBase(username, lebels).getData();
    }

    public List<ZKnowledgeBaseFilesDTO> listKnowledgeBaseFiles(Long knowledgeId, List<Long> fileIds, String account) {
        return knowledgeBaseService.listKnowledgeBaseFiles(knowledgeId, fileIds, account).getData();
    }

    public List<ZKnowledgeBaseFileBlockDTO> listKnowledgeBaseFileBlocks(Long knowledgeId, Long fileId, String account) {
        return knowledgeBaseService.listKnowledgeBaseFileBlocks(knowledgeId, fileId, account).getData();
    }

    public ZKnowledgeBaseFileBlockDTO updateKnowledgeBaseFileBlock(Long knowledgeId, String account, ZKnowledgeBaseFileBlockDTO dto) {
        return knowledgeBaseService.addOrUpdateKnowledgeBaseFileBlock(knowledgeId, account, dto).getData();
    }

    public Void deleteFileInKnowledgeBase(Long knowledgeId, List<Long> fileIds, String account) {
        return knowledgeBaseService.deleteFileInKnowledgeBase(knowledgeId, fileIds, account).getData();
    }

    public Boolean deleteKnowledgeBaseFileBlock(Long knowledgeId, String account, Long fileId, String blockId){
        return knowledgeBaseService.deleteKnowledgeBaseFileBlock(knowledgeId, account, fileId, blockId).getData();
    }
}
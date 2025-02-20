package run.mone.local.docean.service;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import run.mone.knowledge.api.KnowledgeBaseBlockProvider;
import run.mone.knowledge.api.KnowledgeBaseFileProvider;
import run.mone.knowledge.api.KnowledgeBaseProvider;
import run.mone.knowledge.api.dto.KnowledgeBaseDto;
import run.mone.knowledge.api.dto.KnowledgeBaseFileBlockDTO;
import run.mone.knowledge.api.dto.KnowledgeReq;
import run.mone.knowledge.api.dto.KnowledgeResp;
import run.mone.local.docean.util.GsonUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KnowledgeService {

    @Reference(interfaceClass = KnowledgeBaseBlockProvider.class, check = false, group = "$ref.knowledge.service.group", version = "$ref.knowledge.service.version", timeout = 10000)
    private KnowledgeBaseBlockProvider knowledgeBaseBlockProvider;

    @Reference(interfaceClass = KnowledgeBaseFileProvider.class, check = false, group = "$ref.knowledge.service.group", version = "$ref.knowledge.service.version", timeout = 10000)
    private KnowledgeBaseFileProvider knowledgeBaseFileProvider;

    @Reference(interfaceClass = KnowledgeBaseProvider.class, check = false, group = "$ref.knowledge.service.group", version = "$ref.knowledge.service.version", timeout = 10000)
    private KnowledgeBaseProvider knowledgeBaseProvider;

    public String querySimilarKnowledge(Long knowledgeBaseId, String account, String message, Integer limit) {
        KnowledgeReq knowledgeBaseDto = new KnowledgeReq();
        knowledgeBaseDto.setKnowledgeBaseId(knowledgeBaseId);
        knowledgeBaseDto.setQueryText(message);
        knowledgeBaseDto.setSimilarity(0.3F);
        knowledgeBaseDto.setLimit(limit);
        log.info("querySimilarKnowledge req: {}", GsonUtils.gson.toJson(knowledgeBaseDto));
        Result<List<KnowledgeResp>> listResult = knowledgeBaseBlockProvider.querySimilarKnowledge(knowledgeBaseDto);
        log.info("querySimilarKnowledge res: {}", GsonUtils.gson.toJson(listResult));
        List<KnowledgeResp> data = listResult.getData();
        if (null == data) {
            return "";
        }
        try {
            return data.stream().map(it -> it.getContent()).collect(Collectors.joining("\n"));
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return "";
    }

    public String querySimilarKnowledge(Long knowledgeBaseId, String account, String message, Integer limit, float similarity) {
        KnowledgeReq knowledgeBaseDto = new KnowledgeReq();
        knowledgeBaseDto.setKnowledgeBaseId(knowledgeBaseId);
        knowledgeBaseDto.setQueryText(message);
        knowledgeBaseDto.setSimilarity(similarity);
        knowledgeBaseDto.setLimit(limit);
        log.info("querySimilarKnowledge req: {}", GsonUtils.gson.toJson(knowledgeBaseDto));
        Result<List<KnowledgeResp>> listResult = knowledgeBaseBlockProvider.querySimilarKnowledge(knowledgeBaseDto);
        log.info("querySimilarKnowledge res: {}", GsonUtils.gson.toJson(listResult));
        List<KnowledgeResp> data = listResult.getData();
        if (null == data) {
            return "";
        }
        try {
            return data.stream().map(it -> it.getContent()).collect(Collectors.joining("\n"));
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return "";
    }

    public String getKnowledgeBaseFilesContentConcatenated(Long knowledgeBaseId, String account) {
        Result<List<KnowledgeBaseFileBlockDTO>> listResult = knowledgeBaseBlockProvider.listKnowledgeBaseFileBlocks(null, knowledgeBaseId, account);
        if (listResult.getCode() == 0) {
            List<KnowledgeBaseFileBlockDTO> data = listResult.getData();
            if (null == data) {
                return "";
            }
            try {
                return data.stream().map(it -> it.getBlockContent()).collect(Collectors.joining("\n"));
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return "";
    }

    // 这方法也没啥用
    public Long getKnowledgeIdByUserName(String userName) {
        Result<List<KnowledgeBaseDto>> listResult = knowledgeBaseProvider.listKnowledgeBase(userName, "");
        if (listResult.getCode() == 0) {
            List<KnowledgeBaseDto> data = listResult.getData();
            if (null == data) {
                return 0L;
            }
            try {
                return data.stream().map(KnowledgeBaseDto::getId).toList().getFirst();
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return null;
    }


}

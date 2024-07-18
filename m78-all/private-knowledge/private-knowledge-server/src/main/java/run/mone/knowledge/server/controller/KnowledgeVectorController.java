package run.mone.knowledge.server.controller;

import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.mone.knowledge.api.IKnowledgeVectorProvider;
import run.mone.knowledge.api.dto.KnowledgeVectorDto;
import run.mone.knowledge.api.dto.SimilarKnowledgeVectorQry;
import run.mone.knowledge.api.dto.SimilarKnowledgeVectorRsp;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wmin
 * @date 2024/2/20
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/knowledgeVector")
public class KnowledgeVectorController {

    @Resource
    private IKnowledgeVectorProvider knowledgeVectorProvider;

    @PostMapping(value = "/addOrUpdate")
    public Result<Boolean> insertOrUpdateKnowledgeVector(@RequestBody KnowledgeVectorDto knowledgeVectorDto, HttpServletRequest request) {
        log.info("insertOrUpdateKnowledgeVector req:{}", knowledgeVectorDto.getKnowledgeVectorDetailDtoList().size());
        return knowledgeVectorProvider.insertOrUpdateKnowledgeVector(knowledgeVectorDto);
    }

    @PostMapping(value = "/qrySimilar")
    public Result<List<SimilarKnowledgeVectorRsp>> qrySimilarKnowledgeVector(@RequestBody SimilarKnowledgeVectorQry similarKnowledgeVectorQry, HttpServletRequest request) {
        log.info("qrySimilarKnowledgeVector req:{}", similarKnowledgeVectorQry);
        return knowledgeVectorProvider.qrySimilarKnowledgeVector(similarKnowledgeVectorQry);
    }

    @PostMapping(value = "/delete")
    public Result<Boolean> deleteKnowledgeVector(@RequestBody KnowledgeVectorDto knowledgeVectorDto, HttpServletRequest request) {
        log.info("deleteKnowledgeVector req:{}", knowledgeVectorDto);
        return knowledgeVectorProvider.deleteKnowledgeVector(knowledgeVectorDto);
    }
}

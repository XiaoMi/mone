package run.mone.m78.service.bo.knowledge;

import lombok.Data;
import run.mone.ai.z.dto.ZKnowledgeBaseDTO;
import run.mone.ai.z.dto.ZKnowledgeBaseFilesDTO;
import run.mone.m78.service.dto.knowledge.KnowledgeBaseFileResDto;

import java.util.List;

/**
 * @author wmin
 * @date 2024/1/31
 */
@Data
public class KnowledgeConfigDetail {
    private ZKnowledgeBaseDTO zKnowledgeBaseDTO;
    private List<KnowledgeBaseFileResDto> zKnowledgeBaseFilesDTOS;
}

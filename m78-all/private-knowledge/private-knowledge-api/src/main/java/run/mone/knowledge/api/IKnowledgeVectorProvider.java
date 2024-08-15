package run.mone.knowledge.api;

import com.xiaomi.youpin.infra.rpc.Result;
import run.mone.knowledge.api.dto.KnowledgeVectorDto;
import run.mone.knowledge.api.dto.SimilarKnowledgeVectorQry;
import run.mone.knowledge.api.dto.SimilarKnowledgeVectorRsp;

import java.util.List;

/**
 * @author wmin
 * @date 2024/2/5
 */
public interface IKnowledgeVectorProvider {

    Result<Boolean> insertOrUpdateKnowledgeVector(KnowledgeVectorDto param);

    Result<Boolean> deleteKnowledgeVector(KnowledgeVectorDto param);

    /**
     * 查询相似的topN条向量数据
     * @param qry
     * @return
     */
    Result<List<SimilarKnowledgeVectorRsp>> qrySimilarKnowledgeVector(SimilarKnowledgeVectorQry qry);

}

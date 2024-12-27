package run.mone.knowledge.service.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import run.mone.knowledge.service.dao.entity.VKnowledgeVectorMetaPo;

import java.util.List;

/**
 *  映射层。
 *
 * @author wmin
 * @since 2024-02-19
 */
public interface VKnowledgeVectorMetaMapper extends BaseMapper<VKnowledgeVectorMetaPo> {

    @Update({
            "<script>",
            "update v_knowledge_vector_meta set deleted = 1 where id in",
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int softDelete(@Param("ids") List<Integer> ids);

}

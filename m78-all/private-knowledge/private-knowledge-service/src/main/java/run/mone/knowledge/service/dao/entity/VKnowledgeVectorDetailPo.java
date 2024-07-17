package run.mone.knowledge.service.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author wmin
 * @since 2024-02-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "v_knowledge_vector_detail")
public class VKnowledgeVectorDetailPo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer metaId;

    private String type;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 叶子节点tag
     */
    private String leafTag;

    /**
     * 区块/文件内容
     */
    private String content;

    /**
     * 向量化状态0:未开始, 1:进行中, 2:已完成
     */
    private Integer embeddingStatus;

    private String vector;

}

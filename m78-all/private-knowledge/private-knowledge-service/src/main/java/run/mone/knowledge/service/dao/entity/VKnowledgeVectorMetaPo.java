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
@Table(value = "v_knowledge_vector_meta")
public class VKnowledgeVectorMetaPo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String type;

    /**
     * tag1
     */
    private String tag1;

    /**
     * tag2
     */
    private String tag2;

    /**
     * tag3
     */
    private String tag3;

    /**
     * tag4
     */
    private String tag4;

    /**
     * tag5
     */
    private String tag5;

    /**
     * tag6
     */
    private String tag6;

    /**
     * group节点tag
     */
    private String groupTag;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 0：未删除 1：已删除
     */
    private int deleted;

}

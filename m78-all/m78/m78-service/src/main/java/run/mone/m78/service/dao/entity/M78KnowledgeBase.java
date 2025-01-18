package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhangxiaowei6
 * @Date 2024/3/18 15:52
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_knowledge_base")
public class M78KnowledgeBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("knowledge_base_id")
    private Long knowledgeBaseId;

    private String type;

    private int status;

    private int auth;

    @Column("knowledge_base_name")
    private String knowledgeBaseName;

    private String labels;

    private String remark;

    @Column("avatar_url")
    private String avatarUrl;

    private String creator;

    private String updater;

    @Column("create_time")
    private LocalDateTime createTime;

    @Column("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除0-否 1-是
     */
    private Integer deleted;

    /**
     * 空间id
     */
    @Column("work_space_id")
    private Long workSpaceId;

    /**
     * 版本
     */
    private Integer version;

}

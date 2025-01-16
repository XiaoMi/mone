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
 * @Date 2024/3/15 14:50
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_user_collect")
public class M78UserCollect  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;


    private String username;

    private Integer type;

    @Column("collect_id")
    private Long collectId;

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


}

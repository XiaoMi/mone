package run.mone.agentx.entity;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_favorite")
public class Favorite extends BaseEntity {
    
    @Column("user_id")
    private Integer userId;
    
    @Column("target_id")
    private Integer targetId;
    
    @Column("type")
    private Integer type;
} 
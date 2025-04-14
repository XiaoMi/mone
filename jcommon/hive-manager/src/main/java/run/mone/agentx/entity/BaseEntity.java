package run.mone.agentx.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public abstract class BaseEntity {
    @Id
    private Long id;
    private Long ctime;
    private Long utime;
    private Integer state;
}
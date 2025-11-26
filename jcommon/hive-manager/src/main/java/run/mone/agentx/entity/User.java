package run.mone.agentx.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_user")
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private String token;
    private String internalAccount;  // 公司内部账号绑定
}
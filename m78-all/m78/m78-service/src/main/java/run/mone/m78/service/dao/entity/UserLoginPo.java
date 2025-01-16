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

import static run.mone.m78.api.constant.TableConstant.CUSTOM_USER_LOGIN_TABLE;

/**
 *
 * @description
 * @version 1.0
 * @author wtt
 * @date 2024/4/29 10:22
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(CUSTOM_USER_LOGIN_TABLE)
public class UserLoginPo implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("user_name")
    private String userName;

    @Column("password")
    private String password;

    @Column("token")
    private String token;

    @Column("status")
    private Integer status;

    @Column("ctime")
    private Long ctime;

    @Column("utime")
    private Long utime;

    @Column("app_id")
    private Integer appId;

    @Column("last_login_time")
    private Long lastLoginTime;
}

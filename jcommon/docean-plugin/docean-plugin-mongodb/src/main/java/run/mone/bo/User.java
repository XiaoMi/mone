package run.mone.bo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户
 *
 * @author goodjava@qq.com
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity("user")
public class User implements MongoBo{

    @Id
    private String id;

    //用户名
    private String username;

    //密码(加密存储)
    private String password;

    //角色 (user admin)
    private String role;

    //邮箱地址
    private String email;

    //手机号
    private String mobile;

    //头像URL
    private String avatarUrl;

    //个人简介
    private String bio;

    //创建时间
    private long ctime;

    //更新时间
    private long utime;

    //状态(0:正常 1:冻结 2:注销等)
    private int state;

    //版本(用于乐观锁)
    private int version;

    private String token;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
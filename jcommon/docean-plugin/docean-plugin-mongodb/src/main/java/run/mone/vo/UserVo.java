package run.mone.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/4/29 10:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVo implements Serializable {

    private String id;
    private String username;
    private String role;
    private String email;
    private String mobile;
    private String avatarUrl;
    private String bio;
    private long ctime;
    private long utime;
    private int state;
    private int version;


}

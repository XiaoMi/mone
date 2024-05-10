package run.mone.service;

import com.xiaomi.youpin.docean.anno.Service;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;
import run.mone.bo.User;

/**
 * @author goodjava@qq.com
 * @date 2024/4/18 22:49
 */
@Service
public class UserService extends MongoService<User> {


    public UserService() {
        super(User.class);
    }

    /**
     * 根据用户名和密码查找用户
     */
    public User findUserByUsernameAndPassword(String username, String password) {
        Filter filter = Filters.and(
                Filters.eq("username", username),
                Filters.eq("password", password)
        );
        return findFirst(filter);
    }

    /**
     * 更新用户密码
     */
    public boolean updatePassword(String userId, String newPassword) {
        User user = findById(userId);
        if (user != null) {
            user.setPassword(newPassword);
            return update(user);
        }
        return false;
    }

    /**
     * 更新用户个人信息
     */
    public boolean updateProfile(User user) {
        return update(user);
    }


    /**
     * 注册用户
     * <p>
     * 该方法用于注册新用户。首先会根据用户名查找是否已存在相同的用户,如果存在则返回 false 表示注册失败。
     * 如果不存在相同用户,则保存新用户并返回 true 表示注册成功。
     *
     * @param user 待注册的用户对象
     * @return 注册是否成功, 成功返回 true,失败返回 false
     */
    public boolean registerUser(User user) {
        User existingUser = findFirst(Filters.eq("username", user.getUsername()));
        if (existingUser != null) {
            return false; // 用户名已存在
        }
        return save(user);
    }
}

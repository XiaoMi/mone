package run.mone.controller;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.ModelAttribute;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.mvc.ContextHolder;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import run.mone.bo.User;
import run.mone.mapper.UserMapper;
import run.mone.service.UserService;
import run.mone.vo.UserVo;

import javax.annotation.Resource;

/**
 * 用户管理控制器
 *
 * @author goodjava@qq.com
 */
@Controller
@RequestMapping(path = "/user")
public class UserController extends MongodbController<User> {

    @Resource
    private UserService userService;

    public UserController() {
        super(User.class);
    }

    /**
     * 根据用户名和密码查找用户
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户对象
     */
    @RequestMapping(path = "/findByUsernameAndPassword", method = "get")
    public UserVo findUserByUsernameAndPassword(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userService.findUserByUsernameAndPassword(username, password);
        return UserMapper.INSTANCE.userToUserVo(user);
    }

    /**
     * 更新用户密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 是否更新成功
     */
    @RequestMapping(path = "/updatePassword")
    public boolean updatePassword(String userId, String newPassword) {
        return userService.updatePassword(userId, newPassword);
    }

    /**
     * 更新用户个人信息
     *
     * @param user 新的个人信息
     * @return 是否更新成功
     */
    @RequestMapping(path = "/updateProfile")
    public boolean updateProfile(@ModelAttribute("user") User user, User updateUser) {
        updateUser.setId(user.getId());
        return userService.updateProfile(updateUser);
    }


    /**
     * 处理用户注册请求的方法。
     * 调用userService中的registerUser方法，并返回注册结果。
     *
     * @param user 用户对象，包含用户注册信息。
     * @return 返回注册操作的成功与否。
     */
    @RequestMapping(path = "/register")
    public boolean registerUser(User user) {
        return userService.registerUser(user);
    }

    @RequestMapping(path = "/getByUserId")
    public UserVo getByUserId(@RequestParam("userId") String userId) {
        User user = userService.findById(userId);
        return UserMapper.INSTANCE.userToUserVo(user);
    }

    /**
     * 该方法用于获取当前登录用户的信息
     * 通过@RequestMapping注解映射请求路径为"/getLoginUser"
     * 使用@ModelAttribute注解将请求参数绑定到User对象
     * 返回绑定了请求参数的User对象
     */
    @RequestMapping(path = "/getLoginUser")
    public User getLoginUser(@ModelAttribute("user") User user) {
        return user;
    }

    @RequestMapping(path = "/login")
    public String login(User userReq) {
        MvcContext context = ContextHolder.getContext().get();
        User user = userService.findUserByUsernameAndPassword(userReq.getUsername(), userReq.getPassword());
        if (null == user) {
            return "error";
        }
        user.setPassword("");
        if (StringUtils.isEmpty(user.getRole())) {
            user.setRole("user");
        }
        context.session().setAttribute("user", user);
        return "ok";
    }

    @RequestMapping(path = "/logout")
    public String logout(MvcContext context) {
        context.session().removeAttribute("user");
        return "ok";
    }

}

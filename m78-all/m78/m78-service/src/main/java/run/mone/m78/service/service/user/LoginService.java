/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.m78.service.service.user;

import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.bo.user.UserInfoVo;
import run.mone.m78.service.exceptions.GenericServiceException;

import javax.servlet.http.HttpServletRequest;

@Service

/**
 * LoginService类负责处理与用户登录相关的业务逻辑。
 * <p>
 * 该类提供了从当前会话中获取账户信息的方法，并根据配置决定是使用本地账户信息还是从UserService获取用户信息。
 * </p>
 * <p>
 * 主要功能包括：
 * <ul>
 *   <li>从当前会话中获取账户信息</li>
 *   <li>根据配置使用本地账户信息或从UserService获取用户信息</li>
 * </ul>
 * </p>
 * <p>
 * 依赖注入：
 * <ul>
 *   <li>{@code UserService} - 用于获取用户信息</li>
 * </ul>
 * </p>
 * <p>
 * 配置属性：
 * <ul>
 *   <li>{@code isLocal} - 指示是否使用本地账户信息</li>
 *   <li>{@code localUsername} - 本地账户的用户名</li>
 * </ul>
 * </p>
 */

public class LoginService {

    @Value("${is.local:false}")
    private String isLocal;

    @Value("${local.username:username}")
    private String localUsername;

    @Autowired
    private UserService userService;

    /**
     * 从当前会话中获取账户信息
     *
     * @return 当前会话中的账户信息
     */
    public SessionAccount getAccountFromSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return getAccountFromSession(request);
    }

    public SessionAccount getAccountFromSession(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        if (isLocal.equals("true")) {
            return new SessionAccount(localUsername, 0, localUsername, "MI", true);
        }

        UserInfoVo userInfo = userService.getUserInfo();
        if (null == userInfo) {
            throw new GenericServiceException("User does not exist.");
        }
        return new SessionAccount(userInfo.getUsername(), userInfo.getUserType(), userInfo.getName(), userInfo.getTenant(), userInfo.isAdmin());
    }


}

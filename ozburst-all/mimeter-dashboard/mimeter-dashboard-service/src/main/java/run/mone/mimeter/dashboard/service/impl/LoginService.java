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

package run.mone.mimeter.dashboard.service.impl;

import com.xiaomi.mone.tpc.api.service.UserOrgFacade;
import com.xiaomi.mone.tpc.common.param.NullParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.UserInfo;
import run.mone.mimeter.dashboard.common.SessionAccount;

import javax.servlet.http.HttpServletRequest;

import static run.mone.mimeter.dashboard.bo.common.Constants.SKIP_MI_DUN_USER_NAME;

@Service
public class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    @Value("${is_local:false}")
    private String isLocal;

    @Autowired
    private UserV1Service userService;

    @DubboReference(registry = "stRegistry", check = false, group = "staging-open", version = "1.0")
    private UserOrgFacade userOrgFacade;

    public SessionAccount getAccountFromSession(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        if (isLocal.equals("true")) {
            NullParam param = new NullParam();
            param.setAccount("dongzhenxing");
            param.setUserType(0);
            return new SessionAccount("dongzhenxing", "dongzhenxing", true, "default");
        }
        UserInfo userInfo = userService.getUserInfo();
        String username = userInfo != null ? userInfo.getFullAccount() : request.getHeader(SKIP_MI_DUN_USER_NAME);

        NullParam param = new NullParam();
        param.setAccount(username);
        param.setUserType(0);
        String idPath = "";
        try {
            OrgInfoVo orgInfoVo = userOrgFacade.getOrgByAccount(param).getData();
            if (orgInfoVo != null) {
                idPath = orgInfoVo.getIdPath();
            }
        } catch (Exception e) {
            LOGGER.warn("[LoginService.getAccountFromSession], failed to getOrgByAccount, msg: {} ", e.getMessage());
        }

        return new SessionAccount(username, userInfo.getName(), userInfo.isAdmin(), idPath);
    }

}

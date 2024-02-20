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

package run.mone.m78.ip.service;

import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnActionEvent;
import run.mone.m78.ip.bo.UserBo;
import run.mone.m78.ip.common.ApiCall;
import run.mone.m78.ip.common.Context;

import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/6 12:52
 */
public class UserService extends AbstractService {

    private Gson gson = new Gson();


    public List<String> users() {
        ApiCall apiCall = new ApiCall();
        return apiCall.call(ApiCall.USER_API);
    }


    public List<UserBo> userBoList() {
        return null;
    }


    @Override
    public void execute(Context context, AnActionEvent e) {
        this.next(context, e);
    }

}

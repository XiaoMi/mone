/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.m78.server.controller;

import com.mybatisflex.core.paginate.Page;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78CodeGenerationInfo;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.code.generation.info.M78CodeGenerationInfoService;
import run.mone.m78.service.service.user.LoginService;

import javax.servlet.http.HttpServletRequest;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/code/statistics")
@HttpApiModule(value = "CodeStatisticsController", apiController = CodeStatisticsController.class)
public class CodeStatisticsController {

    @Autowired
    private M78CodeGenerationInfoService codeGenerationInfoService;

    @Autowired
    private LoginService loginService;

    @PostMapping("/todayCodeLines")
    @ResponseBody
    @HttpApiDoc(apiName = "查询用户今天生成的代码行数", value = "/api/v1/code/statistics/todayCodeLines", method = MiApiRequestMethod.POST, description = "根据用户查询用户今天生成的代码行数")
    public Result<Long> getTodayCodeLinesByUser(HttpServletRequest request, @RequestParam("userName") String userName) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        long codeLines = codeGenerationInfoService.getTodayCodeLinesByUser(userName);
        return Result.success(codeLines);
    }


    //按用户分组,查询每个用户的生成代码的行数,支持分页(class)
    @PostMapping("/userCodeLines")
    @ResponseBody
    @HttpApiDoc(apiName = "按用户分组查询生成代码行数", value = "/api/v1/code/statistics/userCodeLines", method = MiApiRequestMethod.POST, description = "按用户分组查询每个用户的生成代码行数，支持分页")
    public Result<Page<M78CodeGenerationInfo>> getUserCodeLinesGroupedByUser(HttpServletRequest request,
                                                                             @RequestParam("currentPage") int currentPage,
                                                                             @RequestParam("pageSize") int pageSize) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        Page<M78CodeGenerationInfo> result = codeGenerationInfoService.getUserCodeLinesGroupedByUser(currentPage, pageSize);
        return Result.success(result);
    }

    @GetMapping("/deleteCache")
    @ResponseBody
    public Result<Boolean> deleteCache(HttpServletRequest request, @RequestParam(value = "key",required = false) String key){
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        codeGenerationInfoService.removeCacheByKey(key);
        return Result.success(true);
    }
}

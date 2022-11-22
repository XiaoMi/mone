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

package com.xiaomi.youpin.gwdash.controller;


import com.xiaomi.youpin.gwdash.bo.CollectionParam;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.bo.UserCollectionInfo;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.LoginService;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RestController
public class CollectionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionController.class);
    @Autowired
    private LoginService loginService;
    @Autowired
    private Dao dao;

    @RequestMapping(value = "/api/collection/new", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Void> newCollection(@RequestBody CollectionParam param,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        LOGGER.info("[CollectionController.newAccount] param: {}", param);


        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[CollectionController.new] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        List<UserCollectionInfo>  hasCollected =dao.query(UserCollectionInfo.class, Cnd.where("username","=",account.getUsername()).and("apiInfoId","=",param.getApiInfoId()));
        if(hasCollected.size()>0){
           if(hasCollected.get(0).getStatus()==1){
               return new Result<>(CommonError.InvalidParamError.code,"已经添加过了");
           }
           long utime=System.currentTimeMillis();
           dao.update(UserCollectionInfo.class, Chain.make("status",1).add("utime",utime),Cnd.where("apiInfoId","=",param.getApiInfoId()).and("username","=",account.getUsername()));
           return new Result<>(CommonError.Success.code,"ok");

        }
        UserCollectionInfo userCollectionInfo =new UserCollectionInfo();
        long ctime = System.currentTimeMillis();
        userCollectionInfo.setUsername(account.getUsername());
        userCollectionInfo.setApiInfoId(param.getApiInfoId());
        userCollectionInfo.setCtime(ctime);
        userCollectionInfo.setUtime(ctime);
        userCollectionInfo.setStatus(1);
        dao.insert(userCollectionInfo);

        return new Result<>(CommonError.Success.code,"ok");

    }
    @RequestMapping(value = "/api/collection/cancel", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<List> cancel(@RequestBody CollectionParam collectionParam,
                              HttpServletRequest request,
                              HttpServletResponse response) throws  IOException{
        LOGGER.info("[CollectionController.newAccount] param: {}", collectionParam);
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[CollectionController.new] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        long utime=System.currentTimeMillis();
        dao.update(UserCollectionInfo.class, Chain.make("status",0).add("utime",utime),Cnd.where("apiInfoId","=",collectionParam.getApiInfoId()).and("username","=",account.getUsername()));
       // List<UserCollectionInfo>  collected =  dao.query(UserCollectionInfo.class,Cnd.where("username", "=", username).and("status","=",1));


        return new Result(CommonError.Success.code,"ok");

    }
}

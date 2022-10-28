///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.controller;
//
//import com.xiaomi.youpin.gwdash.bo.MErrorDelParam;
//import com.xiaomi.youpin.gwdash.bo.MErrorListReult;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.MError;
//import com.xiaomi.youpin.gwdash.service.MErrorService;
//import org.nutz.dao.pager.Pager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * @author zhangjunyi
// * created on 2019/12/26 4:12 下午
// */
//@RestController
//public class MErrorController {
//    @Autowired
//    MErrorService mErrorService;
//
//    @RequestMapping(value="/api/mError/list")
//    public Result<MErrorListReult> list(@RequestParam(value = "page") int page, @RequestParam(value="pageSize") int pageSize){
//        Pager p= new Pager();
//        p.setPageNumber(page);
//        p.setPageSize(pageSize);
//        List<MError> list=mErrorService.list(p);
//        int total =mErrorService.getTotal();
//        MErrorListReult ret =new MErrorListReult();
//        ret.setList(list);
//        ret.setTotal(total);
//        return Result.success(ret);
//    }
//    @RequestMapping(value="/api/mError/del",method = RequestMethod.POST)
//    public Result<Boolean> del(@RequestBody MErrorDelParam mErrorDelParam){
//        boolean ret = mErrorService.delete(mErrorDelParam.getId());
//        return Result.success(ret);
//    }
//}
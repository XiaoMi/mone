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
//import com.xiaomi.youpin.gwdash.bo.QuotaRecordParam;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.quota.bo.RecordBo;
//import com.xiaomi.youpin.quota.bo.RecordResult;
//import com.xiaomi.youpin.quota.service.QuotaService;
//import com.xiaomi.youpin.quota.service.RecordService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
///**
// * @author zhangjunyi
// * created on 2020/5/22 10:38 上午
// */
//@RestController
//@Slf4j
//public class QuotaRecordController {
//    @Reference(check = false, interfaceClass = RecordService.class, retries = 0, group = "${ref.quota.service.group}", timeout = 3000)
//    private RecordService recordService;
//
//    @RequestMapping(value="/api/quota/record",method = RequestMethod.POST)
//    public Result<RecordResult>  getQuotaRecords(@RequestBody QuotaRecordParam quotaRecordParam){
//
//        RecordResult  result = null;
//        try {
//            result = recordService.getAllRecords(quotaRecordParam.getPage(),quotaRecordParam.getPageSize());
//        } catch (Exception e) {
//            e.printStackTrace();
//            result =new RecordResult();
//        }
//        return  Result.success(result);
//    }
//}
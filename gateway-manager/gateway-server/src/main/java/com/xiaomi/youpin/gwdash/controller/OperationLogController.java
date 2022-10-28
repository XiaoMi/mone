//package com.xiaomi.youpin.gwdash.controller;
//
//
//import com.xiaomi.youpin.gwdash.bo.OperationLogBo;
//import com.xiaomi.youpin.gwdash.bo.Page;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.service.OperationLogService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@Slf4j
//@RequestMapping("/api/operationlog")
//public class OperationLogController {
//
//
//    @Autowired
//    OperationLogService operationLogService;
//
//
//    /**
//     * 查询操作日志 应用名称、操作人（或资源标识）（根据时间降序或升序）
//     * @return
//     */
//
//    @RequestMapping(value = "/info", method = RequestMethod.GET)
//    public Result<Map<String, Object>> getLogInfo(@RequestParam(value = "appName",required = false) String  appName,
//                                                 @RequestParam(value = "dataId",required = false) String  dataId,
//                                                 @RequestParam(value = "type",required = false,defaultValue = "0") int  type,  //0 正序 1 倒序 默认0
//                                                 @RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex,
//                                                 @RequestParam(value = "pageSize",required = false,defaultValue = "10") int pageSize)  {
//
//
//        Page<OperationLogBo> list = operationLogService.queryLogInfoList(appName,dataId,type,pageIndex,pageSize);
//        Map<String,Object> result = new HashMap<>();
//        result.put("list", list.getList());
//        result.put("total", list.getTotal());
//        result.put("page", list.getPage());
//        result.put("pageSize", list.getPageSize());
//        return Result.success(result);
//    }
//
//
//
//}

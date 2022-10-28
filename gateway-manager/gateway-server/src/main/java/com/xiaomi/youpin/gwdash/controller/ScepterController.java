//package com.xiaomi.youpin.gwdash.controller;
//
//import com.xiaomi.aegis.utils.AegisFacade;
//import com.xiaomi.aegis.vo.UserInfoVO;
//import com.xiaomi.youpin.infra.rpc.Result;
//import com.xiaomi.youpin.scepter.api.bean.GroupRes;
//import com.xiaomi.youpin.scepter.api.bean.LogRes;
//import com.xiaomi.youpin.scepter.api.dto.ConfigDTO;
//import com.xiaomi.youpin.scepter.api.dto.GroupDTO;
//import com.xiaomi.youpin.scepter.api.entity.ScepterGroupExt;
//import com.xiaomi.youpin.scepter.api.service.ScepterService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.jasig.cas.client.validation.Assertion;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import javax.validation.constraints.Max;
//import java.util.Map;
//
///**
// * @author gaoyibo
// *
// * downgrade service
// */
//@RestController
//@RequestMapping("/api/scepter")
//@Slf4j
//@Validated
//public class ScepterController {
//
//    @Reference(interfaceClass = ScepterService.class, timeout = 3000, check = false, group = "", retries = 0)
//
//    private ScepterService scepterService;
//
//    private String getUsername(HttpServletRequest request) {
//        UserInfoVO user = AegisFacade.getUserInfo(request);
//        if (null == user) {
//            return null;
//        }
//        return user.getUser();
//    }
//
//    private static final String PREFIX_GROUP = "/group";
//
//    private static final String PREFIX_CONFIG = "/config";
//
//    private static final String PREFIX_LOG = "/log";
//
//    private static final String PREFIX_USER = "/user";
//
//    @PostMapping(value = PREFIX_GROUP + "/add")
//    public Result<Integer> addGroup(
//            @Validated @RequestBody GroupDTO scepterGroup
//    ) {
//        return scepterService.addGroup(scepterGroup);
//    }
//
//    @PostMapping(PREFIX_GROUP + "/edit")
//    public Result<Integer> editGroup(
//            @Validated(GroupDTO.EditGroup.class) @RequestBody GroupDTO scepterGroup
//    ) {
//        return scepterService.editGroup(scepterGroup);
//    }
//
//    @GetMapping(PREFIX_GROUP + "/list")
//    public Result<GroupRes> getGroupList(
//            @Max(value = 20, message = "一页最多 20 项") @RequestParam("limit") int limit,
//            @RequestParam("offset") int offset
//    ) {
//        return scepterService.getGroupList(limit, offset);
//    }
//
//    @GetMapping(PREFIX_GROUP + "/del")
//    public Result<Integer> delGroup(
//            @RequestParam("id") int id
//    ) {
//        return scepterService.delGroup(id);
//    }
//
//    @GetMapping(value = PREFIX_CONFIG + "/listByGroupid")
//    public Result<ScepterGroupExt> getConfigListInGroup(
//            @RequestParam("groupId") int groupId, @Max(value = 20, message = "一页最多 20 项") @RequestParam("limit") int limit, @RequestParam("offset") int offset
//    ) {
//        return scepterService.getConfigListInGroup(groupId, limit, offset);
//    }
//
//    @GetMapping(PREFIX_CONFIG + "/type")
//    public Result<Map<String, Integer>> typeList() {
//        return scepterService.configType();
//    }
//
//    @PostMapping(value = PREFIX_CONFIG + "/add")
//    public Result<Integer> addConfig(
//            @Validated(ConfigDTO.AddConfig.class) @RequestBody ConfigDTO scepterConfig
//    ) {
//
//        return scepterService.addConfig(scepterConfig);
//    }
//
//    @PostMapping(value = PREFIX_CONFIG + "/pass")
//    public Result<Integer> passConfig(
//            HttpServletRequest request,
//            @RequestBody ConfigDTO scepterConfig
//    ) {
//        String username = getUsername(request);
//        scepterConfig.setReviewer(username);
//
//        return scepterService.pass(scepterConfig);
//    }
//
//    @PostMapping(value = PREFIX_CONFIG + "/nopass")
//    public Result<Integer> nopassConfig(
//            HttpServletRequest request,
//            @RequestBody ConfigDTO scepterConfig
//    ) {
//        String username = getUsername(request);
//        scepterConfig.setReviewer(username);
//
//        return scepterService.nopass(scepterConfig);
//    }
//
//    @PostMapping(value = PREFIX_CONFIG + "/edit")
//    public Result<Integer> editConfig(
//            @Validated(ConfigDTO.EditConfig.class) @RequestBody ConfigDTO scepterConfig
//    ) {
//        return scepterService.editConfig(scepterConfig);
//    }
//
//    @GetMapping(value = PREFIX_CONFIG + "/del")
//    public Result<Integer> delConfig(
//            @RequestParam("id") int id
//    ) {
//        return scepterService.delConfig(id);
//    }
//
//    @GetMapping(value = PREFIX_GROUP + "/down")
//    public Result<Integer> down(
//            @RequestParam("groupId") int groupId
//    ) {
//        return scepterService.downgrade(groupId);
//    }
//
//    @GetMapping(value = PREFIX_GROUP + "/restore")
//    public Result<Integer> restore(
//            @RequestParam("groupId") int groupId
//    ) {
//        return scepterService.restore(groupId);
//    }
//
//    @GetMapping(PREFIX_LOG + "/list")
//    public Result<LogRes> logList(
//            @Max(value = 20, message = "一页最多 20 项") @RequestParam("limit") int limit,
//            @RequestParam("offset") int offset
//    ) {
//        return scepterService.logList(limit, offset);
//    }
//
//    @GetMapping(PREFIX_USER + "/isAdmin")
//    public Result<Boolean> isAdmin(
//            @RequestParam("username") String username
//    ) {
//        return scepterService.isAdmin(username);
//    }
//}

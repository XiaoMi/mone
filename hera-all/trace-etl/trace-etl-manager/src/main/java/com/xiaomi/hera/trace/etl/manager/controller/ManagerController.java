package com.xiaomi.hera.trace.etl.manager.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.hera.trace.etl.domain.HeraTraceConfigVo;
import com.xiaomi.hera.trace.etl.domain.HeraTraceEtlConfig;
import com.xiaomi.hera.trace.etl.domain.PagerVo;
import com.xiaomi.hera.trace.etl.service.ManagerService;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/4/19 10:20 上午
 */
@RestController
@Slf4j
public class ManagerController {

    @NacosValue("${hera.admin.member.list}")
    public String adminMemList;

    @Autowired
    private ManagerService managerService;

    @GetMapping("/manager/getAllPage")
    public Object getAllPage(HeraTraceConfigVo vo, HttpServletRequest request) {
        try {
            AuthUserVo user = UserUtil.getUser();
            if (user == null || StringUtils.isEmpty(user.genFullAccount())) {
                log.warn("getAllPage userInfo is null");
                return Result.fail(GeneralCodes.InternalError, "用户信息为空，请重新登录");
            }
            String userName = user.genFullAccount();
            log.info("userName is : "+userName);
            if(!isAdmin(userName)) {
                vo.setUser(userName);
            }
            initPage(vo);
            return Result.success(managerService.getAllPage(vo));
        } catch (Exception e) {
            log.error("get all page error : ", e);
            return Result.fromException(e);
        }
    }

    /**
     * 是否为admin，admin member list由nacos配置。
     * @param user
     * @return
     */
    private boolean isAdmin(String user) {
        if(StringUtils.isEmpty(adminMemList)){
            return false;
        }
        String[] split = adminMemList.split(",");
        for(String adminMem : split){
            if(user.equals(adminMem)){
                return true;
            }
        }
        return false;
    }

    @GetMapping("/manager/getAllList")
    public Object getAllList(HeraTraceConfigVo vo) {
        try {
            return Result.success(managerService.getAll(vo));
        } catch (Exception e) {
            log.error("get all list error : ", e);
            return Result.fromException(e);
        }
    }

    @GetMapping("/manager/getDetail")
    public Object getDetail(Integer id) {
        try {
            log.info("getDetail param : " + id);
            return Result.success(managerService.getById(id));
        } catch (Exception e) {
            log.error("get all list error : ", e);
            return Result.fromException(e);
        }
    }

    @PostMapping("/manager/insertOrUpdate")
    public Object insertOrUpdate(HeraTraceEtlConfig config, HttpServletRequest request) {
        try {
            AuthUserVo userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.warn("insertOrUpdate userInfo is null");
                return Result.fail(GeneralCodes.InternalError, "用户信息为空，请重新登录");
            }
            String user = userInfo.genFullAccount();
            log.info("insertOrUpdate user : "+user+" param : " + config);
            return managerService.insertOrUpdate(config, null);
        } catch (Exception e) {
            log.error("insert or update error : ", e);
            return Result.fromException(e);
        }
    }

    @PostMapping("/manager/delete")
    public Object delete(HeraTraceEtlConfig config) {
        try {
            log.info("delete param : " + config);
            int delete = managerService.delete(config);
            return delete > 0 ? Result.success(null) : Result.fail(GeneralCodes.InternalError, "删除失败");
        } catch (Exception e) {
            log.error("delete error : ", e);
            return Result.fromException(e);
        }
    }

    private void initPage(PagerVo vo) {
        if (vo.getPageSize() == null) {
            vo.setPageSize(10000);
        }
    }

}

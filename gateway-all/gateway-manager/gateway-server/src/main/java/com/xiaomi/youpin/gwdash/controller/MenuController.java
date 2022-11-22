package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.Menu;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.MenuService;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private LoginService loginService;

//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    public Result<Map<String, Object>> list(HttpServletRequest request,
//                                            @RequestParam(value = "page", defaultValue = "1") int page,
//                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
//        return Result.success(menuService.list(page, pageSize));
//    }
//
//    @RequestMapping(value = "/create", method = RequestMethod.POST)
//    public Result<Boolean> create(HttpServletRequest request,
//                                  @RequestBody Menu menu) {
//        return Result.success(menuService.create(menu));
//    }
//
//    @RequestMapping(value = "/update", method = RequestMethod.POST)
//    public Result<Boolean> update(HttpServletRequest request,
//                                  @RequestBody Menu menu) {
//        return Result.success(menuService.update(menu));
//    }
//
//    @RequestMapping(value = "/delete", method = RequestMethod.GET)
//    public Result<Boolean> delete(HttpServletRequest request,
//                                  @RequestParam(value = "id", required = true) long id) {
//        return Result.success(menuService.delete(id));
//    }

    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public Result<Menu> menu(HttpServletRequest request) {
        return Result.success(menuService.menu(Arrays.asList("tesla-gateway")));
    }

//    @RequestMapping(value = "/roles", method = RequestMethod.GET)
//    public Result<List<RoleBo>> roles(HttpServletRequest request) {
//        return Result.success(loginService.getAllRoles());
//    }

}

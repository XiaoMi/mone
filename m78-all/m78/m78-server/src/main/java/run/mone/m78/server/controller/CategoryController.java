package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.enums.CategoryTypeEnum;
import run.mone.m78.service.bo.category.CategoryVo;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78Category;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.categoty.CategoryService;
import run.mone.m78.service.service.user.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-02 09:48
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/category")
@HttpApiModule(value = "CategoryController", apiController = CategoryController.class)
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LoginService loginService;

    @PostMapping("/create")
    @HttpApiDoc(value = "/api/v1/category/create", method = MiApiRequestMethod.POST, apiName = "创建分类")
    public Result<Boolean> createCategory(HttpServletRequest request, @HttpApiDocClassDefine(value = "categoryName", description = "分类名称", required = true) String categoryName,
                                          @HttpApiDocClassDefine(value = "type", description = "分类类型") @RequestParam("type") Integer type) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        M78Category category = M78Category.builder().name(categoryName).deleted(0).type(type).createTime(LocalDateTime.now()).build();
        boolean res = categoryService.save(category);
        return Result.success(res);
    }

    @PostMapping("/delete")
    @HttpApiDoc(value = "/api/v1/category/delete", method = MiApiRequestMethod.POST, apiName = "删除分类")
    public Result<Boolean> deleteCategory(HttpServletRequest request, @HttpApiDocClassDefine(value = "categoryId", description = "分类id", required = true) Long categoryId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        boolean res = categoryService.deleteCategory(categoryId);
        return Result.success(res);
    }

    @GetMapping("/list")
    @HttpApiDoc(value = "/api/v1/category/list", method = MiApiRequestMethod.GET, apiName = "获取分类列表")
    public Result<List<CategoryVo>> list(HttpServletRequest request, @HttpApiDocClassDefine(value = "type", description = "分类类型") @RequestParam(value = "type", required = false) Integer type) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(categoryService.listCategory(type));

    }

    @GetMapping("/typeList")
    @HttpApiDoc(value = "/api/v1/category/typeList", method = MiApiRequestMethod.GET, apiName = "获取分类类型列表")
    public Result<Map<Integer, String>> typeList(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(Arrays.stream(CategoryTypeEnum.values()).collect(Collectors.toMap(CategoryTypeEnum::getCode, CategoryTypeEnum::getDesc)));
    }


}

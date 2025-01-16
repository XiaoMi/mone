package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.ListResult;
import run.mone.m78.api.bo.recommendCarousel.ListQryReq;
import run.mone.m78.api.bo.recommendCarousel.M78RecommendCarouselInfo;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.recommend.M78RecommendCarouselService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/RecommendCarousel")
@HttpApiModule(value = "RecommendCarouselController", apiController = RecommendCarouselController.class)
public class RecommendCarouselController {

    @Autowired
    private LoginService loginService;

    @Resource
    private M78RecommendCarouselService recommendCarouselService;


    //分页查询list
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result<ListResult<M78RecommendCarouselInfo>> getList(HttpServletRequest request, @RequestBody ListQryReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return recommendCarouselService.listCarousels(req);
    }

    //新增, 入参是
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<Boolean> addCarousel(HttpServletRequest request,
                                       @RequestBody M78RecommendCarouselInfo info) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return recommendCarouselService.addCarousel(info);
    }

    //更新，入参是M78RecommendCarouselInfo info
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<Boolean> updateCarousel(HttpServletRequest request, @RequestBody M78RecommendCarouselInfo info) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return recommendCarouselService.updateCarousel(info);
    }

    //搜索轮播图（模糊）
    @RequestMapping(value = "/getListByAdmin", method = RequestMethod.POST)
    public Result<ListResult<M78RecommendCarouselInfo>> getListByName(HttpServletRequest request,
                                                                @RequestBody ListQryReq req){
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (!account.isAdmin()){
            return Result.fail(GeneralCodes.NotAuthorized,"not authorized");
        }
        return recommendCarouselService.listCarousels(req);
    }

    //禁用/启用轮播图
    @RequestMapping(value = "/updateDisplayStatus", method = RequestMethod.POST)
    public Result<Boolean> updateDisplayStatus(HttpServletRequest request,
                                               @RequestBody M78RecommendCarouselInfo info){
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return recommendCarouselService.updateDisplayStatus(info);
    }
}
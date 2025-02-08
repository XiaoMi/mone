package run.mone.m78.server.controller;

import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.ai.z.dto.ZModelDTO;
import run.mone.m78.api.bo.model.ModelRes;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.service.model.ModelService;
import run.mone.m78.service.service.user.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_BAD_REQUEST;

/**
 * @author wmin
 * @date 2024/1/24
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/model")
public class ModelController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ModelService modelService;

    /**
     * 获取模型列表
     *
     * @param type    类型，默认为1
     * @param request 请求对象
     * @return 模型列表
     */
    @ResponseBody
    @RequestMapping("/list")
    public Result<List<ModelRes>> getList(@RequestParam(value = "type", required = false, defaultValue = "1") Long type,
                                          HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("[ModelController.getList] User not logged in");
            return Result.fail(STATUS_BAD_REQUEST, "User not logged in");
        }
        log.info("[ModelController.getList] User: {}", GsonUtils.gson.toJson(account));
        return modelService.getModelList();
    }

    //获取模型列表,调v1接口
    @ResponseBody
    @RequestMapping("/list/v1")
    public Result<Map<String, List<ModelRes>>> getListV1(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("[ModelController.getListV1] User not logged in");
            return Result.fail(STATUS_BAD_REQUEST, "User not logged in");
        }
        log.info("[ModelController.getListV1] User: {}", GsonUtils.gson.toJson(account));
        return modelService.getModelListV1(null, account.isAdmin());
    }

    //获取模型列表,给首页的聊天模型
    @ResponseBody
    @RequestMapping("/list/v1/chat")
    public Result<Map<String, List<ModelRes>>> getListV1Chat(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("[ModelController.getListV1Chat] User not logged in");
            return Result.fail(STATUS_BAD_REQUEST, "User not logged in");
        }
        log.info("[ModelController.getListV1Chat] User: {}", GsonUtils.gson.toJson(account));
        Predicate<ZModelDTO> predicate = zModelDTO -> zModelDTO.getInfo().containsKey("m78-chat-model");
        List<Predicate<ZModelDTO>> predicates = new ArrayList();
        predicates.add(predicate);
        return modelService.getModelListV1(predicates, account.isAdmin());
    }

    //获取模型列表,给图片理解
    @RequestMapping(value = "/list/filter", method = RequestMethod.GET)
    public Result<Map<String, List<ModelRes>>> getListFilter(HttpServletRequest request,
                                                             @RequestParam String filter) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("[ModelController.getListFilter] User not logged in");
            return Result.fail(STATUS_BAD_REQUEST, "User not logged in");
        }
        log.info("[ModelController.getListFilter] User: {}", GsonUtils.gson.toJson(account));
        Predicate<ZModelDTO> predicate = zModelDTO -> zModelDTO.getInfo().containsKey(filter);
        List<Predicate<ZModelDTO>> predicates = new ArrayList();
        predicates.add(predicate);
        return modelService.getModelListV1(predicates, account.isAdmin());
    }


}

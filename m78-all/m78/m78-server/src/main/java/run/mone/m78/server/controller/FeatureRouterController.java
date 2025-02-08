package run.mone.m78.server.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import com.google.gson.JsonObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.feature.router.FeatureRouterDTO;
import run.mone.m78.api.bo.feature.router.FeatureRouterReq;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.MappingUtils;
import run.mone.m78.service.dao.entity.FeatureRouterTypeEnum;
import run.mone.m78.service.dao.mapper.ChatInfoMapper;
import run.mone.m78.service.dao.entity.ChatInfoPo;
import run.mone.m78.service.dao.entity.FeatureRouter;
import run.mone.m78.service.dao.entity.table.FeatureRouterTableDef;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.feature.router.FeatureRouterService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.api.constant.FeatureRouterConstant.*;
import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author goodjava@qq.com
 * @date 2024/2/4 15:21
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/feature/router")
@HttpApiModule(value = "FeatureRouterController", apiController = FeatureRouterController.class)
public class FeatureRouterController {

    @Resource
    private FeatureRouterService featureRouterService;

    @Autowired
    private LoginService loginService;

    @Resource
    private ChatInfoMapper chatInfoMapper;


    @PostMapping("/update")
    @ResponseBody
    @HttpApiDoc(apiName = "更新FeatureRouter", value = "/api/v1/feature/router/update", method = MiApiRequestMethod.POST, description = "更新FeatureRouter")
    public Result<Boolean> update(HttpServletRequest request, @RequestBody FeatureRouterReq featureRouterReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        ChatInfoPo chatInfo = chatInfoMapper.selectOneById(featureRouterReq.getLabelId());
        if (!account.getUsername().equals(chatInfo.getUser())) {
            return Result.fail(STATUS_BAD_REQUEST, "导出内容与记录对应用户不符!");
        }

        FeatureRouter byId = featureRouterService.getById(featureRouterReq.getId());
        if (byId == null) {
            return Result.fail(STATUS_BAD_REQUEST, "没有找到待更新的导出记录!");
        }

        String userName = account.getUsername();
        // TODO: 当前更新内容只有名称(name)和调用参数(reqData)
        FeatureRouter featureRouter = UpdateEntity.of(FeatureRouter.class, featureRouterReq.getId());
        if (StringUtils.isNotBlank(featureRouterReq.getName())) {
            featureRouter.setName(featureRouterReq.getName());
        }
        featureRouter.setRouterMeta(featureRouterReq.getReqData());
        featureRouterService.update(featureRouter, FeatureRouterTableDef.FEATURE_ROUTER.ID.eq(featureRouter.getId()).and(FeatureRouterTableDef.FEATURE_ROUTER.USER_NAME.eq(userName)));
        return Result.success(true);
    }


    //按id删除featureRouter(class)
    @DeleteMapping("/delete")
    @ResponseBody
    @HttpApiDoc(apiName = "删除FeatureRouter", value = "/api/v1/feature/router/delete", method = MiApiRequestMethod.DELETE, description = "删除FeatureRouter")
    public Result<Boolean> deleteById(HttpServletRequest request, @RequestParam("id") Long id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        boolean deleted = featureRouterService.remove(FeatureRouterTableDef.FEATURE_ROUTER.ID.eq(id).and(FeatureRouterTableDef.FEATURE_ROUTER.USER_NAME.eq(userName)));
        return Result.success(deleted);
    }

    //根据id获取featureRouter信息(class)
    @GetMapping("/getById")
    @ResponseBody
    @HttpApiDoc(apiName = "获取单个FeatureRouter详情", value = "/api/v1/feature/router/getById", method = MiApiRequestMethod.GET, description = "获取单个FeatureRouter详情")
    public Result<FeatureRouterDTO> getById(HttpServletRequest request, @RequestParam("id") Long id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        FeatureRouter featureRouter = featureRouterService.getById(id);
        if (featureRouter == null) {
            return Result.fail(STATUS_NOT_FOUND, "FeatureRouter not found");
        }
        FeatureRouterDTO res = MappingUtils.map(featureRouter, FeatureRouterDTO.class);
        res.setCurl(featureRouterService.getCurlUrl(featureRouter, userName, FeatureRouterTypeEnum.PROBOT.getCode() == res.getType()));
        if (res.getRouterMeta() != null) {
            res.getRouterMeta().remove(ROUTER_META_TYPE_MARK);
            res.getRouterMeta().remove(ROUTER_META_SQL);
            res.getRouterMeta().remove(ROUTER_META_TYPE);
            res.getRouterMeta().remove(ROUTER_META_STORE);
        }
        if (userName.equals(featureRouter.getUserName())) {
            return Result.success(res);
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "FeatureRouter access denied for user:" + userName);
        }
    }

    //根据id获取featureRouter信息(class)
    @GetMapping("/getByBotId")
    @ResponseBody
    @HttpApiDoc(apiName = "依据botId获取单个FeatureRouter详情", value = "/api/v1/feature/router/getByBotId", method = MiApiRequestMethod.GET, description = "依据botId获取单个FeatureRouter详情")
    public Result<FeatureRouterDTO> getByBotId(HttpServletRequest request, @RequestParam("bot") Long botId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        FeatureRouter featureRouter = featureRouterService.getOne(new QueryWrapper().eq("label_id", botId));
        if (featureRouter == null) {
            return Result.fail(STATUS_NOT_FOUND, "FeatureRouter not found");
        }
        FeatureRouterDTO res = MappingUtils.map(featureRouter, FeatureRouterDTO.class);
        res.setCurl(featureRouterService.getCurlUrl(featureRouter, userName, true));
        if (userName.equals(featureRouter.getUserName())) {
            return Result.success(res);
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "FeatureRouter access denied for user:" + userName);
        }
    }


    //获取featureRouter列表(class)
    @GetMapping("/list")
    @ResponseBody
    @HttpApiDoc(apiName = "获取FeatureRouter列表", value = "/api/v1/feature/router/list", method = MiApiRequestMethod.GET, description = "获取FeatureRouter列表")
    public Result<List<FeatureRouterDTO>> list(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        List<FeatureRouter> featureRouters = featureRouterService.list(new QueryWrapper().eq("user_name", userName));
        List<FeatureRouterDTO> res = featureRouters.stream()
                .map(r -> {
                    FeatureRouterDTO dto = MappingUtils.map(r, FeatureRouterDTO.class);
                    dto.setCurl(featureRouterService.getCurlUrl(r, userName, FeatureRouterTypeEnum.PROBOT.getCode() == r.getType()));
                    // HINT: nasty
                    if (dto.getRouterMeta() != null) {
                        dto.getRouterMeta().remove(ROUTER_META_TYPE_MARK);
                        dto.getRouterMeta().remove(ROUTER_META_SQL);
                        dto.getRouterMeta().remove(ROUTER_META_TYPE);
                        dto.getRouterMeta().remove(ROUTER_META_STORE);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return Result.success(res);
    }


    //创建一个新的featureRouter(class)
    @PostMapping("/create")
    @ResponseBody
    @HttpApiDoc(apiName = "创建FeatureRouter", value = "/api/v1/feature/router/create", method = MiApiRequestMethod.POST, description = "创建FeatureRouter")
    public Result<Boolean> create(HttpServletRequest request, @RequestBody FeatureRouterReq featureRouterReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        boolean created = featureRouterService.save(featureRouterReq, account.getUsername());
        return Result.success(created);
    }


    //执行与ChatInfoPo关联的SQL查询(class)
    @PostMapping("/query")
    @ResponseBody
    @HttpApiDoc(apiName = "调用FeatureRouter", value = "/api/v1/feature/router/query", method = MiApiRequestMethod.POST, description = "调用FeatureRouter")
    public Result<List<Map<String, Object>>> executeSqlQuery(HttpServletRequest request, @RequestBody FeatureRouterReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (StringUtils.isBlank(req.getUserName())) {
            req.setUserName(account.getUsername());
        }
        return featureRouterService.query(req);
    }

    @PostMapping("/probot/query")
    @ResponseBody
    @HttpApiDoc(apiName = "Probot调用FeatureRouter", value = "/api/v1/feature/router/probot/query", method = MiApiRequestMethod.POST, description = "Probot调用FeatureRouter")
    public Result<String> exeBotQuery(HttpServletRequest request) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String body = request.getReader().lines().collect(Collectors.joining());
        JsonObject req = GsonUtils.gson.fromJson(body, JsonObject.class);
        return Result.success(GsonUtils.gson.toJson(featureRouterService.executeProbot(req).getData()));
    }
}

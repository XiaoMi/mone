package run.mone.m78.server.controller;

import com.mybatisflex.core.paginate.Page;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.plugins.*;
import run.mone.m78.api.enums.UserRoleEnum;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.M78BotPlugin;
import run.mone.m78.service.dao.entity.M78Workspace;
import run.mone.m78.service.dao.entity.PluginOrgPubStatusEnum;
import run.mone.m78.service.dao.entity.PluginResTypeEnum;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.plugins.BotPluginService;
import run.mone.m78.service.service.user.LoginService;
import run.mone.m78.service.service.workspace.WorkspaceService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_BAD_REQUEST;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

/**
 * @author goodjava@qq.com
 * @date 2024/3/4 19:36
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/botplugin")
@HttpApiModule(value = "BotPluginController", apiController = BotPluginController.class)
public class BotPluginController {


    @Resource
    private BotPluginService botPluginService;

    @Resource
    private WorkspaceService workspaceService;

    @Autowired
    private LoginService loginService;


    //保存或更新M78BotPlugin (class)
    @PostMapping("/create")
    @HttpApiDoc(apiName = "创建组件", value = "/api/v1/botplugin/create", method = MiApiRequestMethod.POST, description = "创建组件")
    public Result<Long> createBotPlugin(HttpServletRequest request, @RequestBody BotPluginDTO plugin) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        if (plugin == null) {
            return Result.fail(STATUS_BAD_REQUEST, "Plugin data must not be null");
        }
        M78BotPlugin po = M78BotPlugin.fromDTO(plugin);
        po.setStatus(PluginOrgPubStatusEnum.NOT_PUB.getCode());
        return botPluginService.saveOrUpdateM78BotPlugin(account.getUsername(), po);
    }

    @PostMapping("/update")
    @HttpApiDoc(apiName = "更新组件", value = "/api/v1/botplugin/update", method = MiApiRequestMethod.POST, description = "更新组件")
    public Result<Long> updateBotPlugin(HttpServletRequest request, @RequestBody BotPluginDTO plugin) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        if (plugin == null) {
            return Result.fail(STATUS_BAD_REQUEST, "Plugin data must not be null");
        }
        M78BotPlugin po = M78BotPlugin.fromDTO(plugin);
        return botPluginService.saveOrUpdateM78BotPlugin(account.getUsername(), po);
    }


    // 根据id删除某个plugin (class)
    @PostMapping("/delete")
    @HttpApiDoc(apiName = "删除组件", value = "/api/v1/botplugin/delete", method = MiApiRequestMethod.POST, description = "删除组件")
    public Result<Boolean> deleteBotPluginById(HttpServletRequest request, @HttpApiDocClassDefine(value = "id", required = true, description = "插件id", defaultValue = "") @RequestParam Long id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (id == null) {
            return Result.fail(STATUS_BAD_REQUEST, "Plugin ID must not be null");
        }
        return botPluginService.deleteM78BotPluginById(account.getUsername(), id);
    }


    // 根据id查询某个plugin (class)
    @GetMapping("/get")
    @HttpApiDoc(apiName = "根据id查询组件", value = "/api/v1/botplugin/get", method = MiApiRequestMethod.GET, description = "根据id查询组件")
    public Result<BotPluginDTO> getBotPluginById(HttpServletRequest request, @HttpApiDocClassDefine(value = "id", required = true, description = "组件id", defaultValue = "") @RequestParam Long id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (id == null) {
            return Result.fail(STATUS_BAD_REQUEST, "Plugin ID must not be null");
        }
        return botPluginService.getM78BotPluginById(id);
    }

    // 根据PluginReq查询plugin列表 (class)
    @PostMapping("/list")
    @HttpApiDoc(apiName = "查询组件列表", value = "/api/v1/botplugin/list", method = MiApiRequestMethod.POST, description = "查询组件列表")
    public Result<Page<BotPluginDTO>> listBotPlugins(HttpServletRequest request, @RequestBody PluginReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (req == null) {
            return Result.fail(STATUS_BAD_REQUEST, "Plugin request data must not be null");
        }

        return botPluginService.listM78BotPluginsByRequest(req, null);
    }

    // 根据PluginReq查询plugin列表 (class)
    @PostMapping("/searchList")
    @HttpApiDoc(apiName = "查询组件列表", value = "/api/v1/botplugin/searchList", method = MiApiRequestMethod.POST, description = "查询组件列表")
    public Result<Page<BotPluginDTO>> searchListBotPlugins(HttpServletRequest request, @RequestBody PluginReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        if (req == null) {
            return Result.fail(STATUS_BAD_REQUEST, "Plugin request data must not be null");
        }

        List<M78Workspace> workspaces = workspaceService.orgNodeList(account, null);

        return botPluginService.listPublishOrOwnM78BotPlugins(req, workspaces);
    }



    @PostMapping("/org/list")
    @HttpApiDoc(apiName = "查询插件列表(插件下所有组件一并返回)", value = "/api/v1/botplugin/org/list", method = MiApiRequestMethod.POST, description = "查询插件列表(插件下所有组件一并返回)")
    public Result<Page<BotPluginOrgDTO>> listBotPluginsByOrg(HttpServletRequest request, @RequestBody PluginOrgReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (req == null) {
            return Result.fail(STATUS_BAD_REQUEST, "Plugin request data must not be null");
        }
        if (ObjectUtils.isNotEmpty(req.getWorkspaceId())) {
            Integer userRole = workspaceService.getWorkspaceRole(account, req.getWorkspaceId());
            if (userRole == null || userRole < UserRoleEnum.USER.getCode()) {
                return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
            }
        }
        String username = account.getUsername();
        return botPluginService.listM78BotPluginsOrg(req, username);
    }

    @GetMapping("/resTypes")
    @HttpApiDoc(apiName = "插件返回内容类型选项", value = "/api/v1/botplugin/resTypes", method = MiApiRequestMethod.GET, description = "插件返回内容类型选项")
    public Result<List<BotPluginResTypeDTO>> getBotPluginResTypeDef(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        List<BotPluginResTypeDTO> res = Arrays.stream(PluginResTypeEnum.values())
                .map(e -> BotPluginResTypeDTO.builder()
                        .code(e.getCode())
                        .name(e.getDesc())
                        .build())
                .collect(Collectors.toList());
        return Result.success(res);
    }

    // 启用或者停用M78BotPlugin
    @PostMapping("/toggle")
    @HttpApiDoc(apiName = "启用或停用组件", value = "/api/v1/botplugin/toggle", method = MiApiRequestMethod.POST, description = "启用或停用组件")
    public Result<Boolean> toggleBotPlugin(HttpServletRequest request, @RequestParam Long id, @RequestParam boolean enable) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (id == null) {
            return Result.fail(STATUS_BAD_REQUEST, "Plugin ID must not be null");
        }
        return botPluginService.enableOrDisablePlugin(id, enable);
    }

    // 测试M78BotPlugin运行
    @PostMapping("/http/test")
    @HttpApiDoc(apiName = "测试组件运行", value = "/api/v1/botplugin/http/test", method = MiApiRequestMethod.POST, description = "测试组件运行")
    public Result<String> testHttpBotPlugin(HttpServletRequest request, @RequestParam Long pluginId, @RequestBody(required = false) Map<String, String> inputMap) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (pluginId == null) {
            return Result.fail(STATUS_BAD_REQUEST, "Plugin ID must not be null");
        }
        if (inputMap == null || inputMap.isEmpty()) {
            log.warn("no input map detected!");
        }
        return botPluginService.testHttpPlugin(pluginId, inputMap);
    }

    // 测试M78BotPlugin运行
    @PostMapping("/dubbo/test")
    @HttpApiDoc(apiName = "测试组件运行", value = "/api/v1/botplugin/dubbo/test", method = MiApiRequestMethod.POST, description = "测试组件运行")
    public Result<Object> testDubboBotPlugin(HttpServletRequest request,
                                             @RequestBody(required = true) DubboTestReq dubboTestReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return botPluginService.testDubboPlugin(dubboTestReq);
    }

}

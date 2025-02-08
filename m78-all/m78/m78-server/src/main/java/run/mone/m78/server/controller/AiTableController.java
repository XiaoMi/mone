package run.mone.m78.server.controller;

import com.google.gson.JsonObject;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.table.*;
import run.mone.m78.api.constant.TableConstant;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.M78AiModel;
import run.mone.m78.service.dao.entity.M78BotDbTable;
import run.mone.m78.service.database.SqlParseUtil;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.bot.BotService;
import run.mone.m78.service.service.datasource.AiTableService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/15/24 16:07
 */

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/ai_table")
@HttpApiModule(value = "AiTableController", apiController = AiTableController.class)
public class AiTableController {

    @Resource
    private AiTableService aiTableService;

    @Resource
    private LoginService loginService;

    @Resource
    private BotService botService;

    //根据传入的表描述，生成表的ddl语句
    @PostMapping(value = "/generate")
    @Deprecated
    public Result<Boolean> generateDDL(HttpServletRequest request, @RequestBody @Valid DbTableReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to generate bot table");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        M78AiModel model = M78AiModel.getAiModelByLiteral(req.getModel());
        boolean created = aiTableService.createTableFromComment(account.getUsername(), req.getTableDesc(), req.getBotId(), req.getWorkspaceId(), model);
        return created ? Result.success(true) : Result.fail(STATUS_INTERNAL_ERROR, "自动生成表失败，请尝试修改需求描述后重试或手动创建表格～");
    }

    @PostMapping(value = "/generateSqlByComment")
    @HttpApiDoc(value = "/api/v1/ai_table/generateSqlByComment", method = MiApiRequestMethod.POST, apiName = "根据输入的需求生成sql")
    public Result<String> generateSqlByComment(HttpServletRequest request, @RequestBody @Valid DbTableCmtReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to generate bot table");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        List<M78BotDbTable> tableBysWorkspaceId = aiTableService.getTableByWorkspaceId(account.getUsername(), req.getWorkspaceId());
        if (CollectionUtils.isEmpty(tableBysWorkspaceId)) {
            return Result.fail(STATUS_BAD_REQUEST, "当前空间中没有关联的数据表!");
        }
        M78AiModel aiModel = M78AiModel.getAiModelByLiteral(Optional.ofNullable(req.getModel()).orElse("NOT_CONFIGURED"));
        /**
         * HINT: 目前单bot关联一张表
         * @see run.mone.m78.service.service.bot.BotService#dbRelated
         */
        JsonObject sqlRes = aiTableService.generateSqlFromDDL(aiTableService.getDDlByTableName(req.getTableName(), req.getType(), req.getConnectionId()), req.getComment(), null, "", account.getUsername(), aiModel);
        String sql = sqlRes.get("sql").getAsString();
        log.info("generate sql by req:{}", req);
        return Result.success(sql);
    }

    @PostMapping(value = "/generateDDL")
    @HttpApiDoc(value = "/api/v1/ai_table/generateDDL", method = MiApiRequestMethod.POST, apiName = "根据表描述生成表ddl语句,但不直接创建表")
    public Result<DbTableReq> generateDDLWithoutTable(HttpServletRequest request, @RequestBody @Valid DbTableReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to generate bot table");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        M78AiModel model = M78AiModel.getAiModelByLiteral(req.getModel());
        DbTableReq.DbTableReqBuilder resBuilder = DbTableReq.builder();
        try {
            String comment = req.getTableDesc();
            JsonObject jsonObject = aiTableService.generateDDLFromComment(comment, model);
            log.info("generateDDL by:{}, res:{}", comment, jsonObject);
            String sql = jsonObject.get("sql").getAsString();
            String ddl = SqlParseUtil.rewriteDDlTableName(sql, TableConstant.TABLE_PREFIX + UUIDUtil.randomNanoId() + "_");
            String tableName = SqlParseUtil.getTableName(ddl);
            log.info("ddl after rewrite:{}", ddl);
            resBuilder.tableName(tableName);
            resBuilder.model(model.name());
            resBuilder.tableDesc(comment);
            resBuilder.workspaceId(req.getWorkspaceId());
            resBuilder.botId(req.getBotId());
            resBuilder.ddl(ddl);
            resBuilder.columnInfoList(aiTableService.ddl2ColumnInfoList(ddl));
        } catch (Throwable e) {
            log.error("Error while try to generate ddl from comment, nested exception is:", e);
            return Result.fail(STATUS_INTERNAL_ERROR, "生成DDL失败，请尝试修改需求描述后重试或手动填写列信息～");
        }
        return Result.success(resBuilder.build());
    }

    @PostMapping(value = "/create")
    @HttpApiDoc(value = "/api/v1/ai_table/create", method = MiApiRequestMethod.POST, apiName = "创建数据表")
    public Result<Boolean> createTable(HttpServletRequest request, @RequestBody DbTableReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to generate bot table");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        boolean created = aiTableService.createTableByReq(account.getUsername(), req);
        return created ? Result.success(true) : Result.fail(STATUS_INTERNAL_ERROR, "创建表失败，请尝试修改需求描述后重试或修改表结构后手动创建表格～");
    }

    @PostMapping(value = "/bindEx")
    @HttpApiDoc(value = "/api/v1/ai_table/bindEx", method = MiApiRequestMethod.POST, apiName = "引入外部数据表到当前工作空间")
    public Result<Boolean> bindExternalTable(HttpServletRequest request, @RequestBody @Valid ExTableConfDTO req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to bind external table");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        Pair<Boolean, String> created = aiTableService.bindExternalTable(account.getUsername(), req);
        return created.getLeft() ? Result.success(true) : Result.fail(STATUS_INTERNAL_ERROR, "引入外部数据表到当前工作空间失败:" + created.getRight());
    }

    @PostMapping(value = "/delete")
    @HttpApiDoc(value = "/api/v1/ai_table/delete", method = MiApiRequestMethod.POST, apiName = "删除数据表")
    public Result<Boolean> deleteTable(HttpServletRequest request, @RequestBody DbTableReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to generate bot table");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        boolean deleted = aiTableService.deleteTable(account.getUsername(), req);
        return deleted ? Result.success(true) : Result.fail(STATUS_INTERNAL_ERROR, "删除数据表失败");
    }

    @GetMapping(value = "/getTableByWorkspaceId")
    @HttpApiDoc(value = "/api/v1/ai_table/getTableByWorkspaceId", method = MiApiRequestMethod.GET, apiName = "根据workspaceId获取表信息")
    public Result<List<M78BotDbTable>> getTableByWorkspaceId(HttpServletRequest request,
                                                             @RequestParam @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id") Long workspaceId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to getTableByBotId");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(aiTableService.getTableByWorkspaceId(account.getUsername(), workspaceId));
    }

    @GetMapping(value = "/getTableDetail")
    @HttpApiDoc(value = "/api/v1/ai_table/getTableDetail", method = MiApiRequestMethod.GET, apiName = "根据表名和用户名获取表详情，包含连接信息")
    public Result<DbTableDetail> getTableDetail(HttpServletRequest request,
                                                @RequestParam @HttpApiDocClassDefine(value = "tableName", description = "表名") String tableName) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to getTableByBotId");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return aiTableService.getTableDetail(account.getUsername(), tableName);
    }

    @GetMapping(value = "/getTableByBotId")
    @HttpApiDoc(value = "/api/v1/ai_table/getTableByBotId", method = MiApiRequestMethod.GET, apiName = "根据botId获取表信息")
    public Result<M78BotDbTable> getTableByBotId(HttpServletRequest request, @RequestParam @HttpApiDocClassDefine(value = "botId", description = "机器人id") Long botId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to getTableByBotId");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(aiTableService.getTableByBotId(account.getUsername(), botId));
    }

    @GetMapping(value = "/getTableByName")
    @HttpApiDoc(value = "/api/v1/ai_table/getTableByName", method = MiApiRequestMethod.GET, apiName = "根据用户名、botId和表名获取表信息")
    public Result<M78BotDbTable> getTableByUsernameAndTableName(HttpServletRequest request,
                                                                @RequestParam(required = false) @HttpApiDocClassDefine(value = "botId", description = "机器人id") Long botId,
                                                                @RequestParam @HttpApiDocClassDefine(value = "tableName", description = "表名") String tableName) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to getTableByUsernameAndTableName");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        M78BotDbTable table = aiTableService.getTableByName(username, botId, tableName);
        if (table == null) {
            log.info("Table not found for username: {} and tableName: {}", username, tableName);
            return Result.fail(STATUS_NOT_FOUND, "Table not found");
        }
        return Result.success(table);
    }

    @PostMapping(value = "/updateColumnInfos")
    @HttpApiDoc(value = "/api/v1/ai_table/updateColumnInfos", method = MiApiRequestMethod.POST, apiName = "更新表字段信息")
    public Result<Boolean> updateColumnInfos(HttpServletRequest request,
                                             @RequestBody DbTableUpdateReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to getTableByBotId");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(aiTableService.updateBotTable(account.getUsername(), req.getTableDesc(), req.getBotId(), req.getWorkspaceId(), req.getTableName(), req.getDemo(), req.getColumnInfoList()));
    }

}

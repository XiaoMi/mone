package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.datasource.*;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.ChatInfoPo;
import run.mone.m78.service.dao.entity.TableInfo;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.chat.ChatInfoService;
import run.mone.m78.service.service.datasource.DatasourceService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

/**
 * @author HawickMason@xiaomi.com
 * @author goodjava@qq.com
 * @date 1/30/24 5:23 PM
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/datasource")
@HttpApiModule(value = "DatasourceController", apiController = DatasourceController.class)
public class DatasourceController {


    @Resource
    private DatasourceService datasourceService;

    @Resource
    private LoginService loginService;

    @Resource
    private ChatInfoService chatInfoService;


    //创建一个ConnectionInfo,并插入到数据库中(class)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @HttpApiDoc(value = "/api/v1/datasource/create", method = MiApiRequestMethod.POST, apiName = "创建数据源")
    public Result<ConnectionInfoDTO> createDatasource(HttpServletRequest request,
                                            @RequestBody ConnectionInfoDTO connectionInfo) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to create datasource");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        connectionInfo.setUserName(account.getUsername());
        return datasourceService.createConnectionInfo(connectionInfo);
    }


    //删除某个Connection(class)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result<Boolean> deleteDatasource(HttpServletRequest request,
                                            @RequestBody ConnectionInfoDTO connectionInfo) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return datasourceService.deleteConnectionInfoById(connectionInfo.getId(), account.getUsername());
    }

    //更新某个Connection(class)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<Void> updateDatasource(HttpServletRequest request,
                                         @RequestBody ConnectionInfoDTO connectionInfo) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to update datasource");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return datasourceService.updateConnectionInfo(connectionInfo, account.getUsername());
    }


    //查询数据源中某张表的数据,代分页功能(class)
    @RequestMapping(value = "/queryTableData", method = RequestMethod.POST)
    public Result<SqlQueryRes> queryTableDataWithPaging(HttpServletRequest request, @RequestBody TableQueryDTO tableQueryDTO) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to query table data with paging");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return datasourceService.queryTableDataWithPaging(tableQueryDTO.getConnectionId(), tableQueryDTO.getTableName(), account.getUsername(), tableQueryDTO.getLowerBound(), tableQueryDTO.getUpperBound());
    }


    @RequestMapping(value = "/content", method = RequestMethod.POST)
    public Result<Boolean> updateContent(HttpServletRequest request,
                                         @RequestBody ConnectionInfoDTO connectionInfo) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to update content");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return datasourceService.updateContent(connectionInfo.getId(), connectionInfo.getCustomKnowledge());
    }

    //获取ConnectionInfo 列表(class)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<List<ConnectionInfoDTO>> listDatasources(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to list datasources");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return datasourceService.listConnectionInfos(account.getUsername());
    }

    //获取指定连接下所有table信息(class)
    @RequestMapping(value = "/tables", method = RequestMethod.GET)
    public Result<List<TableInfo>> getTables(HttpServletRequest request,
                                             @RequestParam("connectionId") int connectionId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get tables");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return datasourceService.tables(connectionId, account.getUsername());
    }

    //获取label信息(就是已经生成过的标签)
    @RequestMapping(value = "/labels", method = RequestMethod.GET)
    public Result<List<ChatInfoPo>> getChatInfoByConnectionId(HttpServletRequest request, @RequestParam("connectionId") int connectionId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get tables");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        List<ChatInfoPo> list = chatInfoService.getChatInfoBySessionIdAndUserName(String.valueOf(connectionId), account.getUsername());
        return Result.success(list);
    }


    //执行指定的SQL查询并返回结果列表，使用给定的数据库连接ID来获取数据库连接信息。(class)
    @RequestMapping(value = "/executeSql", method = RequestMethod.POST)
    public Result<SqlQueryRes> executeSql(HttpServletRequest request,
                                          @RequestBody DatasourceSqlParam datasourceSqlParam) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to execute SQL");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return datasourceService.executeSql(datasourceSqlParam, account.getUsername());
    }

    //删除某个数据源下的table,table name当参数传入(class)
    @RequestMapping(value = "/deleteTable", method = RequestMethod.POST)
    public Result<Boolean> deleteTable(HttpServletRequest request, @RequestParam("datasourceId") int datasourceId, @RequestParam("tableName") String tableName) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to delete table");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return datasourceService.deleteTable(datasourceId, tableName, account.getUsername());
    }

    //查询指定table 的表结构,返回List list中value 的值 是 columnName 和 columnType, 查询 INFORMATION_SCHEMA.`COLUMNS` 数据库(class)
    @RequestMapping(value = "/tableStructure", method = RequestMethod.GET)
    public Result<List<Map<String, String>>> getTableStructure(HttpServletRequest request, @RequestParam("datasourceId") int datasourceId, @RequestParam("tableName") String tableName) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get table structure");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return datasourceService.queryTableStructure(datasourceId, tableName, account.getUsername());
    }

    //你根据table名字和修改后的 List<Map<String, String>> column信息,然后你根据原始的column信息,生成 alter table 的语句并执行(class)
    @RequestMapping(value = "/alterTableColumns", method = RequestMethod.POST)
    @HttpApiDoc(apiName = "修改表结构", value = "/api/v1/datasource/alterTableColumns", method = MiApiRequestMethod.POST, description = "修改表结构")
    public Result<Boolean> alterTableColumns(HttpServletRequest request, @RequestBody TableAlterationDTO tableAlterationDTO) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to alter table columns");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return datasourceService.alterTableColumns(tableAlterationDTO.getDatasourceId(), tableAlterationDTO.getTableName(), tableAlterationDTO.getColumnOperations(), account.getUsername());
    }

    @RequestMapping(value = "/alterTableDatas", method = RequestMethod.POST)
    public Result<Boolean> alterTableDatas(HttpServletRequest request, @RequestBody TableDataAlterationDTO tableDataAlterationDTO) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to alter table columns");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return datasourceService.alterTableDatas(tableDataAlterationDTO, account.getUsername());
    }


}

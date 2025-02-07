package run.mone.m78.service.service.datasource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.datasource.ConnectionInfoDTO;
import run.mone.m78.api.bo.datasource.DatasourceSqlParam;
import run.mone.m78.api.bo.datasource.SqlQueryRes;
import run.mone.m78.api.bo.datasource.TableDataAlterationDTO;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.ResultUtils;
import run.mone.m78.service.dao.entity.ChatInfoPo;
import run.mone.m78.service.dao.entity.ConnectionInfo;
import run.mone.m78.service.dao.entity.QueryContext;
import run.mone.m78.service.dao.entity.TableInfo;
import run.mone.m78.service.dao.entity.table.ConnectionInfoTableDef;
import run.mone.m78.service.dao.mapper.ConnectionInfoMapper;
import run.mone.m78.service.database.SqlExecutor;
import run.mone.m78.service.database.SqlParseUtil;
import run.mone.m78.service.service.api.ApiGeneratorService;
import run.mone.m78.service.service.api.GroovyService;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.chat.ChatInfoService;
import run.mone.m78.service.service.user.UserService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.PromptConstant.PROMPT_GET_SQL;
import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author goodjava@qq.com
 * @date 2024/2/2 15:45
 */
@Service
@Slf4j
public class DatasourceService {


    @Resource
    private ConnectionInfoMapper connectionInfoMapper;

    @Resource
    private ChatgptService chatgptService;

    @Resource
    private SqlExecutor sqlExecutor;

    @Resource
    private ChatInfoService chatInfoService;


    @Resource
    private ApiGeneratorService apiGeneratorService;

    @Resource
    private GroovyService groovyService;

    @Resource
    private UserService userService;

    /**
     * 创建一个ConnectionInfo，并插入到数据库中
     *
     * @param connectionInfoDto 包含连接信息的DTO对象
     * @return 包含操作结果的Result对象，成功时返回ConnectionInfoDTO，失败时返回错误信息
     */
	//创建一个ConnectionInfo,并插入到数据库中(class)
    public Result<ConnectionInfoDTO> createConnectionInfo(ConnectionInfoDTO connectionInfoDto) {
        if (connectionInfoDto == null) {
            return Result.fail(STATUS_BAD_REQUEST, "ConnectionInfo object is null");
        }
        ConnectionInfoDTO res = new ConnectionInfoDTO();
        String userName = connectionInfoDto.getUserName();
        List<ConnectionInfo> connectionInfos = connectionInfoMapper.selectListByCondition(
                ConnectionInfoTableDef.CONNECTION_INFO.USER_NAME.eq(userName));
        if (CollectionUtils.isNotEmpty(connectionInfos)) {
            Optional<ConnectionInfo> exist = connectionInfos.stream().filter(c -> c.getJdbcUrl().equals(connectionInfoDto.getJdbcUrl())).findAny();
            if (exist.isPresent()) {
                BeanUtils.copyProperties(exist.get(), res);
                return Result.success(res);
            }
        }
        try {
            ConnectionInfo connectionInfo = ConnectionInfo.builder()
                    .host(connectionInfoDto.getHost())
                    .jdbcUrl(connectionInfoDto.getJdbcUrl())
                    .pwd(connectionInfoDto.getPwd())
                    .cluster(connectionInfoDto.getCluster())
                    .port(connectionInfoDto.getPort())
                    .queue(connectionInfoDto.getQueue())
                    .type(connectionInfoDto.getType())
                    .kerberos(connectionInfoDto.getKerberos())
                    .database(connectionInfoDto.getDatabase())
                    .user(connectionInfoDto.getUser())
                    .userName(userName)
                    .build();
            connectionInfoMapper.insertSelective(connectionInfo);
            BeanUtils.copyProperties(connectionInfo, res);
        } catch (Exception e) {
            log.error("Error inserting ConnectionInfo into database", e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error inserting ConnectionInfo into database: " + e.getMessage());
        }

        return Result.success(res);
    }

    /**
     * 更新ConnectionInfo
     *
     * @param connectionInfoDto 包含更新信息的ConnectionInfoDTO对象
     * @param username 当前操作的用户名
     * @return 更新结果的Result对象
     */
	//更新ConnectionInfo
    public Result<Void> updateConnectionInfo(ConnectionInfoDTO connectionInfoDto, String username) {
        if (connectionInfoDto == null) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "ConnectionInfo object is null");
        }
        if (!connectionInfoDto.getUserName().equals(username)) {
            return Result.fail(STATUS_FORBIDDEN, "ConnectionInfo's user mismatch!");
        }

        try {
            ConnectionInfo connectionInfo = new ConnectionInfo();
            BeanUtils.copyProperties(connectionInfoDto, connectionInfo);
            connectionInfoMapper.update(connectionInfo);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Error updating ConnectionInfo", e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error updating ConnectionInfo: " + e.getMessage());
        }
    }

    /**
     * 删除指定数据源下的表
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名
     * @param username 用户名
     * @return 删除操作的结果，成功返回true，失败返回错误信息
     */
	//删除某个数据源下的table,table name当参数传入(class)
    public Result<Boolean> deleteTable(int datasourceId, String tableName, String username) {
        ConnectionInfo connectionInfo = connectionInfoMapper.selectOneById(datasourceId);
        if (connectionInfo == null || !connectionInfo.getUserName().equals(username)) {
            return Result.fail(STATUS_NOT_FOUND, "ConnectionInfo not found or access denied");
        }
        try {
            String dropTableSql = String.format("DROP TABLE IF EXISTS %s", tableName);
            sqlExecutor.exec(dropTableSql, new QueryContext(connectionInfo));
            return Result.success(true);
        } catch (Exception e) {
            log.error("Error deleting table: {}", tableName, e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error deleting table: " + e.getMessage());
        }
    }


    /**
     * 查询数据源中某张表的数据，带分页功能
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名
     * @param username 用户名
     * @param lowerLimit 分页下限
     * @param upperLimit 分页上限
     * @return 查询结果，包含数据和总记录数
     */
	//查询数据源中某张表的数据,代分页功能(class)
    public Result<SqlQueryRes> queryTableDataWithPaging(int datasourceId, String tableName, String username, int lowerLimit, int upperLimit) {
        try {
            ConnectionInfo connectionInfo = connectionInfoMapper.selectOneById(datasourceId);
            if (connectionInfo == null || !connectionInfo.getUserName().equals(username)) {
                return Result.fail(STATUS_NOT_FOUND, "ConnectionInfo not found or access denied");
            }
            String sql = String.format("SELECT * FROM %s LIMIT %d , %d", tableName, lowerLimit, upperLimit);
            List<Map<String, Object>> data = sqlExecutor.exec(sql, new QueryContext(connectionInfo));
            String countSql = String.format("SELECT COUNT(*) FROM %s", tableName);
            List<Map<String, Object>> countResult = sqlExecutor.exec(countSql, new QueryContext(connectionInfo));
            int total = Integer.valueOf(countResult.get(0).get("COUNT(*)").toString());
            return Result.success(SqlQueryRes.builder().total(total).data(data).build());
        } catch (Exception e) {
            log.error("Error querying table data with paging", e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error querying table data with paging: " + e.getMessage());
        }
    }


    /**
     * 更新customKnowledge字段
     *
     * @param id 连接信息的ID
     * @param content 要更新的内容
     * @return 更新结果，成功返回true，失败返回相应的错误信息
     */
	//更新customknowledge，入参Integer id, String content
    public Result<Boolean> updateContent(Integer id, String content) {
        ConnectionInfo connectionInfo = connectionInfoMapper.selectOneById(id);
        if (connectionInfo == null) {
            return Result.fail(STATUS_NOT_FOUND, "not exist");
        }

        try {
            connectionInfo.setCustomKnowledge(content);
            connectionInfoMapper.update(connectionInfo);
            return Result.success(true);
        } catch (Exception e) {
            return Result.fail(STATUS_INTERNAL_ERROR, "wrong");
        }
    }

    /**
     * 根据ID和用户名删除ConnectionInfo
     *
     * @param id 要删除的ConnectionInfo的ID
     * @param username 用户名，用于匹配要删除的ConnectionInfo
     * @return 包含删除操作结果的Result对象，成功时返回true，失败时返回错误信息
     */
	//按id删除ConnectionInfo(class)
    public Result<Boolean> deleteConnectionInfoById(int id, String username) {
        try {
            int result = connectionInfoMapper.deleteByCondition(
                    ConnectionInfoTableDef.CONNECTION_INFO.ID.eq(id)
                            .and(ConnectionInfoTableDef.CONNECTION_INFO.USER_NAME.eq(username))
            );
            if (result == 0) {
                log.warn("No ConnectionInfo found with id: {}", id);
                return Result.fail(STATUS_NOT_FOUND, "No ConnectionInfo found with the specified id");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("Error deleting ConnectionInfo by id", e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error deleting ConnectionInfo by id: " + e.getMessage());
        }
    }

    /**
     * 获取指定用户名的ConnectionInfo列表
     *
     * @param username 用户名
     * @return 包含ConnectionInfoDTO列表的Result对象，如果发生异常则返回错误信息
     */
	//获取ConnectionInfo 列表(class)
    public Result<List<ConnectionInfoDTO>> listConnectionInfos(String username) {
        try {
            List<ConnectionInfo> connectionInfos = connectionInfoMapper.selectListByCondition(
                    ConnectionInfoTableDef.CONNECTION_INFO.USER_NAME.eq(username)
            );
            List<ConnectionInfoDTO> dtos = connectionInfos.stream().map(it -> {
                ConnectionInfoDTO dto = new ConnectionInfoDTO();
                BeanUtils.copyProperties(it, dto);
                return dto;
            }).collect(Collectors.toList());
            return Result.success(dtos);
        } catch (Exception e) {
            log.error("Error retrieving list of ConnectionInfos", e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error retrieving ConnectionInfo list: " + e.getMessage());
        }
    }

    /**
     * 获取指定连接下所有表的信息
     *
     * @param connectionId 连接ID
     * @param username 用户名
     * @return 包含表信息的结果对象，如果连接不存在或用户名不匹配则返回null
     */
	//获取指定连接下所有table信息
    public Result<List<TableInfo>> tables(int connectionId, String username) {
        ConnectionInfo connection = connectionInfoMapper.selectOneById(connectionId);
        if (null == connection) {
            return null;
        }
        if (!connection.getUserName().equals(username)) {
            return null;
        }
        return Result.success(sqlExecutor.getTableInfosByConnection(connection, username));
    }

    /**
     * 执行SQL查询或脚本，并返回结果
     *
     * @param datasourceSqlParam 数据源SQL参数
     * @param userName 用户名
     * @return 包含查询结果的Result对象
     */
	public Result<SqlQueryRes> executeSql(DatasourceSqlParam datasourceSqlParam, String userName) {
        String sql = "";
        String uuid = String.valueOf(datasourceSqlParam.getConnectionId());
        String decide = "sql";

        String comment = datasourceSqlParam.getComment();

        //不做ai判定,直接生成代码
        if (comment.startsWith("script:")) {
            comment = comment.substring("script:".length());
            decide = "script";
        }
        log.info("comment:{}", comment);
        try {
            ChatInfoPo chatInfoPo = chatInfoService.getExistingSqlByDocumentIdAndChatContent(uuid, comment);

            String createTableStatement = "";

            log.debug("chatInfoPo {}", chatInfoPo);

            //这个版本先不进行任何操作
            if (chatInfoPo != null && chatInfoPo.getType() == 1) {
                log.debug("code:{}", chatInfoPo.getMappingContent());
                String condditions = chatInfoPo.getConditions();
                if (StringUtils.isNotEmpty(condditions)) {
                    log.info("call script");
                    JsonElement obj = GsonUtils.gson.fromJson(condditions, JsonElement.class);
                    if (obj.isJsonArray()) {
                        JsonObject param = new JsonObject();
                        JsonArray jsonArray = obj.getAsJsonArray();
                        jsonArray.forEach(it -> {
                            JsonObject jsonObject = it.getAsJsonObject();
                            jsonObject.keySet().forEach(it2 -> {
                                JsonElement value = jsonObject.get(it2);
                                param.add(it2, value);
                            });
                        });
                        Object res = groovyService.invoke(chatInfoPo.getMappingContent(), "execute", ImmutableMap.of(), param, null);
                        if (res instanceof JsonObject) {
                            JsonObject apiResult = (JsonObject) res;
                            List<Map<String, Object>> mapList = ResultUtils.convertApiResultToJsonMaps(apiResult);
                            return Result.success(SqlQueryRes.builder().total(mapList.size()).data(mapList).build());
                        }
                    }
                }
                return Result.success(SqlQueryRes.builder().total(0).data(Lists.newArrayList()).build());
            }

            if (null == chatInfoPo) {
                if (!decide.equals("script")) {
                    createTableStatement = ddl(datasourceSqlParam, userName);
                    Map<String, String> res = apiGeneratorService.decideUseSqlOrScript(ImmutableMap.of("comment", comment, "ddl", createTableStatement), userName);
                    decide = res.get("decide");
                }
                //通过ai判断是否生成脚本
                log.info("decideUseSqlOrScript decide:{}", decide);
                //使用脚本
                if (decide.equals("script")) {
                    //通过ai直接生成脚本
                    Map<String, String> codeRes = apiGeneratorService.generateCodeUsingChatGptService(ImmutableMap.of("context", "", "comment", comment), userName);
                    log.info("generateCodeUsingChatGptService res:{}", codeRes);
                    String code = codeRes.get("code");
                    //参数名列表:a,b,c 类似这样
                    String params = codeRes.get("params");

                    chatInfoService.recordChatInfo(ChatInfoPo.builder()
                            .sessionId(String.valueOf(datasourceSqlParam.getConnectionId()))
                            .user(userName)
                            .content(comment)
                            .chatInfoMeta(ImmutableMap.of("params", params, "code", code))
                            .type(1)
                            .mappingContent(code).build());

                    return Result.success(SqlQueryRes.builder().total(0).data(Lists.newArrayList()).build());
                }
            }

            String existingSqlByDocumentIdAndChatContent = (null == chatInfoPo) ? null : chatInfoPo.getMappingContent();

            //先看看存储过解析出来的sql没有
            if (existingSqlByDocumentIdAndChatContent != null) {
                sql = existingSqlByDocumentIdAndChatContent;
            } else if (SqlParseUtil.isSqlStatement(comment)) {
                //如果输入的是sql,则直接运行
                sql = comment;
                chatInfoService.recordChatInfo(ChatInfoPo.builder().sessionId(String.valueOf(datasourceSqlParam.getConnectionId())).user(userName)
                        .content(datasourceSqlParam.getComment())
                        .mappingContent(sql).build());
            } else {
                Map<String, String> chatgptParams = Maps.newHashMap();
                chatgptParams.put("qryDesc", datasourceSqlParam.getComment());
                createTableStatement = ddl(datasourceSqlParam, userName);
                chatgptParams.put("DDL", createTableStatement);
                chatgptParams.put("customKnowledge", datasourceSqlParam.getCustomKnowledge());
                //让ai产生sql
                String model = userService.getUserConfig(userName, true).getDocumentModel(Config.model);
                Result<String> chatgptRes = chatgptService.call(PROMPT_GET_SQL, chatgptParams, "sql", model);
                sql = chatgptRes.getData();
                //需要存储一份到chatInfoPo中,这样就不用二次解析sql了
                chatInfoService.recordChatInfo(ChatInfoPo.builder().sessionId(String.valueOf(datasourceSqlParam.getConnectionId())).user(userName)
                        .content(datasourceSqlParam.getComment())
                        .mappingContent(sql).build());
            }
            log.info("DatasourceService.executeSql, sql:{}", sql);

            if (datasourceSqlParam.getLowerBound() == null) {
                datasourceSqlParam.setLowerBound(0);
            }

            if (datasourceSqlParam.getUpperBound() == null) {
                datasourceSqlParam.setUpperBound(5000);
            }

            String countSql = SqlParseUtil.transformSelectToCount(sql);
            log.info("count sql:{}", countSql);

            List<Map<String, Object>> countResult = executeSqlQueryUsingConnectionId(datasourceSqlParam.getConnectionId(), countSql);
            int total = Integer.valueOf(countResult.get(0).get("COUNT(*)").toString());

            String querySql = SqlParseUtil.addLimitToSelectSql(sql, datasourceSqlParam.getLowerBound(), datasourceSqlParam.getUpperBound());
            log.info("query sql:{}", querySql);

            List<Map<String, Object>> result = executeSqlQueryUsingConnectionId(datasourceSqlParam.getConnectionId(), querySql);
            return Result.success(SqlQueryRes.builder().total(total).data(result).build());
        } catch (Exception e) {
            log.error("Error executing SQL: {}", sql, e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error executing SQL, checkout the log!");
        }
    }

    @NotNull
    private String ddl(DatasourceSqlParam datasourceSqlParam, String userName) {
        List<TableInfo> tableList = sqlExecutor.getTableInfos(datasourceSqlParam.getConnectionId(), userName);
        //选择部分表,如果不选择,则是全部表(需要根据客户端的输入)
        if (datasourceSqlParam.getTableNames() != null && datasourceSqlParam.getTableNames().size() > 0) {
            tableList = tableList.stream().filter(it -> datasourceSqlParam.getTableNames().contains(it.getTableName())).collect(Collectors.toList());
        }
        String createTableStatement = tableList.stream().map(it -> sqlExecutor.fetchCreateTableStatement(datasourceSqlParam.getConnectionId(), it.getTableName())).collect(Collectors.joining("\n\n"));
        return createTableStatement;
    }


    /**
     * 执行指定的SQL查询并返回结果列表，使用给定的数据库连接ID来获取数据库连接信息。
     */
    public List<Map<String, Object>> executeSqlQueryUsingConnectionId(Integer connectionId, String sql) {
        ConnectionInfo connection = connectionInfoMapper.selectOneById(connectionId);
        return sqlExecutor.exec(sql, new QueryContext(connection));
    }

    /**
     * Stupid OVERLOAD...
     */
    public List<Map<String, Object>> executeSqlQueryUsingConnectionId(Long connectionId, String sql) {
        ConnectionInfo connection = connectionInfoMapper.selectOneById(connectionId);
        return sqlExecutor.exec(sql, new QueryContext(connection));
    }

    /**
     * 查询指定表的表结构，返回包含列名和列类型的列表。
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名
     * @param username 用户名
     * @return 包含表结构信息的结果对象，成功时返回列名和列类型的列表，失败时返回错误信息
     */
	//查询指定table 的表结构,返回List list中value 的值 是 columnName 和 columnType, 查询 INFORMATION_SCHEMA.`COLUMNS` 数据库(class)
    public Result<List<Map<String, String>>> queryTableStructure(int datasourceId, String tableName, String username) {
        ConnectionInfo connectionInfo = connectionInfoMapper.selectOneById(datasourceId);
        if (connectionInfo == null || !connectionInfo.getUserName().equals(username)) {
            return Result.fail(STATUS_NOT_FOUND, "ConnectionInfo not found or access denied");
        }
        try {
            // 修改SQL查询以包括COLUMN_COMMENT字段
            String sql = String.format("SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT, COLUMN_KEY, EXTRA, IS_NULLABLE, COLUMN_DEFAULT FROM INFORMATION_SCHEMA.`COLUMNS` WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s'", connectionInfo.getDatabase(), tableName);
            List<Map<String, Object>> columns = sqlExecutor.exec(sql, new QueryContext(connectionInfo));
            List<Map<String, String>> columnDetails = columns.stream().map(column -> {
                Map<String, String> details = new HashMap<>();
                details.put("columnName", (String) column.get("COLUMN_NAME"));
                details.put("columnType", (String) column.get("COLUMN_TYPE"));
                // 添加columnComment到映射中
                details.put("columnComment", (String) column.get("COLUMN_COMMENT"));
                details.put("isNullable", (String) column.get("IS_NULLABLE"));
                details.put("columnDefault", (String) column.get("COLUMN_DEFAULT"));
//                details.put("columnAvatar", SqlTypeClassifier.classifyType((String) column.get("COLUMN_TYPE")));
                if (column.containsKey("COLUMN_KEY") && "PRI".equals((String) column.get("COLUMN_KEY"))) {
                    details.put("isPrimaryKey", (String) column.get("COLUMN_NAME"));
                }
                return details;
            }).collect(Collectors.toList());
            return Result.success(columnDetails);
        } catch (Exception e) {
            log.error("Error querying table structure for table: {}", tableName, e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error querying table structure: " + e.getMessage());
        }
    }

    /**
     * 根据提供的表数据修改信息执行相应的数据库操作（插入、删除、更新）。
     *
     * @param tableDataAlterationDTO 包含表数据修改信息的DTO对象
     * @param username 当前操作用户的用户名
     * @return 包含操作结果的Result对象，成功时返回true，失败时返回错误信息
     */
	public Result<Boolean> alterTableDatas(TableDataAlterationDTO tableDataAlterationDTO, String username) {
        ConnectionInfo connectionInfo = connectionInfoMapper.selectOneById(tableDataAlterationDTO.getDatasourceId());
        if (connectionInfo == null || !connectionInfo.getUserName().equals(username)) {
            return Result.fail(STATUS_NOT_FOUND, "ConnectionInfo not found or access denied");
        }
        try {
            // 准备语句
            String alterStatement = "";
            switch (tableDataAlterationDTO.getOperationType()) {
                case "INSERT":
                    String keys = String.join(",", tableDataAlterationDTO.getNewData().keySet());
                    String values = tableDataAlterationDTO.getNewData().values().stream()
                            .map(value -> "'" + value + "'")
                            .collect(Collectors.joining(","));
                    alterStatement = String.format("INSERT INTO %s (%s) VALUES (%s)", tableDataAlterationDTO.getTableName(), keys, values);
                    break;
                case "DELETE":
                    alterStatement = String.format("DELETE FROM %s WHERE id=%s", tableDataAlterationDTO.getTableName(), tableDataAlterationDTO.getId());
                    break;
                case "UPDATE":
                    String update = tableDataAlterationDTO.getUpdateData().entrySet().stream()
                            .map(entry -> entry.getKey() + "='" + entry.getValue() + "'")
                            .collect(Collectors.joining(", "));
                    alterStatement = String.format("UPDATE %s SET %s where id=%s ", tableDataAlterationDTO.getTableName(), update, tableDataAlterationDTO.getId());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operation type: " + tableDataAlterationDTO.getOperationType());
            }

            // 执行语句
            sqlExecutor.exec(alterStatement, new QueryContext(connectionInfo));

            return Result.success(true);
        } catch (Exception e) {
            log.error("Error altering table datas for table: {}", tableDataAlterationDTO.getTableName(), e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error altering table datas: " + e.getMessage());
        }
    }


    /**
     * 根据表名和修改后的列信息生成并执行ALTER TABLE语句
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名
     * @param columnOperations 列操作信息列表，每个操作包含列名、操作类型、列类型、是否可为空、默认值和注释等信息
     * @param username 用户名，用于验证权限
     * @return 包含操作结果的Result对象，成功时返回true，失败时返回错误信息
     */
	//你根据table名字和修改后的 List<Map<String, String>> column信息,然后你根据原始的column信息,生成 alter table 的语句并执行(class)
    public Result<Boolean> alterTableColumns(int datasourceId, String tableName, List<Map<String, String>> columnOperations, String username) {
        ConnectionInfo connectionInfo = connectionInfoMapper.selectOneById(datasourceId);
        if (connectionInfo == null || !connectionInfo.getUserName().equals(username)) {
            return Result.fail(STATUS_NOT_FOUND, "ConnectionInfo not found or access denied");
        }
        if (connectionInfo.getDatabase().equals("m78")) {
            return Result.fail(STATUS_FORBIDDEN, "ConnectionInfo is RESERVED!!! NO ACTION ALLOW!");
        }
        try {
            // 准备ALTER语句
            List<String> alterStatements = new ArrayList<>();

            // 处理每个列的操作
            for (Map<String, String> columnOperation : columnOperations) {
                if (columnOperation.containsKey("isPrimaryKey") && columnOperation.get("isPrimaryKey") != null) {
                    log.info("skip modifying primary key of table: {}", tableName);
                    continue;
                }
                String operationType = columnOperation.get("operationType");
                String columnName = columnOperation.get("columnName");
                String columnType = columnOperation.get("columnType");
                String nullable = columnOperation.get("nullable");
                String defaultValue = columnOperation.get("defaultValue");
                String comment = columnOperation.get("columnComment"); // 新增comment字段

                String alterStatement = "";
                switch (operationType) {
                    case "ADD":
                        alterStatement = String.format("ALTER TABLE %s ADD COLUMN %s %s", tableName, columnName, columnType);
                        break;
                    case "MODIFY":
                        alterStatement = String.format("ALTER TABLE %s MODIFY COLUMN %s %s", tableName, columnName, columnType);
                        break;
                    case "DROP":
                        alterStatement = String.format("ALTER TABLE %s DROP COLUMN %s", tableName, columnName);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid operation type: " + operationType);
                }

                if (!operationType.equals("DROP")) {
                    if ("NO".equals(nullable)) {
                        alterStatement += " NOT NULL";
                    }
                    if (defaultValue != null) {
                        alterStatement += " DEFAULT '" + defaultValue + "'";
                    }
                    if (comment != null) {
                        alterStatement += " COMMENT '" + comment + "'";
                    }
                }

                alterStatements.add(alterStatement);
            }

            // 执行每个ALTER TABLE语句
            for (String alterSql : alterStatements) {
                sqlExecutor.exec(alterSql, new QueryContext(connectionInfo));
            }

            return Result.success(true);
        } catch (Exception e) {
            log.error("Error altering table columns for table: {}", tableName, e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error altering table columns: " + e.getMessage());
        }
    }


    /**
     * 获取指定表的列信息，列信息包括是否是主键、列名称和列类型
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名称
     * @param username 用户名
     * @return 包含列信息的结果列表，如果失败则返回错误信息
     */
	//获取指定表的列信息,列信息包括是否是主键 列名称  列类型(class)
    public Result<List<Map<String, Object>>> getTableColumns(int datasourceId, String tableName, String username) {
        ConnectionInfo connectionInfo = connectionInfoMapper.selectOneById(datasourceId);
        if (connectionInfo == null || !connectionInfo.getUserName().equals(username)) {
            return Result.fail(STATUS_NOT_FOUND, "ConnectionInfo not found or access denied");
        }
        try {
            // 查询列信息，包括列名称、列类型和是否为主键
            String sql = String.format(
                    "SELECT COLUMN_NAME, COLUMN_TYPE, " +
                            "(CASE WHEN COLUMN_KEY = 'PRI' THEN TRUE ELSE FALSE END) AS IS_PRIMARY_KEY " +
                            "FROM INFORMATION_SCHEMA.COLUMNS " +
                            "WHERE TABLE_SCHEMA = %s AND TABLE_NAME = %s",
                    connectionInfo.getDatabase(), tableName
            );

            // 执行查询
            List<Map<String, Object>> columnsInfo = sqlExecutor.exec(sql, new QueryContext(connectionInfo), connectionInfo.getDatabase(), tableName);

            // 构建结果列表
            List<Map<String, Object>> columnDetails = columnsInfo.stream().map(column -> {
                Map<String, Object> details = new HashMap<>();
                details.put("columnName", column.get("COLUMN_NAME"));
                details.put("columnType", column.get("COLUMN_TYPE"));
                details.put("isPrimaryKey", column.get("IS_PRIMARY_KEY"));
                return details;
            }).collect(Collectors.toList());

            return Result.success(columnDetails);
        } catch (Exception e) {
            log.error("Error retrieving columns for table: {}", tableName, e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error retrieving table columns: " + e.getMessage());
        }
    }



}

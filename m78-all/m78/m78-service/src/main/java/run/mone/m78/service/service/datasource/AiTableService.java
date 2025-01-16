package run.mone.m78.service.service.datasource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.mone.m78.api.bo.datasource.ConnectionInfoDTO;
import run.mone.m78.api.bo.table.*;
import run.mone.m78.api.constant.TableConstant;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.M78AiModel;
import run.mone.m78.service.common.M78StringUtils;
import run.mone.m78.service.common.SqlTypeClassifier;
import run.mone.m78.service.dao.entity.ConnectionInfo;
import run.mone.m78.service.dao.entity.M78BotDbTable;
import run.mone.m78.service.dao.entity.M78BotTableTypeEnum;
import run.mone.m78.service.dao.entity.QueryContext;
import run.mone.m78.service.dao.mapper.M78BotDbTableMapper;
import run.mone.m78.service.database.SqlExecutor;
import run.mone.m78.service.database.SqlParseUtil;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.database.UserDBConfig;
import run.mone.m78.service.exceptions.*;
import run.mone.m78.service.service.base.ChatgptService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.mybatisflex.core.query.QueryMethods.noCondition;
import static run.mone.m78.api.constant.CommonConstant.AI_TABLE_TIMEOUT;
import static run.mone.m78.api.constant.PromptConstant.PROMPT_M78_GENERATE_DDL;
import static run.mone.m78.api.constant.PromptConstant.PROMPT_M78_GENERATE_SQL_FROM_REQUIREMENT;
import static run.mone.m78.service.dao.entity.table.M78BotDbTableTableDef.M78_BOT_DB_TABLE;

/**
 * @author goodjava@qq.com
 * @date 2024/3/14 23:09
 */
@Slf4j
@Service
public class AiTableService {

    @Resource
    private M78BotDbTableMapper botDbTableMapper;

    @Resource
    private ChatgptService chatgptService;

    @Resource
    private SqlExecutor sqlExecutor;

    @Resource
    private DatasourceService datasourceService;

    @Resource
    private UserDBConfig userDBConfig;

    private static final ThreadPoolExecutor AI_TABLE_POOL_EXECUTOR =
            new ThreadPoolExecutor(
                    20,
                    20,
                    0,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(1000),
                    new BasicThreadFactory.Builder().namingPattern("ai-table-pool-%d").build(),
                    new ThreadPoolExecutor.CallerRunsPolicy());

    private volatile QueryContext userDbQueryContext;

    /**
     * 初始化方法，在对象创建后调用，设置用户数据库查询上下文。
     * 使用从userDBConfig获取的配置信息来构建ConnectionInfo对象，并初始化QueryContext。
     */
	@PostConstruct
    public void init() {
        userDbQueryContext = new QueryContext(ConnectionInfo.builder()
                .host(userDBConfig.getUserDh())
                .port(userDBConfig.getUserDp())
                .user(userDBConfig.getUserUname())
                .pwd(userDBConfig.getUserPwd())
                .database(userDBConfig.getUserDn())
                .jdbcUrl(userDBConfig.getUserUrl())
                .build());
    }

    /**
     * 根据注释生成表的DDL语句
     *
     * @param comment 表的注释信息
     * @param model 使用的AI模型
     * @return 生成的DDL语句的Json对象
     * @throws InternalException 当执行过程中发生中断或执行异常时抛出
     * @throws TimeOutException 当调用模型生成表DDL超时时抛出
     */
	//根据注释生成表的ddl语句
    public JsonObject generateDDLFromComment(String comment, M78AiModel model) {
        Future<JsonObject> future = AI_TABLE_POOL_EXECUTOR.submit(() -> chatgptService.callWithModel(PROMPT_M78_GENERATE_DDL, ImmutableMap.of("comment", comment), model.name()));
        try {
            return future.get(AI_TABLE_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalException("执行异常，请稍后重试～");
        } catch (TimeoutException e) {
            log.warn("调用模型生成表ddl超时, 详情:", e);
            throw new TimeOutException("调用模型生成表ddl超时, 请尝试修改描述或修改模型后重试～");
        }
    }

    /**
     * 根据传入的表名和列信息列表生成建表语句并返回
     *
     * @param tableName 表名
     * @param columnInfoList 列信息列表
     * @return 生成的建表语句
     * @throws InvalidArgumentException 如果表名或列信息列表为空
     */
	//根据传入的String tableName和List<M78ColumnInfo> columnInfoList, 生成建表语句并返回
    public String generateCreateTableStatement(String tableName, List<M78ColumnInfo> columnInfoList) {
        if (StringUtils.isBlank(tableName) || CollectionUtils.isEmpty(columnInfoList)) {
            throw new InvalidArgumentException("表名和列描述为空!");
        }
        StringBuilder ddlBuilder = new StringBuilder();
        ddlBuilder.append("CREATE TABLE `").append(tableName).append("` (\n");
        List<String> primaryKeyColumns = new ArrayList<>();
        for (M78ColumnInfo columnInfo : columnInfoList) {
            ddlBuilder.append("  `").append(columnInfo.getName()).append("` ").append(SqlTypeClassifier.getDefaultTypeMapping(columnInfo.getType()));
            if (columnInfo.getPrimaryKey()) {
                primaryKeyColumns.add(columnInfo.getName());
                ddlBuilder.append(" NOT NULL ");
                ddlBuilder.append(" AUTO_INCREMENT");

            } else if (!columnInfo.getNecessary()) {
                ddlBuilder.append(" DEFAULT NULL");
            }
            if (StringUtils.isNotBlank(columnInfo.getDesc())) {
                ddlBuilder.append(" COMMENT '").append(columnInfo.getDesc()).append("'");
            }
            ddlBuilder.append(",\n");
        }
        if (!primaryKeyColumns.isEmpty()) {
            ddlBuilder.append("  PRIMARY KEY (");
            ddlBuilder.append(primaryKeyColumns.stream().map(name -> "`" + name + "`").collect(Collectors.joining(", ")));
            ddlBuilder.append(")\n");
        } else {
            ddlBuilder.delete(ddlBuilder.length() - 2, ddlBuilder.length()); // Remove the last comma and newline
        }
        ddlBuilder.append("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
        return ddlBuilder.toString();
    }


    /**
     * 根据表的DDL语句和需求生成SQL语句
     *
     * @param ddl 表的DDL语句
     * @param requirement 需求描述
     * @param dataContext 数据上下文，可为空
     * @param demo 示例数据，可为空
     * @param userName 用户名
     * @param model 使用的AI模型
     * @return 生成的SQL语句，封装在JsonObject中
     */
	//根据表的ddl语句和需求生成sql语句(class)
    public JsonObject generateSqlFromDDL(String ddl, String requirement, String dataContext, String demo, String userName, M78AiModel model) {
        return chatgptService.callWithModel(PROMPT_M78_GENERATE_SQL_FROM_REQUIREMENT,
                ImmutableMap.of("ddl", ddl,
                        "requirement", requirement,
                        "user_name", userName,
                        "dataContext", dataContext != null ? dataContext : "",
                        "demo", demo != null ? demo : ""), model.name());
    }

    /**
     * 根据评论创建表格
     *
     * @param userName 用户名
     * @param comment 评论内容
     * @param botId 机器人ID
     * @param workspaceId 工作区ID
     * @param model AI模型
     * @return 表格创建是否成功
     * @throws InternalException 执行异常时抛出
     * @throws TimeOutException 超时时抛出
     * @deprecated 使用新的方法替代
     */
	@Deprecated
    public boolean createTableFromComment(String userName, String comment, Long botId, Long workspaceId, M78AiModel model) {
        Future<Boolean> future = AI_TABLE_POOL_EXECUTOR.submit(() -> doCreateTableFromCommentLegacy(userName, comment, botId, workspaceId, model));
        try {
            return future.get(AI_TABLE_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalException("执行异常，请稍后重试～");
        } catch (TimeoutException e) {
            log.warn("调用模型生成表并保存超时, 详情:", e);
            throw new TimeOutException("调用模型生成表并保存超时, 请尝试修改描述或修改模型后重试～");
        }
    }

    @Deprecated
    private boolean doCreateTableFromCommentLegacy(String userName, String comment, Long botId, Long workspaceId, M78AiModel model) {
        log.info("create table from comment:{}, model:{}", comment, model);
        try {
            JsonObject jsonObject = generateDDLFromComment(comment, model);
            log.info("create table from comment:{}, by:{}", comment, jsonObject);
            String sql = jsonObject.get("sql").getAsString();
            String ddl = SqlParseUtil.rewriteDDlTableName(sql, TableConstant.TABLE_PREFIX + UUIDUtil.randomNanoId() + "_");
            String tableName = SqlParseUtil.getTableName(ddl);
            log.info("ddl after rewrite:{}", ddl);
            return save(userName, comment, botId, workspaceId, ddl, null, tableName);
        } catch (Throwable e) {
            log.error("Error while try to create table from comment, nested exception is:", e);
            return false;
        }
    }

    private boolean save(String userName, String comment, Long botId, Long workspaceId, String ddl,  List<M78ColumnInfo> columnInfoList, String tableName) {
        QueryContext queryContext = getUserDBQueryContext();
        sqlExecutor.exec(ddl, queryContext);
        saveM78BotDbTable(M78BotDbTable.builder()
                .botId(botId)
                .workspaceId(workspaceId)
                .tableName(tableName)
                .tableDesc(comment)
                .creator(userName)
                .type(M78BotTableTypeEnum.INTERNAL.getCode())
                .createTime(LocalDateTime.now())
                .columnInfoList(
                        CollectionUtils.isEmpty(columnInfoList)
                        ?  getColumnDetails(tableName, queryContext).stream()
                                .map(d -> M78ColumnInfo.builder()
                                        .name(d.get("columnName"))
                                        .desc(d.get("columnComment"))
                                        .type(d.get("columnType"))
                                        .primaryKey(StringUtils.isNotBlank(d.get("isPrimaryKey")))
                                        .necessary(false)
                                        .build())
                                .collect(Collectors.toList())
                        : columnInfoList
                )
                .build());
        return true;
    }

    /**
     * 根据botId获取关联的数据表
     *
     * @param userName 用户名
     * @param botId 机器人ID
     * @return 关联的M78BotDbTable对象
     * @throws InvalidArgumentException 如果未找到关联的数据表或数据表数量不为1
     */
	public M78BotDbTable getTableByBotId(String userName, Long botId) {
        List<M78BotDbTable> botTables = botDbTableMapper.selectListByQuery(new QueryWrapper().eq("bot_id", botId));
        // TODO: for now botTables should be a size one list
        if (CollectionUtils.isEmpty(botTables)) {
            throw new InvalidArgumentException("未找到botId为" + botId + ",关联的数据表");
        }
        if (botTables.size() != 1) {
            log.warn("multiple tables related to botId:{}, will take the first one for now!", botId);
        }
        return botTables.getFirst();
    }

    /**
     * 根据用户名、机器人ID和表名获取数据库表信息
     *
     * @param userName 用户名
     * @param botId 机器人ID
     * @param tableName 表名
     * @return 数据库表信息
     * @throws IllegalArgumentException 如果表名为空
     * @throws NotFoundException 如果未找到对应的表
     */
	public M78BotDbTable getTableByName(String userName, Long botId, String tableName) {
        log.info("getTableByName, userName:{}, tableName:{}", userName, tableName);
        Preconditions.checkArgument(StringUtils.isNotBlank(tableName), "查询的表名不能为空!");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(M78_BOT_DB_TABLE.DEFAULT_COLUMNS)
                .from(M78_BOT_DB_TABLE)
                .where(M78_BOT_DB_TABLE.TABLE_NAME.eq(tableName))
                //.and(botId != null ? M78_BOT_DB_TABLE.BOT_ID.eq(botId) : noCondition())
                .and(StringUtils.isNotBlank(userName) ? M78_BOT_DB_TABLE.CREATOR.eq(userName) : noCondition());
        M78BotDbTable m78BotDbTable = botDbTableMapper.selectOneByQuery(queryWrapper);
        if (m78BotDbTable == null) {
            throw new NotFoundException("未找到botId:" + botId + ", 用户:" + userName + "下的表:" + tableName);
        }
        return m78BotDbTable;
    }

    /**
     * 根据ID获取M78BotDbTable表信息
     *
     * @param id 表的ID，不能为空
     * @return 对应ID的M78BotDbTable表信息
     * @throws IllegalArgumentException 如果ID为空
     * @throws NotFoundException 如果未找到对应ID的表
     */
	public M78BotDbTable getTableById(Long id) {
        log.info("getTableById, id:{}", id);
        Preconditions.checkArgument(id != null, "查询的表id不能为空!");
        M78BotDbTable m78BotDbTable = botDbTableMapper.selectOneById(id);
        if (m78BotDbTable == null) {
            throw new NotFoundException("未找到id为:" + id + "的表!");
        }
        log.info("id:{}, tableName:{}", id, m78BotDbTable.getTableName());
        return m78BotDbTable;
    }

    /**
     * 根据工作区ID获取数据表列表
     *
     * @param userName 用户名
     * @param workspaceId 工作区ID
     * @return 数据表列表，如果未找到则返回空列表
     */
	public List<M78BotDbTable> getTableByWorkspaceId(String userName, Long workspaceId) {
        List<M78BotDbTable> botTables = new ArrayList<>();
        botTables = botDbTableMapper.selectListByQuery(new QueryWrapper().eq("workspace_id", workspaceId));
        if (CollectionUtils.isEmpty(botTables)) {
            log.warn("未找到workspaceId为:{},下有任何数据表!", workspaceId);
        }
        return botTables;
    }

    /**
     * 获取指定用户下的表详情
     *
     * @param userName 用户名
     * @param tableName 表名
     * @return 包含表详情的结果对象，如果未找到表则返回失败信息
     */
	public Result<DbTableDetail> getTableDetail(String userName, String tableName) {
        log.info("Get Table Detail for userName:{}, tableName:{}", userName, tableName);
        M78BotDbTable m78BotDbTable = botDbTableMapper.selectOneByQuery(new QueryWrapper().eq("creator", userName).eq("table_name", tableName));
        if (m78BotDbTable == null) {
            return Result.fail(ExCodes.STATUS_NOT_FOUND, "未找到用户:" + userName + "下的表:" + tableName);
        }
        DbTableDetail res = DbTableDetail.builder()
                .connectionInfo(
                        ConnectionInfoDTO.builder()
                                .host(userDBConfig.getUserDh())
                                .port(userDBConfig.getUserDp())
                                .user(userDBConfig.getUserUname())
                                .pwd(userDBConfig.getUserPwd())
                                .database(userDBConfig.getUserDn())
                                .jdbcUrl(userDBConfig.getUserUrl())
                                .userName(userName)
                                .build()
                )
                .tableName(tableName)
                .build();
        return Result.success(res);
    }

    /**
     * 更新机器人数据库表的信息
     *
     * @param userName 用户名
     * @param tableDesc 表描述
     * @param botId 机器人ID
     * @param workspaceId 工作空间ID
     * @param tableName 表名
     * @param demo 示例数据
     * @param columnInfoList 列信息列表
     * @return 更新是否成功
     */
	public boolean updateBotTable(String userName, String tableDesc, Long botId, Long workspaceId, String tableName, String demo, List<M78ColumnInfo> columnInfoList) {
        M78BotDbTable tableByBotId = getTableByName(userName, botId, tableName);
        M78BotDbTable update = UpdateEntity.of(M78BotDbTable.class, tableByBotId.getId());
        update.setTableName(tableName);
        update.setColumnInfoList(columnInfoList);
        update.setDemo(demo);
        update.setTableDesc(tableDesc);
        boolean updateColTable = updateColumnInfos(tableName, columnInfoList);
        return botDbTableMapper.update(update) > 0 && updateColTable;
    }

    private List<Map<String, String>> getColumnDetails(String tableName, QueryContext queryContext) {
        String database = queryContext.getConnectionInfo().getDatabase();
        String sql = String.format("SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT, COLUMN_KEY, EXTRA, IS_NULLABLE, COLUMN_DEFAULT FROM INFORMATION_SCHEMA.`COLUMNS` WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s'", database, tableName);
        List<Map<String, Object>> columns = sqlExecutor.exec(sql, queryContext);
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
        return columnDetails;
    }

    /**
     * 根据传入的List<M78ColumnInfo> columnInfoList和tableName，修改表名为tableName的表，将这个表的列更新为columnInfoList中所定义的列
     *
     * @param tableName 表名
     * @param columnInfoList 列信息列表
     * @return 更新是否成功
     */
	// 根据传入的List<M78ColumnInfo> columnInfoList和tableName，修改表名为tableName的表，将这个表的列更新为columnInfoList中所定义的列
    public boolean updateColumnInfos(String tableName, List<M78ColumnInfo> columnInfoList) {
        log.info("Modifying table columns for table: {}", tableName);
        try {
            QueryContext queryContext = getUserDBQueryContext();

            // Fetch existing columns from the database
            List<Map<String, String>> existingColumns = getColumnDetails(tableName, queryContext);

            // Create a map of existing column names for quick lookup
            Set<String> existingColumnNames = existingColumns.stream()
                    .map(column -> column.get("columnName"))
                    .collect(Collectors.toSet());

            Set<String> existingPrimaryKeyColumnNames = existingColumns.stream()
                    .filter(column -> StringUtils.isNotBlank(column.get("isPrimaryKey")))
                    .map(column -> column.get("columnName"))
                    .collect(Collectors.toSet());

            // Generate SQL for adding, modifying, and dropping columns
            List<String> alterStatements = new ArrayList<>();
            for (M78ColumnInfo columnInfo : columnInfoList) {
                String columnName = columnInfo.getName();
                String columnType = SqlTypeClassifier.getDefaultTypeMapping(columnInfo.getType());
                if (existingColumnNames.contains(columnName)) {
                    // Modify existing column
                    if (existingPrimaryKeyColumnNames.contains(columnName)) {
                        continue;
                    }
                    alterStatements.add(String.format("ALTER TABLE `%s` MODIFY COLUMN `%s` %s COMMENT '%s'",
                            tableName, columnName, columnType, columnInfo.getDesc()));
                } else {
                    // Add new column
                    alterStatements.add(String.format("ALTER TABLE `%s` ADD COLUMN `%s` %s COMMENT '%s'",
                            tableName, columnName, columnType, columnInfo.getDesc()));
                }
            }

            // Identify columns to drop
            for (String existingColumnName : existingColumnNames) {
                if (columnInfoList.stream().noneMatch(columnInfo -> columnInfo.getName().equals(existingColumnName))
                        && !existingPrimaryKeyColumnNames.contains(existingColumnName)) {
                    // Drop column not present in the new list
                    alterStatements.add(String.format("ALTER TABLE `%s` DROP COLUMN `%s`", tableName, existingColumnName));
                }
            }

            // Execute alter statements
            for (String alterStatement : alterStatements) {
                sqlExecutor.exec(alterStatement, queryContext);
            }

            // Update the M78BotDbTable record with the new column information
//            M78BotDbTable botDbTable = M78BotDbTable.builder()
//                    .tableName(tableName)
//                    .columnInfoList(columnInfoList)
//                    .build();
//            saveM78BotDbTable(botDbTable);

            return true;
        } catch (Throwable e) {
            log.error("Error while trying to modify table columns for table: {}, nested exception is: ", tableName, e);
            return false;
        }
    }

    /**
     * 执行SQL生成并返回结果的JSON字符串
     *
     * @param sql 要执行的SQL语句
     * @return 执行结果的JSON字符串
     */
	public String executeGenerateSql(String sql) {
        return GsonUtils.gson.toJson(sqlExecutor.exec(sql, getUserDBQueryContext()));
    }

    public String executeGenerateSql(DbTableAnalysisBo analysisBo) {
        String sql = analysisBo.getSql();
        String type = analysisBo.getSqlType();
        Integer tableType = analysisBo.getTableType();
        if (M78BotTableTypeEnum.INTERNAL.getCode() == tableType) {
            if (type.equals("select")) {
                log.info("select sql:{}", sql);
                return GsonUtils.gson.toJson(sqlExecutor.exec(sql, getUserDBQueryContext()));
            }
            if (type.equals("insert") || type.equals("delete") || type.equals("update")) {
                log.info("update sql:{}", sql);
                sqlExecutor.update(sql, getUserDBQueryContext());
            }
            return "ok";
        } else if (M78BotTableTypeEnum.EXTERNAL.getCode() == tableType) {
            if (type.equals("select")) {
                // mason TODO 预期当前对外部表只读
                log.info("select sql extern:{}", sql);
                return GsonUtils.gson.toJson(datasourceService.executeSqlQueryUsingConnectionId(analysisBo.getConnectionId(), sql));
            }
        }
        return "ok";
    }

    /**
     * 根据表名获取DDL语句
     *
     * @param tableName 表名
     * @param tableType 表类型
     * @param connectionId 连接ID
     * @return 表的DDL语句
     */
	public String getDDlByTableName(String tableName, Integer tableType, Long connectionId) {
        if (tableType == null || M78BotTableTypeEnum.EXTERNAL == M78BotTableTypeEnum.getTypeEnumByCode(tableType) && Objects.nonNull(connectionId)) {
            return sqlExecutor.fetchCreateTableStatement(connectionId, tableName);
        } else {
            return sqlExecutor.fetchCreateTableStatement(getUserDBQueryContext(), tableName);
        }
    }

    /**
     * 根据表名获取样本数据
     *
     * @param tableName 表名
     * @return 样本数据列表，每个样本数据为一个包含字段名和字段值的Map
     */
	public List<Map<String, Object>> getSampleDataByTableName(String tableName) {
        List<Map<String, Object>> res = new ArrayList<>();
        try {
            List<Map<String, Object>> sampleData = sqlExecutor.exec("select * from " + tableName + " limit 4", getUserDBQueryContext());
            if (CollectionUtils.isNotEmpty(sampleData)) {
                res = sampleData;
            }
        } catch (Exception e) {
            log.error("Failed to get sampled data, nested exception is:", e);
        }
        return res;
    }

    /**
     * 保存或更新M78BotDbTable对象
     *
     * @param table 要保存或更新的M78BotDbTable对象
     */
	public void saveM78BotDbTable(M78BotDbTable table) {
        botDbTableMapper.insertOrUpdate(table);
    }

    /**
     * 根据表ID删除数据库表
     *
     * @param tableId 表的ID
     */
	@Transactional
    public void dropTableById(Long tableId) {
        log.warn("deleting ai table with id:{}", tableId);
        M78BotDbTable m78BotDbTable = botDbTableMapper.selectOneById(tableId);
        botDbTableMapper.deleteById(tableId);
        log.warn("dropping table:{}", m78BotDbTable.getTableName());
        sqlExecutor.exec("DROP TABLE IF EXISTS `" + m78BotDbTable.getTableName() + "`", getUserDBQueryContext());
    }

    private QueryContext getUserDBQueryContext() {
        return userDbQueryContext;
    }

    private boolean isCreating(Long botId, String userName) {
        if (botId == null) {
            return true;
        }
        try {
            M78BotDbTable tableByBotId = getTableByBotId(userName, botId);
            return tableByBotId == null;
        } catch (Exception e) {
            log.warn("check if is creating table got error, but not fatal, msg:{}", e.getMessage());
            return true;
        }
    }

    /**
     * 将DDL语句解析为M78ColumnInfo对象的列表
     *
     * @param ddl DDL语句
     * @return 解析后的M78ColumnInfo对象列表
     * @throws JSQLParserException 如果解析DDL语句时发生错误
     */
	public List<M78ColumnInfo> ddl2ColumnInfoList(String ddl) throws JSQLParserException {
        List<Map<String, String>> columnDetails = SqlParseUtil.parseTableColumnDetails(ddl);
        List<M78ColumnInfo> res = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(columnDetails)) {
            res = columnDetails.stream()
                    .map(d -> M78ColumnInfo.builder()
                            .name(M78StringUtils.stripStr(d.get("columnName"), "`"))
                            .desc(d.get("columnComment"))
                            .type(d.get("columnType"))
                            .primaryKey(StringUtils.isNotBlank(d.get("isPrimaryKey")))
                            .necessary(false)
                            .build())
                    .collect(Collectors.toList());
        }
        return res;
    }

    /**
     * 根据请求创建数据库表
     *
     * @param username 用户名
     * @param req 包含建表信息的请求对象
     * @return 表是否创建成功
     */
	public boolean createTableByReq(String username, DbTableReq req) {
        // rewrite table name
        String tableName = req.getTableName();
        Preconditions.checkArgument(StringUtils.isNotBlank(tableName), "建表时表名不能为空");
        if (!StringUtils.startsWith(tableName, TableConstant.TABLE_PREFIX)) {
            tableName = TableConstant.TABLE_PREFIX  + UUIDUtil.randomNanoId() + "_" + tableName;
        }
        String ddl = "";
        if (StringUtils.isNotBlank(req.getDdl())) {
            // AI 建表
            ddl = req.getDdl();
        } else {
            // 手工建表
            Preconditions.checkArgument(CollectionUtils.isNotEmpty(req.getColumnInfoList()), "手工建表时列信息不能为空");
            ddl = generateCreateTableStatement(tableName, req.getColumnInfoList());
        }
        return save(username, req.getTableDesc(), req.getBotId(), req.getWorkspaceId(), ddl, req.getColumnInfoList(), tableName);
    }

    /**
     * 删除指定用户的表
     *
     * @param username 用户名，不能为空
     * @param req 包含表ID的请求对象，不能为空且表ID不能为空
     * @return 如果表存在并成功删除，返回true；否则返回false
     */
	public boolean deleteTable(String username, DbTableReq req) {
        Preconditions.checkArgument(StringUtils.isNotBlank(username), "用户名不能为空");
        Preconditions.checkArgument(req != null && req.getId() != null, "表id不能为空");
        M78BotDbTable m78BotDbTable = botDbTableMapper.selectOneByQuery(new QueryWrapper().eq("creator", username).eq("id", req.getId()));
        if (m78BotDbTable == null) {
            log.warn("No table id:{}, under user:{}. Will not perform deletion!", req.getId(), username);
            return false;
        }
        log.info("deleting table:{}, at id:{}", m78BotDbTable.getTableName(), req.getId());
        dropTableById(req.getId());
        return true;
    }

    /**
     * 绑定外部表到数据库
     *
     * @param username 用户名
     * @param req 包含外部表配置信息的请求对象
     * @return 包含操作是否成功的布尔值和消息的Pair对象
     */
	public Pair<Boolean, String> bindExternalTable(String username, ExTableConfDTO req) {
        try {
            for (ExTableConfDTO.ExTable exTable : req.getExTables()) {
                saveM78BotDbTable(M78BotDbTable.builder()
                        .workspaceId(req.getWorkspaceId())
                        .tableName(exTable.getTableName())
                        .tableDesc(exTable.getTableDesc())
                        .creator(username)
                        .createTime(LocalDateTime.now())
                        .type(M78BotTableTypeEnum.EXTERNAL.getCode())
                        .connectionId(req.getConnectionId())
                        .build());
            }
            return Pair.of(true, "success");
        } catch (Throwable e) {
            log.error("failed to bind external table, nested exception is:", e);
            return Pair.of(false, e.getMessage());
        }
    }


}

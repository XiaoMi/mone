package run.mone.m78.service.database;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import run.mone.m78.service.common.M78StringUtils;

import java.util.*;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/9/24 8:32 PM
 */
@Slf4j
public class SqlParseUtil {

    private SqlParseUtil() {
        // default constructor to prevent instantiation
    }

    /**
     * Parses the given SQL query to extract the table name, returning the first table name found or an empty string if none found, and logs warnings or errors as appropriate.
     */
    public static String getTableName(String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            List<String> tableList = tablesNamesFinder.getTableList(statement);
            if (CollectionUtils.isEmpty(tableList)) {
                log.warn("this should not happen! you should check you sql: {}", sql);
                return "";
            }
            if (tableList.size() != 1) {
                log.warn("this should not happen! you should check you sql: {}", sql);
            }
            return tableList.get(0);
        } catch (Exception e) {
            log.error("Error while try to get tableName from sql : {}, nested exception is: ", sql, e);
            return "";
        }
    }

    /**
     * Parses the given SQL query to extract column names and their corresponding values from the WHERE clause, returning a list of maps with column names as keys and their values as map values.
     */
    public static List<Map<String, Object>> getColumnNames(String sqlStr) throws JSQLParserException {
        Select s = (Select) CCJSqlParserUtil.parse(sqlStr);
        PlainSelect select = (PlainSelect) s.getSelectBody();

        SelectItem selectItem =
                select.getSelectItems().get(0);
        Table table = (Table) select.getFromItem();

        Expression where = select.getWhere();

        if (where == null) {
            return new ArrayList<>();
        }

        // List to hold the column names
        List<Map<String, Object>> columnNames = new ArrayList<>();

        // Use ExpressionDeParser to parse the WHERE clause and extract column names
        ExpressionDeParser expressionDeParser = new ExpressionDeParser() {
            @Override
            public void visit(Column column) {

                super.visit(column);
            }

            @Override
            public void visit(EqualsTo equalsTo) {
                Expression rightExp = equalsTo.getRightExpression();
                String value = "";
                if (rightExp instanceof StringValue) {
                    StringValue sv = (StringValue) rightExp;
                    value = sv.getValue();
                } else if (rightExp instanceof LongValue) {
                    LongValue lv = (LongValue) rightExp;
                    value = lv.getStringValue();
                } else {
                    value = rightExp.toString();
                }
                columnNames.add(ImmutableMap.of(equalsTo.getLeftExpression().toString(), value));
                super.visit(equalsTo);
            }
        };
        where.accept(expressionDeParser);

        return columnNames;
    }

    /**
     * Updates the WHERE clause of a given SQL SELECT statement with new column values provided in a map, returning the modified SQL statement.
     */
    public static String updateSqlWhereParts(String originalSql, Map<String, Object> newValues) throws JSQLParserException {
        log.info("original sql: {}", originalSql);

        if (MapUtils.isEmpty(newValues)) {
            return originalSql;
        }

        Select selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
        PlainSelect selectBody = (PlainSelect) selectStatement.getSelectBody();
        Expression where = selectBody.getWhere();

        if (where == null) {
            return originalSql;
        }

        // Use ExpressionDeParser to parse the WHERE clause and update column values
        ExpressionDeParser expressionDeParser = new ExpressionDeParser() {
            @Override
            public void visit(EqualsTo equalsTo) {
                // Get the left expression as a column
                String columnName = equalsTo.getLeftExpression().toString();
                // Check if the column is in the map of new values
                if (newValues.containsKey(columnName)) {
                    // Update the right expression with the new value
                    Object newValue = newValues.get(columnName);
                    if (newValue instanceof String) {
                        equalsTo.setRightExpression(new StringValue("'" + newValue + "'"));
                    } else if (newValue instanceof Long) {
                        equalsTo.setRightExpression(new LongValue(newValue.toString()));
                    }
                }
                super.visit(equalsTo);
            }
        };

        // Use SelectDeParser to rebuild the select statement with the updated WHERE clause
        SelectDeParser selectDeParser = new SelectDeParser(expressionDeParser, new StringBuilder());
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(selectDeParser.getBuffer());
        where.accept(expressionDeParser);

        // Get the updated SQL query
        String res = selectDeParser.getBuffer().toString();
        log.info("updated conditions: {} ### updated sql: {}", res, selectBody);
        return selectBody.toString();
    }

    //判断是否是sql语句
    public static boolean isSqlStatement(String sql) {
        try {
            // 尝试解析SQL语句
            CCJSqlParserUtil.parse(sql);
            // 如果没有抛出异常，说明是有效的SQL语句
            return true;
        } catch (JSQLParserException e) {
            // 解析过程中抛出异常，说明不是有效的SQL语句
            return false;
        }
    }

    //给你一个sql语句,这个sql语句是select语句,你把select后,列的数据改为 count(*) 然后返回完整的sql(class)
    public static String transformSelectToCount(String sql) throws JSQLParserException {
        Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect selectBody = (PlainSelect) selectStatement.getSelectBody();
        // Replace the select items with a new function 'COUNT(*)'
        Function count = new Function();
        count.setName("COUNT");
        count.setAllColumns(true);
        selectBody.setSelectItems(Collections.singletonList(new SelectExpressionItem(count)));
        return selectStatement.toString();
    }


    //给你一个sql语句,这个sql语句是select语句,你帮我加上limit限制,上下限是参数,如果代码里已经有limit则直接忽略,返回原有sql(class)
    public static String addLimitToSelectSql(String sql, int lowerBound, int upperBound) throws JSQLParserException {
        Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect selectBody = (PlainSelect) selectStatement.getSelectBody();
        Limit limit = selectBody.getLimit();
        if (limit != null) {
            // SQL already contains LIMIT, return the original SQL
            return sql;
        }
        // Add new LIMIT to the SQL
        Limit newLimit = new Limit();
        newLimit.setRowCount(new LongValue(upperBound));
        newLimit.setOffset(new LongValue(lowerBound));
        selectBody.setLimit(newLimit);
        return selectStatement.toString();
    }

    //给你一个select语句,帮我解析出来表名字和字段名字,有可能是一个复杂查询设计多张表(class)
    public static Pair<String, List<String>> parseTableNameAndColumns(String sql) throws JSQLParserException {
        Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect selectBody = (PlainSelect) selectStatement.getSelectBody();
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableNames = tablesNamesFinder.getTableList(selectStatement);
        List<String> columnNames = new ArrayList<>();

        for (SelectItem item : selectBody.getSelectItems()) {
            item.accept(new SelectItemVisitorAdapter() {
                @Override
                public void visit(AllColumns columns) {
                    columnNames.add("*");
                }

                @Override
                public void visit(AllTableColumns columns) {
                    columnNames.add(columns.getTable().getName() + ".*");
                }

                @Override
                public void visit(SelectExpressionItem item) {
                    Expression expression = item.getExpression();
                    if (expression instanceof Column) {
                        Column column = (Column) expression;
                        columnNames.add(column.getColumnName());
                    } else {
                        columnNames.add(expression.toString());
                    }
                }
            });
        }

        String tableName = tableNames.isEmpty() ? "" : tableNames.get(0);
        return Pair.of(tableName, columnNames);
    }

    public static boolean isJoinQuery(String sql) throws JSQLParserException {
        Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect selectBody = (PlainSelect) selectStatement.getSelectBody();

        List<Join> joins = selectBody.getJoins();
        // If the joins list is not null and not empty, it means there is at least one JOIN statement
        return joins != null && !joins.isEmpty();
    }

    public enum SqlOperationType {
        SELECT,
        INSERT,
        UPDATE,
        DELETE,
        UNKNOWN
    }

    public static SqlOperationType getSqlOperationType(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);

        if (statement instanceof Select) {
            return SqlOperationType.SELECT;
        } else if (statement instanceof Insert) {
            return SqlOperationType.INSERT;
        } else if (statement instanceof Update) {
            return SqlOperationType.UPDATE;
        } else if (statement instanceof Delete) {
            return SqlOperationType.DELETE;
        } else {
            return SqlOperationType.UNKNOWN;
        }
    }

    // 传入一个DDL类型的sql，将表名修改为以user_m78_开头，并返回修改后的sql
    public static String rewriteDDlTableName(String ddlSql, String tablePrefix) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(ddlSql);
        if (statement instanceof CreateTable) {
            CreateTable createTable = (CreateTable) statement;
            String originalTableName = createTable.getTable().getName();
            if (originalTableName.startsWith("`") && originalTableName.endsWith("`")) {
                originalTableName = originalTableName.substring(1, originalTableName.length() - 1);
            }
            String modifiedTableName = tablePrefix + originalTableName;
            createTable.getTable().setName(modifiedTableName);
            return createTable.toString();
        } else {
            throw new IllegalArgumentException("The provided SQL does not represent a DDL statement for creating a table.");
        }
    }

    // 根据表的ddl语句解析生成List<Map<String, String>> columnDetails
    public static List<Map<String, String>> parseTableColumnDetails(String ddl) throws JSQLParserException {
        CreateTable createTableStatement = (CreateTable) CCJSqlParserUtil.parse(ddl);
        List<ColumnDefinition> columnDefinitions = createTableStatement.getColumnDefinitions();
        List<Map<String, String>> columnDetails = new ArrayList<>();
        for (ColumnDefinition columnDef : columnDefinitions) {
            Map<String, String> details = new HashMap<>();
            details.put("columnName", columnDef.getColumnName());
            ColDataType colDataType = columnDef.getColDataType();
            String dataType = colDataType.getDataType();
            List<String> argumentsStringList = colDataType.getArgumentsStringList();
            if (CollectionUtils.isNotEmpty(argumentsStringList)) {
                dataType = dataType + "(" + M78StringUtils.collection2DelimitedStr(argumentsStringList, ",") + ")";
            }
            details.put("columnType", dataType);
            List<String> columnSpecs = columnDef.getColumnSpecs();
            if (columnSpecs != null) {
                String specs = String.join(" ", columnSpecs);
                details.put("columnSpecs", specs);
                String comment = extractColumnComments(columnSpecs);
                details.put("columnComment", M78StringUtils.stripStr(comment, "'"));
                String pkCol = hasPrimaryKeyConstraint(columnSpecs, columnDef);
                if (StringUtils.isNotBlank(pkCol)) {
                    details.put("isPrimaryKey", pkCol);
                }
            }
            columnDetails.add(details);
        }
        return columnDetails;
    }

    public static String extractColumnComments(List<String> specs) {
        if (CollectionUtils.isEmpty(specs)) {
            return "";
        }
        for (int i = 0; i < specs.size(); i++) {
            if ("COMMENT".equalsIgnoreCase(specs.get(i)) && i + 1 < specs.size()) {
                return specs.get(i + 1);
            }
        }
        return "";
    }

    // 从columnDefinitions中提取到的ColumnSpecs里获取列上是否有主键约束
    public static String hasPrimaryKeyConstraint(List<String> specs, ColumnDefinition columnDef) {
        for (String spec : specs) {
            if ("PRIMARY".equalsIgnoreCase(spec) || "PRIMARY KEY".equalsIgnoreCase(spec)) {
                return columnDef.getColumnName();
            }
        }
        return "";
    }
}

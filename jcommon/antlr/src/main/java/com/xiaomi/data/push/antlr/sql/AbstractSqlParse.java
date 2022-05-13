/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.data.push.antlr.sql;


import com.xiaomi.data.push.antlr.sql.constants.Constants;
import com.xiaomi.data.push.antlr.sql.constants.OperatorType;
import com.xiaomi.data.push.antlr.sql.exceptions.SqlParseException;
import com.xiaomi.data.push.antlr.sql.model.SqlElement;
import com.xiaomi.data.push.antlr.sql.model.TableInfo;
import com.xiaomi.data.push.antlr.sql.util.Pair;
import com.xiaomi.data.push.antlr.sql.util.StringPairUtils;
import com.xiaomi.data.push.antlr.sql.util.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractSqlParse implements SqlParse {

    private static Logger log = LoggerFactory.getLogger(AbstractSqlParse.class);

    protected final String columnSplit = ",";
    protected Map<String, String> tableAliaMap;
    protected HashSet<String> columnsStack;
    protected Stack<String> limitStack;

    protected boolean hasJoin;

    protected String currentDb;

    protected HashSet<String> splitColumn(Set<String> columns, Map<String, String> tableMap) {
        return (HashSet<String>) columns.stream()
                .flatMap(column -> Arrays.stream(column.split(columnSplit)))
                .collect(Collectors.toSet())
                .stream()
                .map(column -> {
                    if (column.contains(Constants.POINT)) {
                        Pair<String, String> pair = StringPairUtils.getPointPair(column);
                        String aDefault = tableMap.getOrDefault(pair.getLeft(), pair.getLeft());
                        return aDefault + Constants.POINT + pair.getRight();
                    }
                    return column;
                }).collect(Collectors.toSet());
    }


    protected HashSet<String> getColumnsTop() {
        if (columnsStack.isEmpty()) {
            return new HashSet<>(0);
        }
        return columnsStack;
    }


    protected String getLimitTop() {
        if (limitStack.isEmpty()) {
            return null;
        }
        return limitStack.pop();
    }


    protected TableInfo buildTableInfo(String name, String db, OperatorType type) {
        TableInfo info = new TableInfo(name, db, type, splitColumn(getColumnsTop(), tableAliaMap));
        info.setLimit(getLimitTop());
        return info;
    }

    protected TableInfo buildTableInfo(String dbAndTable, OperatorType type) {
        TableInfo info = new TableInfo(dbAndTable, type, currentDb, splitColumn(getColumnsTop(), tableAliaMap));
        info.setLimit(getLimitTop());
        return info;
    }

    /**
     * 替换sql注释
     *
     * @param sqlText sql
     * @return 替换后的sl
     */
    protected String replaceNotes(String sqlText) {
        StringBuilder newSql = new StringBuilder();
        String lineBreak = "\n";
        String empty = "";
        String trimLine;
        for (String line : sqlText.split(lineBreak)) {
            trimLine = line.trim();
            if (!trimLine.startsWith("--") && !trimLine.startsWith("download")) {
                //过滤掉行内注释
                line = line.replaceAll("/\\*.*\\*/", empty);
                if (StringPairUtils.isNotBlank(line)) {
                    newSql.append(line).append(lineBreak);
                }
            }
        }
        return newSql.toString();
    }


    /**
     * ;分割多段sql
     *
     * @param sqlText sql
     * @return
     */
    protected ArrayList<String> splitSql(String sqlText) {
        String[] sqlArray = sqlText.split(Constants.SEMICOLON);
        ArrayList<String> newSqlArray = new ArrayList<>(sqlArray.length);
        String command = "";
        int arrayLen = sqlArray.length;
        String oneCmd;
        for (int i = 0; i < arrayLen; i++) {
            oneCmd = sqlArray[i];
            boolean keepSemicolon = (oneCmd.endsWith("'") && i + 1 < arrayLen && sqlArray[i + 1].startsWith("'"))
                    || (oneCmd.endsWith("\"") && i + 1 < arrayLen && sqlArray[i + 1].startsWith("\""));
            if (oneCmd.endsWith("\\")) {
                command += org.apache.commons.lang.StringUtils.chop(oneCmd) + Constants.SEMICOLON;
                continue;
            } else if (keepSemicolon) {
                command += oneCmd + Constants.SEMICOLON;
                continue;
            } else {
                command += oneCmd;
            }
            if (!StringPairUtils.isNotBlank(command)) {
                continue;
            }
            newSqlArray.add(command);
            command = "";
        }
        return newSqlArray;
    }


    @Override
    public SqlElement parse(String sqlText) throws SqlParseException {

        ArrayList<String> sqlArray = this.splitSql(this.replaceNotes(sqlText));
        HashSet<TableInfo> inputTables = new HashSet<>();
        HashSet<TableInfo> outputTables = new HashSet<>();
        HashSet<TableInfo> tempTables = new HashSet<>();

        columnsStack = new HashSet<>();
        tableAliaMap = new HashMap<>();
        limitStack = new Stack<>();
        currentDb = "default";
        hasJoin = false;
        for (String sql : sqlArray) {
            if (sql.charAt(sql.length() - 1) == ';') {
                sql = sql.substring(0, sql.length() - 1);
            }
            if (!StringPairUtils.isNotBlank(sql)) {
                continue;
            }
            columnsStack.clear();
            tableAliaMap.clear();
            limitStack.clear();
            Tuple3<HashSet<TableInfo>, HashSet<TableInfo>, HashSet<TableInfo>> subTuple = this.parseInternal(sql);
            inputTables.addAll(subTuple._1());
            outputTables.addAll(subTuple._2());
            tempTables.addAll(subTuple._3());
        }

        tempTables.forEach(table -> {
            Iterator<TableInfo> iterator = inputTables.iterator();
            while (iterator.hasNext()) {
                TableInfo checkTable = iterator.next();
                if (checkTable.getName().equals(table.getName())) {
                    iterator.remove();
                    break;
                }
            }
        });

        return new SqlElement(inputTables, outputTables, tempTables, hasJoin);
    }

    /**
     * 抽象解析
     *
     * @param sqlText sql
     * @return tuple4
     * @throws SqlParseException
     */
    protected abstract Tuple3<HashSet<TableInfo>, HashSet<TableInfo>, HashSet<TableInfo>> parseInternal(String sqlText) throws SqlParseException;

    protected void print(String plan) {
        log.info("************ignore plan******\n" + plan);
    }
}

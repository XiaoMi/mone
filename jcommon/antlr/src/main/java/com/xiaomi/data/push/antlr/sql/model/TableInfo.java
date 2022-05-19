package com.xiaomi.data.push.antlr.sql.model;

import com.xiaomi.data.push.antlr.sql.constants.Constants;
import com.xiaomi.data.push.antlr.sql.constants.OperatorType;
import com.xiaomi.data.push.antlr.sql.util.Pair;
import com.xiaomi.data.push.antlr.sql.util.StringPairUtils;
import lombok.Data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class TableInfo {

    /**
     * 表名
     */
    private String name;

    /**
     * 库名
     */
    private String dbName;

    private OperatorType type;

    private Set<String> columns;

    private String limit;

    private boolean selectAll;

    private boolean isDb;

    public TableInfo() {
    }

    public TableInfo(String dbName, OperatorType type) {
        this.dbName = dbName;
        this.type = type;
        this.isDb = true;
    }

    public TableInfo(String name, String dbName, OperatorType type, HashSet<String> columns) {
        this.name = name;
        this.dbName = dbName;
        this.type = type;
        this.columns = new HashSet<>(columns);
        columns.clear();
        optimizeColumn();
    }

    public TableInfo(String dbAndTableName, OperatorType type, String defaultDb, HashSet<String> columns) {
        if (dbAndTableName.contains(Constants.POINT)) {
            Pair<String, String> pair = StringPairUtils.getPointPair(dbAndTableName);
            this.name = pair.getRight();
            this.dbName = pair.getLeft();
        } else {
            this.name = dbAndTableName;
            this.dbName = defaultDb;
        }

        this.columns = filterColumns(this.name, columns);
        this.type = type;
        columns.clear();
        optimizeColumn();
    }

    private Set<String> filterColumns(String tableName, HashSet<String> columns) {
        Set<String> resultColumns = new HashSet<>();
        Iterator<String> it = columns.iterator();
        while (it.hasNext()) {
            String column = it.next();
            if (column.startsWith(tableName + Constants.POINT)) {
                resultColumns.add(StringPairUtils.getLastPoint(Constants.POINT, column));
                // 遍历完就删除
                it.remove();
            }
        }

        // 没有 t1.a, t1.b这种字段，默认返回全部字段
        if (resultColumns.size() == 0) {
            resultColumns = new HashSet<>(columns);
            columns.clear();
        }

        return resultColumns;
    }



    public Set<String> getColumns() {
        return columns;
    }

    private void optimizeColumn() {
        String dbAndName = this.dbName + Constants.POINT + this.name;
        this.columns = this.columns.stream().map(column -> {
            if (!selectAll && column.endsWith("*")) {
                selectAll = true;
            }
            if (column.contains(Constants.POINT)) {
                Pair<String, String> pair = StringPairUtils.getLastPointPair(column);
                if (pair.getLeft().equals(dbAndName)) {
                    return pair.getRight();
                }
            }
            return column;
        }).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (isDb) {
            str.append("[库]").append(dbName).append("[").append(type.name()).append("]");
        } else {
            str.append("[表]").append(dbName).append(Constants.POINT).append(name).append("[").append(type.name()).append("]");
        }

        if (this.columns != null && this.columns.size() > 0) {
            str.append(" column[ ");
            this.columns.forEach(columns -> str.append(columns).append(" "));
            str.append("]");
        }
        if (limit != null) {
            str.append(" limit[ ").append(limit).append(" ]");
        }
        return str.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TableInfo)) {
            return false;
        }

        TableInfo info = (TableInfo) obj;
        return this.dbName.equals(info.dbName) && this.name.equals(info.name) && this.type == info.type;
    }

    @Override
    public int hashCode() {
        if (this.name != null) {
            return this.dbName.hashCode() + this.name.hashCode() + this.type.hashCode();
        }
        return this.dbName.hashCode() + this.type.hashCode();
    }
}

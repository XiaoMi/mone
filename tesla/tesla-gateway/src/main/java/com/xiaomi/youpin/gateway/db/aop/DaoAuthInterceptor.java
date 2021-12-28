package com.xiaomi.youpin.gateway.db.aop;

import com.xiaomi.data.push.antlr.sql.PrestoSqlParse;
import com.xiaomi.data.push.antlr.sql.SqlParse;
import com.xiaomi.data.push.antlr.sql.constants.OperatorType;
import com.xiaomi.data.push.antlr.sql.exceptions.SqlParseException;
import com.xiaomi.data.push.antlr.sql.model.SqlElement;
import com.xiaomi.data.push.antlr.sql.model.TableInfo;
import com.xiaomi.mione.serverless.ScriptContext;
import com.xiaomi.youpin.gateway.config.DaoAuthConfig;
import com.xiaomi.youpin.gateway.exception.GatewayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xiaomi.data.push.antlr.sql.constants.OperatorType.READ;

/**
 * sql权限校验拦截器
 *
 * @author shanwenbang@xiaomi.com
 * @date 2021/4/20
 */
@Slf4j
public class DaoAuthInterceptor implements DaoInterceptor {

    private DaoAuthConfig daoAuthConfig;

    public DaoAuthInterceptor(DaoAuthConfig daoAuthConfig) {
        this.daoAuthConfig = daoAuthConfig;
    }

    @Override
    public void filter(DaoInterceptorChain chain) throws DaoException {
        if (sqlAuthCheck(chain)) {
            chain.doChain();
        } else {
            log.error("DaoAuthInterceptor access false");
            throw new GatewayException("sql auth failed");
        }
    }

    boolean sqlAuthCheck(DaoInterceptorChain chain) {
        try {
            ScriptContext scriptContext = ScriptContext.getContext();
            if (null == scriptContext) {
                return true;
            }
            Map<String, String> attachments = scriptContext.getAttachments();
            if ("false".equals(attachments.get("dbAuthCheck"))) {
                return true;
            }
            if (!"true".equals(attachments.get("serverless"))) {
                return true;
            }
            String apiId = attachments.get("id");
            if (StringUtils.isBlank(apiId)) {
                return true;
            }

            Map<String, List<DaoAuthConfig.AuthItem>> authConfigMap = daoAuthConfig.getDaoAuthConfig();
            // 未进行权限配置，先不鉴权
            if (null == authConfigMap) {
                return true;
            }
            List<DaoAuthConfig.AuthItem> authItemList = authConfigMap.get(apiId);
            if (null == authItemList || authItemList.size() == 0) {
                return true;
            }

            doSqlAuthCheck(chain.getDaoStatement().toString(), apiId, authItemList);
        } catch (GatewayException e) {
            log.error("sqlAuthCheck GatewayException:{}", e);
            throw e;
        } catch (Exception e) {
            log.error("sqlAuthCheck exception:{}", e);
            //先强行返回校验通过
            return true;
        }
        return  true;
    }

    private void doSqlAuthCheck(String sql, String apiId, List<DaoAuthConfig.AuthItem> authItemList) {
        SqlParse sqlParse = new PrestoSqlParse();
        try {
            SqlElement sqlElement = sqlParse.parse(sql);
            doSqlAuthCheck1(sqlElement.getInputSets(), authItemList);
        } catch (SqlParseException e) {
            log.error("SqlParseException:{}", e);
        }
    }

    private void doSqlAuthCheck1(Set<TableInfo> tableInfos, List<DaoAuthConfig.AuthItem> authItemList) {
        for (TableInfo tableInfo : tableInfos) {
            OperatorType operatorType = tableInfo.getType();
            Map<String, String> columnAuthMap = authItemList.stream()
                    .filter(a -> "1".equals(a.getAuthTarget())
                            && a.getAuthTarget().startsWith(tableInfo.getName() + "|"))
                    .collect(Collectors.toMap(a->a.getSplitTarget(1), b->b.getAuthCode()));

            switch (operatorType) {
                case READ:
                case WRITE:
                    Set<String> columns = tableInfo.getColumns();
                    // select count(*), count(1) 之类的查询，columns为空，此时只要求有该表的任一字段read权限就好
                    if (CollectionUtils.isEmpty(columns) && operatorType == READ) {
                        if (MapUtils.isNotEmpty(columnAuthMap)) {
                            return;
                        } else {
                            throw new GatewayException(String.format("have no auth operate:%s field", operatorType));
                        }
                    }

                    for (String column : columns) {
                        String authCode = columnAuthMap.get(column);
                        if (null == authCode) {
                            throw new GatewayException(String.format("have no auth operate:%s field:%s", operatorType, column));
                        } else {
                            if (operatorType == READ) {
                                if (authCode.equalsIgnoreCase("r") || authCode.equalsIgnoreCase("rw")) {
                                    break;
                                } else {
                                    throw new GatewayException(String.format("have no auth operate:%s field:%s", operatorType, column));
                                }
                            } else {
                                if (authCode.equalsIgnoreCase("w") || authCode.equalsIgnoreCase("rw")) {
                                    break;
                                } else {
                                    throw new GatewayException(String.format("have no auth operate:%s field:%s", operatorType, column));
                                }
                            }
                        }
                    }
                    break;
                case CREATE:
                case ALTER:
                case DROP:
                    //todo 当前一律禁止
                    log.warn("auth failed, forbidden DDL operate:{}, tableInfo:{}", tableInfo.getType(), tableInfo);
                    throw new GatewayException("auth failed, forbidden DDL operate:" + tableInfo.getType());
                default:
                    break;
            }
        }
    }

}

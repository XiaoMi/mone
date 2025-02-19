package run.mone.local.docean.fsm.flow;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.DaoUp;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.util.JsonElementUtils;
import run.mone.local.docean.fsm.bo.FlowContext;
import run.mone.local.docean.fsm.bo.FlowReq;
import run.mone.local.docean.fsm.bo.FlowRes;
import run.mone.local.docean.fsm.bo.InputData;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.GsonUtils;
import run.mone.local.docean.util.TemplateUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/27/24 15:51
 */
@Slf4j
public class DataBaseFlow extends BotFlow {

    private String sql = "";

    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        loadSql();
        log.info("execute database flow block, sql:{}", sql);
        JsonObject resObj = new JsonObject();
        try {
            Sql sql = Sqls.create(this.sql);
            sql.setCallback(Sqls.callback.records());
            Sql res = DaoUp.me().dao().execute(sql);
            Object result = res.getResult();
            if (result != null) {
                resObj.add("data", GsonUtils.gson.toJsonTree(result));
            }
            log.info("execute sql res:{}", result);
        } catch (Throwable e){
            log.error("execute sql error,", e);
            return FlowRes.failure(e.getMessage());
        }
        storeResultsInReferenceData(context, resObj);
        return FlowRes.success(resObj);
    }

    private void loadSql() {
        InputData inputSql = this.inputMap.get(CommonConstants.TY_SQL_INPUT_MARK);
        log.info("inputSql:{}", inputSql);
        String sqlStr = inputSql.getValue().getAsString();
        // HINT: escape @ due to NutzDao silly actions...
        sqlStr = sqlStr.replaceAll("@", "@@");

        Map<String, String> m = new HashMap<>();
        inputMap.entrySet().stream().filter(e -> e.getValue().isOriginalInput()).forEach(it -> {
            JsonElement element = it.getValue().getValue();
            String value = JsonElementUtils.getValue(element);
            // HINT: escape @ due to NutzDao silly actions...
            value = value.replaceAll("@", "@@");
            m.put(it.getKey(), value);
        });
        // TODO: 将来可以通过不同占位符来区分PreparedStatement和字符串替换场景，当前变量替换实现不放注入...
        String finalSql = TemplateUtils.renderTemplate(sqlStr, m);
        log.info("finalSql:{}", finalSql);
        sql = finalSql;
    }

    @Override
    public String getFlowName() {
        return "database";
    }
}

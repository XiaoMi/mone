package run.mone.local.docean.fsm.flow;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.DaoUp;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.FlowContext;
import run.mone.local.docean.fsm.bo.FlowReq;
import run.mone.local.docean.fsm.bo.FlowRes;
import run.mone.local.docean.util.GsonUtils;


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

    }

    @Override
    public String getFlowName() {
        return "database";
    }
}

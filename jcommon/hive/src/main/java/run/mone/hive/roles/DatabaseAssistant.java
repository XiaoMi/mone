package run.mone.hive.roles;

import run.mone.hive.actions.Action;
import run.mone.hive.actions.db.DesignSchemaAction;
import run.mone.hive.actions.db.GenerateSQLAction;
import run.mone.hive.actions.db.QueryDataAction;
import run.mone.hive.actions.db.ModifyDataAction;
import run.mone.hive.schema.RoleContext;

import java.util.Arrays;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/1/12 20:17
 */
public class DatabaseAssistant extends Role {

    public DatabaseAssistant() {
        super(
                "DatabaseAssistant",
                "I am a database assistant specialized in helping with database operations including schema design, " +
                        "SQL generation, data querying and modification.",
                "Help users manage database operations efficiently and accurately",
                "Must ensure data security and follow database best practices"
        );
    }

    @Override
    protected void init() {
        super.init();

        // 设置反应模式为顺序执行
        this.rc.setReactMode(RoleContext.ReactMode.REACT);

        // 初始化Actions
        List<Action> actions = Arrays.asList(
                new DesignSchemaAction(),
                new GenerateSQLAction(),
                new QueryDataAction(),
                new ModifyDataAction()
        );

        // 设置Actions
        setActions(actions);
    }

}
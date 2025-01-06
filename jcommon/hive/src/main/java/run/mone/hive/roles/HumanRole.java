
package run.mone.hive.roles;

import run.mone.hive.actions.HumanConfirmAction;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.HumanProvider;
import run.mone.hive.schema.Message;

import java.util.ArrayList;
import java.util.HashSet;

public class HumanRole extends Role {


    public HumanRole(String name, String profile) {
        this.name = name;
        this.profile = profile;
        this.watchList = new HashSet<>();
        init();
    }

    @Override
    public void init() {
        super.init();
        this.setActions(new ArrayList<>() {{
            add(new HumanConfirmAction());
        }});
    }


}

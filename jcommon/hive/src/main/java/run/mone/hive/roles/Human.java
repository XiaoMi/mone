
package run.mone.hive.roles;

import run.mone.hive.actions.HumanConfirmAction;

import java.util.ArrayList;
import java.util.HashSet;

public class Human extends Role {


    public Human(String name, String profile) {
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

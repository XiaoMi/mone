package run.mone.hive.roles;

import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.WriteDesign;
import run.mone.hive.actions.WritePRD;

@Slf4j
public class Architect extends Role {

    private String name = "Bob";
    private String profile = "Architect";
    private String goal = "design a concise, usable, complete software system";
    private String constraints = "make sure the architecture is simple enough and use appropriate open source " +
            "libraries. Use same language as user requirement";

    public Architect() {
        super();
        init();
    }

    @Override
    protected void init() {
        super.init();
        this.setName(name);
        this.setProfile(profile);
        this.setGoal(goal);
        this.setConstraints(constraints);
        setActions(Collections.singletonList(new WriteDesign()));
        watchActions(Collections.singletonList(WritePRD.class));
    }
} 
package run.mone.hive.roles;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.WriteDesign;
import run.mone.hive.actions.WriteAction;

import java.util.Collections;

@Slf4j
public class ProjectManager extends Role {
    private String name = "Eve";
    private String profile = "Project Manager";
    private String goal = "break down tasks according to PRD/technical design, generate a task list, " +
            "and analyze task dependencies to start with the prerequisite modules";
    private String constraints = "use same language as user requirement";

    public ProjectManager() {
        super();
        init();
    }

    @Override
    protected void init() {
        super.init();
        setActions(Collections.singletonList(new WriteAction()));
        watchActions(Collections.singletonList(WriteDesign.class));
    }
} 
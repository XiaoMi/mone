package run.mone.hive.roles;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.WriteDesign;
import run.mone.hive.actions.WriteAction;

import java.util.Collections;

@Slf4j
public class ProjectManager extends Role {

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
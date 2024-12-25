package run.mone.hive.roles;

import run.mone.hive.actions.UserRequirement;
import run.mone.hive.actions.WritePRD;
import run.mone.hive.schema.RoleContext;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.PrepareDocuments;
import run.mone.hive.utils.CommonUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ProductManager extends Role {
    private String name = "Alice";
    private String profile = "Product Manager";
    private String goal = "efficiently create a successful product that meets market demands and user expectations";
    private String constraints = "utilize the same language as the user requirements for seamless communication";
    private String todoAction = "";

    public ProductManager() {
        super();
        init();
    }

    @Override
    protected void init() {
        super.init();
        // Set available actions
        setActions(Arrays.asList(
            new PrepareDocuments(),
            new WritePRD()
        ));
        
        // Set watched actions
        watchActions(Arrays.asList(
            UserRequirement.class,
            PrepareDocuments.class
        ));
        
        // Set initial todo action
        this.todoAction = CommonUtils.anyToName(WritePRD.class);
        
        // Set reaction mode
        getRc().setReactMode(RoleContext.ReactMode.BY_ORDER);
    }

}
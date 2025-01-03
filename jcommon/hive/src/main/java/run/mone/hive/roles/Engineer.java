package run.mone.hive.roles;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.WriteCode;

import java.util.List;


/**
 * @author goodjava@qq.com
 * 工程师(编写代码)
 */
@Slf4j
public class Engineer extends Role {


    public Engineer() {
        super("Engineer");
        setActions(List.of(new WriteCode()));
    }


}
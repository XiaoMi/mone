
package run.mone.hive.roles;

import run.mone.hive.actions.common.DecideNextSpeakerAction;
import run.mone.hive.schema.RoleContext;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/1/12 20:17
 */
public class Coordinator extends Role {

    public Coordinator() {
        super(
                "Coordinator",
                "I am a coordinator responsible for managing the conversation flow and deciding which role should speak next.",
                "Coordinate the conversation and determine the next speaker",
                "Must ensure fair and efficient communication among all roles"
        );
    }

    @Override
    protected void init() {
        super.init();
        this.rc.setReactMode(RoleContext.ReactMode.BY_ORDER);
        setActions(List.of(new DecideNextSpeakerAction()));
    }
}

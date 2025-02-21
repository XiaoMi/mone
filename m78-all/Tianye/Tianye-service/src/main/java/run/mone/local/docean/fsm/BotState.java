package run.mone.local.docean.fsm;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
public class BotState {

    protected BotFsm fsm;

    private String remoteIpPort;

    private String promptMetaInfo;

    private String name;

    private int version;

    public void enter(BotContext context) {

    }

    public BotRes execute(BotReq req, BotContext context) {
        return BotRes.success(null);
    }


    public void exit() {

    }

}

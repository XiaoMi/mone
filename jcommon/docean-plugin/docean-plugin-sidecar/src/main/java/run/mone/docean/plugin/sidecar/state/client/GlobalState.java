package run.mone.docean.plugin.sidecar.state.client;

import lombok.Getter;
import lombok.Setter;
import run.mone.api.IClient;

/**
 * @author goodjava@qq.com
 * @date 2023/3/1 09:51
 */
public class GlobalState extends BaseState {

    @Getter
    private boolean res;

    @Setter
    private IClient client;


    @Override
    public void execute() {
        if (null != client) {
            res = client.isShutdown();
        } else {
            res = false;
        }
    }
}

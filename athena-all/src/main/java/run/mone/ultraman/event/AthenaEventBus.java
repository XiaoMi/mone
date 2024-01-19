package run.mone.ultraman.event;

import com.google.common.eventbus.EventBus;
import lombok.Getter;

/**
 * @author goodjava@qq.com
 * @date 2023/6/24 10:48
 */
public class AthenaEventBus {

    private EventBus eventBus;

    @Getter
    private EventListener listener;

    private AthenaEventBus() {
        eventBus = new EventBus();
        this.listener = new EventListener();
        eventBus.register(this.listener);
    }

    private static final class LazyHolder{
        private static final AthenaEventBus ins = new AthenaEventBus();
    }

    public static final AthenaEventBus ins() {
        return LazyHolder.ins;
    }


    public void post(Object obj) {
        eventBus.post(obj);
    }

}

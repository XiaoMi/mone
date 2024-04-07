package run.mone.local.docean.fsm.debug;

import com.google.common.util.concurrent.Monitor;

/**
 * @author goodjava@qq.com
 * @date 2024/3/25 11:41
 */
public class DebugController {

    private final Monitor monitor = new Monitor();

    //默认是不需要debug
    private boolean debug = false;

    public DebugController(boolean debug) {
        this.debug = debug;
    }

    public DebugController() {
    }

    // Guard 条件，当 debug 为 false 时满足
    private final Monitor.Guard notDebugging = new Monitor.Guard(monitor) {
        @Override
        public boolean isSatisfied() {
            return !debug;
        }
    };

    public void setDebug(boolean debug) {
        monitor.enter();
        try {
            this.debug = debug;
        } finally {
            monitor.leave();
        }
    }

    public void waitForDebug() throws InterruptedException {
        // 等待 notDebugging 条件满足
        monitor.enterWhen(notDebugging);
        try {
            // 在这里执行需要在 debug 模式下阻塞的代码
        } finally {
            monitor.leave();
        }
    }


}

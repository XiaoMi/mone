package run.mone.ultraman.state;

import com.xiaomi.youpin.tesla.ip.bo.robot.ProjectAiMessageManager;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 10:51
 */
@Slf4j
public class FsmManager {

    private String project;


    public FsmManager setProject(String project) {
        this.project = project;
        return this;
    }

    @Getter
    private AthenaFsm fsm = new AthenaFsm();


    public FsmManager() {

    }

    public void init() {
        new Thread(() -> execute()).start();
    }


    @SneakyThrows
    public void execute() {
        while (true) {
            try {
                if (!ProjectAiMessageManager.getInstance().getMap().containsKey(this.project)) {
                    //直接退出了
                    log.info("project {} fsm quit", this.project);
                    fsm.getContext().quit = true;
                    break;
                }
                fsm.execute();
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            } finally {
                Thread.sleep(500);
            }
        }
    }

}

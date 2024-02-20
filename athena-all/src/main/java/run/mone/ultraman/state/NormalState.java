package run.mone.ultraman.state;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/12/2 22:21
 */
@Slf4j
public class NormalState extends AthenaState {

    @Override
    public void enter(StateContext context) {
        context.reset();
        this.fsm.getEventQueue().clear();
    }

    @Override
    public void execute(StateReq req, StateContext context) {
        AthenaEvent event = this.fsm.getEventQueue().poll();
        if (null == event) {
            return;
        }

        //每个prompt可以外挂很多提问(这些就是那些提问)
        List<String> list = event.getPromptInfo().getAddon();
        if (null == list) {
            log.error("addon list is empty");
            return;
        }

        List<String> addonList = list.stream().map(it -> {
            List<String> l = Splitter.on("&&").splitToList(it);
            return l.get(0);
        }).collect(Collectors.toList());
        context.setAddonList(addonList);

        req.setPromptInfo(event.getPromptInfo());
        req.setPromptType(event.getPromptType());
        context.setProject(event.getProject());
        context.setStep(0);
        context.setFinishStep(list.size());

        this.fsm.changeState(new InitQuestionState());
    }
}

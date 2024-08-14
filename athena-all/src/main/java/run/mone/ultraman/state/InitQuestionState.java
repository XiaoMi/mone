package run.mone.ultraman.state;

import com.google.common.base.Splitter;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import org.apache.commons.lang3.StringUtils;
import run.mone.ultraman.common.GsonUtils;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/12/17 12:55
 */
public class InitQuestionState extends AthenaState {


    @Override
    public void execute(StateReq req, StateContext context) {
        int index = context.getStep();
        //只记录第一次的状态
        context.getSnapshots().putIfAbsent(index, Snapshot.builder().stateContext(GsonUtils.gson.toJson(context)).build());

        PromptInfo info = req.getPromptInfo();
        //获取问题
        String question = info.getAddon().get(context.getStep());
        question = getQuestion(context, question);

        context.setQuestionMeta(info.getAddon_metas().get(context.getStep()).getMeta());
        context.setQuestion(question);
        context.setStep(context.getStep() + 1);
        context.setPromptLables(info.getLabels());
        this.fsm.changeState(new QuestionState());
    }


    private static String getQuestion(StateContext context, String question) {
        //example
        //请选择模块&&模块列表&&
        List<String> list = Splitter.on("&&").splitToList(question);
        if (list.size() == 3) {
            //让问题可以和工程相结合(通过提示词实现)

            String beforePrompt = list.get(1);
            context.getBeforePrompt().clear();
            if (StringUtils.isNotEmpty(beforePrompt)) {
                context.getBeforePrompt().addAll(Splitter.on(",").splitToList(beforePrompt));
            }

            String afterPrompt = list.get(2);
            context.getAfterPrompt().clear();
            if (StringUtils.isNotEmpty(afterPrompt)) {
                context.getAfterPrompt().addAll(Splitter.on(",").splitToList(afterPrompt));
            }

            question = list.get(0);
        }
        return question;
    }
}

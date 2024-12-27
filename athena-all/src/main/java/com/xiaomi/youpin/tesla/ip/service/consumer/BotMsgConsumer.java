package com.xiaomi.youpin.tesla.ip.service.consumer;

import com.google.common.base.Stopwatch;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.xiaomi.youpin.tesla.ip.bo.Action;
import com.xiaomi.youpin.tesla.ip.bo.AiMessage;
import com.xiaomi.youpin.tesla.ip.bo.AiMessageType;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.bo.M78CodeGenerationInfo;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.NotificationCenter;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.service.LocalAiService;
import com.xiaomi.youpin.tesla.ip.util.ActionUtils;
import run.mone.ultraman.common.CodeUtils;
import run.mone.ultraman.common.ImportCode;
import run.mone.ultraman.listener.event.TaskEvent;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2024/6/18 09:51
 */
public class BotMsgConsumer implements Consumer<AiMessage> {

    private GenerateCodeReq req;
    private String out;
    private ImportCode importCode;
    private Stopwatch sw;

    private StringBuilder sb = new StringBuilder();

    public BotMsgConsumer(GenerateCodeReq req, String out, ImportCode importCode, Stopwatch sw) {
        this.req = req;
        this.out = out;
        this.importCode = importCode;
        this.sw = sw;
    }

    @Override
    public void accept(AiMessage msg) {
        if (out.equals("athena")) {
            // 发到Athena
            LocalAiService.sendMsg(msg, req.getProject().getName());
        }
        if (out.equals("athena_chat")) {
            // 发到Athena，非代码，纯聊天
            msg.setCode(false);
            LocalAiService.sendMsg(msg, req.getProject().getName());
        }
        //实现打字机效果
        if (out.equals("editor")) {
            //输出到编辑器
            if (msg.getType().equals(AiMessageType.begin)) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(TaskEvent.TOPIC).onEvent(TaskEvent.builder().message("begin").build());
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (req.getPromptInfo().getLabelValue(Const.PROMPT_LABEL_TYPE, "comment").equals("comment")) {
                        //为生成注释做好准备
                        CodeService.moveToMethodAndInsertLine(req.getProject());
                    }
                    if (req.getPromptInfo().getLabelValue(Const.PROMPT_LABEL_TYPE, "").equals("method")) {
                        //不再插入回车
                        if (req.getParam().containsKey("__skip_enter")) {
                            return;
                        }
                        //为插入方法做准备(挪动到行尾,然后插入一个回车)
                        CodeService.moveCaretToEndOfLine(req.getEditor());
                        CodeService.writeCode2(req.getProject(), req.getEditor(), "\n");
                    }
                });
            }

            //这里会插入ide中
            if (msg.getType().equals(AiMessageType.process)) {
                importCode.append(msg.getText());
                sb.append(msg.getText());
            }

            //生成代码结束了
            if (msg.getType().equals(AiMessageType.success)) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(TaskEvent.TOPIC).onEvent(TaskEvent.builder().message("end").time(sw.elapsed(TimeUnit.SECONDS)).build());
                if (Action.GENERATE_CODE == ActionUtils.getActionByReq(req)) {
                    CodeUtils.uploadCodeGenInfo(sb.toString(), getComment(), this.req.getProject().getName(), this.req.getClassName());
                }
            }

            //发生了错误
            if (msg.getType().equals(AiMessageType.failure)) {
                NotificationCenter.notice(msg.getText(), NotificationType.ERROR);
            }
        }
    }

    private String getComment() {
        String comment = "";
        if (null != req.getParam().get(Const.GENERATE_CODE_COMMENT)) {
            comment = req.getParam().get(Const.GENERATE_CODE_COMMENT);
        }else{
            comment = this.req.getCurrentLine();
        }
        return comment;
    }

}

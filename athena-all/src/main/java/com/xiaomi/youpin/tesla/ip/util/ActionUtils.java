package com.xiaomi.youpin.tesla.ip.util;

import com.xiaomi.youpin.tesla.ip.bo.Action;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.common.Const;

public class ActionUtils {

    public static Action getActionByReq(GenerateCodeReq req){
        if (req.getPromptInfo().getLabels().getOrDefault("selectComment", "").equals("true")) {
            return Action.GENERATE_CODE;
        } else if (Const.ATHENA_CHAT.equals(req.getPromptInfo().getPromptName())) {
            return Action.CHAT;
        } else if (Const.GENERATE_UNIT_TEST.equals(req.getPromptInfo().getPromptName())) {
            return Action.UNIT_TEST;
        } else if (req.getPromptInfo().getLabels().containsKey("git_push")) {
            return Action.GIT_PUSH;
        } else if (req.getPromptInfo().getLabels().containsKey("modify_method_name")) {
            return Action.SMART_NAMING;
        } else if (req.getPromptInfo().getLabelValue("action", "").equals("bug_fix")) {
            return Action.BUG_FIX;
        } else if(Const.CODE_REVIEW.equals(req.getPromptInfo().getPromptName())) {
            return Action.CODE_SUGGESTION;
        }else{
            return Action.GENERATE_COMMENT;
        }
    }
}

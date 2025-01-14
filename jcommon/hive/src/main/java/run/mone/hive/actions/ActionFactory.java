package run.mone.hive.actions;

import run.mone.hive.actions.programmer.*;

public class ActionFactory {

    public static Action createAction(String actionType) {
        return switch (actionType.toLowerCase()) {
            case "writecode" -> new WriteCode();
            case "writeprd" -> new WritePRD();
            case "writetest" -> new WriteTest();
            case "writedesign" -> new WriteDesign();
            case "debugerror" -> new DebugError();
            case "runcode" -> new RunCode();
            case "summarizecode" -> new SummarizeCode();
            default -> throw new IllegalArgumentException("Unknown action type: " + actionType);
        };
    }
    
    public static boolean isValidActionType(String actionType) {
        try {
            createAction(actionType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

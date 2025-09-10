package run.mone.hive.task;

/**
 * 任务状态管理
 * 对应Cline中的TaskState类
 */
public class TaskState {
    
    // Focus Chain / Todo List Management
    private int apiRequestCount = 0;
    private int apiRequestsSinceLastTodoUpdate = 0;
    private String currentFocusChainChecklist = null;
    private boolean todoListWasUpdatedByUser = false;
    
    // Plan mode specific state
    private boolean didRespondToPlanAskBySwitchingMode = false;
    
    // Task Abort / Cancellation
    private boolean abort = false;
    
    // 任务执行状态
    private int consecutiveMistakeCount = 0;
    private boolean didRejectTool = false;
    
    public TaskState() {
    }
    
    /**
     * 增加API请求计数
     */
    public void incrementApiRequestCount() {
        this.apiRequestCount++;
        this.apiRequestsSinceLastTodoUpdate++;
    }
    
    /**
     * 增加API请求计数（只增加todo更新计数）
     */
    public void incrementApiRequestsSinceLastTodoUpdate() {
        this.apiRequestsSinceLastTodoUpdate++;
    }
    
    /**
     * 重置待办列表更新计数器
     */
    public void resetApiRequestsSinceLastTodoUpdate() {
        this.apiRequestsSinceLastTodoUpdate = 0;
    }
    
    /**
     * 增加连续错误计数
     */
    public void incrementConsecutiveMistakeCount() {
        this.consecutiveMistakeCount++;
    }
    
    // Getters and Setters
    public int getApiRequestCount() {
        return apiRequestCount;
    }
    
    public void setApiRequestCount(int apiRequestCount) {
        this.apiRequestCount = apiRequestCount;
    }
    
    public int getApiRequestsSinceLastTodoUpdate() {
        return apiRequestsSinceLastTodoUpdate;
    }
    
    public void setApiRequestsSinceLastTodoUpdate(int apiRequestsSinceLastTodoUpdate) {
        this.apiRequestsSinceLastTodoUpdate = apiRequestsSinceLastTodoUpdate;
    }
    
    public String getCurrentFocusChainChecklist() {
        return currentFocusChainChecklist;
    }
    
    public void setCurrentFocusChainChecklist(String currentFocusChainChecklist) {
        this.currentFocusChainChecklist = currentFocusChainChecklist;
    }
    
    public boolean isTodoListWasUpdatedByUser() {
        return todoListWasUpdatedByUser;
    }
    
    public void setTodoListWasUpdatedByUser(boolean todoListWasUpdatedByUser) {
        this.todoListWasUpdatedByUser = todoListWasUpdatedByUser;
    }
    
    public boolean isDidRespondToPlanAskBySwitchingMode() {
        return didRespondToPlanAskBySwitchingMode;
    }
    
    public void setDidRespondToPlanAskBySwitchingMode(boolean didRespondToPlanAskBySwitchingMode) {
        this.didRespondToPlanAskBySwitchingMode = didRespondToPlanAskBySwitchingMode;
    }
    
    public boolean isAbort() {
        return abort;
    }
    
    public void setAbort(boolean abort) {
        this.abort = abort;
    }
    
    public int getConsecutiveMistakeCount() {
        return consecutiveMistakeCount;
    }
    
    public void setConsecutiveMistakeCount(int consecutiveMistakeCount) {
        this.consecutiveMistakeCount = consecutiveMistakeCount;
    }
    
    public boolean isDidRejectTool() {
        return didRejectTool;
    }
    
    public void setDidRejectTool(boolean didRejectTool) {
        this.didRejectTool = didRejectTool;
    }
}

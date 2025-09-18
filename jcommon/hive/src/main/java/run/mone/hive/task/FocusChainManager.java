package run.mone.hive.task;

import lombok.Data;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Focus Chain管理器
 * 对应Cline中的FocusChainManager类
 */
@Data
public class FocusChainManager {
    
    private final String taskId;
    private final TaskState taskState;
    private Mode mode;
    private final String taskDirectory;
    private final FocusChainSettings focusChainSettings;
    private final LLMTaskProcessor llm;
    
    // 回调接口
    private Consumer<String> sayCallback;
    private Runnable postStateToWebviewCallback;
    
    // 文件监控
    private WatchService watchService;
    private ScheduledExecutorService fileWatcherExecutor;
    private boolean hasTrackedFirstProgress = false;
    
    // 文件路径
    private String focusChainFilePath;
    
    public FocusChainManager(String taskId, TaskState taskState, Mode mode, 
                           String taskDirectory, FocusChainSettings focusChainSettings, LLMTaskProcessor llm) {
        this.taskId = taskId;
        this.taskState = taskState;
        this.mode = mode;
        this.taskDirectory = taskDirectory;
        this.focusChainSettings = focusChainSettings;
        this.llm = llm;
        
        try {
            // 确保Focus Chain文件存在
            this.focusChainFilePath = FocusChainFileUtils.ensureFocusChainFile(
                taskDirectory, taskId, null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize focus chain file", e);
        }
    }
    
    /**
     * 设置回调函数
     */
    public void setSayCallback(Consumer<String> sayCallback) {
        this.sayCallback = sayCallback;
    }
    
    public void setPostStateToWebviewCallback(Runnable postStateToWebviewCallback) {
        this.postStateToWebviewCallback = postStateToWebviewCallback;
    }
    
    /**
     * 更新模式
     */
    public void updateMode(Mode mode) {
        this.mode = mode;
    }
    
    /**
     * 生成Focus Chain指令
     * 对应Cline中的generateFocusChainInstructions方法
     */
    public String generateFocusChainInstructions() {
        // 根据不同情况生成不同的指令
        if (mode == Mode.PLAN) {
            return FocusChainPrompts.getListInstructionsPlanMode();
        }
        
        if (taskState.isDidRespondToPlanAskBySwitchingMode()) {
            // 刚从Plan切换到Act模式 - 强制要求创建TODO列表
            return FocusChainPrompts.getListInstructionsInitial();
        }
        
        // 其他情况推荐更新
        return FocusChainPrompts.getListInstructionsRecommended();
    }
    
    /**
     * 判断是否应该包含Focus Chain指令
     * 对应Cline中的shouldIncludeFocusChainInstructions方法
     */
    public boolean shouldIncludeFocusChainInstructions() {
        // Always include when in Plan mode
        boolean inPlanMode = (mode == Mode.PLAN);
        
        // Always include when switching from Plan > Act
        boolean justSwitchedFromPlanMode = taskState.isDidRespondToPlanAskBySwitchingMode();
        
        // Always include when user had edited the list manually
        boolean userUpdatedList = taskState.isTodoListWasUpdatedByUser();
        
        // Include when reaching the reminder interval, configured by settings
        boolean reachedReminderInterval = 
            taskState.getApiRequestsSinceLastTodoUpdate() >= focusChainSettings.getRemindClineInterval();
        
        // Include on first API request or if list does not exist
        boolean isFirstApiRequest = 
            taskState.getApiRequestCount() == 1 && taskState.getCurrentFocusChainChecklist() == null;
        
        // Include if no list has been created and multiple requests have completed
        boolean hasNoTodoListAfterMultipleRequests = 
            taskState.getCurrentFocusChainChecklist() == null && taskState.getApiRequestCount() >= 2;
        
        return reachedReminderInterval ||
               justSwitchedFromPlanMode ||
               userUpdatedList ||
               inPlanMode ||
               isFirstApiRequest ||
               hasNoTodoListAfterMultipleRequests;
    }
    
    /**
     * 从工具响应更新Focus Chain列表
     * 对应Cline中的updateFCListFromToolResponse方法
     */
    public void updateFCListFromToolResponse(String taskProgress) {
        try {
            // Reset the counter if task_progress was provided
            if (taskProgress != null && !taskProgress.trim().isEmpty()) {
                taskState.resetApiRequestsSinceLastTodoUpdate();
            }
            
            // If model provides task_progress update, write it to the markdown file
            if (taskProgress != null && !taskProgress.trim().isEmpty()) {
                String previousList = taskState.getCurrentFocusChainChecklist();
                taskState.setCurrentFocusChainChecklist(taskProgress.trim());
                
                System.out.println(String.format(
                    "[Task %s] focus chain list: LLM provided focus chain list update via task_progress parameter. Length %d > %d",
                    taskId, 
                    previousList != null ? previousList.length() : 0, 
                    taskState.getCurrentFocusChainChecklist().length()
                ));
                
                // Parse focus chain list counts for telemetry
                FocusChainFileUtils.FocusChainCounts counts = 
                    FocusChainFileUtils.parseFocusChainListCounts(taskProgress.trim());
                
                // Track first progress creation
                if (!hasTrackedFirstProgress && counts.getTotalItems() > 0) {
                    System.out.println(String.format(
                        "[Task %s] First focus chain progress created: %d total items", 
                        taskId, counts.getTotalItems()
                    ));
                    hasTrackedFirstProgress = true;
                }
                // Track progress updates
                else if (hasTrackedFirstProgress && counts.getTotalItems() > 0) {
                    System.out.println(String.format(
                        "[Task %s] Focus chain progress updated: %d total, %d completed", 
                        taskId, counts.getTotalItems(), counts.getCompletedItems()
                    ));
                }
                
                // Write the model's update to the markdown file
                try {
                    writeFocusChainToDisk(taskProgress.trim());
                    
                    // Send the task_progress message to the UI immediately
                    if (sayCallback != null) {
                        sayCallback.accept("task_progress: " + taskProgress.trim());
                    }
                } catch (IOException e) {
                    System.err.println(String.format(
                        "[Task %s] focus chain list: Failed to write to markdown file: %s", 
                        taskId, e.getMessage()
                    ));
                    
                    // Fall back to creating a task_progress message directly if file write fails
                    if (sayCallback != null) {
                        sayCallback.accept("task_progress: " + taskProgress.trim());
                    }
                }
            } else {
                // No model update provided, check if markdown file exists and load it
                String markdownTodoList = readFocusChainFromDisk();
                if (markdownTodoList != null) {
                    taskState.setCurrentFocusChainChecklist(markdownTodoList);
                    
                    // Create a task_progress message to display the focus chain list in the UI
                    if (sayCallback != null) {
                        sayCallback.accept("task_progress: " + markdownTodoList);
                    }
                } else {
                    System.out.println(String.format(
                        "[Task %s] focus chain list: No valid task progress to update with", taskId
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println(String.format(
                "[Task %s] focus chain list: Error in updateFCListFromToolResponse: %s", 
                taskId, e.getMessage()
            ));
        }
    }
    
    /**
     * 写入Focus Chain到磁盘
     */
    private void writeFocusChainToDisk(String todoList) throws IOException {
        try {
            String fileContent = FocusChainFileUtils.createFocusChainMarkdownContent(taskId, todoList);
            FocusChainFileUtils.writeFocusChainFile(focusChainFilePath, fileContent);
            
            System.out.println(String.format(
                "[Task %s] focus chain list: Successfully wrote %d characters to markdown file", 
                taskId, fileContent.length()
            ));
        } catch (IOException e) {
            System.err.println(String.format(
                "[Task %s] focus chain list: FILE WRITE FAILED - Error: %s", 
                taskId, e.getMessage()
            ));
            throw e;
        }
    }
    
    /**
     * 从磁盘读取Focus Chain
     */
    public String readFocusChainFromDisk() {
        try {
            String content = FocusChainFileUtils.readFocusChainFile(focusChainFilePath);
            if (content != null) {
                return FocusChainFileUtils.extractFocusChainListFromText(content);
            }
        } catch (IOException e) {
            System.err.println(String.format(
                "[Task %s] focus chain list: Failed to read from disk: %s", 
                taskId, e.getMessage()
            ));
        }
        return null;
    }
    
    /**
     * 设置文件监控
     */
    public void setupFocusChainFileWatcher() throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(taskDirectory);
        dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        
        fileWatcherExecutor = Executors.newSingleThreadScheduledExecutor();
        fileWatcherExecutor.scheduleWithFixedDelay(this::checkFileChanges, 1, 1, TimeUnit.SECONDS);
        
        System.out.println(String.format(
            "[Task %s] focus chain list: File watcher setup completed for %s", 
            taskId, focusChainFilePath
        ));
    }
    
    /**
     * 检查文件变化
     */
    private void checkFileChanges() {
        try {
            WatchKey key = watchService.poll();
            if (key != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path changed = (Path) event.context();
                    if (changed.toString().equals("focus-chain.md")) {
                        // 文件被修改，更新状态
                        taskState.setTodoListWasUpdatedByUser(true);
                        updateFCListFromMarkdownFileAndNotifyUI();
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            System.err.println(String.format(
                "[Task %s] focus chain list: Error in file watcher: %s", 
                taskId, e.getMessage()
            ));
        }
    }
    
    /**
     * 从markdown文件更新Focus Chain列表并通知UI
     */
    private void updateFCListFromMarkdownFileAndNotifyUI() {
        try {
            String markdownContent = readFocusChainFromDisk();
            if (markdownContent != null) {
                taskState.setCurrentFocusChainChecklist(markdownContent);
                
                if (sayCallback != null) {
                    sayCallback.accept("task_progress: " + markdownContent);
                }
                
                if (postStateToWebviewCallback != null) {
                    postStateToWebviewCallback.run();
                }
                
                System.out.println(String.format(
                    "[Task %s] focus chain list: Updated from markdown file and notified UI", taskId
                ));
            }
        } catch (Exception e) {
            System.err.println(String.format(
                "[Task %s] focus chain list: Error updating from markdown file: %s", 
                taskId, e.getMessage()
            ));
        }
    }
    
    /**
     * 检查完成时的未完成进度
     */
    public void checkIncompleteProgressOnCompletion() {
        if (focusChainSettings.isEnabled() && taskState.getCurrentFocusChainChecklist() != null) {
            FocusChainFileUtils.FocusChainCounts counts = 
                FocusChainFileUtils.parseFocusChainListCounts(taskState.getCurrentFocusChainChecklist());
            
            // Only track if there are items and not all are marked as completed
            if (counts.getTotalItems() > 0 && counts.getCompletedItems() < counts.getTotalItems()) {
                int incompleteItems = counts.getIncompleteItems();
                System.out.println(String.format(
                    "[Task %s] Task completed with incomplete focus chain items: %d total, %d completed, %d incomplete",
                    taskId, counts.getTotalItems(), counts.getCompletedItems(), incompleteItems
                ));
            }
        }
    }
    
    /**
     * 清理资源
     */
    public void dispose() {
        if (fileWatcherExecutor != null && !fileWatcherExecutor.isShutdown()) {
            fileWatcherExecutor.shutdown();
            try {
                if (!fileWatcherExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    fileWatcherExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                fileWatcherExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                System.err.println("Error closing watch service: " + e.getMessage());
            }
        }
    }
    
    /**
     * 分析任务完成时的未完成项目
     * 对应Cline中的analyzeIncompleteItemsOnCompletion方法
     */
    public void analyzeIncompleteItemsOnCompletion() {
        if (!focusChainSettings.isEnabled() || taskState.getCurrentFocusChainChecklist() == null) {
            return;
        }
        
        try {
            String currentList = taskState.getCurrentFocusChainChecklist();
            FocusChainFileUtils.FocusChainCounts counts = 
                FocusChainFileUtils.parseFocusChainListCounts(currentList);
            
            if (counts.getIncompleteItems() > 0) {
                sayCallback.accept(String.format(
                    "Task completed with %d incomplete items out of %d total items (%.1f%% completion rate)",
                    counts.getIncompleteItems(),
                    counts.getTotalItems(),
                    (double) counts.getCompletedItems() / counts.getTotalItems() * 100
                ));
                
                // 这里可以添加遥测数据发送
                // telemetryService.captureIncompleteItems(taskId, counts.getIncompleteItems());
            } else {
                sayCallback.accept("Task completed with all Focus Chain items finished!");
            }
        } catch (Exception e) {
            System.err.println("[Task " + taskId + "] Error analyzing incomplete items: " + e.getMessage());
        }
    }
}

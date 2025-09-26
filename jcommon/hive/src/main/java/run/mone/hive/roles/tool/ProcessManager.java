package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 进程管理单例类
 * 统一管理所有由 ExecuteCommandTool 创建的进程，包括前台和后台进程
 * 
 * @author goodjava@qq.com
 * @date 2025/1/11
 */
@Slf4j
public class ProcessManager {
    
    private static volatile ProcessManager instance;
    private static final Object lock = new Object();
    
    // 进程信息存储
    private final Map<String, ProcessInfo> processes = new ConcurrentHashMap<>();
    private final AtomicLong processIdCounter = new AtomicLong(1);
    
    // 私有构造函数，防止外部实例化
    private ProcessManager() {
        log.info("ProcessManager 单例初始化");
    }
    
    /**
     * 获取单例实例
     */
    public static ProcessManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ProcessManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * 进程信息数据类
     */
    @Data
    public static class ProcessInfo {
        private String processId;
        private long pid;
        private Process process;
        private String command;
        private String workingDirectory;
        private LocalDateTime startTime;
        private boolean isBackground;
        private String status; // running, stopped, error
        
        public ProcessInfo(String processId, long pid, Process process, String command, 
                          String workingDirectory, boolean isBackground) {
            this.processId = processId;
            this.pid = pid;
            this.process = process;
            this.command = command;
            this.workingDirectory = workingDirectory;
            this.startTime = LocalDateTime.now();
            this.isBackground = isBackground;
            this.status = "running";
        }
        
        /**
         * 检查进程是否还在运行
         */
        public boolean isAlive() {
            if (process == null) {
                return false;
            }
            boolean alive = process.isAlive();
            if (!alive && "running".equals(status)) {
                status = "stopped";
            }
            return alive;
        }
        
        /**
         * 获取进程运行时间（秒）
         */
        public long getRunningTimeSeconds() {
            return java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds();
        }
    }
    
    /**
     * 注册新进程
     * 
     * @param process 进程对象
     * @param command 执行的命令
     * @param workingDirectory 工作目录
     * @param isBackground 是否为后台进程
     * @return 生成的进程ID
     */
    public String registerProcess(Process process, String command, String workingDirectory, boolean isBackground) {
        if (process == null) {
            throw new IllegalArgumentException("Process cannot be null");
        }
        
        String processId = generateProcessId(isBackground);
        long pid = process.pid();
        
        ProcessInfo processInfo = new ProcessInfo(processId, pid, process, command, workingDirectory, isBackground);
        processes.put(processId, processInfo);
        
        log.info("注册新进程: {} (PID: {}, 命令: {}, 后台: {})", processId, pid, command, isBackground);
        return processId;
    }
    
    /**
     * 生成进程ID
     */
    private String generateProcessId(boolean isBackground) {
        String prefix = isBackground ? "bg_" : "fg_";
        return prefix + processIdCounter.getAndIncrement();
    }
    
    /**
     * 获取进程信息
     * 
     * @param processId 进程ID
     * @return 进程信息，如果不存在返回null
     */
    public ProcessInfo getProcessInfo(String processId) {
        return processes.get(processId);
    }
    
    /**
     * 获取进程对象
     * 
     * @param processId 进程ID
     * @return 进程对象，如果不存在返回null
     */
    public Process getProcess(String processId) {
        ProcessInfo processInfo = processes.get(processId);
        return processInfo != null ? processInfo.getProcess() : null;
    }
    
    /**
     * 检查进程是否在运行
     * 
     * @param processId 进程ID
     * @return 是否在运行
     */
    public boolean isProcessRunning(String processId) {
        ProcessInfo processInfo = processes.get(processId);
        if (processInfo == null) {
            return false;
        }
        
        boolean alive = processInfo.isAlive();
        if (!alive) {
            log.debug("进程 {} 已停止运行", processId);
        }
        return alive;
    }
    
    /**
     * 停止指定进程
     * 
     * @param processId 进程ID
     * @param forceKill 是否强制终止
     * @return 是否成功停止
     */
    public boolean stopProcess(String processId, boolean forceKill) {
        ProcessInfo processInfo = processes.get(processId);
        if (processInfo == null) {
            log.warn("未找到进程: {}", processId);
            return false;
        }
        
        Process process = processInfo.getProcess();
        if (process == null || !process.isAlive()) {
            log.info("进程 {} 已经不在运行", processId);
            processInfo.setStatus("stopped");
            return true;
        }
        
        try {
            if (forceKill) {
                // 强制终止
                process.destroyForcibly();
                log.info("强制终止进程: {} (PID: {})", processId, processInfo.getPid());
            } else {
                // 优雅关闭
                process.destroy();
                
                // 等待3秒，如果还没退出就强制关闭
                boolean terminated = process.waitFor(3, TimeUnit.SECONDS);
                if (!terminated) {
                    log.warn("进程 {} 未在3秒内优雅退出，强制终止", processId);
                    process.destroyForcibly();
                }
            }
            
            processInfo.setStatus("stopped");
            log.info("成功停止进程: {} (PID: {})", processId, processInfo.getPid());
            return true;
            
        } catch (InterruptedException e) {
            log.error("停止进程时被中断: {}", processId, e);
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            log.error("停止进程时发生异常: {}", processId, e);
            processInfo.setStatus("error");
            return false;
        }
    }
    
    /**
     * 停止指定进程（优雅关闭）
     */
    public boolean stopProcess(String processId) {
        return stopProcess(processId, false);
    }
    
    /**
     * 强制终止指定进程
     */
    public boolean killProcess(String processId) {
        return stopProcess(processId, true);
    }
    
    /**
     * 停止所有进程
     * 
     * @param forceKill 是否强制终止
     * @return 成功停止的进程数量
     */
    public int stopAllProcesses(boolean forceKill) {
        int stoppedCount = 0;
        
        for (String processId : new ArrayList<>(processes.keySet())) {
            if (stopProcess(processId, forceKill)) {
                stoppedCount++;
            }
        }
        
        log.info("已停止 {} 个进程", stoppedCount);
        return stoppedCount;
    }
    
    /**
     * 停止所有进程（优雅关闭）
     */
    public int stopAllProcesses() {
        return stopAllProcesses(false);
    }
    
    /**
     * 停止所有后台进程
     * 
     * @return 成功停止的后台进程数量
     */
    public int stopAllBackgroundProcesses() {
        int stoppedCount = 0;
        
        for (ProcessInfo processInfo : processes.values()) {
            if (processInfo.isBackground() && processInfo.isAlive()) {
                if (stopProcess(processInfo.getProcessId())) {
                    stoppedCount++;
                }
            }
        }
        
        log.info("已停止 {} 个后台进程", stoppedCount);
        return stoppedCount;
    }
    
    /**
     * 清理已停止的进程
     * 
     * @return 清理的进程数量
     */
    public int cleanupStoppedProcesses() {
        int cleanedCount = 0;
        
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, ProcessInfo> entry : processes.entrySet()) {
            ProcessInfo processInfo = entry.getValue();
            if (!processInfo.isAlive()) {
                toRemove.add(entry.getKey());
                cleanedCount++;
            }
        }
        
        for (String processId : toRemove) {
            processes.remove(processId);
            log.debug("清理已停止的进程: {}", processId);
        }
        
        if (cleanedCount > 0) {
            log.info("清理了 {} 个已停止的进程", cleanedCount);
        }
        
        return cleanedCount;
    }
    
    /**
     * 获取所有进程的状态信息
     * 
     * @return 包含所有进程状态的JsonObject
     */
    public JsonObject getAllProcessesStatus() {
        JsonObject result = new JsonObject();
        JsonObject processesJson = new JsonObject();
        
        // 先清理已停止的进程
        cleanupStoppedProcesses();
        
        int runningCount = 0;
        int backgroundCount = 0;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (ProcessInfo processInfo : processes.values()) {
            JsonObject processJson = new JsonObject();
            processJson.addProperty("pid", processInfo.getPid());
            processJson.addProperty("command", processInfo.getCommand());
            processJson.addProperty("working_directory", processInfo.getWorkingDirectory());
            processJson.addProperty("start_time", processInfo.getStartTime().format(formatter));
            processJson.addProperty("running_time_seconds", processInfo.getRunningTimeSeconds());
            processJson.addProperty("is_background", processInfo.isBackground());
            processJson.addProperty("status", processInfo.getStatus());
            processJson.addProperty("is_alive", processInfo.isAlive());
            
            processesJson.add(processInfo.getProcessId(), processJson);
            
            if (processInfo.isAlive()) {
                runningCount++;
                if (processInfo.isBackground()) {
                    backgroundCount++;
                }
            }
        }
        
        result.add("processes", processesJson);
        result.addProperty("total_count", processes.size());
        result.addProperty("running_count", runningCount);
        result.addProperty("background_count", backgroundCount);
        result.addProperty("timestamp", LocalDateTime.now().format(formatter));
        
        return result;
    }
    
    /**
     * 获取后台进程的状态信息
     * 
     * @return 包含后台进程状态的JsonObject
     */
    public JsonObject getBackgroundProcessesStatus() {
        JsonObject result = new JsonObject();
        JsonObject processesJson = new JsonObject();
        
        cleanupStoppedProcesses();
        
        int runningBackgroundCount = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (ProcessInfo processInfo : processes.values()) {
            if (processInfo.isBackground()) {
                JsonObject processJson = new JsonObject();
                processJson.addProperty("pid", processInfo.getPid());
                processJson.addProperty("command", processInfo.getCommand());
                processJson.addProperty("working_directory", processInfo.getWorkingDirectory());
                processJson.addProperty("start_time", processInfo.getStartTime().format(formatter));
                processJson.addProperty("running_time_seconds", processInfo.getRunningTimeSeconds());
                processJson.addProperty("status", processInfo.getStatus());
                processJson.addProperty("is_alive", processInfo.isAlive());
                
                processesJson.add(processInfo.getProcessId(), processJson);
                
                if (processInfo.isAlive()) {
                    runningBackgroundCount++;
                }
            }
        }
        
        result.add("processes", processesJson);
        result.addProperty("background_count", runningBackgroundCount);
        result.addProperty("timestamp", LocalDateTime.now().format(formatter));
        
        return result;
    }
    
    /**
     * 根据命令查找进程
     * 
     * @param command 命令字符串（支持部分匹配）
     * @return 匹配的进程ID列表
     */
    public List<String> findProcessesByCommand(String command) {
        List<String> matchingProcesses = new ArrayList<>();
        
        if (StringUtils.isBlank(command)) {
            return matchingProcesses;
        }
        
        String lowerCommand = command.toLowerCase();
        
        for (ProcessInfo processInfo : processes.values()) {
            if (processInfo.getCommand().toLowerCase().contains(lowerCommand)) {
                matchingProcesses.add(processInfo.getProcessId());
            }
        }
        
        return matchingProcesses;
    }
    
    /**
     * 获取进程总数
     */
    public int getProcessCount() {
        return processes.size();
    }
    
    /**
     * 获取运行中的进程数
     */
    public int getRunningProcessCount() {
        cleanupStoppedProcesses();
        return (int) processes.values().stream().filter(ProcessInfo::isAlive).count();
    }
    
    /**
     * 获取后台进程数
     */
    public int getBackgroundProcessCount() {
        cleanupStoppedProcesses();
        return (int) processes.values().stream()
                .filter(p -> p.isBackground() && p.isAlive())
                .count();
    }
    
    /**
     * 清空所有进程记录（会先尝试停止所有运行中的进程）
     */
    public void clear() {
        log.info("清空所有进程记录，当前有 {} 个进程", processes.size());
        
        // 先停止所有运行中的进程
        stopAllProcesses(true);
        
        // 清空记录
        processes.clear();
        
        log.info("已清空所有进程记录");
    }
    
    /**
     * 移除进程记录（不会停止进程）
     * 
     * @param processId 进程ID
     * @return 是否成功移除
     */
    public boolean removeProcess(String processId) {
        ProcessInfo removed = processes.remove(processId);
        if (removed != null) {
            log.debug("移除进程记录: {}", processId);
            return true;
        }
        return false;
    }
    
    /**
     * 杀死所有进程（强制终止），每个进程独立处理，互不影响
     * 
     * @return 成功杀死的进程数量
     */
    public int killAllProcesses() {
        int killedCount = 0;
        int totalCount = processes.size();
        
        log.info("开始强制杀死所有进程，共 {} 个进程", totalCount);
        
        // 使用新的列表避免并发修改异常
        List<String> processIds = new ArrayList<>(processes.keySet());
        
        for (String processId : processIds) {
            try {
                ProcessInfo processInfo = processes.get(processId);
                if (processInfo == null) {
                    log.debug("进程 {} 已不存在，跳过", processId);
                    continue;
                }
                
                Process process = processInfo.getProcess();
                if (process == null) {
                    log.debug("进程 {} 的Process对象为null，跳过", processId);
                    continue;
                }
                
                if (!process.isAlive()) {
                    log.debug("进程 {} 已经不在运行，标记为stopped", processId);
                    processInfo.setStatus("stopped");
                    continue;
                }
                
                // 强制杀死进程
                process.destroyForcibly();
                processInfo.setStatus("killed");
                killedCount++;
                
                log.info("成功强制杀死进程: {} (PID: {}, 命令: {})", 
                        processId, processInfo.getPid(), processInfo.getCommand());
                
            } catch (Exception e) {
                // 每个进程的异常都独立处理，不影响其他进程
                log.error("杀死进程 {} 时发生异常: {}", processId, e.getMessage(), e);
                
                // 尝试标记进程状态为error
                try {
                    ProcessInfo processInfo = processes.get(processId);
                    if (processInfo != null) {
                        processInfo.setStatus("error");
                    }
                } catch (Exception statusException) {
                    log.error("设置进程 {} 状态时发生异常: {}", processId, statusException.getMessage());
                }
            }
        }
        
        log.info("强制杀死进程完成，成功杀死 {}/{} 个进程", killedCount, totalCount);
        return killedCount;
    }
    
    /**
     * 自动清理已完成的进程（状态为 completed, failed, error, timeout, interrupted 的进程）
     * 这些进程已经结束，可以安全地从管理器中移除
     * 
     * @return 清理的进程数量
     */
    public int autoCleanupCompletedProcesses() {
        int cleanedCount = 0;
        
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, ProcessInfo> entry : processes.entrySet()) {
            ProcessInfo processInfo = entry.getValue();
            String status = processInfo.getStatus();
            
            // 如果进程状态表明已经完成（不管成功还是失败），且确实不再运行，则可以清理
            if (("completed".equals(status) || "failed".equals(status) || 
                 "error".equals(status) || "timeout".equals(status) || 
                 "interrupted".equals(status)) && !processInfo.isAlive()) {
                toRemove.add(entry.getKey());
                cleanedCount++;
            }
        }
        
        for (String processId : toRemove) {
            processes.remove(processId);
            log.debug("自动清理已完成的进程: {}", processId);
        }
        
        if (cleanedCount > 0) {
            log.info("自动清理了 {} 个已完成的进程", cleanedCount);
        }
        
        return cleanedCount;
    }
}

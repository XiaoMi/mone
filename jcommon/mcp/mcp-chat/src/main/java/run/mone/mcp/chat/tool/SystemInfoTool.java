package run.mone.mcp.chat.tool;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;

import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

/**
 * Tool for retrieving system information about the current computer.
 *
 * @author goodjava@qq.com
 */
public class SystemInfoTool implements ITool {

    @Override
    public String getName() {
        return "system_info";
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public String description() {
        return """
                A tool designed to retrieve and display information about the current computer system.
                Use this tool when the user wants to check hardware specifications, operating system details,
                memory usage, disk space, or other system configuration information.
                
                **When to use:** Choose this tool when the user asks about their computer's configuration,
                hardware details, system resources, or performance metrics.
                
                **Output:** The tool will return detailed information about the requested system components
                in a structured format.
                """;
    }

    @Override
    public String parameters() {
        return """
                - info_type: (required) The specific system information to retrieve. Must be one of:
                  - 'overview': General system information including OS, processor, memory, etc.
                  - 'os': Operating system details including version, build, architecture.
                  - 'cpu': CPU information including model, cores, speed, usage.
                  - 'memory': RAM information including total, used, available.
                  - 'disk': Storage information including disk space, usage, partitions.
                  - 'network': Network information including interfaces, IP addresses, connectivity.
                  - 'gpu': Graphics card information if available.
                - format: (optional) Output format preference ('text', 'json'). Defaults to 'text'.
                """;
    }

    @Override
    public String usage() {
        return """
                (Attention: If you are using this tool, you MUST return the system information within the <system_info> tag):
                
                Example 1: Get system overview
                <system_info>
                  <info_type>overview</info_type>
                  <format>text</format>
                  <result>
                    操作系统: Windows 10 专业版 (64位)
                    处理器: Intel Core i7-10700K @ 3.80GHz (8核16线程)
                    内存: 32GB (使用率: 45%)
                    磁盘: C盘 500GB SSD (可用: 320GB)
                         D盘 2TB HDD (可用: 1.5TB)
                    显卡: NVIDIA GeForce RTX 3080 (10GB)
                    网络: 已连接 (192.168.1.100)
                  </result>
                </system_info>
                
                Example 2: Get detailed CPU information
                <system_info>
                  <info_type>cpu</info_type>
                  <format>text</format>
                  <result>
                    处理器: Intel Core i7-10700K
                    架构: x86_64
                    核心数: 8物理核心, 16逻辑核心
                    基础频率: 3.80 GHz
                    最大频率: 5.10 GHz
                    缓存: 16MB Intel Smart Cache
                    当前使用率: 25%
                    温度: 45°C
                  </result>
                </system_info>
                
                Example 3: Get disk information
                <system_info>
                  <info_type>disk</info_type>
                  <format>text</format>
                  <result>
                    磁盘总数: 3
                    - C盘 (系统):
                      类型: SSD
                      总容量: 500 GB
                      已用: 180 GB (36%)
                      可用: 320 GB (64%)
                      文件系统: NTFS
                
                    - D盘 (数据):
                      类型: HDD
                      总容量: 2 TB
                      已用: 500 GB (25%)
                      可用: 1.5 TB (75%)
                      文件系统: NTFS
                
                    - E盘 (备份):
                      类型: HDD
                      总容量: 4 TB
                      已用: 1.2 TB (30%)
                      可用: 2.8 TB (70%)
                      文件系统: NTFS
                  </result>
                </system_info>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            String infoType = inputJson.has("info_type") ? inputJson.get("info_type").getAsString() : "all";
            String format = inputJson.has("format") ? inputJson.get("format").getAsString() : "text";

            StringBuilder infoBuilder = new StringBuilder();

            switch (infoType.toLowerCase()) {
                case "cpu":
                    infoBuilder.append(getCpuInfo());
                    break;
                case "memory":
                    infoBuilder.append(getMemoryInfo());
                    break;
                case "disk":
                    infoBuilder.append(getDiskInfo());
                    break;
                case "all":
                default:
                    infoBuilder.append("===== CPU信息 =====\n");
                    infoBuilder.append(getCpuInfo()).append("\n\n");
                    infoBuilder.append("===== 内存信息 =====\n");
                    infoBuilder.append(getMemoryInfo()).append("\n\n");
                    infoBuilder.append("===== 磁盘信息 =====\n");
                    infoBuilder.append(getDiskInfo());
                    break;
            }

            result.addProperty("result", infoBuilder.toString());
            return result;
        } catch (Exception e) {
            result.addProperty("error", "获取系统信息失败: " + e.getMessage());
            return result;
        }
    }

    private String getCpuInfo() {
        StringBuilder info = new StringBuilder();
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            info.append("处理器: ").append(System.getenv("PROCESSOR_IDENTIFIER")).append("\n");
            info.append("架构: ").append(osBean.getArch()).append("\n");
            info.append("可用处理器数: ").append(osBean.getAvailableProcessors()).append("\n");
            info.append("系统名称: ").append(osBean.getName()).append("\n");
            info.append("系统版本: ").append(osBean.getVersion()).append("\n");

            // 尝试获取更详细的CPU信息（如果运行在支持的平台上）
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
                info.append("CPU负载: ").append(String.format("%.2f%%", sunOsBean.getSystemCpuLoad() * 100)).append("\n");
                info.append("进程CPU时间: ").append(sunOsBean.getProcessCpuTime() / 1000000).append(" ms");
            }
        } catch (Exception e) {
            info.append("获取CPU信息时出错: ").append(e.getMessage());
        }
        return info.toString();
    }

    private String getMemoryInfo() {
        StringBuilder info = new StringBuilder();
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
            MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();

            // 堆内存信息
            info.append("堆内存:\n");
            info.append("  初始大小: ").append(formatSize(heapMemory.getInit())).append("\n");
            info.append("  已使用: ").append(formatSize(heapMemory.getUsed())).append("\n");
            info.append("  已提交: ").append(formatSize(heapMemory.getCommitted())).append("\n");
            info.append("  最大值: ").append(formatSize(heapMemory.getMax())).append("\n");

            // 非堆内存信息
            info.append("非堆内存:\n");
            info.append("  初始大小: ").append(formatSize(nonHeapMemory.getInit())).append("\n");
            info.append("  已使用: ").append(formatSize(nonHeapMemory.getUsed())).append("\n");
            info.append("  已提交: ").append(formatSize(nonHeapMemory.getCommitted())).append("\n");
            info.append("  最大值: ").append(nonHeapMemory.getMax() == -1 ? "无限制" : formatSize(nonHeapMemory.getMax())).append("\n");

            // 尝试获取系统内存信息（如果运行在支持的平台上）
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
                info.append("系统物理内存:\n");
                info.append("  总内存: ").append(formatSize(sunOsBean.getTotalPhysicalMemorySize())).append("\n");
                info.append("  可用内存: ").append(formatSize(sunOsBean.getFreePhysicalMemorySize())).append("\n");
                info.append("  已使用: ").append(formatSize(sunOsBean.getTotalPhysicalMemorySize() - sunOsBean.getFreePhysicalMemorySize())).append("\n");
                info.append("  使用率: ").append(String.format("%.2f%%", (double) (sunOsBean.getTotalPhysicalMemorySize() - sunOsBean.getFreePhysicalMemorySize()) / sunOsBean.getTotalPhysicalMemorySize() * 100));
            }
        } catch (Exception e) {
            info.append("获取内存信息时出错: ").append(e.getMessage());
        }
        return info.toString();
    }

    private String getDiskInfo() {
        StringBuilder info = new StringBuilder();
        try {
            File[] roots = File.listRoots();
            info.append("磁盘总数: ").append(roots.length).append("\n");

            for (File root : roots) {
                info.append("- ").append(root.getAbsolutePath()).append(":\n");
                info.append("  总容量: ").append(formatSize(root.getTotalSpace())).append("\n");
                info.append("  可用空间: ").append(formatSize(root.getUsableSpace())).append("\n");
                info.append("  已用空间: ").append(formatSize(root.getTotalSpace() - root.getUsableSpace())).append("\n");

                double usedPercentage = (double) (root.getTotalSpace() - root.getUsableSpace()) / root.getTotalSpace() * 100;
                info.append("  使用率: ").append(String.format("%.2f%%", usedPercentage)).append("\n");
            }
        } catch (Exception e) {
            info.append("获取磁盘信息时出错: ").append(e.getMessage());
        }
        return info.toString();
    }

    private String formatSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
} 
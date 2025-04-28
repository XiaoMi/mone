package run.mone.hive.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.tool.ITool;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2025/4/16 09:38
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegInfoDto implements Serializable {

    private String name;

    private String version;

    private String group;

    private String ip;

    private int port;

    private Map<String, String> toolMap;

    private Map<String, String> mcpToolMap;
    
    /**
     * 将 RegInfoDto 转换为 RegInfo
     */
    public RegInfo toRegInfo() {
        RegInfo regInfo = new RegInfo();
        regInfo.setName(this.name);
        regInfo.setVersion(this.version);
        regInfo.setGroup(this.group);
        regInfo.setIp(this.ip);
        regInfo.setPort(this.port);
        
        // 将 Map<String, String> 转换为 Map<String, ITool>
        Map<String, ITool> toolMap = new HashMap<>();
        if (this.toolMap != null) {
            for (Map.Entry<String, String> entry : this.toolMap.entrySet()) {
                String toolName = entry.getKey();
                String toolType = entry.getValue();
                // 这里我们只存储工具名称，不创建具体实例
                // 实际使用时，可以根据需要创建具体的工具实例
                toolMap.put(toolName, null);
            }
        }
        regInfo.setToolMap(toolMap);
        
        // 同样处理 mcpToolMap
        Map<String, McpSchema.Tool> mcpToolMap = new HashMap<>();
        if (this.mcpToolMap != null) {
            for (Map.Entry<String, String> entry : this.mcpToolMap.entrySet()) {
                String toolName = entry.getKey();
                String toolType = entry.getValue();
                // 这里我们只存储工具名称，不创建具体实例
                // 实际使用时，可以根据需要创建具体的工具实例
                mcpToolMap.put(toolName, null);
            }
        }
        regInfo.setMcpToolMap(mcpToolMap);
        
        return regInfo;
    }
    
    /**
     * 从 RegInfo 创建 RegInfoDto
     */
    public static RegInfoDto fromRegInfo(RegInfo regInfo) {
        RegInfoDto dto = new RegInfoDto();
        dto.setName(regInfo.getName());
        dto.setVersion(regInfo.getVersion());
        dto.setGroup(regInfo.getGroup());
        dto.setIp(regInfo.getIp());
        dto.setPort(regInfo.getPort());
        
        // 将 Map<String, ITool> 转换为 Map<String, String>
        Map<String, String> toolMap = new HashMap<>();
        if (regInfo.getToolMap() != null) {
            for (Map.Entry<String, ITool> entry : regInfo.getToolMap().entrySet()) {
                String toolName = entry.getKey();
                ITool tool = entry.getValue();
                if (tool != null) {
                    toolMap.put(toolName, tool.description());
                } else {
                    toolMap.put(toolName, toolName);
                }
            }
        }
        dto.setToolMap(toolMap);
        
        // 同样处理 mcpToolMap
        Map<String, String> mcpToolMap = new HashMap<>();
        if (regInfo.getMcpToolMap() != null) {
            for (Map.Entry<String, McpSchema.Tool> entry : regInfo.getMcpToolMap().entrySet()) {
                String toolName = entry.getKey();
                McpSchema.Tool tool = entry.getValue();
                if (tool != null) {
                    mcpToolMap.put(toolName, GsonUtils.gson.toJson(tool));
                } else {
                    mcpToolMap.put(toolName, toolName);
                }
            }
        }
        dto.setMcpToolMap(mcpToolMap);
        
        return dto;
    }
} 
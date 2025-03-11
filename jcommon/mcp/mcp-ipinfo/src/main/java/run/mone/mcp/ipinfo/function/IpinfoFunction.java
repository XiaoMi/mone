package run.mone.mcp.ipinfo.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.ipinfo.service.IpinfoService;

@Data
@Slf4j
public class IpinfoFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "ipinfo_executor";
    private String desc = "ipinfo executor";
    private ObjectMapper objectMapper;

    private String ipinfoToolSchema = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["get_ip", "get_ip_details"],
                        "description": "Get IP address and information about an IP address.\
                        Use this tool to:\
                        - Determine the user's geographic location to coarse granularity\
                        - Get information about the user's internet service provider\
                        - Get IP address and information about a specific IP address"
                    },
                    "ip": {
                        "type": "string",
                        "description": "The IP address to look up. If None, returns information about the requesting client's IP address."
                    }
                },
                "required": ["operation"]
            }
            """;

    public IpinfoFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> params) {
        String IPINFO_API_TOKEN = System.getenv().getOrDefault("IPINFO_API_TOKEN", "");
        
        String operation = (String) params.get("operation");
        String ip = (String) params.get("ip");

        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation is required");
        }

        if (operation.equals("get_ip")) {
            return getIp();
        } else if (operation.equals("get_ip_details")) {
            if (ip == null || ip.trim().isEmpty()) {
                ip = IpinfoService.getClientIp();
            }
            return getIpDetail(IPINFO_API_TOKEN, ip);
        } else {
            throw new IllegalArgumentException("Invalid operation");
        }   
    }

    public McpSchema.CallToolResult getIp() {
        String clientIp = IpinfoService.getClientIp();
        
        if(!clientIp.isEmpty()) {
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Client IP: " + clientIp)),
                    false
            );
        } else {
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Get IP failed")),
                    true
            );
        }
    }

    public McpSchema.CallToolResult getIpDetail(String IPINFO_API_TOKEN, String ip) {
        try {
            Map<String, Object> ipDetail = IpinfoService.getIpDetail(IPINFO_API_TOKEN, ip);

            if (ipDetail == null || ipDetail.isEmpty()) {
                log.error("Get IP detail failed");
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Get IP detail failed")),
                        true
                );
            }
            log.info("Get IP detail success");
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(new Gson().toJson(ipDetail))),
                    false
            );
        } catch (Exception e) {
            log.error("Get IP detail failed: ", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }
}
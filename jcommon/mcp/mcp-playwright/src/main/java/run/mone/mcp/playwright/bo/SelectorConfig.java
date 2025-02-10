package run.mone.mcp.playwright.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectorConfig {
    private String id;
    private String domain;    // 域名
    private String url;       // 具体URL
    private String selector;  // CSS选择器
    private String key;       // 自定义key
    private String value;     // 对应的value
    private String description; // 配置描述
}

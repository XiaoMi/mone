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
    private String selector;
    private String key; 
    private String value;
    private String description;
}

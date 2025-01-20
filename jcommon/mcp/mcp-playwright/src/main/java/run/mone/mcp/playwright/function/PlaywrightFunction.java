package run.mone.mcp.playwright.function;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class PlaywrightFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "execute_playwright";
    private String desc = "Execute Playwright actions";

    private String playwrightToolSchema = """
            {
                "type": "object",
                "properties": {
                    "action": {
                        "type": "string",
                        "enum": ["load_page", "take_screenshot", "click_button", "modify_text"],
                        "description": "Action to perform"
                    },
                    "url": {
                        "type": "string",
                        "description": "URL to load (for load_page action)"
                    },
                    "selector": {
                        "type": "string",
                        "description": "CSS selector for element (for click_button and modify_text actions)"
                    },
                    "text": {
                        "type": "string",
                        "description": "Text to input (for modify_text action)"
                    }
                },
                "required": ["action"]
            }
            """;

    private Playwright playwright;
    private Browser browser;
    private Page page;

    public PlaywrightFunction() {
        log.info("Initializing PlaywrightFunction...");
        playwright = Playwright.create();
        //设置是否是无头浏览器
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false));
        page = browser.newPage();
        log.info("PlaywrightFunction initialized successfully");
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String action = (String) args.get("action");
        log.info("Executing Playwright action: {}", action);

        try {
            switch (action) {
                case "load_page":
                    return loadPage((String) args.get("url"));
                case "take_screenshot":
                    return takeScreenshot();
                case "click_button":
                    return clickButton((String) args.get("selector"));
                case "modify_text":
                    return modifyText((String) args.get("selector"), (String) args.get("text"));
                default:
                    throw new IllegalArgumentException("Unknown action: " + action);
            }
        } catch (Exception e) {
            log.error("Error executing Playwright action", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }

    private McpSchema.CallToolResult loadPage(String url) {
        page.navigate(url);
        page.waitForLoadState(LoadState.NETWORKIDLE);
        log.info("Page loaded: {}", url);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Page loaded: " + url), new McpSchema.TextContent("Source: " + page.content())),
                false
        );
    }

    //获取page的源代码(class)
    public McpSchema.CallToolResult getPageSource() {
        String pageSource = page.content();
        log.info("Page source retrieved");
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Page source: " + pageSource)),
                false
        );
    }

    private McpSchema.CallToolResult takeScreenshot() {
        String path = System.getProperty("user.home") + "/Desktop/" + "screenshot.png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
        log.info("Screenshot taken: {}", path);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Screenshot taken: " + path)),
                false
        );
    }

    private McpSchema.CallToolResult clickButton(String selector) {
        page.click(selector);
        log.info("Button clicked: {}", selector);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Button clicked: " + selector)),
                false
        );
    }

    private McpSchema.CallToolResult modifyText(String selector, String text) {
        page.fill(selector, text);
        log.info("Text modified: {} -> {}", selector, text);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Text modified: " + selector + " -> " + text)),
                false
        );
    }

    public void close() {
        if (page != null) {
            page.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
        log.info("PlaywrightFunction resources closed");
    }
}

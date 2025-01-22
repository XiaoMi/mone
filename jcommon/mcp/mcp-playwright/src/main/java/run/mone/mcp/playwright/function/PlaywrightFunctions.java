package run.mone.mcp.playwright.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.Page.NavigateOptions;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.RequestOptions;
import com.microsoft.playwright.options.WaitUntilState;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Data
public class PlaywrightFunctions {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Playwright playwright;
    private static Browser browser;
    private static Page page;
    private static APIRequestContext apiContext;

    public static Playwright getPlaywright() {
        return playwright;
    }

    public static Browser getBrowser() {
        return browser;
    }

    public static Page getPage() {
        return page;
    }

    // 确保浏览器实例存在
    private static Page ensureBrowser(Integer width, Integer height, Boolean headless) {
        try {
            if (playwright == null) {
                playwright = Playwright.create();
            }
            if (browser == null) {
                String headlessProp = System.getProperty("playwright.headless");
                boolean isHeadless = headlessProp != null 
                    ? Boolean.parseBoolean(headlessProp) 
                    : (headless != null ? headless : false);
                browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(isHeadless));
            }
            if (page == null || page.isClosed()) {
                page = browser.newPage(new Browser.NewPageOptions()
                    .setViewportSize(width != null ? width : 1920, 
                                   height != null ? height : 1080));
            }
            return page;
        } catch (Exception e) {
            log.error("Failed to ensure browser", e);
            throw new RuntimeException("Failed to ensure browser", e);
        }
    }

    // 确保API上下文存在
    private static APIRequestContext ensureApiContext(String baseUrl) {
        try {
            if (playwright == null) {
                playwright = Playwright.create();
            }
            if (apiContext == null) {
                apiContext = playwright.request().newContext(new APIRequest.NewContextOptions()
                    .setBaseURL(baseUrl));
            }
            return apiContext;
        } catch (Exception e) {
            log.error("Failed to ensure API context", e);
            throw new RuntimeException("Failed to ensure API context", e);
        }
    }

    // 工具方法:解析JSON对象
    private static <T> T parseObject(Object obj, Class<T> clazz) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(obj), clazz);
        } catch (Exception e) {
            log.error("Failed to parse JSON: {}", obj, e);
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    @Data
    public static class NavigateFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_navigate";
        
        private String desc = "Navigate to a URL";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "url": { "type": "string" },
                    "width": { 
                        "type": "number",
                        "description": "Viewport width in pixels (default: 1920)"
                    },
                    "height": { 
                        "type": "number",
                        "description": "Viewport height in pixels (default: 1080)"
                    },
                    "timeout": { 
                        "type": "number",
                        "description": "Navigation timeout in milliseconds"
                    },
                    "waitUntil": { 
                        "type": "string",
                        "description": "Navigation wait condition"
                    },
                    "headless": { 
                        "type": "boolean",
                        "description": "Whether to run in headless mode (default: false)"
                    }
                },
                "required": ["url"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                Integer width = args.get("width") != null ? ((Number)args.get("width")).intValue() : 1024;
                Integer height = args.get("height") != null ? ((Number)args.get("height")).intValue() : 768;
                Boolean headless = args.get("headless") != null ? (Boolean)args.get("headless") : false;

                Page page = ensureBrowser(width, height, headless);
                
                NavigateOptions options = new NavigateOptions()
                    .setTimeout(args.get("timeout") != null ? ((Number)args.get("timeout")).intValue() : 30000)
                    .setWaitUntil(args.get("waitUntil") != null ? 
                        WaitUntilState.valueOf(args.get("waitUntil").toString().toUpperCase()) : 
                        WaitUntilState.LOAD);

                page.navigate(args.get("url").toString(), options);

                String message = String.format("Navigated to %s with %s wait%s", 
                    args.get("url"),
                    args.get("waitUntil") != null ? args.get("waitUntil") : "load",
                    (width != null && height != null) ? String.format(" (viewport: %dx%d)", width, height) : ""
                );

                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(message)), 
                    false
                );
            } catch (Exception e) {
                log.error("Navigation failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Navigation failed: " + e.getMessage() + " ,cause: " + e.getCause())),
                    true
                );
            }
        }
    }

    @Data
    public static class ScreenshotFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_screenshot";
        
        private String desc = "Take a screenshot of the current page or a specific element";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "name": { 
                        "type": "string",
                        "description": "Name for the screenshot"
                    },
                    "selector": { 
                        "type": "string",
                        "description": "CSS selector for element to screenshot"
                    },
                    "width": { 
                        "type": "number",
                        "description": "Width in pixels (default: 800)"
                    },
                    "height": { 
                        "type": "number",
                        "description": "Height in pixels (default: 600)"
                    },
                    "storeBase64": { 
                        "type": "boolean",
                        "description": "Store screenshot in base64 format (default: true)"
                    },
                    "savePng": { 
                        "type": "boolean",
                        "description": "Save screenshot as PNG file (default: false)"
                    },
                    "downloadsDir": { 
                        "type": "string",
                        "description": "Custom downloads directory path (default: user's Downloads folder)"
                    }
                },
                "required": ["name"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                Page page = ensureBrowser(
                    args.get("width") != null ? ((Number)args.get("width")).intValue() : 1024,
                    args.get("height") != null ? ((Number)args.get("height")).intValue() : 768,
                    args.get("headless") != null ? (Boolean)args.get("headless") : false
                );


                Path path = args.get("savePng") != null && (Boolean)args.get("savePng") ? 
                        Paths.get(args.get("downloadsDir") != null ? 
                            args.get("downloadsDir").toString() : 
                            System.getProperty("user.home") + "/Downloads",
                            args.get("name") + "-" + Instant.now().toString().replace(":", "-") + ".png"
                        ) : Paths.get(System.getProperty("java.io.tmpdir"), "screenshot.png");

                Page.ScreenshotOptions pageOptions = new Page.ScreenshotOptions()
                    .setPath(path);

                ElementHandle.ScreenshotOptions elementOptions = new ElementHandle.ScreenshotOptions()
                    .setPath(path);

                byte[] screenshot;
                if (args.get("selector") != null) {
                    ElementHandle element = page.querySelector(args.get("selector").toString());
                    if (element == null) {
                        return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Element not found: " + args.get("selector"))),
                            true
                        );
                    }
                    screenshot = element.screenshot(elementOptions);
                } else {
                    screenshot = page.screenshot(pageOptions);
                }

                List<McpSchema.Content> content = new java.util.ArrayList<>();
                
                if (args.get("savePng") != null && (Boolean)args.get("savePng")) {
                    content.add(new McpSchema.TextContent(
                        "Screenshot saved to: " + path.toString()
                    ));
                }

                if (args.get("storeBase64") == null || (Boolean)args.get("storeBase64")) {
                    content.add(new McpSchema.ImageContent(null, null, "image/png", 
                        Base64.getEncoder().encodeToString(screenshot),
                        "image/png"
                    ));
                }

                return new McpSchema.CallToolResult(content, false);
            } catch (Exception e) {
                log.error("Screenshot failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Screenshot failed: " + e.getMessage() + " ,cause: " + e.getCause())),
                    true
                );
            }
        }
    }

    @Data
    public static class ClickFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_click";
        
        private String desc = "Click an element on the page";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "selector": { 
                        "type": "string",
                        "description": "CSS selector for element to click"
                    }
                },
                "required": ["selector"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                Page page = ensureBrowser(null, null, null);
                page.click(args.get("selector").toString());
                
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Clicked: " + args.get("selector"))),
                    false
                );
            } catch (Exception e) {
                log.error("Click failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Failed to click " + args.get("selector") + ": " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class FillFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_fill";
        
        private String desc = "Fill out an input field";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "selector": { 
                        "type": "string",
                        "description": "CSS selector for input field"
                    },
                    "value": { 
                        "type": "string",
                        "description": "Value to fill"
                    }
                },
                "required": ["selector", "value"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                Page page = ensureBrowser(null, null, null);
                page.waitForSelector(args.get("selector").toString());
                page.fill(args.get("selector").toString(), args.get("value").toString());
                
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Filled " + args.get("selector") + " with: " + args.get("value")
                    )),
                    false
                );
            } catch (Exception e) {
                log.error("Fill failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Failed to fill " + args.get("selector") + ": " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class SelectFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_select";
        
        private String desc = "Select an element on the page with Select tag";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "selector": { 
                        "type": "string",
                        "description": "CSS selector for element to select"
                    },
                    "value": { 
                        "type": "string",
                        "description": "Value to select"
                    }
                },
                "required": ["selector", "value"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                Page page = ensureBrowser(null, null, null);
                page.waitForSelector(args.get("selector").toString());
                page.selectOption(args.get("selector").toString(), args.get("value").toString());
                
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Selected " + args.get("selector") + " with: " + args.get("value")
                    )),
                    false
                );
            } catch (Exception e) {
                log.error("Select failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Failed to select " + args.get("selector") + ": " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class HoverFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_hover";
        
        private String desc = "Hover an element on the page";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "selector": { 
                        "type": "string",
                        "description": "CSS selector for element to hover"
                    }
                },
                "required": ["selector"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                Page page = ensureBrowser(null, null, null);
                page.waitForSelector(args.get("selector").toString());
                page.hover(args.get("selector").toString());
                
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Hovered " + args.get("selector"))),
                    false
                );
            } catch (Exception e) {
                log.error("Hover failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Failed to hover " + args.get("selector") + ": " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class EvaluateFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_evaluate";
        
        private String desc = "Execute JavaScript in the browser console";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "script": { 
                        "type": "string",
                        "description": "JavaScript code to execute"
                    }
                },
                "required": ["script"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                Page page = ensureBrowser(null, null, null);
                Object result = page.evaluate(args.get("script").toString());
                
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Execution result:\n" + objectMapper.writeValueAsString(result)
                    )),
                    false
                );
            } catch (Exception e) {
                log.error("Script execution failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Script execution failed: " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class GetFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_get";
        
        private String desc = "Perform an HTTP GET request";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "url": { 
                        "type": "string",
                        "description": "URL to perform GET operation"
                    }
                },
                "required": ["url"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                APIRequestContext context = ensureApiContext(args.get("url").toString());
                APIResponse response = context.get(args.get("url").toString());
                
                return new McpSchema.CallToolResult(
                    List.of(
                        new McpSchema.TextContent("Performed GET Operation " + args.get("url")),
                        new McpSchema.TextContent("Response: " + response.text()),
                        new McpSchema.TextContent("Response code " + response.status())
                    ),
                    false
                );
            } catch (Exception e) {
                log.error("GET request failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Failed to perform GET operation on " + args.get("url") + ": " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class PostFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_post";
        
        private String desc = "Perform an HTTP POST request";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "url": { 
                        "type": "string",
                        "description": "URL to perform POST operation"
                    },
                    "value": { 
                        "type": "string",
                        "description": "Data to post in the body"
                    }
                },
                "required": ["url", "value"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                APIRequestContext context = ensureApiContext(args.get("url").toString());
                RequestOptions options = RequestOptions.create()
                    .setData(args.get("value"))
                    .setHeader("Content-Type", "application/json");
                
                APIResponse response = context.post(args.get("url").toString(), options);
                
                return new McpSchema.CallToolResult(
                    List.of(
                        new McpSchema.TextContent(
                            "Performed POST Operation " + args.get("url") + 
                            " with data " + args.get("value")
                        ),
                        new McpSchema.TextContent("Response: " + response.text()),
                        new McpSchema.TextContent("Response code " + response.status())
                    ),
                    false
                );
            } catch (Exception e) {
                log.error("POST request failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Failed to perform POST operation on " + args.get("url") + ": " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class PutFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_put";
        
        private String desc = "Perform an HTTP PUT request";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "url": { 
                        "type": "string",
                        "description": "URL to perform PUT operation"
                    },
                    "value": { 
                        "type": "string",
                        "description": "Data to PUT in the body"
                    }
                },
                "required": ["url", "value"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                APIRequestContext context = ensureApiContext(args.get("url").toString());
                RequestOptions options = RequestOptions.create()
                    .setData(args.get("value"))
                    .setHeader("Content-Type", "application/json");
                
                APIResponse response = context.put(args.get("url").toString(), options);
                
                return new McpSchema.CallToolResult(
                    List.of(
                        new McpSchema.TextContent(
                            "Performed PUT Operation " + args.get("url") + 
                            " with data " + args.get("value")
                        ),
                        new McpSchema.TextContent("Response: " + response.text()),
                        new McpSchema.TextContent("Response code " + response.status())
                    ),
                    false
                );
            } catch (Exception e) {
                log.error("PUT request failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Failed to perform PUT operation on " + args.get("url") + ": " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class PatchFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_patch";
        
        private String desc = "Perform an HTTP PATCH request";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "url": { 
                        "type": "string",
                        "description": "URL to perform PATCH operation"
                    },
                    "value": { 
                        "type": "string",
                        "description": "Data to PATCH in the body"
                    }
                },
                "required": ["url", "value"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                APIRequestContext context = ensureApiContext(args.get("url").toString());
                RequestOptions options = RequestOptions.create()
                    .setData(args.get("value"))
                    .setHeader("Content-Type", "application/json");
                
                APIResponse response = context.patch(args.get("url").toString(), options);
                
                return new McpSchema.CallToolResult(
                    List.of(
                        new McpSchema.TextContent(
                            "Performed PATCH Operation " + args.get("url") + 
                            " with data " + args.get("value")
                        ),
                        new McpSchema.TextContent("Response: " + response.text()),
                        new McpSchema.TextContent("Response code " + response.status())
                    ),
                    false
                );
            } catch (Exception e) {
                log.error("PATCH request failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Failed to perform PATCH operation on " + args.get("url") + ": " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class DeleteFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_delete";
        
        private String desc = "Perform an HTTP DELETE request";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "url": { 
                        "type": "string",
                        "description": "URL to perform DELETE operation"
                    }
                },
                "required": ["url"]
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                APIRequestContext context = ensureApiContext(args.get("url").toString());
                APIResponse response = context.delete(args.get("url").toString());
                
                return new McpSchema.CallToolResult(
                    List.of(
                        new McpSchema.TextContent("Performed DELETE Operation " + args.get("url")),
                        new McpSchema.TextContent("Response code " + response.status())
                    ),
                    false
                );
            } catch (Exception e) {
                log.error("DELETE request failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Failed to perform DELETE operation on " + args.get("url") + ": " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class GetContentFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_get_content";
        
        private String desc = "Get content from the current page or a specific element";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "selector": { 
                        "type": "string",
                        "description": "CSS selector for target element (optional)"
                    },
                    "contentType": {
                        "type": "string", 
                        "enum": ["text", "html"],
                        "description": "Type of content to retrieve (default: text)"
                    },
                    "wait": {
                        "type": "boolean",
                        "description": "Whether to wait for element to be present (default: true)"
                    },
                    "timeout": {
                        "type": "number",
                        "description": "Maximum time to wait in milliseconds (default: 30000)"
                    },
                    "waitForLoadState": {
                        "type": "string",
                        "enum": ["load", "domcontentloaded", "networkidle"],
                        "description": "Wait for specific load state (default: load)"
                    },
                    "waitForSelector": {
                        "type": "string",
                        "description": "Additional selector to wait for before getting content (optional)"
                    }
                }
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            try {
                Page page = ensureBrowser(null, null, null);
                
                String selector = args.get("selector") != null ? args.get("selector").toString() : null;
                String contentType = args.get("contentType") != null ? args.get("contentType").toString() : "text";
                boolean wait = args.get("wait") != null ? (Boolean)args.get("wait") : true;
                int timeout = args.get("timeout") != null ? ((Number)args.get("timeout")).intValue() : 30000;
                String waitForLoadState = args.get("waitForLoadState") != null ? 
                    args.get("waitForLoadState").toString() : "load";
                String waitForSelector = args.get("waitForSelector") != null ? 
                    args.get("waitForSelector").toString() : null;

                // 等待页面加载状态
                page.waitForLoadState(LoadState.valueOf(waitForLoadState.toUpperCase()), 
                    new Page.WaitForLoadStateOptions().setTimeout(timeout));

                // 如果指定了额外的等待选择器
                if (waitForSelector != null) {
                    page.waitForSelector(waitForSelector, 
                        new Page.WaitForSelectorOptions().setTimeout(timeout));
                }

                String content;
                if (selector != null) {
                    if (wait) {
                        page.waitForSelector(selector, 
                            new Page.WaitForSelectorOptions().setTimeout(timeout));
                    }
                    ElementHandle element = page.querySelector(selector);
                    if (element == null) {
                        return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Element not found: " + selector)),
                            true
                        );
                    }
                    content = contentType.equals("html") ? 
                        (String)element.evaluate("el => el.innerHTML") :
                        element.textContent();
                } else {
                    content = contentType.equals("html") ? 
                        page.content() :
                        (String)page.evaluate("() => document.body.innerText");
                }

                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(content)),
                    false
                );
            } catch (Exception e) {
                log.error("Failed to get content", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "Failed to get content: " + e.getMessage()
                    )),
                    true
                );
            }
        }
    }

    @Data
    public static class CleanupFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "playwright_cleanup";
        
        private String desc = "Cleanup all Playwright resources (browser, page, contexts)";

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "force": {
                        "type": "boolean",
                        "description": "Force cleanup even if operations are in progress (default: false)"
                    }
                }
            }
            """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> args) {
            List<String> messages = new ArrayList<>();
            boolean hasError = false;
            
            try {
                // 清理Page
                if (page != null) {
                    try {
                        page.close();
                        messages.add("Page closed successfully");
                        page = null;
                    } catch (Exception e) {
                        messages.add("Failed to close page: " + e.getMessage());
                        hasError = true;
                    }
                }

                // 清理APIContext
                if (apiContext != null) {
                    try {
                        apiContext.dispose();
                        messages.add("API context disposed successfully");
                    } catch (Exception e) {
                        messages.add("Failed to dispose API context: " + e.getMessage());
                        hasError = true;
                    } finally {
                        apiContext = null;
                    }
                }

                // 清理Browser
                if (browser != null) {
                    try {
                        browser.close();
                        messages.add("Browser closed successfully");
                    } catch (Exception e) {
                        messages.add("Failed to close browser: " + e.getMessage());
                        hasError = true;
                    } finally {
                        browser = null;
                    }
                }

                // 清理Playwright
                if (playwright != null) {
                    try {
                        playwright.close();
                        messages.add("Playwright closed successfully");
                    } catch (Exception e) {
                        messages.add("Failed to close playwright: " + e.getMessage());
                        hasError = true;
                    } finally {
                        playwright = null;
                    }
                }

                return new McpSchema.CallToolResult(
                    messages.stream()
                        .map(McpSchema.TextContent::new)
                        .collect(Collectors.toList()),
                    hasError
                );
                
            } catch (Exception e) {
                log.error("Cleanup failed", e);
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Cleanup failed: " + e.getMessage())),
                    true
                );
            }
        }
    }

}

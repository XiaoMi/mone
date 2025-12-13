package run.mone.mcp.multimodal.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.multimodal.android.AndroidService;

import java.util.List;
import java.util.Map;

/**
 * Android 设备操作 MCP Function
 * 提供对 Android 设备的各种操作，包括点击、长按、滑动、拖拽、输入、截图等
 *
 * @author goodjava@qq.com
 * @date 2025/12/13
 */
@Slf4j
@Component
public class AndroidFunction implements McpFunction {

    private final AndroidService androidService;

    @Autowired
    public AndroidFunction(AndroidService androidService) {
        this.androidService = androidService;
    }

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["click", "longPress", "type", "scroll", "drag", "openApp", "pressHome", "pressBack", "screenshot", "screenshotBase64", "connect", "disconnect", "getDevices", "launchApp", "forceStopApp"],
                        "description": "要执行的操作类型: click=点击, longPress=长按, type=输入文字(支持中文), scroll=滚动, drag=拖拽, openApp=通过应用名打开应用, pressHome=按Home键, pressBack=按返回键, screenshot=截图保存文件, screenshotBase64=截图返回Base64, connect=连接设备, disconnect=断开设备, getDevices=获取设备列表, launchApp=通过包名启动应用, forceStopApp=强制停止应用"
                    },
                    "x": {
                        "type": "integer",
                        "description": "X坐标 (用于 click, longPress, scroll 操作)"
                    },
                    "y": {
                        "type": "integer",
                        "description": "Y坐标 (用于 click, longPress, scroll 操作)"
                    },
                    "startX": {
                        "type": "integer",
                        "description": "起始X坐标 (用于 drag 操作)"
                    },
                    "startY": {
                        "type": "integer",
                        "description": "起始Y坐标 (用于 drag 操作)"
                    },
                    "endX": {
                        "type": "integer",
                        "description": "结束X坐标 (用于 drag 操作)"
                    },
                    "endY": {
                        "type": "integer",
                        "description": "结束Y坐标 (用于 drag 操作)"
                    },
                    "text": {
                        "type": "string",
                        "description": "要输入的文字，支持中文 (用于 type 操作)"
                    },
                    "direction": {
                        "type": "string",
                        "enum": ["up", "down", "left", "right"],
                        "description": "滚动方向 (用于 scroll 操作)"
                    },
                    "appName": {
                        "type": "string",
                        "description": "应用名称，支持中英文别名如：微信/wechat、QQ、抖音/douyin、淘宝/taobao 等 (用于 openApp 操作)"
                    },
                    "packageName": {
                        "type": "string",
                        "description": "应用包名，如 com.tencent.mm (用于 launchApp, forceStopApp 操作)"
                    },
                    "filePath": {
                        "type": "string",
                        "description": "截图保存路径 (用于 screenshot 操作，可选)"
                    },
                    "host": {
                        "type": "string",
                        "description": "设备IP地址 (用于 connect, disconnect 操作)"
                    },
                    "port": {
                        "type": "integer",
                        "description": "设备端口，默认5555 (用于 connect 操作)"
                    },
                    "deviceSerial": {
                        "type": "string",
                        "description": "设备序列号，如 192.168.1.100:5555 (可选，不指定则使用第一个在线设备)"
                    },
                    "durationMs": {
                        "type": "integer",
                        "description": "操作持续时间(毫秒)，用于 longPress(默认1000)、drag(默认500) 操作"
                    },
                    "distance": {
                        "type": "integer",
                        "description": "滚动距离(像素)，用于 scroll 操作，默认300"
                    }
                },
                "required": ["operation"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        String deviceSerial = (String) arguments.get("deviceSerial");

        try {
            Flux<String> result = switch (operation) {
                // 点击操作
                case "click" -> androidService.tap(
                        parseIntArg(arguments, "x"),
                        parseIntArg(arguments, "y"),
                        deviceSerial);

                // 长按操作
                case "longPress" -> {
                    int durationMs = parseIntArgOrDefault(arguments, "durationMs", 1000);
                    yield androidService.longPress(
                            parseIntArg(arguments, "x"),
                            parseIntArg(arguments, "y"),
                            durationMs,
                            deviceSerial);
                }

                // 输入文字（支持中文，自动切换输入法）
                case "type" -> androidService.inputTextWithImeSwitching(
                        (String) arguments.get("text"),
                        deviceSerial);

                // 滚动操作
                case "scroll" -> {
                    int distance = parseIntArgOrDefault(arguments, "distance", 300);
                    int scrollDuration = parseIntArgOrDefault(arguments, "durationMs", 300);
                    yield androidService.scroll(
                            parseIntArg(arguments, "x"),
                            parseIntArg(arguments, "y"),
                            (String) arguments.get("direction"),
                            distance,
                            scrollDuration,
                            deviceSerial);
                }

                // 拖拽操作
                case "drag" -> {
                    int dragDuration = parseIntArgOrDefault(arguments, "durationMs", 500);
                    yield androidService.drag(
                            parseIntArg(arguments, "startX"),
                            parseIntArg(arguments, "startY"),
                            parseIntArg(arguments, "endX"),
                            parseIntArg(arguments, "endY"),
                            dragDuration,
                            deviceSerial);
                }

                // 通过应用名打开应用
                case "openApp" -> androidService.openApp(
                        (String) arguments.get("appName"),
                        deviceSerial);

                // 通过包名启动应用
                case "launchApp" -> androidService.launchApp(
                        (String) arguments.get("packageName"),
                        deviceSerial);

                // 强制停止应用
                case "forceStopApp" -> androidService.forceStopApp(
                        (String) arguments.get("packageName"),
                        deviceSerial);

                // 按 Home 键
                case "pressHome" -> androidService.pressHome(deviceSerial);

                // 按返回键
                case "pressBack" -> androidService.pressBack(deviceSerial);

                // 截图保存到文件
                case "screenshot" -> androidService.screenshot(
                        (String) arguments.get("filePath"),
                        deviceSerial);

                // 截图返回 Base64
                case "screenshotBase64" -> androidService.screenshotBase64(deviceSerial);

                // 连接设备
                case "connect" -> {
                    String host = (String) arguments.get("host");
                    int port = parseIntArgOrDefault(arguments, "port", 5555);
                    yield androidService.connect(host, port);
                }

                // 断开设备
                case "disconnect" -> {
                    String address = (String) arguments.get("host");
                    if (address != null && !address.contains(":")) {
                        int port = parseIntArgOrDefault(arguments, "port", 5555);
                        address = address + ":" + port;
                    }
                    yield androidService.disconnect(address);
                }

                // 获取设备列表
                case "getDevices" -> androidService.getDevices()
                        .map(devices -> devices.isEmpty()
                                ? "没有已连接的设备"
                                : "已连接设备:\n" + String.join("\n", devices));

                default -> throw new IllegalArgumentException("未知操作: " + operation);
            };

            return result.map(res -> new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(res)), false));

        } catch (Exception e) {
            log.error("执行 Android 操作失败: {}", operation, e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误: " + e.getMessage())), true));
        }
    }

    /**
     * 解析整数参数
     */
    private int parseIntArg(Map<String, Object> arguments, String key) {
        Object value = arguments.get(key);
        if (value == null) {
            throw new IllegalArgumentException("缺少必需参数: " + key);
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

    /**
     * 解析整数参数，带默认值
     */
    private int parseIntArgOrDefault(Map<String, Object> arguments, String key, int defaultValue) {
        Object value = arguments.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public String getName() {
        return "stream_android";
    }

    @Override
    public String getDesc() {
        return "Android 设备操作工具，支持通过 ADB 控制 Android 设备。" +
                "功能包括：点击(click)、长按(longPress)、输入文字(type，支持中文)、" +
                "滚动(scroll)、拖拽(drag)、打开应用(openApp)、按键(pressHome/pressBack)、" +
                "截图(screenshot/screenshotBase64)、设备管理(connect/disconnect/getDevices)等。";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
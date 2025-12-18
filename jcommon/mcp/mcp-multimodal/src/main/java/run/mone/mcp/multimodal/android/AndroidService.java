package run.mone.mcp.multimodal.android;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.RawImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import run.mone.mcp.multimodal.util.AndroidResponseParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Android 设备操作服务
 * 基于 ddmlib 实现对远程 Android 设备的控制
 *
 * 支持功能：
 * - 连接/断开远程设备
 * - 点击操作 (input tap)
 * - 文字输入 (input text)
 * - 滚屏操作 (input swipe)
 * - 截图 (screencap)
 * - 输入法切换 (ime)
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "android.adb.enabled", havingValue = "true", matchIfMissing = false)
public class AndroidService {

    private AndroidDebugBridge bridge;
    private final Map<String, IDevice> connectedDevices = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;

    // 默认超时时间（毫秒）
    private static final long DEFAULT_TIMEOUT_MS = 30000;
    private static final long COMMAND_TIMEOUT_MS = 10000;

    /**
     * 远程 Android 设备 IP 地址
     * 可通过环境变量 ANDROID_DEVICE_HOST 或配置 android.device.host 设置
     */
    @Value("${android.device.host:#{systemEnvironment['ANDROID_DEVICE_HOST'] ?: ''}}")
    private String deviceHost;

    /**
     * 远程 Android 设备端口
     * 可通过环境变量 ANDROID_DEVICE_PORT 或配置 android.device.port 设置
     */
    @Value("${android.device.port:#{systemEnvironment['ANDROID_DEVICE_PORT'] ?: '5555'}}")
    private String devicePort;

    /**
     * 是否自动连接远程设备
     */
    @Value("${android.device.auto-connect:true}")
    private boolean autoConnect;

    // @PostConstruct
    public void init() {
        try {
            // 1. 初始化 ADB
            log.info("1. 初始化 Android Debug Bridge...");
            AndroidDebugBridge.init(false);

            // 2. 查找 adb 路径
            String adbPath = findAdbPath();
            if (adbPath == null) {
                log.error("无法找到 adb，请确保 ANDROID_HOME 环境变量已设置或 adb 在 PATH 中");
                return;
            }

            log.info("   ADB 路径: {}", adbPath);

            // 3. 创建 Bridge
            bridge = AndroidDebugBridge.createBridge(adbPath, false);
            waitForDeviceList();

            // 4. 连接远程设备（如果配置了）
            if (autoConnect && deviceHost != null && !deviceHost.isEmpty()) {
                int port = 5555;
                try {
                    port = Integer.parseInt(devicePort);
                } catch (NumberFormatException e) {
                    log.warn("无效的端口号: {}，使用默认端口 5555", devicePort);
                }

                String address = deviceHost + ":" + port;
                log.info("2. 连接到远程设备: {}", address);

                ProcessBuilder pb = new ProcessBuilder(adbPath, "connect", address);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("   {}", line);
                }
                process.waitFor();

                // 等待设备连接
                Thread.sleep(2000);

                // 5. 获取设备
                log.info("3. 获取已连接的设备...");
                IDevice[] devices = bridge.getDevices();
                log.info("   找到 {} 个设备", devices.length);

                for (IDevice device : devices) {
                    log.info("   - {} [{}] {}",
                            device.getSerialNumber(),
                            device.getState(),
                            device.isOnline() ? "(在线)" : "(离线)");

                    // 更新设备缓存
                    if (device.isOnline()) {
                        connectedDevices.put(device.getSerialNumber(), device);
                        // 初始化屏幕分辨率
                        initScreenSize(device);
                    }
                }

                if (connectedDevices.isEmpty()) {
                    log.warn("没有找到在线的设备，请确保设备已开启无线调试");
                }
            } else {
                log.info("未配置远程设备地址，跳过自动连接");
                log.info("可通过以下方式配置：");
                log.info("  - 环境变量: ANDROID_DEVICE_HOST=<设备IP>");
                log.info("  - 配置文件: android.device.host=<设备IP>");
            }

            initialized = true;
            log.info("Android Debug Bridge 初始化成功");
        } catch (Exception e) {
            log.error("初始化 Android Debug Bridge 失败", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (bridge != null) {
                AndroidDebugBridge.disconnectBridge();
                AndroidDebugBridge.terminate();
            }
            log.info("Android Debug Bridge 已关闭");
        } catch (Exception e) {
            log.error("关闭 Android Debug Bridge 失败", e);
        }
    }

    /**
     * 查找 adb 可执行文件路径
     */
    private String findAdbPath() {
        // 1. 检查 ANDROID_HOME 环境变量
        String androidHome = System.getenv("ANDROID_HOME");
        if (androidHome != null) {
            String adbPath = androidHome + "/platform-tools/adb";
            if (new File(adbPath).exists()) {
                return adbPath;
            }
        }

        // 2. 检查 ANDROID_SDK_ROOT 环境变量
        String androidSdkRoot = System.getenv("ANDROID_SDK_ROOT");
        if (androidSdkRoot != null) {
            String adbPath = androidSdkRoot + "/platform-tools/adb";
            if (new File(adbPath).exists()) {
                return adbPath;
            }
        }

        // 3. 尝试在 PATH 中查找
        try {
            ProcessBuilder pb = new ProcessBuilder("which", "adb");
            Process process = pb.start();
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
            String path = reader.readLine();
            if (path != null && !path.isEmpty() && new File(path).exists()) {
                return path;
            }
        } catch (Exception ignored) {
        }

        // 4. 常见安装位置
        String[] commonPaths = {
            "/usr/local/bin/adb",
            System.getProperty("user.home") + "/Library/Android/sdk/platform-tools/adb",
            System.getProperty("user.home") + "/Android/Sdk/platform-tools/adb"
        };

        for (String path : commonPaths) {
            if (new File(path).exists()) {
                return path;
            }
        }

        return null;
    }

    /**
     * 等待设备列表加载
     */
    private void waitForDeviceList() {
        int timeout = 5000;
        int waited = 0;
        while (!bridge.hasInitialDeviceList() && waited < timeout) {
            try {
                Thread.sleep(100);
                waited += 100;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 连接到远程 Android 设备
     *
     * @param host 设备 IP 地址
     * @param port 端口号（默认 5555）
     * @return 连接结果
     */
    public Flux<String> connect(String host, int port) {
        return Flux.create(sink -> {
            try {
                if (!initialized) {
                    sink.next("错误: ADB 未初始化");
                    sink.complete();
                    return;
                }

                String address = host + ":" + port;
                log.info("正在连接到设备: {}", address);

                // 使用 adb connect 命令连接远程设备
                ShellOutputReceiver receiver = new ShellOutputReceiver();

                // 需要通过系统命令执行 adb connect
                ProcessBuilder pb = new ProcessBuilder(findAdbPath(), "connect", address);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                int exitCode = process.waitFor();
                String result = output.toString().trim();

                if (exitCode == 0 && (result.contains("connected") || result.contains("already connected"))) {
                    // 等待设备出现在设备列表中
                    Thread.sleep(1000);

                    // 刷新设备列表
                    IDevice[] devices = bridge.getDevices();
                    for (IDevice device : devices) {
                        if (device.getSerialNumber().equals(address) ||
                            device.getSerialNumber().contains(host)) {
                            connectedDevices.put(address, device);
                            // 初始化屏幕分辨率
                            initScreenSize(device);
                            log.info("成功连接到设备: {}", address);
                            sink.next("成功连接到设备: " + address);
                            sink.complete();
                            return;
                        }
                    }

                    sink.next("已发送连接请求: " + result);
                } else {
                    sink.next("连接失败: " + result);
                }
                sink.complete();
            } catch (Exception e) {
                log.error("连接设备失败", e);
                sink.next("连接设备异常: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * 断开设备连接
     */
    public Flux<String> disconnect(String address) {
        return Flux.create(sink -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(findAdbPath(), "disconnect", address);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                process.waitFor();
                connectedDevices.remove(address);

                sink.next("断开连接: " + output.toString().trim());
                sink.complete();
            } catch (Exception e) {
                log.error("断开连接失败", e);
                sink.next("断开连接异常: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * 获取当前连接的设备列表
     */
    public Flux<List<String>> getDevices() {
        return Flux.create(sink -> {
            try {
                if (!initialized || bridge == null) {
                    sink.next(new ArrayList<>());
                    sink.complete();
                    return;
                }

                IDevice[] devices = bridge.getDevices();
                List<String> deviceList = new ArrayList<>();
                for (IDevice device : devices) {
                    String info = String.format("%s [%s] - %s",
                        device.getSerialNumber(),
                        device.getState().name(),
                        device.isOnline() ? "在线" : "离线");
                    deviceList.add(info);

                    // 更新设备缓存
                    if (device.isOnline()) {
                        connectedDevices.put(device.getSerialNumber(), device);
                    }
                }

                sink.next(deviceList);
                sink.complete();
            } catch (Exception e) {
                log.error("获取设备列表失败", e);
                sink.next(new ArrayList<>());
                sink.complete();
            }
        });
    }

    /**
     * 获取指定设备（优先使用指定设备，否则使用第一个在线设备）
     */
    private IDevice getDevice(String deviceSerial) {
        if (bridge == null) {
            return null;
        }

        IDevice[] devices = bridge.getDevices();

        // 如果指定了设备序列号
        if (deviceSerial != null && !deviceSerial.isEmpty()) {
            for (IDevice device : devices) {
                if (device.getSerialNumber().equals(deviceSerial) && device.isOnline()) {
                    return device;
                }
            }
            // 尝试从缓存获取
            return connectedDevices.get(deviceSerial);
        }

        // 返回第一个在线设备
        for (IDevice device : devices) {
            if (device.isOnline()) {
                return device;
            }
        }

        return null;
    }

    /**
     * 在指定坐标执行点击操作
     *
     * @param x X 坐标
     * @param y Y 坐标
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> tap(int x, int y, String deviceSerial) {
        return executeShellCommand(
            String.format("input tap %d %d", x, y),
            deviceSerial,
            String.format("成功在坐标 (%d, %d) 执行点击", x, y)
        );
    }

    /**
     * 在指定坐标执行长按操作
     * 对应 Action: long_press(point='<point>x1 y1</point>')
     *
     * @param x X 坐标
     * @param y Y 坐标
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> longPress(int x, int y, String deviceSerial) {
        return longPress(x, y, 1000, deviceSerial);
    }

    /**
     * 在指定坐标执行长按操作（可指定时长）
     *
     * @param x X 坐标
     * @param y Y 坐标
     * @param durationMs 长按持续时间（毫秒）
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> longPress(int x, int y, int durationMs, String deviceSerial) {
        // 长按通过 swipe 实现，起点和终点相同
        return executeShellCommand(
            String.format("input swipe %d %d %d %d %d", x, y, x, y, durationMs),
            deviceSerial,
            String.format("成功在坐标 (%d, %d) 执行长按 %dms", x, y, durationMs)
        );
    }

    /**
     * 在指定坐标向指定方向滚动
     * 对应 Action: scroll(point='<point>x1 y1</point>', direction='down or up or right or left')
     *
     * @param x 起始 X 坐标
     * @param y 起始 Y 坐标
     * @param direction 滚动方向: up, down, left, right
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> scroll(int x, int y, String direction, String deviceSerial) {
        return scroll(x, y, direction, 300, 300, deviceSerial);
    }

    /**
     * 在指定坐标向指定方向滚动（可指定距离和时长）
     *
     * @param x 起始 X 坐标
     * @param y 起始 Y 坐标
     * @param direction 滚动方向: up, down, left, right
     * @param distance 滚动距离（像素）
     * @param durationMs 滚动持续时间（毫秒）
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> scroll(int x, int y, String direction, int distance, int durationMs, String deviceSerial) {
        int endX = x;
        int endY = y;

        switch (direction.toLowerCase()) {
            case "up":
                // 向上滚动：手指向上滑，内容向下移动
                endY = y - distance;
                break;
            case "down":
                // 向下滚动：手指向下滑，内容向上移动
                endY = y + distance;
                break;
            case "left":
                // 向左滚动：手指向左滑
                endX = x - distance;
                break;
            case "right":
                // 向右滚动：手指向右滑
                endX = x + distance;
                break;
            default:
                return Flux.just("错误: 不支持的滚动方向 '" + direction + "'，支持: up, down, left, right");
        }

        return executeShellCommand(
            String.format("input swipe %d %d %d %d %d", x, y, endX, endY, durationMs),
            deviceSerial,
            String.format("成功在坐标 (%d, %d) 向 %s 滚动", x, y, direction)
        );
    }

    /**
     * 拖拽操作
     * 对应 Action: drag(start_point='<point>x1 y1</point>', end_point='<point>x2 y2</point>')
     *
     * @param startX 起始 X 坐标
     * @param startY 起始 Y 坐标
     * @param endX 结束 X 坐标
     * @param endY 结束 Y 坐标
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> drag(int startX, int startY, int endX, int endY, String deviceSerial) {
        return drag(startX, startY, endX, endY, 500, deviceSerial);
    }

    /**
     * 拖拽操作（可指定时长）
     *
     * @param startX 起始 X 坐标
     * @param startY 起始 Y 坐标
     * @param endX 结束 X 坐标
     * @param endY 结束 Y 坐标
     * @param durationMs 拖拽持续时间（毫秒）
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> drag(int startX, int startY, int endX, int endY, int durationMs, String deviceSerial) {
        return executeShellCommand(
            String.format("input swipe %d %d %d %d %d", startX, startY, endX, endY, durationMs),
            deviceSerial,
            String.format("成功执行拖拽: (%d,%d) -> (%d,%d)", startX, startY, endX, endY)
        );
    }

    /**
     * 通过应用名称打开应用
     * 对应 Action: open_app(app_name='')
     * 支持常见应用的中英文名称，如：微信、wechat、qq、抖音 等
     *
     * @param appName 应用名称（支持中英文别名）
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> openApp(String appName, String deviceSerial) {
        // 尝试从映射表中查找包名
        String packageName = APP_NAME_TO_PACKAGE.get(appName.toLowerCase());
        if (packageName == null) {
            // 尝试原始名称（区分大小写）
            packageName = APP_NAME_TO_PACKAGE.get(appName);
        }

        if (packageName != null) {
            log.info("通过应用名称 '{}' 找到包名: {}", appName, packageName);
            return launchApp(packageName, deviceSerial);
        }

        // 如果映射表中没有，尝试将 appName 当作包名直接使用
        if (appName.contains(".")) {
            log.info("将 '{}' 作为包名尝试启动", appName);
            return launchApp(appName, deviceSerial);
        }

        // 尝试通过 pm 命令搜索包名
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next("错误: 没有可用的设备");
                    sink.complete();
                    return;
                }

                // 搜索包含该名称的包
                ShellOutputReceiver receiver = new ShellOutputReceiver();
                device.executeShellCommand(
                    "pm list packages | grep -i " + appName,
                    receiver,
                    COMMAND_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS
                );

                String output = receiver.getOutput().trim();
                if (!output.isEmpty()) {
                    // 取第一个匹配的包名
                    String[] lines = output.split("\n");
                    if (lines.length > 0) {
                        String foundPackage = lines[0].replace("package:", "").trim();
                        log.info("通过搜索找到包名: {}", foundPackage);
                        launchApp(foundPackage, deviceSerial)
                            .subscribe(sink::next, sink::error, sink::complete);
                        return;
                    }
                }

                sink.next("错误: 无法找到应用 '" + appName + "'，请确认应用名称或使用包名");
                sink.complete();
            } catch (Exception e) {
                log.error("打开应用失败", e);
                sink.next("打开应用失败: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * 输入文字
     *
     * @param text 要输入的文字
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> inputText(String text, String deviceSerial) {
        // 对特殊字符进行转义
        String escapedText = text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("'", "\\'")
            .replace(" ", "%s")
            .replace("&", "\\&")
            .replace("<", "\\<")
            .replace(">", "\\>")
            .replace("|", "\\|")
            .replace(";", "\\;")
            .replace("(", "\\(")
            .replace(")", "\\)");

        return executeShellCommand(
            String.format("input text \"%s\"", escapedText),
            deviceSerial,
            "成功输入文字: " + (text.length() > 20 ? text.substring(0, 20) + "..." : text)
        );
    }

    /**
     * 使用 ADB Keyboard 输入文字（支持中文和特殊字符）
     * 需要设备安装 ADBKeyboard 应用
     *
     * @param text 要输入的文字
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> inputTextWithAdbKeyboard(String text, String deviceSerial) {
        // 使用 ADB Keyboard 的广播方式输入中文
        String base64Text = Base64.getEncoder().encodeToString(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return executeShellCommand(
            String.format("am broadcast -a ADB_INPUT_B64 --es msg %s", base64Text),
            deviceSerial,
            "成功通过 ADB Keyboard 输入文字: " + (text.length() > 20 ? text.substring(0, 20) + "..." : text)
        );
    }

    /**
     * 滑动/滚屏操作
     *
     * @param startX 起始 X 坐标
     * @param startY 起始 Y 坐标
     * @param endX 结束 X 坐标
     * @param endY 结束 Y 坐标
     * @param durationMs 滑动持续时间（毫秒）
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> swipe(int startX, int startY, int endX, int endY, int durationMs, String deviceSerial) {
        return executeShellCommand(
            String.format("input swipe %d %d %d %d %d", startX, startY, endX, endY, durationMs),
            deviceSerial,
            String.format("成功执行滑动: (%d,%d) -> (%d,%d)", startX, startY, endX, endY)
        );
    }

    /**
     * 向上滚动
     */
    public Flux<String> scrollUp(String deviceSerial) {
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next("错误: 没有可用的设备");
                    sink.complete();
                    return;
                }

                // 获取屏幕尺寸
                int[] size = getScreenSize(device);
                int centerX = size[0] / 2;
                int startY = size[1] * 2 / 3;
                int endY = size[1] / 3;

                swipe(centerX, startY, centerX, endY, 300, deviceSerial)
                    .subscribe(sink::next, sink::error, sink::complete);
            } catch (Exception e) {
                log.error("向上滚动失败", e);
                sink.next("向上滚动失败: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * 向下滚动
     */
    public Flux<String> scrollDown(String deviceSerial) {
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next("错误: 没有可用的设备");
                    sink.complete();
                    return;
                }

                // 获取屏幕尺寸
                int[] size = getScreenSize(device);
                int centerX = size[0] / 2;
                int startY = size[1] / 3;
                int endY = size[1] * 2 / 3;

                swipe(centerX, startY, centerX, endY, 300, deviceSerial)
                    .subscribe(sink::next, sink::error, sink::complete);
            } catch (Exception e) {
                log.error("向下滚动失败", e);
                sink.next("向下滚动失败: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * 获取屏幕尺寸
     */
    private int[] getScreenSize(IDevice device) {
        try {
            ShellOutputReceiver receiver = new ShellOutputReceiver();
            device.executeShellCommand("wm size", receiver, COMMAND_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            String output = receiver.getOutput();
            // 解析输出，格式如: "Physical size: 1080x2400"
            if (output.contains("x")) {
                String[] parts = output.split(":");
                if (parts.length > 1) {
                    String sizePart = parts[parts.length - 1].trim();
                    String[] dimensions = sizePart.split("x");
                    if (dimensions.length == 2) {
                        return new int[]{
                            Integer.parseInt(dimensions[0].trim()),
                            Integer.parseInt(dimensions[1].trim())
                        };
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取屏幕尺寸失败，使用默认值", e);
        }
        // 默认尺寸
        return new int[]{1080, 1920};
    }

    /**
     * 初始化屏幕分辨率到 AndroidResponseParser
     * 在设备连接成功后调用，确保坐标转换使用正确的屏幕尺寸
     *
     * @param device 设备对象
     */
    private void initScreenSize(IDevice device) {
        try {
            int[] size = getScreenSize(device);
            AndroidResponseParser.setScreenSize(size[0], size[1]);
            log.info("已初始化屏幕分辨率: {}x{}", size[0], size[1]);
        } catch (Exception e) {
            log.warn("初始化屏幕分辨率失败，使用默认值", e);
        }
    }

    /**
     * 截取设备屏幕
     *
     * @param filePath 保存路径（可选）
     * @param deviceSerial 设备序列号（可选）
     * @return 保存的文件路径或 base64 编码
     */
    public Flux<String> screenshot(String filePath, String deviceSerial) {
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next("错误: 没有可用的设备");
                    sink.complete();
                    return;
                }

                log.info("正在截取设备屏幕...");

                // 使用 ddmlib 的截图功能
                RawImage rawImage = device.getScreenshot();
                if (rawImage == null) {
                    sink.next("错误: 无法获取屏幕截图");
                    sink.complete();
                    return;
                }

                // 转换为 BufferedImage
                BufferedImage image = new BufferedImage(
                    rawImage.width, rawImage.height, BufferedImage.TYPE_INT_ARGB);

                int index = 0;
                int bytesPerPixel = rawImage.bpp >> 3;
                for (int y = 0; y < rawImage.height; y++) {
                    for (int x = 0; x < rawImage.width; x++) {
                        int value = rawImage.getARGB(index);
                        image.setRGB(x, y, value);
                        index += bytesPerPixel;
                    }
                }

                // 确定保存路径
                String savePath = filePath;
                if (savePath == null || savePath.isEmpty()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String timestamp = dateFormat.format(new Date());
                    String fileName = "android_screenshot_" + timestamp + "_" +
                                     UUID.randomUUID().toString().substring(0, 8) + ".png";

                    String userHome = System.getProperty("user.home");
                    File screenshotDir = new File(userHome, "Screenshots");
                    if (!screenshotDir.exists()) {
                        screenshotDir.mkdirs();
                    }

                    savePath = new File(screenshotDir, fileName).getAbsolutePath();
                }

                // 确保扩展名
                if (!savePath.toLowerCase().endsWith(".png")) {
                    savePath = savePath + ".png";
                }

                // 保存图片
                File outputFile = new File(savePath);
                File parentDir = outputFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }

                ImageIO.write(image, "png", outputFile);

                log.info("截图保存成功: {}", savePath);
                sink.next(savePath);
                sink.complete();
            } catch (Exception e) {
                log.error("截图失败", e);
                sink.next("截图失败: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * 截图并返回 Base64 编码
     *
     * @param deviceSerial 设备序列号（可选）
     * @return Base64 编码的图片
     */
    public Flux<String> screenshotBase64(String deviceSerial) {
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next("错误: 没有可用的设备");
                    sink.complete();
                    return;
                }

                RawImage rawImage = device.getScreenshot();
                if (rawImage == null) {
                    sink.next("错误: 无法获取屏幕截图");
                    sink.complete();
                    return;
                }

                // 转换为 BufferedImage
                BufferedImage image = new BufferedImage(
                    rawImage.width, rawImage.height, BufferedImage.TYPE_INT_ARGB);

                int index = 0;
                int bytesPerPixel = rawImage.bpp >> 3;
                for (int y = 0; y < rawImage.height; y++) {
                    for (int x = 0; x < rawImage.width; x++) {
                        int value = rawImage.getARGB(index);
                        image.setRGB(x, y, value);
                        index += bytesPerPixel;
                    }
                }

                // 转为 Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

                sink.next(base64);
                sink.complete();
            } catch (Exception e) {
                log.error("截图失败", e);
                sink.next("截图失败: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * 获取当前输入法列表
     *
     * @param deviceSerial 设备序列号（可选）
     * @return 输入法列表
     */
    public Flux<List<String>> listInputMethods(String deviceSerial) {
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next(new ArrayList<>());
                    sink.complete();
                    return;
                }

                ShellOutputReceiver receiver = new ShellOutputReceiver();
                device.executeShellCommand("ime list -s", receiver, COMMAND_TIMEOUT_MS, TimeUnit.MILLISECONDS);

                String output = receiver.getOutput();
                List<String> imes = new ArrayList<>();
                for (String line : output.split("\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        imes.add(trimmed);
                    }
                }

                sink.next(imes);
                sink.complete();
            } catch (Exception e) {
                log.error("获取输入法列表失败", e);
                sink.next(new ArrayList<>());
                sink.complete();
            }
        });
    }

    /**
     * 切换输入法
     *
     * @param imeId 输入法 ID（如 com.android.inputmethod.latin/.LatinIME）
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> setInputMethod(String imeId, String deviceSerial) {
        return executeShellCommand(
            "ime set " + imeId,
            deviceSerial,
            "成功切换输入法为: " + imeId
        );
    }

    /**
     * 启用输入法
     *
     * @param imeId 输入法 ID
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> enableInputMethod(String imeId, String deviceSerial) {
        return executeShellCommand(
            "ime enable " + imeId,
            deviceSerial,
            "成功启用输入法: " + imeId
        );
    }

    /**
     * 禁用输入法
     *
     * @param imeId 输入法 ID
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> disableInputMethod(String imeId, String deviceSerial) {
        return executeShellCommand(
            "ime disable " + imeId,
            deviceSerial,
            "成功禁用输入法: " + imeId
        );
    }

    /**
     * 获取当前使用的输入法
     *
     * @param deviceSerial 设备序列号（可选）
     * @return 当前输入法 ID
     */
    public Flux<String> getCurrentInputMethod(String deviceSerial) {
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next("错误: 没有可用的设备");
                    sink.complete();
                    return;
                }

                ShellOutputReceiver receiver = new ShellOutputReceiver();
                device.executeShellCommand(
                    "settings get secure default_input_method",
                    receiver,
                    COMMAND_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS
                );

                String output = receiver.getOutput().trim();
                sink.next(output);
                sink.complete();
            } catch (Exception e) {
                log.error("获取当前输入法失败", e);
                sink.next("获取当前输入法失败: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * ADB Keyboard 输入法 ID
     */
    private static final String ADB_KEYBOARD_IME = "com.android.adbkeyboard/.AdbIME";

    /**
     * 常见应用名称到包名的映射
     */
    private static final Map<String, String> APP_NAME_TO_PACKAGE = new HashMap<>() {{
        // 社交通讯
        put("微信", "com.tencent.mm");
        put("wechat", "com.tencent.mm");
        put("qq", "com.tencent.mobileqq");
        put("QQ", "com.tencent.mobileqq");
        put("钉钉", "com.alibaba.android.rimet");
        put("dingtalk", "com.alibaba.android.rimet");
        put("飞书", "com.ss.android.lark");
        put("lark", "com.ss.android.lark");
        put("企业微信", "com.tencent.wework");
        put("whatsapp", "com.whatsapp");
        put("telegram", "org.telegram.messenger");

        // 浏览器
        put("chrome", "com.android.chrome");
        put("谷歌浏览器", "com.android.chrome");
        put("firefox", "org.mozilla.firefox");
        put("火狐", "org.mozilla.firefox");
        put("edge", "com.microsoft.emmx");
        put("浏览器", "com.android.browser");

        // 视频娱乐
        put("抖音", "com.ss.android.ugc.aweme");
        put("douyin", "com.ss.android.ugc.aweme");
        put("tiktok", "com.zhiliaoapp.musically");
        put("快手", "com.smile.gifmaker");
        put("bilibili", "tv.danmaku.bili");
        put("b站", "tv.danmaku.bili");
        put("哔哩哔哩", "tv.danmaku.bili");
        put("youtube", "com.google.android.youtube");
        put("爱奇艺", "com.qiyi.video");
        put("优酷", "com.youku.phone");
        put("腾讯视频", "com.tencent.qqlive");

        // 购物
        put("淘宝", "com.taobao.taobao");
        put("taobao", "com.taobao.taobao");
        put("京东", "com.jingdong.app.mall");
        put("jd", "com.jingdong.app.mall");
        put("拼多多", "com.xunmeng.pinduoduo");
        put("pinduoduo", "com.xunmeng.pinduoduo");
        put("美团", "com.sankuai.meituan");
        put("饿了么", "me.ele");

        // 出行
        put("高德地图", "com.autonavi.minimap");
        put("amap", "com.autonavi.minimap");
        put("百度地图", "com.baidu.BaiduMap");
        put("滴滴", "com.sdu.didi.psnger");
        put("didi", "com.sdu.didi.psnger");

        // 支付
        put("支付宝", "com.eg.android.AlipayGphone");
        put("alipay", "com.eg.android.AlipayGphone");

        // 音乐
        put("网易云音乐", "com.netease.cloudmusic");
        put("cloudmusic", "com.netease.cloudmusic");
        put("qq音乐", "com.tencent.qqmusic");
        put("酷狗音乐", "com.kugou.android");
        put("spotify", "com.spotify.music");

        // 工具
        put("相机", "com.android.camera");
        put("camera", "com.android.camera");
        put("相册", "com.android.gallery3d");
        put("gallery", "com.android.gallery3d");
        put("设置", "com.android.settings");
        put("settings", "com.android.settings");
        put("时钟", "com.android.deskclock");
        put("clock", "com.android.deskclock");
        put("计算器", "com.android.calculator2");
        put("calculator", "com.android.calculator2");
        put("日历", "com.android.calendar");
        put("calendar", "com.android.calendar");
        put("文件管理", "com.android.filemanager");
        put("通讯录", "com.android.contacts");
        put("contacts", "com.android.contacts");
        put("电话", "com.android.dialer");
        put("phone", "com.android.dialer");
        put("短信", "com.android.mms");
        put("messages", "com.android.mms");
    }};

    /**
     * 使用 ADB Keyboard 输入文字的完整流程
     * 操作步骤：
     * 1. 查询当前默认输入法
     * 2. 切换到 ADB Keyboard 输入法
     * 3. 输入内容
     * 4. 切换回原来的输入法
     *
     * @param text 要输入的文字（支持中文和特殊字符）
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> inputTextWithImeSwitching(String text, String deviceSerial) {
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next("错误: 没有可用的设备");
                    sink.complete();
                    return;
                }

                // 1. 获取当前默认输入法
                ShellOutputReceiver receiver = new ShellOutputReceiver();
                device.executeShellCommand(
                    "settings get secure default_input_method",
                    receiver,
                    COMMAND_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS
                );
                String originalIme = receiver.getOutput().trim();
                log.info("当前输入法: {}", originalIme);

                // 2. 切换到 ADB Keyboard 输入法
                receiver = new ShellOutputReceiver();
                device.executeShellCommand(
                    "ime set " + ADB_KEYBOARD_IME,
                    receiver,
                    COMMAND_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS
                );
                log.info("已切换到 ADB Keyboard 输入法");

                // 等待输入法切换完成
                Thread.sleep(200);

                // 3. 使用 ADB Keyboard 输入文字
                String base64Text = Base64.getEncoder().encodeToString(
                    text.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                );
                receiver = new ShellOutputReceiver();
                device.executeShellCommand(
                    String.format("am broadcast -a ADB_INPUT_B64 --es msg %s", base64Text),
                    receiver,
                    COMMAND_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS
                );
                log.info("已输入文字: {}", text.length() > 20 ? text.substring(0, 20) + "..." : text);

                // 等待输入完成
                Thread.sleep(200);

                // 4. 切换回原来的输入法
                if (originalIme != null && !originalIme.isEmpty() && !originalIme.equals("null")) {
                    receiver = new ShellOutputReceiver();
                    device.executeShellCommand(
                        "ime set " + originalIme,
                        receiver,
                        COMMAND_TIMEOUT_MS,
                        TimeUnit.MILLISECONDS
                    );
                    log.info("已切换回原输入法: {}", originalIme);
                }

                sink.next("成功输入文字: " + (text.length() > 20 ? text.substring(0, 20) + "..." : text));
                sink.complete();
            } catch (Exception e) {
                log.error("输入文字失败", e);
                sink.next("输入文字失败: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * 按下返回键
     */
    public Flux<String> pressBack(String deviceSerial) {
        return executeShellCommand("input keyevent KEYCODE_BACK", deviceSerial, "成功按下返回键");
    }

    /**
     * 按下 Home 键
     */
    public Flux<String> pressHome(String deviceSerial) {
        return executeShellCommand("input keyevent KEYCODE_HOME", deviceSerial, "成功按下 Home 键");
    }

    /**
     * 按下最近任务键
     */
    public Flux<String> pressRecent(String deviceSerial) {
        return executeShellCommand("input keyevent KEYCODE_APP_SWITCH", deviceSerial, "成功按下最近任务键");
    }

    /**
     * 按下电源键
     */
    public Flux<String> pressPower(String deviceSerial) {
        return executeShellCommand("input keyevent KEYCODE_POWER", deviceSerial, "成功按下电源键");
    }

    /**
     * 获取设备上已安装的所有应用列表
     *
     * @param deviceSerial 设备序列号（可选）
     * @param includeSystemApps 是否包含系统应用
     * @return 应用包名列表
     */
    public Flux<List<String>> listInstalledApps(String deviceSerial, boolean includeSystemApps) {
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next(new ArrayList<>());
                    sink.complete();
                    return;
                }

                ShellOutputReceiver receiver = new ShellOutputReceiver();
                // pm list packages: -3 仅第三方应用，不加参数则包含所有应用
                String command = includeSystemApps ? "pm list packages" : "pm list packages -3";
                device.executeShellCommand(command, receiver, COMMAND_TIMEOUT_MS, TimeUnit.MILLISECONDS);

                String output = receiver.getOutput();
                List<String> apps = new ArrayList<>();
                for (String line : output.split("\n")) {
                    String trimmed = line.trim();
                    // 格式为 "package:com.example.app"
                    if (trimmed.startsWith("package:")) {
                        apps.add(trimmed.substring(8));
                    }
                }

                // 排序
                Collections.sort(apps);

                sink.next(apps);
                sink.complete();
            } catch (Exception e) {
                log.error("获取应用列表失败", e);
                sink.next(new ArrayList<>());
                sink.complete();
            }
        });
    }

    /**
     * 获取应用详细信息
     *
     * @param packageName 应用包名
     * @param deviceSerial 设备序列号（可选）
     * @return 应用信息
     */
    public Flux<String> getAppInfo(String packageName, String deviceSerial) {
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next("错误: 没有可用的设备");
                    sink.complete();
                    return;
                }

                ShellOutputReceiver receiver = new ShellOutputReceiver();
                device.executeShellCommand("dumpsys package " + packageName + " | head -50", receiver, COMMAND_TIMEOUT_MS, TimeUnit.MILLISECONDS);

                String output = receiver.getOutput();
                sink.next(output);
                sink.complete();
            } catch (Exception e) {
                log.error("获取应用信息失败", e);
                sink.next("获取应用信息失败: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * 启动应用
     *
     * @param packageName 应用包名
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> launchApp(String packageName, String deviceSerial) {
        return executeShellCommand(
            "monkey -p " + packageName + " -c android.intent.category.LAUNCHER 1",
            deviceSerial,
            "成功启动应用: " + packageName
        );
    }

    /**
     * 强制停止应用
     *
     * @param packageName 应用包名
     * @param deviceSerial 设备序列号（可选）
     * @return 操作结果
     */
    public Flux<String> forceStopApp(String packageName, String deviceSerial) {
        return executeShellCommand(
            "am force-stop " + packageName,
            deviceSerial,
            "成功停止应用: " + packageName
        );
    }

    /**
     * 执行 shell 命令
     */
    private Flux<String> executeShellCommand(String command, String deviceSerial, String successMessage) {
        return Flux.create(sink -> {
            try {
                IDevice device = getDevice(deviceSerial);
                if (device == null) {
                    sink.next("错误: 没有可用的设备");
                    sink.complete();
                    return;
                }

                log.debug("执行命令: {}", command);
                ShellOutputReceiver receiver = new ShellOutputReceiver();
                device.executeShellCommand(command, receiver, COMMAND_TIMEOUT_MS, TimeUnit.MILLISECONDS);

                String output = receiver.getOutput();
                if (output != null && !output.isEmpty() && output.toLowerCase().contains("error")) {
                    sink.next("命令执行出错: " + output);
                } else {
                    sink.next(successMessage);
                }
                sink.complete();
            } catch (Exception e) {
                log.error("执行命令失败: {}", command, e);
                sink.next("命令执行失败: " + e.getMessage());
                sink.complete();
            }
        });
    }

    /**
     * Shell 输出接收器
     */
    private static class ShellOutputReceiver implements IShellOutputReceiver {
        private final StringBuilder output = new StringBuilder();
        private volatile boolean cancelled = false;

        @Override
        public void addOutput(byte[] data, int offset, int length) {
            output.append(new String(data, offset, length));
        }

        @Override
        public void flush() {
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        public void cancel() {
            this.cancelled = true;
        }

        public String getOutput() {
            return output.toString();
        }
    }
}
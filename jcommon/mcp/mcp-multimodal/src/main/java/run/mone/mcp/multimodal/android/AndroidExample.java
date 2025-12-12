package run.mone.mcp.multimodal.android;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.RawImage;
import lombok.Data;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.mcp.multimodal.config.Prompt;

import com.google.common.collect.Lists;
import reactor.core.publisher.Flux;
import run.mone.hive.schema.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Android 设备连接和截图示例
 *
 * 使用前请确保：
 * 1. 已安装 Android SDK 并配置 ANDROID_HOME 环境变量
 * 2. 目标设备已开启 USB 调试或无线调试
 * 3. 对于无线调试，需要先通过 USB 连接并执行 adb tcpip 5555
 */
public class AndroidExample {

    public static void main(String[] args) {
        // 远程设备地址（根据实际情况修改）
        String deviceHost = System.getenv("deviceHost");
        int devicePort = 37369;

        // 如果命令行传入参数
        if (args.length >= 1) {
            deviceHost = args[0];
        }
        if (args.length >= 2) {
            devicePort = Integer.parseInt(args[1]);
        }

        System.out.println("=== Android 设备连接和截图示例 ===\n");

        AndroidExample example = new AndroidExample();
        example.run(deviceHost, devicePort);
    }

    public void run(String host, int port) {
        AndroidDebugBridge bridge = null;

        try {
            // 1. 初始化 ADB
            System.out.println("1. 初始化 Android Debug Bridge...");
            AndroidDebugBridge.init(false);

            // 2. 查找 adb 路径
            String adbPath = findAdbPath();
            if (adbPath == null) {
                System.err.println("错误: 无法找到 adb，请确保 ANDROID_HOME 已设置");
                return;
            }
            System.out.println("   ADB 路径: " + adbPath);

            // 3. 创建 Bridge
            bridge = AndroidDebugBridge.createBridge(adbPath, false);
            waitForDeviceList(bridge);

            // 4. 连接远程设备
            String address = host + ":" + port;
            System.out.println("\n2. 连接到远程设备: " + address);

            ProcessBuilder pb = new ProcessBuilder(adbPath, "connect", address);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("   " + line);
            }
            process.waitFor();

            // 等待设备连接
            Thread.sleep(2000);

            // 5. 获取设备
            System.out.println("\n3. 获取已连接的设备...");
            IDevice[] devices = bridge.getDevices();
            System.out.println("   找到 " + devices.length + " 个设备");

            IDevice targetDevice = null;
            for (IDevice device : devices) {
                System.out.println("   - " + device.getSerialNumber() +
                    " [" + device.getState() + "]" +
                    (device.isOnline() ? " (在线)" : " (离线)"));

                if (device.isOnline() && targetDevice == null) {
                    targetDevice = device;
                }
            }

            if (targetDevice == null) {
                System.err.println("\n错误: 没有找到在线的设备");
                System.out.println("\n提示: 请确保设备已开启无线调试:");
                System.out.println("   1. USB 连接设备");
                System.out.println("   2. 执行: adb tcpip 5555");
                System.out.println("   3. 断开 USB，获取设备 IP");
                System.out.println("   4. 运行此程序: java AndroidExample <设备IP> 5555");
                return;
            }

            System.out.println("\n   使用设备: " + targetDevice.getSerialNumber());

            // 6. 获取设备信息
            System.out.println("\n4. 获取设备信息...");
            printDeviceInfo(targetDevice);

//            captureScreen(targetDevice);

            // 8. 获取已安装应用列表
//            System.out.println("\n6. 获取已安装应用列表...");
//            listInstalledApps(targetDevice, false); // false = 仅第三方应用

//            CoordinateResult res = getCoordinateByInstruction(targetDevice, "帮我点击微信");
//            CoordinateResult res = getCoordinateByInstruction(targetDevice, "帮我点击雹这个app");
//            CoordinateResult res = getCoordinateByInstruction(targetDevice, "帮我点击Clash");
            CoordinateResult res = getCoordinateByInstruction(targetDevice, "帮我点击小扫把");
//            CoordinateResult res = getCoordinateByInstruction(targetDevice, "帮我点击T这个群组");
//            CoordinateResult res = getCoordinateByInstruction(targetDevice, "帮我点击通讯录");
            System.out.println(res);

            // 9. 通过 ADB 执行点击操作
            if (res != null && res.getX() >= 0 && res.getY() >= 0) {
                System.out.println("\n--- 执行点击操作 ---");
                tapScreen(targetDevice, res.getX(), res.getY());
            }

            System.out.println("\n=== 完成 ===");

        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 清理
            if (bridge != null) {
                AndroidDebugBridge.disconnectBridge();
                AndroidDebugBridge.terminate();
            }
        }
    }

    private void captureScreen(IDevice targetDevice) {
        // 7. 截图
        System.out.println("\n5. 截取屏幕...");
        String screenshotPath = takeScreenshot(targetDevice);

        if (screenshotPath != null) {
            System.out.println("   截图保存成功: " + screenshotPath);
        } else {
            System.err.println("   截图失败");
        }
    }

    /**
     * 查找 adb 路径
     */
    private String findAdbPath() {
        // 检查 ANDROID_HOME
        String androidHome = System.getenv("ANDROID_HOME");
        if (androidHome != null) {
            String adbPath = androidHome + "/platform-tools/adb";
            if (new File(adbPath).exists()) {
                return adbPath;
            }
        }

        // 检查 ANDROID_SDK_ROOT
        String androidSdkRoot = System.getenv("ANDROID_SDK_ROOT");
        if (androidSdkRoot != null) {
            String adbPath = androidSdkRoot + "/platform-tools/adb";
            if (new File(adbPath).exists()) {
                return adbPath;
            }
        }

        // 常见路径
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
    private void waitForDeviceList(AndroidDebugBridge bridge) {
        int timeout = 5000;
        int waited = 0;
        while (!bridge.hasInitialDeviceList() && waited < timeout) {
            try {
                Thread.sleep(100);
                waited += 100;
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * 打印设备信息
     */
    private void printDeviceInfo(IDevice device) {
        try {
            // 获取屏幕尺寸
            ShellOutputReceiver sizeReceiver = new ShellOutputReceiver();
            device.executeShellCommand("wm size", sizeReceiver, 5000, TimeUnit.MILLISECONDS);
            System.out.println("   屏幕尺寸: " + sizeReceiver.getOutput().trim());

            // 获取 Android 版本
            ShellOutputReceiver versionReceiver = new ShellOutputReceiver();
            device.executeShellCommand("getprop ro.build.version.release", versionReceiver, 5000, TimeUnit.MILLISECONDS);
            System.out.println("   Android 版本: " + versionReceiver.getOutput().trim());

            // 获取设备型号
            ShellOutputReceiver modelReceiver = new ShellOutputReceiver();
            device.executeShellCommand("getprop ro.product.model", modelReceiver, 5000, TimeUnit.MILLISECONDS);
            System.out.println("   设备型号: " + modelReceiver.getOutput().trim());

        } catch (Exception e) {
            System.err.println("   获取设备信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取已安装应用列表并打印
     *
     * @param device 设备
     * @param includeSystemApps 是否包含系统应用
     */
    private void listInstalledApps(IDevice device, boolean includeSystemApps) {
        try {
            ShellOutputReceiver receiver = new ShellOutputReceiver();
            // pm list packages: -3 仅第三方应用，不加参数则包含所有应用
            String command = includeSystemApps ? "pm list packages" : "pm list packages -3";
            device.executeShellCommand(command, receiver, 10000, TimeUnit.MILLISECONDS);

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

            // 打印应用列表
            System.out.println("   " + (includeSystemApps ? "所有应用" : "第三方应用") + " (共 " + apps.size() + " 个):");
            System.out.println("   ----------------------------------------");
            for (int i = 0; i < apps.size(); i++) {
                System.out.println("   " + (i + 1) + ". " + apps.get(i));
            }
            System.out.println("   ----------------------------------------");

        } catch (Exception e) {
            System.err.println("   获取应用列表失败: " + e.getMessage());
        }
    }

    /**
     * 截取屏幕
     */
    private String takeScreenshot(IDevice device) {
        try {
            // 使用 ddmlib 获取原始图像
            RawImage rawImage = device.getScreenshot();
            if (rawImage == null) {
                System.err.println("   无法获取屏幕数据");
                return null;
            }

            System.out.println("   屏幕分辨率: " + rawImage.width + " x " + rawImage.height);
            System.out.println("   色彩深度: " + rawImage.bpp + " bpp");

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

            // 生成文件名
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String fileName = "android_screenshot_" + timestamp + "_" +
                             UUID.randomUUID().toString().substring(0, 8) + ".png";

            // 保存到用户目录的 Screenshots 文件夹
            String userHome = System.getProperty("user.home");
            File screenshotDir = new File(userHome, "Screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            File outputFile = new File(screenshotDir, fileName);
            ImageIO.write(image, "png", outputFile);

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            System.err.println("   截图异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Shell 输出接收器
     */
    private static class ShellOutputReceiver implements IShellOutputReceiver {
        private final StringBuilder output = new StringBuilder();

        @Override
        public void addOutput(byte[] data, int offset, int length) {
            output.append(new String(data, offset, length));
        }

        @Override
        public void flush() {
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public String getOutput() {
            return output.toString();
        }
    }

    /**
     * 坐标结果类
     */
    @Data
    public static class CoordinateResult {
        private int x;
        private int y;
        private String action;      // 动作类型: click, left_double, right_single, drag, scroll 等
        private String thought;     // AI 的思考过程
        private String rawResponse; // 原始响应

        public CoordinateResult(int x, int y, String action, String thought, String rawResponse) {
            this.x = x;
            this.y = y;
            this.action = action;
            this.thought = thought;
            this.rawResponse = rawResponse;
        }
    }

    /**
     * 根据用户指令，截屏并通过大模型获取目标元素的坐标
     *
     * @param device      Android 设备
     * @param instruction 用户指令，例如 "点击登录按钮"
     * @param llmConfig   LLM 配置
     * @return CoordinateResult 包含 x, y 坐标和动作类型
     */
    public CoordinateResult getCoordinateByInstruction(IDevice device, String instruction, LLMConfig llmConfig) {
        try {
            // 1. 截屏获取 base64 图片和图片尺寸
            System.out.println("正在截取屏幕...");
            ScreenshotResult screenshotResult = takeScreenshotWithSize(device);
            if (screenshotResult == null) {
                throw new RuntimeException("截屏失败");
            }
            System.out.println("截屏成功，图片尺寸: " + screenshotResult.width + " x " + screenshotResult.height);

            // 2. 获取 Android 设备的实际屏幕尺寸
            int[] deviceScreenSize = getScreenSize(device);
            System.out.println("设备屏幕尺寸: " + deviceScreenSize[0] + " x " + deviceScreenSize[1]);

            // 3. 构建系统提示词
            // 注意：提示词应该要求 AI 返回 0-1000 范围的相对坐标
            // 坐标系统：左上角 (0,0) 到 右下角 (1000,1000)
            String systemPrompt = Prompt.androidSystemPrompt
                    .replace("{language}", "Chinese")
                    .replace("{instruction}", instruction);

            // 4. 调用 LLM
            LLM llm = new LLM(llmConfig);

            // 使用 getLlmCompoundMsg 构建多模态消息
            LLM.LLMCompoundMsg msg = LLM.getLlmCompoundMsg(
                    instruction,
                    Message.builder()
                            .images(Lists.newArrayList(screenshotResult.base64Image))
                            .build()
            );
            msg.setImageType("png");

            System.out.println("正在调用大模型分析...");

            // 使用 compoundMsgCall 调用
            Flux<String> flux = llm.compoundMsgCall(msg, systemPrompt);
            StringBuilder sb = new StringBuilder();
            flux.doOnNext(chunk->{
                sb.append(chunk);
            }).collectList().block();

            String response = sb.toString();
            System.out.println("\n大模型响应完成:"+response);

            // 5. 解析响应获取相对坐标（0-1000 范围）
            CoordinateResult result = parseCoordinateFromResponse(response);
            if (result == null) {
                return null;
            }

            // 6. 坐标转换：从相对坐标（0-1000）转换为 Android 设备屏幕绝对坐标
            if (result.getX() >= 0 && result.getY() >= 0) {
                System.out.println("\n--- 坐标转换（Python 项目一致的方式） ---");
                System.out.println("相对坐标(0-1000): (" + result.getX() + ", " + result.getY() + ")");

                // 7. 转换为设备屏幕坐标
                Point absolutePoint = relativeToAbsoluteCoordinates(
                        new Point(result.getX(), result.getY()),
                        new Dimension(deviceScreenSize[0], deviceScreenSize[1])
                );

                System.out.println("设备屏幕坐标: (" + absolutePoint.x + ", " + absolutePoint.y + ")");

                // 8. 在原始图片上画红点（需要先将相对坐标转换为图像坐标）
                // 将相对坐标转换为图像像素坐标用于标记
                int imageX = (int) (result.getX() * screenshotResult.width / 1000.0);
                int imageY = (int) (result.getY() * screenshotResult.height / 1000.0);
                
//                String markedImagePath = drawRedDotOnImage(
//                        screenshotResult.getImagePath(),
//                        imageX,
//                        imageY
//                );
//                if (markedImagePath != null) {
//                    System.out.println("标记后的图片已保存到: " + markedImagePath);
//                }

                // 更新结果中的坐标为转换后的设备屏幕绝对坐标
                result.setX(absolutePoint.x);
                result.setY(absolutePoint.y);
            }

            return result;

        } catch (Exception e) {
            System.err.println("获取坐标失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 截图结果，包含 base64 图片和图片尺寸
     */
    @Data
    public static class ScreenshotResult {
        private String base64Image;
        private int width;
        private int height;
        private String imagePath;  // 保存到 /tmp 的图片路径

        public ScreenshotResult(String base64Image, int width, int height, String imagePath) {
            this.base64Image = base64Image;
            this.width = width;
            this.height = height;
            this.imagePath = imagePath;
        }
    }

    /**
     * 将相对坐标（0-1000）转换为 Android 设备屏幕的绝对坐标
     * 
     * 坐标系统说明：
     * - 输入坐标范围：(0,0) 左上角 到 (1000,1000) 右下角（相对坐标系统）
     * - 输出坐标范围：设备实际屏幕像素坐标
     * 
     * 此方法与 Python 项目中的 _convert_relative_to_absolute 方法保持一致
     *
     * @param relativeCoords 相对坐标 [x, y]，范围 0-1000
     * @param screenSize     Android 设备屏幕尺寸 [width, height]
     * @return 转换后的 Android 设备屏幕绝对坐标 [x, y]
     */
    public static Point relativeToAbsoluteCoordinates(Point relativeCoords, Dimension screenSize) {
        // Python 代码中的转换逻辑：
        // x = int(element[0] / 1000 * screen_width)
        // y = int(element[1] / 1000 * screen_height)
        
        int screenX = (int) (relativeCoords.x / 1000.0 * screenSize.width);
        int screenY = (int) (relativeCoords.y / 1000.0 * screenSize.height);

        return new Point(screenX, screenY);
    }

    /**
     * 将图像像素坐标转换为相对坐标（0-1000）
     * 
     * @param imageCoords 图像中的像素坐标 [x, y]
     * @param imgSize     图像尺寸 [width, height]
     * @return 相对坐标 [x, y]，范围 0-1000
     */
    public static Point imageToRelativeCoordinates(Point imageCoords, Dimension imgSize) {
        // 将图像像素坐标转换为 0-1000 的相对坐标
        int relativeX = (int) (imageCoords.x * 1000.0 / imgSize.width);
        int relativeY = (int) (imageCoords.y * 1000.0 / imgSize.height);
        
        return new Point(relativeX, relativeY);
    }

    /**
     * 【已废弃】将图像坐标直接转换为 Android 设备屏幕坐标
     * 
     * 建议使用新的两步转换方式：
     * 1. imageToRelativeCoordinates() - 图像坐标 -> 相对坐标(0-1000)
     * 2. relativeToAbsoluteCoordinates() - 相对坐标(0-1000) -> 屏幕绝对坐标
     *
     * @deprecated 使用 relativeToAbsoluteCoordinates 替代
     */
    @Deprecated
    public static Point imageToAndroidScreenCoordinates(Point imageCoords, Dimension imgSize, Dimension screenSize) {
        // 旧方法：直接从图像坐标转换为屏幕坐标
        double scaleX = (double) screenSize.width / imgSize.width;
        double scaleY = (double) screenSize.height / imgSize.height;

        int screenX = (int) (imageCoords.x * scaleX);
        int screenY = (int) (imageCoords.y * scaleY);

        return new Point(screenX, screenY);
    }

    /**
     * 在图片上画红点并保存为新图片
     *
     * @param originalImagePath 原始图片路径
     * @param x                 红点的 x 坐标
     * @param y                 红点的 y 坐标
     * @return 新图片的路径
     */
    private String drawRedDotOnImage(String originalImagePath, int x, int y) {
        try {
            // 读取原始图片
            File originalFile = new File(originalImagePath);
            BufferedImage originalImage = ImageIO.read(originalFile);

            // 创建新图片（复制原图）
            BufferedImage markedImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );

            // 获取 Graphics2D 对象
            Graphics2D g2d = markedImage.createGraphics();

            // 绘制原图
            g2d.drawImage(originalImage, 0, 0, null);

            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 画红点
            int dotRadius = 15;  // 红点半径
            g2d.setColor(Color.RED);
            g2d.fillOval(x - dotRadius, y - dotRadius, dotRadius * 2, dotRadius * 2);

            // 画红点边框（更明显）
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(x - dotRadius, y - dotRadius, dotRadius * 2, dotRadius * 2);

            // 画十字准星（更容易定位）
            int crossSize = 25;
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x - crossSize, y, x + crossSize, y);
            g2d.drawLine(x, y - crossSize, x, y + crossSize);

            g2d.dispose();

            // 生成新文件名
            String originalName = originalFile.getName();
            String baseName = originalName.substring(0, originalName.lastIndexOf('.'));
            String newFileName = baseName + "_marked.png";
            String newImagePath = "/tmp/" + newFileName;

            // 保存新图片
            File newFile = new File(newImagePath);
            ImageIO.write(markedImage, "png", newFile);

            return newImagePath;

        } catch (Exception e) {
            System.err.println("画红点失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 在 Android 设备屏幕上执行点击操作
     *
     * @param device 设备
     * @param x      X 坐标
     * @param y      Y 坐标
     */
    private void tapScreen(IDevice device, int x, int y) {
        try {
            String command = String.format("input tap %d %d", x, y);
            System.out.println("执行点击命令: " + command);

            ShellOutputReceiver receiver = new ShellOutputReceiver();
            device.executeShellCommand(command, receiver, 5000, TimeUnit.MILLISECONDS);

            String output = receiver.getOutput();
            if (output != null && !output.trim().isEmpty()) {
                System.out.println("点击命令输出: " + output);
            } else {
                System.out.println("成功在坐标 (" + x + ", " + y + ") 执行点击");
            }
        } catch (Exception e) {
            System.err.println("点击操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取 Android 设备屏幕尺寸
     */
    private int[] getScreenSize(IDevice device) {
        try {
            ShellOutputReceiver receiver = new ShellOutputReceiver();
            device.executeShellCommand("wm size", receiver, 5000, TimeUnit.MILLISECONDS);

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
            System.err.println("获取屏幕尺寸失败，使用默认值: " + e.getMessage());
        }
        // 默认尺寸
        return new int[]{1080, 1920};
    }

    /**
     * 截屏并返回 Base64 编码的图片及其尺寸
     */
    private ScreenshotResult takeScreenshotWithSize(IDevice device) {
        try {
            RawImage rawImage = device.getScreenshot();
            if (rawImage == null) {
                System.err.println("无法获取屏幕数据");
                return null;
            }

            int imgWidth = rawImage.width;
            int imgHeight = rawImage.height;

            // 转换为 BufferedImage
            BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

            int index = 0;
            int bytesPerPixel = rawImage.bpp >> 3;
            for (int y = 0; y < imgHeight; y++) {
                for (int x = 0; x < imgWidth; x++) {
                    int value = rawImage.getARGB(index);
                    image.setRGB(x, y, value);
                    index += bytesPerPixel;
                }
            }

            // 保存原始图片到 /tmp 目录
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String fileName = "android_screenshot_" + timestamp + ".png";
            String imagePath = "/tmp/" + fileName;
            File outputFile = new File(imagePath);
            ImageIO.write(image, "png", outputFile);
            System.out.println("截图已保存到: " + imagePath);

            // 转为 Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            return new ScreenshotResult(base64, imgWidth, imgHeight, imagePath);

        } catch (Exception e) {
            System.err.println("截图失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 截屏并返回 Base64 编码的图片
     */
    private String takeScreenshotBase64(IDevice device) {
        try {
            RawImage rawImage = device.getScreenshot();
            if (rawImage == null) {
                System.err.println("无法获取屏幕数据");
                return null;
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
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            System.err.println("截图转 Base64 失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从 LLM 响应中解析相对坐标
     * 
     * 响应格式示例:
     * Thought: 用户想要点击登录按钮，我在屏幕中找到了登录按钮的位置...
     * Action: click(point='<point>540 500</point>')
     * 
     * 注意：坐标应该是 0-1000 范围的相对坐标（与 Python 项目一致）
     * - 左上角: (0, 0)
     * - 右下角: (1000, 1000)
     * - 屏幕中心: (500, 500)
     */
    private CoordinateResult parseCoordinateFromResponse(String response) {
        String thought = "";
        String action = "";
        int x = -1;
        int y = -1;

        // 解析 Thought
        Pattern thoughtPattern = Pattern.compile("Thought:\\s*(.+?)(?=Action:|$)", Pattern.DOTALL);
        Matcher thoughtMatcher = thoughtPattern.matcher(response);
        if (thoughtMatcher.find()) {
            thought = thoughtMatcher.group(1).trim();
        }

        // 解析 Action 和坐标
        // 支持多种动作: click, left_double, right_single, scroll 等
        Pattern actionPattern = Pattern.compile("Action:\\s*(\\w+)\\(.*?point='<point>(\\d+)\\s+(\\d+)</point>'");
        Matcher actionMatcher = actionPattern.matcher(response);
        if (actionMatcher.find()) {
            action = actionMatcher.group(1);
            x = Integer.parseInt(actionMatcher.group(2));
            y = Integer.parseInt(actionMatcher.group(3));
        }

        // 如果是 drag 动作，需要解析 start_point
        if (x == -1 && y == -1) {
            Pattern dragPattern = Pattern.compile("Action:\\s*(drag)\\(start_point='<point>(\\d+)\\s+(\\d+)</point>'");
            Matcher dragMatcher = dragPattern.matcher(response);
            if (dragMatcher.find()) {
                action = dragMatcher.group(1);
                x = Integer.parseInt(dragMatcher.group(2));
                y = Integer.parseInt(dragMatcher.group(3));
            }
        }

        // 检查是否为 finished 动作
        if (action.isEmpty()) {
            Pattern finishedPattern = Pattern.compile("Action:\\s*(finished|wait)\\(");
            Matcher finishedMatcher = finishedPattern.matcher(response);
            if (finishedMatcher.find()) {
                action = finishedMatcher.group(1);
            }
        }

        if (x == -1 && y == -1 && !action.equals("finished") && !action.equals("wait")) {
            System.err.println("无法从响应中解析坐标");
            return null;
        }

        return new CoordinateResult(x, y, action, thought, response);
    }

    /**
     * 简化版本：使用默认的 DOUBAO_VISION 配置获取坐标
     */
    public CoordinateResult getCoordinateByInstruction(IDevice device, String instruction) {
        // 使用 DOUBAO_VISION 作为默认的视觉模型
        LLMConfig config = LLMConfig.builder()
                .llmProvider(LLMProvider.DOUBAO_UI_TARS)
                .build();
        return getCoordinateByInstruction(device, instruction, config);
    }
}
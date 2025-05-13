package run.mone.mcp.multimodal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多模态界面操作服务
 * 实现基于截图的点击、拖拽、输入等操作
 */
@Service
@Slf4j
public class MultimodalService {

    private static final Logger log = LoggerFactory.getLogger(MultimodalService.class);

    @Autowired
    private LLM llm;

    private Robot robot;

    public MultimodalService() {
        try {
            System.setProperty("java.awt.headless", "false");
            robot = new Robot();
            robot.setAutoDelay(200);
        } catch (AWTException e) {
            log.error("初始化Robot失败", e);
//            throw new RuntimeException("初始化Robot失败", e);
        }
    }

    /**
     * 分析屏幕截图
     */
    public Flux<String> analyzeScreenshot(String imageBase64, String instruction) {
        String prompt = "请分析这张屏幕截图，并根据用户的指令确定要执行的操作：\n" +
                "用户指令: " + instruction + "\n" +
                "截图内容: [此处有一张图片，已转为Base64编码]";
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    /**
     * 执行左键点击操作
     */
    public Flux<String> click(int x, int y) {
        try {
            // 1. 首先移动到目标窗口
            robot.mouseMove(x, y);
            robot.delay(200);

            // 2. 点击一次获取窗口焦点
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(100);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(500); // 等待窗口获取焦点


            robot.mouseMove(x, y);
            robot.delay(100);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(50);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            return Flux.just("成功在坐标 (" + x + ", " + y + ") 执行了左键点击");
        } catch (Exception e) {
            return Flux.just("点击操作失败：" + e.getMessage());
        }
    }

    /**
     * 执行右键点击操作
     */
    public Flux<String> rightClick(int x, int y) {
        try {
            robot.mouseMove(x, y);
            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            return Flux.just("成功在坐标 (" + x + ", " + y + ") 执行了右键点击");
        } catch (Exception e) {
            return Flux.just("右键点击操作失败：" + e.getMessage());
        }
    }

    /**
     * 执行双击操作
     */
    public Flux<String> doubleClick(int x, int y) {
        try {
            robot.mouseMove(x, y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(150);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            return Flux.just("成功在坐标 (" + x + ", " + y + ") 执行了双击");
        } catch (Exception e) {
            return Flux.just("双击操作失败：" + e.getMessage());
        }
    }

    /**
     * 执行拖拽操作
     */
    public Flux<String> dragAndDrop(int startX, int startY, int endX, int endY) {
        try {
            robot.mouseMove(startX, startY);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(200);

            // 平滑移动
            int steps = 10;
            for (int i = 0; i < steps; i++) {
                int x = startX + (endX - startX) * i / steps;
                int y = startY + (endY - startY) * i / steps;
                robot.mouseMove(x, y);
                robot.delay(50);
            }

            robot.mouseMove(endX, endY);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            return Flux.just("成功执行从 (" + startX + ", " + startY + ") 到 (" + endX + ", " + endY + ") 的拖拽操作");
        } catch (Exception e) {
            return Flux.just("拖拽操作失败：" + e.getMessage());
        }
    }

    /**
     * 截取全屏并保存为PNG图片
     * @param filePath 指定保存的文件路径，如果为null则使用默认路径和文件名
     * @return 保存的图片文件路径
     */
    /**
     * 分别截取每个屏幕并保存为独立文件
     *
     * @param baseFilePath 基本文件路径，将会在文件名中添加屏幕编号
     * @return 包含所有操作结果的Flux
     */
    public Flux<String> captureEachScreenSeparately(String baseFilePath) {
        try {
            // 获取所有屏幕设备
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] screens = ge.getScreenDevices();

            if (screens.length == 0) {
                return Flux.just("错误: 未检测到屏幕设备");
            }

            // 处理baseFilePath
            if (baseFilePath == null || baseFilePath.isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String timestamp = dateFormat.format(new Date());
                String userHome = System.getProperty("user.home");
                File screenshotDir = new File(userHome, "Screenshots");
                if (!screenshotDir.exists()) {
                    screenshotDir.mkdirs();
                }
                baseFilePath = new File(screenshotDir, "screenshot_" + timestamp).getAbsolutePath();
            }

            // 移除扩展名以便添加屏幕编号
            if (baseFilePath.toLowerCase().endsWith(".png")) {
                baseFilePath = baseFilePath.substring(0, baseFilePath.length() - 4);
            }

            List<String> results = new ArrayList<>();

            // 为每个屏幕创建截图
            for (int i = 0; i < screens.length; i++) {
                GraphicsDevice screen = screens[i];
                GraphicsConfiguration config = screen.getDefaultConfiguration();
                Rectangle bounds = config.getBounds();

                // 截取当前屏幕
                BufferedImage screenshot = robot.createScreenCapture(bounds);

                // 生成当前屏幕的文件路径
                String screenFilePath = baseFilePath + "_screen" + (i + 1) + ".png";
                File outputFile = new File(screenFilePath);

                System.out.println(outputFile);

                // 保存图片
                ImageIO.write(screenshot, "png", outputFile);

                results.add(String.format(
                        "屏幕 %d (%dx%d, 位置: %d,%d) 截图已保存为: %s",
                        i + 1, bounds.width, bounds.height, bounds.x, bounds.y, screenFilePath));
            }

            // 添加一个总结信息
            results.add(String.format("总共截取了 %d 个屏幕的图像", screens.length));

            return Flux.fromIterable(results);
        } catch (Exception e) {
            log.error("分别截取屏幕失败", e);
            return Flux.just("截图失败: " + e.getMessage());
        }
    }

    /**
     * 使用macOS原生的截屏命令进行全屏截图
     *
     * @param filePath 指定保存的文件路径，如果为null则使用默认路径和文件名
     * @return 保存的图片文件路径
     */
    public Flux<String> captureMacScreenshot(String filePath) {
        try {
            // 如果未指定文件路径，则生成默认路径
            if (filePath == null || filePath.isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String timestamp = dateFormat.format(new Date());
                String fileName = "screenshot_" + timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + ".png";

                // 使用用户主目录下的 Screenshots 文件夹
                String userHome = System.getProperty("user.home");
                File screenshotDir = new File(userHome, "Screenshots");
                if (!screenshotDir.exists()) {
                    screenshotDir.mkdirs();
                }

                filePath = new File(screenshotDir, fileName).getAbsolutePath();
            }

            // 确保文件扩展名为 .png
            if (!filePath.toLowerCase().endsWith(".png")) {
                filePath += ".png";
            }

            // 确保目标目录存在且可写
            File outputFile = new File(filePath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    log.error("无法创建目录: " + parentDir.getAbsolutePath());
                    return Flux.just("错误: 无法创建目标目录");
                }
            }

            if (parentDir != null && !parentDir.canWrite()) {
                log.error("没有写入权限: " + parentDir.getAbsolutePath());
                return Flux.just("错误: 没有目标目录的写入权限");
            }

            log.info("准备使用screencapture命令截图，保存到: " + filePath);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "screencapture", filePath);

            // 重定向错误流，以便我们可以读取
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // 读取命令输出
            StringBuilder output = new StringBuilder();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                // 检查文件是否实际创建
                if (outputFile.exists() && outputFile.length() > 0) {
                    log.info("成功使用macOS原生命令截图，文件大小: " + outputFile.length() + " 字节");
                    return Flux.just("成功使用macOS原生命令截取全屏并保存为: " + filePath);
                } else {
                    log.error("screencapture命令执行成功，但文件未创建或为空");
                    return Flux.just("错误: 截图文件未创建或为空");
                }
            } else {
                log.error("screencapture命令执行失败，退出码: " + exitCode + ", 输出: " + output);

                // 根据错误代码提供更具体的错误消息
                String errorMsg;
                if (exitCode == 1) {
                    errorMsg = "用户可能取消了截图或没有屏幕截图权限";
                } else {
                    errorMsg = "未知错误";
                }

                return Flux.just("macOS截屏命令执行失败: " + errorMsg + " (退出码: " + exitCode + ")");
            }
        } catch (IOException | InterruptedException e) {
            log.error("macOS截屏失败", e);
            return Flux.just("macOS截屏失败: " + e.getMessage());
        }
    }

    /**
     * 使用AppleScript执行全屏幕截图，可以捕获所有窗口包括最小化的窗口
     *
     * @param filePath 指定保存的文件路径
     * @return 保存的图片文件路径
     */
    public Flux<String> captureAllScreensWithAppleScript(String filePath) {
        try {
            // 检查是否为macOS
            String osName = System.getProperty("os.name").toLowerCase();
            if (!osName.contains("mac")) {
                return Flux.just("错误: 此功能仅支持macOS系统");
            }

            // 处理文件路径
            if (filePath == null || filePath.isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String timestamp = dateFormat.format(new Date());
                String fileName = "screenshot_" + timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + ".png";

                String userHome = System.getProperty("user.home");
                File screenshotDir = new File(userHome, "Screenshots");
                if (!screenshotDir.exists()) {
                    screenshotDir.mkdirs();
                }

                filePath = new File(screenshotDir, fileName).getAbsolutePath();
            }

            if (!filePath.toLowerCase().endsWith(".png")) {
                filePath += ".png";
            }

            // 创建AppleScript脚本
            // 这个脚本会展示所有窗口并截图
            String appleScript =
                    "tell application \"System Events\"\n" +
                            "   set allProcesses to every process whose visible is true\n" +
                            "   repeat with proc in allProcesses\n" +
                            "      set frontmost of proc to true\n" +
                            "      delay 0.2\n" +
                            "   end repeat\n" +
                            "end tell\n" +
                            "delay 0.5\n" +
                            "do shell script \"screencapture -x '" + filePath.replace("'", "\\'") + "'\"\n";

            // 创建临时脚本文件
            File scriptFile = File.createTempFile("screenshot_script_", ".scpt");
            try (java.io.FileWriter writer = new java.io.FileWriter(scriptFile)) {
                writer.write(appleScript);
            }

            log.info("准备使用AppleScript执行全屏截图，保存到: " + filePath);

            // 执行AppleScript
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "osascript", scriptFile.getAbsolutePath());
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // 读取输出
            StringBuilder output = new StringBuilder();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();

            // 清理临时文件
            scriptFile.delete();

            if (exitCode == 0) {
                File outputFile = new File(filePath);
                if (outputFile.exists() && outputFile.length() > 0) {
                    return Flux.just("成功使用AppleScript截取全屏并保存为: " + filePath);
                } else {
                    return Flux.just("错误: AppleScript执行成功但截图文件未创建或为空");
                }
            } else {
                log.error("AppleScript执行失败，退出码: " + exitCode + ", 输出: " + output);
                return Flux.just("AppleScript截图失败: " + output);
            }
        } catch (Exception e) {
            log.error("AppleScript截图失败", e);
            return Flux.just("AppleScript截图失败: " + e.getMessage());
        }
    }

    /**
     * 执行键盘输入
     */
    public Flux<String> typeText(String text) {
        try {
            for (char c : text.toCharArray()) {
                typeChar(c);
            }
            return Flux.just("成功输入文本：" + text);
        } catch (Exception e) {
            return Flux.just("文本输入失败：" + e.getMessage());
        }
    }

    /**
     * 输入单个字符
     */
    private void typeChar(char c) {
        try {
            boolean upperCase = Character.isUpperCase(c);
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);

            if (upperCase) {
                robot.keyPress(KeyEvent.VK_SHIFT);
            }

            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);

            if (upperCase) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        } catch (Exception e) {
            throw new RuntimeException("字符输入失败: " + c, e);
        }
    }

    /**
     * 执行组合键
     */
    public Flux<String> pressHotkey(List<String> keys) {
        try {
            List<Integer> keyCodes = keys.stream()
                    .map(this::getKeyCode)
                    .toList();

            // 按下所有键
            for (Integer keyCode : keyCodes) {
                robot.keyPress(keyCode);
            }

            // 延迟一点时间
            robot.delay(100);

            // 按相反顺序释放所有键
            for (int i = keyCodes.size() - 1; i >= 0; i--) {
                robot.keyRelease(keyCodes.get(i));
            }

            return Flux.just("成功执行组合键: " + String.join("+", keys));
        } catch (Exception e) {
            return Flux.just("组合键执行失败：" + e.getMessage());
        }
    }

    /**
     * 获取键码
     */
    private int getKeyCode(String key) {
        return switch (key.toUpperCase()) {
            case "CTRL" -> KeyEvent.VK_CONTROL;
            case "ALT" -> KeyEvent.VK_ALT;
            case "SHIFT" -> KeyEvent.VK_SHIFT;
            case "CMD", "META", "COMMAND" -> KeyEvent.VK_META;
            case "ENTER" -> KeyEvent.VK_ENTER;
            case "ESC", "ESCAPE" -> KeyEvent.VK_ESCAPE;
            case "TAB" -> KeyEvent.VK_TAB;
            case "SPACE" -> KeyEvent.VK_SPACE;
            case "BACKSPACE" -> KeyEvent.VK_BACK_SPACE;
            case "DELETE" -> KeyEvent.VK_DELETE;
            case "HOME" -> KeyEvent.VK_HOME;
            case "END" -> KeyEvent.VK_END;
            case "PAGEUP" -> KeyEvent.VK_PAGE_UP;
            case "PAGEDOWN" -> KeyEvent.VK_PAGE_DOWN;
            case "UP" -> KeyEvent.VK_UP;
            case "DOWN" -> KeyEvent.VK_DOWN;
            case "LEFT" -> KeyEvent.VK_LEFT;
            case "RIGHT" -> KeyEvent.VK_RIGHT;
            case "F1" -> KeyEvent.VK_F1;
            case "F2" -> KeyEvent.VK_F2;
            case "F3" -> KeyEvent.VK_F3;
            case "F4" -> KeyEvent.VK_F4;
            case "F5" -> KeyEvent.VK_F5;
            case "F6" -> KeyEvent.VK_F6;
            case "F7" -> KeyEvent.VK_F7;
            case "F8" -> KeyEvent.VK_F8;
            case "F9" -> KeyEvent.VK_F9;
            case "F10" -> KeyEvent.VK_F10;
            case "F11" -> KeyEvent.VK_F11;
            case "F12" -> KeyEvent.VK_F12;
            default -> {
                if (key.length() == 1) {
                    yield KeyEvent.getExtendedKeyCodeForChar(key.charAt(0));
                } else {
                    throw new IllegalArgumentException("不支持的键: " + key);
                }
            }
        };
    }
} 
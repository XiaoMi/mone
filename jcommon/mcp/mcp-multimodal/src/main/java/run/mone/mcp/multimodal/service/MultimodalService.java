package run.mone.mcp.multimodal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多模态界面操作服务
 * 实现基于截图的点击、拖拽、输入等操作
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "mcp.agent.type", havingValue = "default", matchIfMissing = true)
public class MultimodalService {

    @Autowired
    private LLM llm;

    private Robot robot;

    /**
     * 应用名称映射表
     * key: 简称/别名, value: macOS中的实际应用名称
     */
    private static final Map<String, String> APP_NAME_MAPPING = new HashMap<>() {{
        // 浏览器
        put("chrome", "Google Chrome");
        put("谷歌浏览器", "Google Chrome");
        
        // 微信
        put("微信", "WeChat");
        put("wechat", "WeChat");
        put("wx", "WeChat");
        
        // 华泰证券
        put("华泰证券", "专业版Ⅲ(MAC)");
        put("华泰", "专业版Ⅲ(MAC)");
        put("huatai", "专业版Ⅲ(MAC)");
        
        // 其他常用应用
        put("cursor", "Cursor");
        put("idea", "IntelliJ IDEA");
        put("终端", "Terminal");
        put("terminal", "Terminal");
        put("访达", "Finder");
        put("finder", "Finder");
    }};

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

    //mac 下滑动滚轮 (class)
    public Flux<String> scrollWheel(int amount) {
        try {
            robot.mouseWheel(amount);
            return Flux.just("执行了滚轮滑动，滑动量: " + amount);
        } catch (Exception e) {
            return Flux.just("滚轮滑动操作失败：" + e.getMessage());
        }
    }

    /**
     * 执行左键点击操作
     */
    public Flux<String> click(int x, int y) {
        try {
            // 1. 首先移动到目标窗口
            robot.mouseMove(x, y);
            robot.delay(200);

//            // 2. 点击一次获取窗口焦点
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(100);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(500); // 等待窗口获取焦点
//
//
//            robot.mouseMove(x, y);
//            robot.delay(100);
//            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//            robot.delay(50);
//            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
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
     * 执行键盘输入 - 版本2：基于剪贴板的复制粘贴方式
     * 优势：能够更好地支持中文、特殊字符和长文本输入
     * 
     * @param text 要输入的文本
     * @return 操作结果
     */
    public Flux<String> typeTextV2(String text) {
        try {
            // 获取系统剪贴板
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            
            // 保存当前剪贴板内容（可选，用于恢复）
            // 注意：这里为了简单起见，不保存原内容，如果需要可以添加
            
            // 将文本复制到剪贴板
            StringSelection stringSelection = new StringSelection(text);
            clipboard.setContents(stringSelection, null);
            
            // 短暂延迟，确保剪贴板内容已设置
            robot.delay(100);
            
            // 检测操作系统，使用对应的粘贴快捷键
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("mac")) {
                // macOS: Cmd+V
                robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_V);
                robot.delay(50);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_META);
            } else {
                // Windows/Linux: Ctrl+V
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.delay(50);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
            }
            
            // 等待粘贴完成
            robot.delay(200);
            
            log.info("成功使用剪贴板方式输入文本，长度: " + text.length());
            return Flux.just("成功使用剪贴板方式输入文本：" + (text.length() > 50 ? text.substring(0, 50) + "..." : text));
        } catch (Exception e) {
            log.error("剪贴板方式文本输入失败", e);
            return Flux.just("剪贴板方式文本输入失败：" + e.getMessage());
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
     * 使用Robot截取全屏
     *
     * @param filePath 指定保存的文件路径，如果为null则使用默认路径和文件名
     * @return 保存的图片文件路径，如果截图失败则返回null
     */
    public Flux<String> captureScreenshotWithRobot(String filePath) {
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
                    return Flux.just(null);
                }
            }

            if (parentDir != null && !parentDir.canWrite()) {
                log.error("没有写入权限: " + parentDir.getAbsolutePath());
                return Flux.just(null);
            }

            log.info("准备使用Robot截图，保存到: " + filePath);

            // 获取默认屏幕设备
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            // 使用Robot捕获屏幕
            BufferedImage screenshot = robot.createScreenCapture(screenRect);

            // 保存图片
            ImageIO.write(screenshot, "png", outputFile);

            if (outputFile.exists() && outputFile.length() > 0) {
                log.info("成功使用Robot截图，文件大小: " + outputFile.length() + " 字节");
                return Flux.just(filePath);
            } else {
                log.error("Robot截图成功，但文件未创建或为空");
                return Flux.just(null);
            }
        } catch (IOException e) {
            log.error("Robot截图失败", e);
            return Flux.just(null);
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

    /**
     * 激活指定的应用程序窗口（会自动切换到应用所在的Space）
     * 注意：此功能仅支持 macOS 系统
     * 
     * @param appNameOrAlias 应用名称或别名（如 "chrome", "微信", "华泰证券"）
     * @return 操作结果消息
     */
    public Flux<String> activateApplication(String appNameOrAlias) {
        try {
            // 检查操作系统
            String osName = System.getProperty("os.name").toLowerCase();
            if (!osName.contains("mac")) {
                log.warn("应用激活功能仅支持 macOS 系统，当前系统: {}", osName);
                return Flux.just("应用激活功能仅支持 macOS 系统，当前系统: " + osName);
            }
            
            // 从映射表中获取实际的应用名称
            String actualAppName = APP_NAME_MAPPING.getOrDefault(
                appNameOrAlias.toLowerCase(), 
                appNameOrAlias
            );
            
            log.info("准备激活应用: {} (实际名称: {})", appNameOrAlias, actualAppName);
            
            // 首先检查应用是否正在运行
            if (!isApplicationRunning(actualAppName)) {
                log.warn("应用 {} 未运行，尝试启动", actualAppName);
                // 可以选择启动应用或返回错误
                return Flux.just("应用 " + actualAppName + " 未运行，请先启动应用");
            }
            
            // 使用 AppleScript 激活应用（会自动切换Space）
            String script = String.format("tell application \"%s\" to activate", actualAppName);
            ProcessBuilder pb = new ProcessBuilder("osascript", "-e", script);
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                // 等待窗口切换完成
                robot.delay(800);
                log.info("成功激活应用: {}", actualAppName);
                return Flux.just("成功激活应用: " + actualAppName);
            } else {
                log.error("激活应用失败: {}, 输出: {}", actualAppName, output);
                return Flux.just("激活应用失败: " + actualAppName + ", 错误: " + output);
            }
        } catch (IOException | InterruptedException e) {
            log.error("激活应用异常", e);
            return Flux.just("激活应用异常: " + e.getMessage());
        }
    }
    
    /**
     * 检查应用是否正在运行
     * 注意：此功能仅支持 macOS 系统
     * 
     * @param appName 应用名称
     * @return true 如果应用正在运行
     */
    public boolean isApplicationRunning(String appName) {
        try {
            // 检查操作系统
            String osName = System.getProperty("os.name").toLowerCase();
            if (!osName.contains("mac")) {
                log.warn("应用检查功能仅支持 macOS 系统，当前系统: {}", osName);
                return false;
            }
            
            String script = String.format(
                "tell application \"System Events\" to (name of processes) contains \"%s\"",
                appName
            );
            ProcessBuilder pb = new ProcessBuilder("osascript", "-e", script);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            String result = reader.readLine();
            
            return "true".equalsIgnoreCase(result);
        } catch (IOException e) {
            log.error("检查应用运行状态失败", e);
            return false;
        }
    }
    
    /**
     * 获取当前运行的所有应用列表
     * 注意：此功能仅支持 macOS 系统
     * 
     * @return 运行中的应用名称列表
     */
    public Flux<List<String>> getRunningApplications() {
        try {
            // 检查操作系统
            String osName = System.getProperty("os.name").toLowerCase();
            if (!osName.contains("mac")) {
                log.warn("获取运行应用列表功能仅支持 macOS 系统，当前系统: {}", osName);
                return Flux.just(new ArrayList<>());
            }
            
            String script = "tell application \"System Events\" to get name of every application process whose background only is false";
            ProcessBuilder pb = new ProcessBuilder("osascript", "-e", script);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            String result = reader.readLine();
            
            if (result != null && !result.isEmpty()) {
                // 输出格式为: "App1, App2, App3"
                List<String> apps = Arrays.stream(result.split(","))
                    .map(String::trim)
                    .toList();
                
                log.info("当前运行的应用: {}", apps);
                return Flux.just(apps);
            }
            
            return Flux.just(new ArrayList<>());
        } catch (IOException e) {
            log.error("获取运行应用列表失败", e);
            return Flux.just(new ArrayList<>());
        }
    }
    
    /**
     * 获取系统已安装的应用列表
     * 注意：此功能仅支持 macOS 系统
     * 
     * @return 已安装的应用名称列表
     */
    public Flux<List<String>> getInstalledApplications() {
        try {
            // 检查操作系统
            String osName = System.getProperty("os.name").toLowerCase();
            if (!osName.contains("mac")) {
                log.warn("获取已安装应用列表功能仅支持 macOS 系统，当前系统: {}", osName);
                return Flux.just(new ArrayList<>());
            }
            
            List<String> apps = new ArrayList<>();
            
            // 扫描 /Applications 目录
            File applicationsDir = new File("/Applications");
            if (applicationsDir.exists() && applicationsDir.isDirectory()) {
                File[] appFiles = applicationsDir.listFiles((dir, name) -> name.endsWith(".app"));
                if (appFiles != null) {
                    for (File app : appFiles) {
                        String appName = app.getName().replace(".app", "");
                        apps.add(appName);
                    }
                }
            }
            
            // 扫描用户 Applications 目录
            String userHome = System.getProperty("user.home");
            File userAppsDir = new File(userHome, "Applications");
            if (userAppsDir.exists() && userAppsDir.isDirectory()) {
                File[] appFiles = userAppsDir.listFiles((dir, name) -> name.endsWith(".app"));
                if (appFiles != null) {
                    for (File app : appFiles) {
                        String appName = app.getName().replace(".app", "");
                        if (!apps.contains(appName)) {
                            apps.add(appName);
                        }
                    }
                }
            }
            
            apps.sort(String::compareTo);
            log.info("找到 {} 个已安装的应用", apps.size());
            return Flux.just(apps);
        } catch (Exception e) {
            log.error("获取已安装应用列表失败", e);
            return Flux.just(new ArrayList<>());
        }
    }
    
    /**
     * 带应用激活的截图方法（推荐使用）
     * 在截图前先激活指定应用，确保截取到正确的窗口
     * 注意：应用激活功能仅支持 macOS 系统
     * 
     * @param appNameOrAlias 应用名称或别名
     * @param filePath 保存路径（可选）
     * @return 保存的图片文件路径
     */
    public Flux<String> captureScreenshotWithAppActivation(String appNameOrAlias, String filePath) {
        try {
            // 检查操作系统
            String osName = System.getProperty("os.name").toLowerCase();
            boolean isMacOS = osName.contains("mac");
            
            if (!isMacOS) {
                log.warn("应用激活功能仅支持 macOS 系统，当前系统: {}，将使用普通截图方式", osName);
                return captureScreenshotWithRobot(filePath);
            }
            
            // macOS 系统：先激活应用
            String activationResult = activateApplication(appNameOrAlias).blockFirst();
            log.info("应用激活结果: {}", activationResult);
            
            if (activationResult != null && activationResult.contains("成功")) {
                // 额外等待确保窗口完全显示
                robot.delay(500);
                
                // 执行截图
                return captureScreenshotWithRobot(filePath);
            } else {
                log.warn("应用 {} 激活失败，使用普通截图方式: {}", appNameOrAlias, activationResult);
                // 降级：执行普通截图
                return captureScreenshotWithRobot(filePath);
            }
        } catch (Exception e) {
            log.error("带应用激活的截图失败", e);
            return Flux.just("截图失败: " + e.getMessage());
        }
    }
} 
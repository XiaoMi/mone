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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 多模态界面操作服务
 * 实现基于截图的点击、拖拽、输入等操作
 */
@Service
@Slf4j
public class MultimodalService {

    @Autowired
    private LLM llm;

    private Robot robot;

    public MultimodalService() {
        try {
            System.setProperty("java.awt.headless", "false");
            robot = new Robot();
            robot.setAutoDelay(200);
        } catch (AWTException e) {
            log.error(e.getMessage(),e);
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
            robot.mouseMove(x, y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
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
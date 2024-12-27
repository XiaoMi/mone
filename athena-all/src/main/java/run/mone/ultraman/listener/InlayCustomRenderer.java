package run.mone.ultraman.listener;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.impl.FontInfo;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 * @date 2024/5/31 14:57
 */
public class InlayCustomRenderer implements EditorCustomElementRenderer {


    private final String text;

    @Getter
    private final List<String> lines;

    private final String indent;

    public InlayCustomRenderer(String text, String indent) {
        this.text = text;
        this.lines = Stream.of(text.split("\n")).collect(Collectors.toList());
        this.indent = indent;
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        FontMetrics metrics = inlay.getEditor().getContentComponent().getFontMetrics(inlay.getEditor().getColorsScheme().getFont(EditorFontType.PLAIN));
        int maxWidth = 0;
        for (String line : text.split("\n")) {
            int width = 0;
            if (StringUtils.isNotEmpty(indent)) {
                width = metrics.stringWidth(line + indent);
            } else {
                width = metrics.stringWidth(line);
            }
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
        //return Math.max(getWidth(inlay.getEditor(), text), 1);

       /* FontMetrics fontMetrics = inlay.getEditor().getContentComponent().getFontMetrics(inlay.getEditor().getColorsScheme().getFont(EditorFontType.PLAIN));
        // 计算一个字符的宽度
        int charWidth = fontMetrics.charWidth('m'); // 假设使用 'm' 字符来计算平均字符宽度
        return lines.stream().mapToInt(line -> fontMetrics.stringWidth(line) + charWidth).max().orElse(0);*/
    }

    @Override
    public int calcHeightInPixels(@NotNull Inlay inlay) {
        FontMetrics metrics = inlay.getEditor().getContentComponent().getFontMetrics(inlay.getEditor().getColorsScheme().getFont(EditorFontType.PLAIN));
        return metrics.getHeight() * text.split("\n").length;
    }

    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
        //EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        //Color backgroundColor = scheme.getDefaultBackground();
        // 设置自定义元素的背景颜色
        //g.setColor(backgroundColor);
        //g.clearRect(targetRegion.x, targetRegion.y, targetRegion.width, targetRegion.height);
        // 绘制提示内容时修改为灰色
        g.setColor(JBColor.gray);
        Font font = inlay.getEditor().getColorsScheme().getFont(EditorFontType.PLAIN);
        g.setFont(font);
        FontMetrics fontMetrics = inlay.getEditor().getContentComponent().getFontMetrics(font);
        Editor editor = inlay.getEditor();
        int y = 0;
        int lineHeight = 0;
        if (StringUtils.isNotEmpty(indent)) {
            lineHeight = fontMetrics.getHeight();
            y = targetRegion.y + fontMetrics.getAscent();
        } else {
            float lineSpacing = editor.getColorsScheme().getLineSpacing();
            lineHeight = (int) (Math.ceil(fontMetrics.getHeight() * lineSpacing));
            y = targetRegion.y + editor.getAscent();
        }
        // 绘制多行文本并应用缩进
        for (String line : text.split("\n")) {
            if (StringUtils.isNotEmpty(indent)) {
                g.drawString(indent + line, targetRegion.x, y);
            } else {
                g.drawString(line, targetRegion.x, y);
            }
            y += lineHeight;
        }
        /*// 获取编辑器的行间距
        Editor editor = inlay.getEditor();
        float lineSpacing = editor.getColorsScheme().getLineSpacing();

        // Draw each line, adjusting the y position for each line
        int lineHeight = (int) (Math.ceil(fontMetrics.getHeight() * lineSpacing));
        int y = targetRegion.y + editor.getAscent();
        for (String line : lines) {
            g.drawString(indent + line, targetRegion.x, y);
            y += lineHeight;
        }*/
    }

}

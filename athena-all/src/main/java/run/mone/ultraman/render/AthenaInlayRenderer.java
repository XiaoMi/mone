package run.mone.ultraman.render;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author goodjava@qq.com
 * @date 2023/7/21 14:08
 */
public class AthenaInlayRenderer implements EditorCustomElementRenderer {

    private final String hintText;

    private final Editor editor;

    public AthenaInlayRenderer(String hintText, Editor editor) {
        this.hintText = hintText;
        this.editor = editor;
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
        Font font = colorsScheme.getFont(EditorFontType.PLAIN);
        return EditorUtil.textWidth(editor, hintText, 0, hintText.length(), font.getStyle(), 0);
    }


    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle r, @NotNull TextAttributes textAttributes) {
        g.setColor(Color.RED);
        String content = hintText;
        String[] lines = content.split("\n");
        int fontHeight = g.getFontMetrics().getHeight();
        for (int i = 0; i < lines.length; i++) {
            g.drawString(lines[i], r.x, r.y + i * fontHeight + fontHeight);
        }

    }
}

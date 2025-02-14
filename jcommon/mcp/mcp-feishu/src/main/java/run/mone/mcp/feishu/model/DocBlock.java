package run.mone.mcp.feishu.model;

import com.lark.oapi.service.docx.v1.model.Block;
import com.lark.oapi.service.docx.v1.model.Text;
import com.lark.oapi.service.docx.v1.model.TextElement;
import com.lark.oapi.service.docx.v1.model.TextRun;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class DocBlock {
    private String blockId;
    private String docId;
    private String parentId;
    private List<Element> elements = new ArrayList<>();

    @Data
    @Accessors(chain = true)
    public static class Element {
        private int type;
        private String content;
    }

    public Block[] toFeishuBlocks() {
        if (parentId == null || parentId.isEmpty()) {
            throw new IllegalStateException("Parent block ID is required");
        }

        List<Block> blocks = new ArrayList<>();
        
        for (Element element : elements) {
            Block.Builder blockBuilder = Block.newBuilder()
                    .parentId(this.parentId)
                    .blockType(element.getType());

            // 创建文本内容
            Text text = new Text();
            TextElement textElement = new TextElement();
            TextRun textRun = new TextRun();
            textRun.setContent(element.getContent());
            textElement.setTextRun(textRun);
            text.setElements(new TextElement[]{textElement});

            // 根据type设置对应的字段
            switch (element.getType()) {
                case 1: // 页面 Block
                    blockBuilder.page(text);
                    break;
                case 2: // 文本 Block
                    blockBuilder.text(text);
                    break;
                case 3: // 标题 1 Block
                    blockBuilder.heading1(text);
                    break;
                case 4: // 标题 2 Block
                    blockBuilder.heading2(text);
                    break;
                case 5: // 标题 3 Block
                    blockBuilder.heading3(text);
                    break;
                case 6: // 标题 4 Block
                    blockBuilder.heading4(text);
                    break;
                case 7: // 标题 5 Block
                    blockBuilder.heading5(text);
                    break;
                case 8: // 标题 6 Block
                    blockBuilder.heading6(text);
                    break;
                case 9: // 标题 7 Block
                    blockBuilder.heading7(text);
                    break;
                case 10: // 标题 8 Block
                    blockBuilder.heading8(text);
                    break;
                case 11: // 标题 9 Block
                    blockBuilder.heading9(text);
                    break;
                case 12: // 无序列表 Block
                    blockBuilder.bullet(text);
                    break;
                case 13: // 有序列表 Block
                    blockBuilder.ordered(text);
                    break;
                case 14: // 代码块 Block
                    blockBuilder.code(text);
                    break;
                case 15: // 引用 Block
                    blockBuilder.quote(text);
                    break;
                default:
                    throw new IllegalStateException("Unsupported block type: " + element.getType());
            }
            
            blocks.add(blockBuilder.build());
        }
        
        return blocks.toArray(new Block[0]);
    }
} 
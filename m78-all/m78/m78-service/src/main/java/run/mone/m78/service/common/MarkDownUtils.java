package run.mone.m78.service.common;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

/**
 * @author goodjava@qq.com
 * @date 2024/3/12 11:07
 */
public class MarkDownUtils {


    public static String extractCodeBlock(String markdown) {
        try {
            // 创建Markdown解析器
            Parser parser = Parser.builder().build();
            Document document = parser.parse(markdown);
            Mutable<String> res = new MutableObject<>("");
            // 创建自定义访问者，用于移除所有的代码块
            NodeVisitor visitor = new NodeVisitor(new VisitHandler(FencedCodeBlock.class, node -> {
                FencedCodeBlock codeBlock = (FencedCodeBlock) node;
                BasedSequence info = codeBlock.getInfo();
                // 检查代码块的语言标记是否为"json"
                if (info.equals("json") || info.equals("java")) {
                    // 如果是，则从文档中移除该代码块
                    res.setValue(codeBlock.getChildChars().toString());
                }
            }));

            visitor.visit(document);

            if (StringUtils.isNotEmpty(res.getValue())) {
                return res.getValue();
            }
            return markdown;
        } catch (Throwable ignore) {
            return markdown;
        }
    }

}

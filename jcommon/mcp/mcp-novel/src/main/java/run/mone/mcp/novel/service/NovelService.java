
package run.mone.mcp.novel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.util.List;

@Service
public class NovelService {

    @Autowired
    private LLM llm;

    public String extractPlotPoints(String novel) {
        String prompt = """
                # 小说情节提取指南
                您是一位经验丰富的文学编辑，曾负责过多部经典作品的情节设计和编辑工作。请以专业编辑的视角，对这篇小说进行系统的情节分析。
                                
                # 分析要求
                 - 提取所有事件，情节必须详细，要让看到的人能知道所有的前因后果
                 - 提取原则：只记录客观事实，不加入主观评价，省略心理活动，使用简洁现代语言
                 - 回答不需要任何形式的分段，只是一个简单的文本描述
                 
                下面是需要提取情节的小说：
                """
                + novel;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String outlineImitation(String outline) {
        String prompt = """
                请你扮演一位专业的故事大纲创作者。我会给你一个故事大纲作为参考，请你创作一个全新的故事大纲，需要满足以下要求：
                                
                1. 改变故事背景：可以选择任何其他类型的故事背景（如古代、架空历史、重生、科幻、玄幻、现代都市等）
                2. 改变核心关系：创造新的人物关系作为故事主线
                3. 调整情节发展：使用全新的事件和转折点
                4. 创新结局设计：设计一个符合新背景的独特结局
                5. 具体要求：
                   - 保持8~10个主要情节段落
                   - 每个段落的篇幅要相对均衡
                   - 确保故事逻辑性和完整性
                   - 增加独特的细节描写
                                
                请基于以上要求，创作一个与原作完全不同的故事大纲：
                """
                + outline;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String styleImitation(String text, String copyTextStyle) {
        String prompt = """
                你是一位专业的文字风格模仿专家。请按照以下给定的原始文本风格，改写我提供的新内容：
                                
                [原始风格示例]：
                """
                + copyTextStyle +
                """
                        要求：
                        1. 准确把握原文的语气、节奏和表达特点
                        2. 保持相似的修辞手法、语言风格
                        3. 保留原文的情感基调
                        4. 确保改写后的内容自然流畅，不生硬模仿
                                                
                        请按照以上要求进行风格仿写。
                                                
                        [需要改写的新内容]：
                        """
                + text;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

}
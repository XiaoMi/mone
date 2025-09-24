package run.mone.mcp.chat.tool;

import run.mone.hive.roles.tool.ITool;

/**
 * Tool for processing documents, including drafting, summarizing, translating, and polishing text.
 * @author goodjava@qq.com
 */
public class DocumentProcessingTool implements ITool {

    @Override
    public String getName() {
        return "document_processing";
    }

    @Override
    public String description() {
        return """
            A tool designed for various text manipulation tasks on documents or text snippets. Use this tool when the user explicitly asks to:
            1.  **Draft:** Create new text content based on a prompt (e.g., emails, reports, outlines).
            2.  **Summarize:** Condense longer text into key points or a brief overview.
            3.  **Translate:** Convert text from one language to another.
            4.  **Polish/Rewrite:** Improve clarity, grammar, style, or tone of existing text.

            **When to use:** Choose this tool when the core request involves generating or transforming text in a structured way beyond simple conversation. For general chat or questions not involving these specific actions, use the 'chat' tool.

            **Output:** The tool will return the processed text according to the requested action and parameters.
            """;
    }

    @Override
    public String parameters() {
        return """
                - action: (required) The specific document processing task to perform. Must be one of: 'draft', 'summarize', 'translate', 'polish'.
                - input_text: (required for 'summarize', 'translate', 'polish') The original text content to be processed. Not required for 'draft'.
                - prompt: (required for 'draft', optional for others) Specific instructions for the task.
                    - For 'draft': Describe the desired content, format, tone, and recipient (e.g., "Draft a formal email to the team about the meeting schedule change").
                    - For 'summarize': Specify desired length or format (e.g., "Summarize into 3 bullet points", "Provide a one-paragraph summary").
                    - For 'translate': Specify the target language (also use target_language parameter). Can add context if needed.
                    - For 'polish': Specify the goal (e.g., "Make this text more formal", "Correct grammar and spelling", "Rewrite to be more persuasive").
                - target_language: (required for 'translate') The language to translate the 'input_text' into (e.g., 'English', 'Spanish', 'French').
                - output_format: (optional) Specify the desired format for the output (e.g., 'plain_text', 'markdown', 'bullet_points'). Defaults to 'plain_text' if not specified.
                """;
    }

    @Override
    public String usage() {
        return """
            (Attention: If you are using this tool, you MUST return the processed content in Chinese 中文 within the <processed_content> tag):

            Example 1: Drafting an email
            <document_processing>
              <action>draft</action>
              <prompt>草拟一封邮件，通知团队本周五下午的团建活动安排，包括时间、地点和注意事项。</prompt>
              <output_format>plain_text</output_format>
              <processed_content>【这里是草拟好的中文邮件内容】</processed_content>
            </document_processing>

            Example 2: Summarizing text
            <document_processing>
              <action>summarize</action>
              <input_text>【这里是需要总结的长篇中文文本】</input_text>
              <prompt>将上述文本总结成三个要点。</prompt>
              <output_format>bullet_points</output_format>
              <processed_content>
              *   要点一：【总结的第一个中文要点】
              *   要点二：【总结的第二个中文要点】
              *   要点三：【总结的第三个中文要点】
              </processed_content>
            </document_processing>

            Example 3: Translating text (Output still mandated as Chinese based on ChatTool's pattern)
            <document_processing>
              <action>translate</action>
              <input_text>This is an important announcement.</input_text>
              <target_language>Chinese</target_language>
              <prompt>将输入的英文翻译成中文。</prompt>
              <processed_content>【这里是翻译后的中文文本，例如：这是一个重要的通知。】</processed_content>
            </document_processing>

            Example 4: Polishing text
            <document_processing>
              <action>polish</action>
              <input_text>俺觉得这个方案忒好了。</input_text>
              <prompt>请将这段话润色，使其更书面化和专业。</prompt>
              <processed_content>【这里是润色后的中文文本，例如：我认为这个方案非常出色。】</processed_content>
            </document_processing>
            """;
    }

}
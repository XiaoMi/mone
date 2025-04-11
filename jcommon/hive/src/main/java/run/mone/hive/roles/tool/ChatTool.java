package run.mone.hive.roles.tool;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 11:10
 */
public class ChatTool implements ITool{

    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String description() {
        return """
            A tool for handling general conversations and chat interactions. Use this when the user's input is conversational and doesn't require specific functional tools.
            **IM Chat Splitting (Use Sparingly):** You are currently in an Instant Messaging (IM) chat scenario. **Your default approach should be to provide a single, cohesive response using one `<message>` tag.** However, *only if* you assess that breaking down a longer response into several shorter, sequential messages would *significantly* improve readability or feel much more natural for the IM flow (like explaining steps or building suspense), you *may* choose to split the response.
            **If splitting, adhere to these strict rules:**
            1.  Provide multiple `<message>` tags within the `<chat>` block, in the exact order they should be sent.
            2.  **Do NOT split into more than three (3) `<message>` tags.** A single message or two messages is often sufficient if splitting is needed. Use three only when absolutely necessary for clarity.
            3.  Use this splitting technique **judiciously and only when the benefit to the IM conversation flow is clear and substantial.** Otherwise, stick to a single `<message>`.
            This tool enables natural dialogue-based interactions. Use it for engaging in general discussions, providing information, or offering support through conversation when other specialized tools are not applicable.
            """;
    }

    @Override
    public String parameters() {
        return """
                - message: (required) One or more (up to 3) chat messages to send to the user. Provide one `<message>` tag for a standard single response (preferred). Provide 2 or 3 `<message>` tags (in sequence) *only* if splitting according to the rules above. Each message part must be natural, friendly, maintain coherence, and be relevant to the user's input.
                """;
    }

    @Override
    public String usage() {
        return """
            (Attention: If you are using this tool, you MUST return the message(s) in Chinese 中文):
            Example 1: Single Message Response (Preferred)
            <chat>
            <message>这是你需要的信息，包括要点A、B和C。</message>
            </chat>
            """;
    }

}

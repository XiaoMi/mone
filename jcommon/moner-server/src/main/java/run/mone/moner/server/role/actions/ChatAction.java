package run.mone.moner.server.role.actions;

import run.mone.hive.actions.Action;

/**
 * @author goodjava@qq.com
 * @date 2025/2/12 13:47
 */
public class ChatAction extends Action {

    public ChatAction() {
        setName("ChatAction");
        setDescription("""
                #.chat(聊天TOOL)
                Description: A tool for handling general conversations and chat interactions. This tool should be used when the user's input is conversational in nature and doesn't require specific functional tools. It enables natural dialogue-based interactions in scenarios where other specialized tools are not applicable. Use this tool for engaging in general discussions, providing information, or offering support through conversation.
                如果用户需求描述中更偏向于日常对话、问候等，请使用聊天工具(chat)。
                举例：
                - 用户描述："我要买一个新的手机"
                请使用购物相关的工具。
                
                - 用户描述："你好，今天过得怎么样？"
                <chat><message>我今天过得非常充实</message></chat>。
                
                注意：如果使用聊天工具，你只需要返回
                <chat>
                <message>Your chat message here</message>
                </chat>
                不需要进行说明。
                Parameters:
                - message: (required) The chat message to respond to the user. The message should be natural, friendly, and maintain coherence and relevance with the user's input.
                Usage:
                <chat>
                <message>Your chat message here</message>
                </chat>
                
                """);
    }
}

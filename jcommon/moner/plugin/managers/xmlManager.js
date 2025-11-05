// XML动作管理器类
export class XMLManager {
    constructor() {
        // 不再需要 DOMParser
    }

    /**
     * 解析XML字符串，提取action标签
     * @param {string} xmlString - 包含action标签的XML字符串
     * @returns {Array<XmlAction>} 解析出的动作列表
     */
    parseActions(xmlString) {
        try {
            const actions = [];

            // 1) 解析 <chat> 消息（支持多个 <message>）
            const chatBlockRegex = /<chat[^>]*>([\s\S]*?)<\/chat>/i;
            const chatBlockMatch = xmlString.match(chatBlockRegex);
            if (chatBlockMatch) {
                const chatInner = chatBlockMatch[1] || '';
                const messageRegex = /<message[^>]*>([\s\S]*?)<\/message>/gi;
                let msgMatch;
                while ((msgMatch = messageRegex.exec(chatInner)) !== null) {
                    const msgText = (msgMatch[1] || '').trim();
                    if (msgText) {
                        actions.push({ type: 'chat', attributes: { message: msgText } });
                    }
                }
            }

            // 1.1) 解析 <attempt_completion>（提取 <result>、<usage>、<command>）
            const attemptBlockRegex = /<attempt_completion[^>]*>([\s\S]*?)<\/attempt_completion>/gi;
            let attemptMatch;
            while ((attemptMatch = attemptBlockRegex.exec(xmlString)) !== null) {
                const attemptInner = attemptMatch[1] || '';

                // 提取 <result>
                const resultRegex = /<result[^>]*>([\s\S]*?)<\/result>/i;
                const resultMatch = attemptInner.match(resultRegex);

                // 提取 <usage>（嵌套在 result 内或外都尝试）
                const usageRegex = /<usage[^>]*>([\s\S]*?)<\/usage>/i;
                let usageRaw = null;
                let usage = null;
                let resultText = '';

                if (resultMatch) {
                    let resultInner = resultMatch[1] || '';
                    const usageInResult = resultInner.match(usageRegex);
                    if (usageInResult) {
                        usageRaw = (usageInResult[1] || '').trim();
                        // 从结果里移除 <usage> 块，保留纯文本
                        resultInner = resultInner.replace(usageRegex, '').trim();
                    }
                    resultText = resultInner.trim();
                }

                // 如果 result 外还有独立的 <usage>
                if (!usageRaw) {
                    const usageOutside = attemptInner.match(usageRegex);
                    if (usageOutside) {
                        usageRaw = (usageOutside[1] || '').trim();
                    }
                }

                // 尝试解析 usage JSON
                if (usageRaw) {
                    try {
                        usage = JSON.parse(usageRaw);
                    } catch (e) {
                        usage = usageRaw; // 解析失败则原样返回字符串
                    }
                }

                // 提取 <command>（可选）
                const commandRegex = /<command[^>]*>([\s\S]*?)<\/command>/i;
                const commandMatch = attemptInner.match(commandRegex);
                const commandText = commandMatch ? (commandMatch[1] || '').trim() : undefined;

                // 仅在存在 result 或 usage/command 时推入
                if (resultText || usage !== null || commandText) {
                    const attributes = {};
                    if (resultText) attributes.result = resultText;
                    if (usage !== null) attributes.usage = usage;
                    if (commandText) attributes.command = commandText;
                    actions.push({ type: 'attempt_completion', attributes });
                }
            }

            // 2) 解析 <action ...> 标签（原有逻辑）
            const actionPattern = /<action\s+([^>]*)(?:>([\s\S]*?)<\/action>|\/?>)/gi;
            let match;
            while ((match = actionPattern.exec(xmlString)) !== null) {
                const [, attributesStr, content] = match;
                const action = this.parseActionAttributes(attributesStr);
                if (action) {
                    if (content && content.trim()) {
                        action.content = content.trim();
                    }
                    actions.push(action);
                }
            }

            return actions;
        } catch (error) {
            console.error('Error parsing XML:', error);
            throw new Error('Failed to parse XML string');
        }
    }

    /**
     * 解析属性字符串
     * @param {string} attributesStr - 属性字符串
     * @returns {XmlAction|null} 解析出的动作对象
     */
    parseActionAttributes(attributesStr) {
        try {
            // 匹配属性名和值: name="value" 或 name='value'
            const attrPattern = /(\w+)=["']([^"']*)["']/g;
            const attributes = {};
            let type = null;
            let match;

            while ((match = attrPattern.exec(attributesStr)) !== null) {
                const [, name, value] = match;
                if (name === 'type') {
                    type = value;
                } else {
                    attributes[name] = value;
                }
            }

            if (!type) {
                console.warn('Action node missing type attribute');
                return null;
            }

            return {
                type: type,
                attributes: attributes
            };
        } catch (error) {
            console.error('Error parsing action attributes:', error);
            return null;
        }
    }

    /**
     * 验证action是否有效
     * @param {XmlAction} action - 要验证的动作对象
     * @returns {boolean} 是否有效
     */
    validateAction(action) {
        // 基本验证
        if (!action || !action.type) {
            return false;
        }

        // 根据不同的action type进行特定验证
        switch (action.type) {
            case 'createNewTab':
                return !!action.attributes.url;
            // 可以添加其他类型的验证
            default:
                return true;
        }
    }
}

/**
 * @typedef {Object} XmlAction
 * @property {string} type - 动作类型
 * @property {Object} attributes - 动作的属性集合
 * @property {string} [content] - 动作的文本内容（可选）
 */

// 创建单例实例
const xmlManager = new XMLManager();
export default xmlManager; 

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

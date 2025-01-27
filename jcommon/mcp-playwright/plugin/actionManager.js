// 页面操作管理类
export class ActionManager {
    constructor() {
        // 默认操作配置
        this.defaultOptions = {
            // 点击配置
            clickOptions: {
                delay: 100,        // 点击前延迟时间（毫秒）
                doubleClick: false // 是否双击
            },
            // 填写内容配置
            fillOptions: {
                delay: 50,         // 每个字符输入延迟（毫秒）
                clear: true,       // 填写前是否清空
                blur: true         // 填写后是否失去焦点
            },
            // 选择配置
            selectOptions: {
                timeout: 5000      // 等待元素出现的超时时间
            }
        };

        // 记录最后操作的元素
        this.lastElement = null;
    }

    // 等待元素出现
    async waitForElement(selector, options = {}) {
        const timeout = options.timeout || this.defaultOptions.selectOptions.timeout;
        const startTime = Date.now();

        return new Promise((resolve, reject) => {
            const checkElement = () => {
                const element = document.querySelector(selector);
                if (element) {
                    resolve(element);
                    return;
                }

                if (Date.now() - startTime >= timeout) {
                    reject(new Error(`Timeout waiting for element: ${selector}`));
                    return;
                }

                requestAnimationFrame(checkElement);
            };

            checkElement();
        });
    }

    // 点击元素
    async click(selector, options = {}) {
        try {
            const element = await this.waitForElement(selector);
            const clickOpts = { ...this.defaultOptions.clickOptions, ...options };

            // 保存最后操作的元素
            this.lastElement = element;

            // 滚动元素到视图
            element.scrollIntoView({ behavior: 'smooth', block: 'center' });

            // 点击前延迟
            await new Promise(resolve => setTimeout(resolve, clickOpts.delay));

            // 添加点击高亮效果
            this.addClickEffect(element);

            // 执行点击
            if (clickOpts.doubleClick) {
                element.dispatchEvent(new MouseEvent('dblclick', {
                    bubbles: true,
                    cancelable: true,
                    view: window
                }));
            } else {
                element.click();
            }

            console.log('Clicked element:', selector);
            return true;
        } catch (error) {
            console.error('Error clicking element:', error);
            throw error;
        }
    }

    // 填写内容
    async fill(selector, text, options = {}) {
        try {
            const element = await this.waitForElement(selector);
            const fillOpts = { ...this.defaultOptions.fillOptions, ...options };

            // 保存最后操作的元素
            this.lastElement = element;

            // 聚焦元素
            element.focus();

            // 如果需要，清空现有内容
            if (fillOpts.clear) {
                element.value = '';
            }

            // 模拟人工输入
            for (const char of text) {
                element.value += char;
                element.dispatchEvent(new Event('input', { bubbles: true }));
                await new Promise(resolve => setTimeout(resolve, fillOpts.delay));
            }

            // 触发change事件
            element.dispatchEvent(new Event('change', { bubbles: true }));

            // 如果需要，失去焦点
            if (fillOpts.blur) {
                element.blur();
            }

            console.log('Filled element:', selector, 'with text:', text);
            return true;
        } catch (error) {
            console.error('Error filling element:', error);
            throw error;
        }
    }

    // 选择下拉框选项
    async select(selector, value, options = {}) {
        try {
            const element = await this.waitForElement(selector);
            
            if (element.tagName.toLowerCase() !== 'select') {
                throw new Error('Element is not a select element');
            }

            // 保存最后操作的元素
            this.lastElement = element;

            // 设置选中值
            element.value = value;
            element.dispatchEvent(new Event('change', { bubbles: true }));

            console.log('Selected value:', value, 'in element:', selector);
            return true;
        } catch (error) {
            console.error('Error selecting option:', error);
            throw error;
        }
    }

    // 添加点击视觉效果
    addClickEffect(element) {
        const ripple = document.createElement('div');
        ripple.style.cssText = `
            position: absolute;
            width: 20px;
            height: 20px;
            background: rgba(255, 0, 0, 0.4);
            border-radius: 50%;
            transform: translate(-50%, -50%);
            pointer-events: none;
            animation: ripple 0.5s linear;
        `;

        const rect = element.getBoundingClientRect();
        ripple.style.left = rect.left + rect.width / 2 + 'px';
        ripple.style.top = rect.top + rect.height / 2 + 'px';

        document.body.appendChild(ripple);
        setTimeout(() => ripple.remove(), 500);
    }

    // 获取最后操作的元素
    getLastElement() {
        return this.lastElement;
    }

    // 清除最后操作的元素
    clearLastElement() {
        this.lastElement = null;
    }

    // 检查元素是否可见
    isVisible(element) {
        const rect = element.getBoundingClientRect();
        return (
            rect.width > 0 &&
            rect.height > 0 &&
            window.getComputedStyle(element).visibility !== 'hidden' &&
            window.getComputedStyle(element).display !== 'none'
        );
    }

    // 检查元素是否可操作
    isInteractable(element) {
        return (
            this.isVisible(element) &&
            !element.disabled &&
            window.getComputedStyle(element).pointerEvents !== 'none'
        );
    }
}

// 创建单例实例
const actionManager = new ActionManager();
export default actionManager; 
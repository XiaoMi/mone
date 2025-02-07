// 页面操作管理类
export class ActionManager2 {
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
            },
            // 添加回车操作配置
            enterOptions: {
                delay: 100,        // 回车前延迟时间（毫秒）
                focusFirst: true   // 是否先聚焦元素
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

            this.lastElement = element;
            element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            await new Promise(resolve => setTimeout(resolve, clickOpts.delay));
            this.addClickEffect(element);

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
            console.log('开始填写内容 selector:'+selector);
            const element = await this.waitForElement(selector);
            const fillOpts = { ...this.defaultOptions.fillOptions, ...options };

            this.lastElement = element;
            element.focus();

            if (fillOpts.clear) {
                element.value = '';
            }

            for (const char of text) {
                element.value += char;
                element.dispatchEvent(new Event('input', { bubbles: true }));
                await new Promise(resolve => setTimeout(resolve, fillOpts.delay));
            }

            element.dispatchEvent(new Event('change', { bubbles: true }));

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

    // 其他方法保持不变...
    // (为了简洁，我省略了其他方法，但它们应该保持相同的实现)
}

// 创建单例实例并导出
const actionManager2 = new ActionManager2();
export default actionManager2; 
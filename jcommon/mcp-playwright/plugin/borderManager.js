// 定义需要添加边框的元素类型
const VALID_ELEMENTS = ['div', 'section', 'article', 'main', 'aside', 'header', 'footer', 'nav'];

// 定义最小元素尺寸（像素）
const MIN_ELEMENT_SIZE = 50;

export class BorderManager {
    constructor() {
        this.borderedElements = new Set();
        this.isActive = false;
    }

    // 检查元素是否满足添加边框的条件
    isValidElement(element) {
        const rect = element.getBoundingClientRect();
        
        // 检查元素大小
        if (rect.width < MIN_ELEMENT_SIZE || rect.height < MIN_ELEMENT_SIZE) {
            return false;
        }

        // 检查元素类型
        if (!VALID_ELEMENTS.includes(element.tagName.toLowerCase())) {
            return false;
        }

        // 检查是否已经有边框
        if (this.borderedElements.has(element)) {
            return false;
        }

        // 检查父元素是否已经有边框（避免嵌套）
        let parent = element.parentElement;
        while (parent) {
            if (this.borderedElements.has(parent)) {
                return false;
            }
            parent = parent.parentElement;
        }

        return true;
    }

    // 添加边框
    addBorder(element) {
        const originalStyle = element.getAttribute('style') || '';
        element.style.border = '2px solid red';
        element.style.boxSizing = 'border-box';
        element.dataset.originalStyle = originalStyle;
        this.borderedElements.add(element);
    }

    // 移除边框
    removeBorder(element) {
        if (element.dataset.originalStyle) {
            element.setAttribute('style', element.dataset.originalStyle);
        } else {
            element.removeAttribute('style');
        }
        delete element.dataset.originalStyle;
        this.borderedElements.delete(element);
    }

    // 切换边框状态
    toggle() {
        this.isActive = !this.isActive;
        return this.isActive;
    }

    // 清除所有边框
    clearAllBorders() {
        this.borderedElements.forEach(element => {
            this.removeBorder(element);
        });
        this.borderedElements.clear();
    }
} 
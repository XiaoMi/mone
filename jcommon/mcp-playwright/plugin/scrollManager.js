// 滚动条管理类
export class ScrollManager {
    constructor() {
        // 存储初始滚动位置
        this.initialPosition = {
            x: 0,
            y: 0
        };
        
        // 存储上一次滚动位置
        this.lastPosition = {
            x: 0,
            y: 0
        };

        // 默认滚动配置
        this.defaultScrollOptions = {
            behavior: 'smooth',
            block: 'start',
            inline: 'nearest'
        };
    }

    // 获取当前滚动位置
    getCurrentPosition() {
        return {
            x: window.pageXOffset || window.scrollX,
            y: window.pageYOffset || window.scrollY
        };
    }

    // 保存当前位置
    saveCurrentPosition() {
        const position = this.getCurrentPosition();
        this.lastPosition = { ...position };
        return position;
    }

    // 滚动到指定位置
    async scrollTo(x, y, options = {}) {
        try {
            const scrollOptions = {
                behavior: options.behavior || this.defaultScrollOptions.behavior,
                // 其他选项...
            };

            window.scrollTo({
                left: x,
                top: y,
                behavior: scrollOptions.behavior
            });

            // 如果是平滑滚动，等待滚动完成
            if (scrollOptions.behavior === 'smooth') {
                await this.waitForScrollEnd();
            }

            console.log('Scrolled to position:', { x, y });
            return true;
        } catch (error) {
            console.error('Error scrolling to position:', error);
            throw error;
        }
    }

    // 滚动到元素
    async scrollToElement(element, options = {}) {
        try {
            if (!(element instanceof Element)) {
                throw new Error('Invalid element provided');
            }

            const scrollOptions = {
                ...this.defaultScrollOptions,
                ...options
            };

            element.scrollIntoView(scrollOptions);

            // 如果是平滑滚动，等待滚动完成
            if (scrollOptions.behavior === 'smooth') {
                await this.waitForScrollEnd();
            }

            console.log('Scrolled to element:', element);
            return true;
        } catch (error) {
            console.error('Error scrolling to element:', error);
            throw error;
        }
    }

    // 自动滚动到底部
    async autoScrollToBottom(options = {}) {
        try {
            const {
                speed = 50,  // 滚动速度（像素/帧）
                interval = 16  // 滚动间隔（毫秒）
            } = options;

            return new Promise((resolve) => {
                const scrollInterval = setInterval(() => {
                    const currentPosition = this.getCurrentPosition();
                    const maxScroll = Math.max(
                        document.body.scrollHeight,
                        document.documentElement.scrollHeight
                    ) - window.innerHeight;

                    if (currentPosition.y >= maxScroll) {
                        clearInterval(scrollInterval);
                        resolve(true);
                        return;
                    }

                    window.scrollBy(0, speed);
                }, interval);
            });
        } catch (error) {
            console.error('Error auto-scrolling to bottom:', error);
            throw error;
        }
    }

    // 返回到上一个位置
    async returnToLastPosition() {
        try {
            await this.scrollTo(this.lastPosition.x, this.lastPosition.y);
            console.log('Returned to last position:', this.lastPosition);
            return true;
        } catch (error) {
            console.error('Error returning to last position:', error);
            throw error;
        }
    }

    // 获取滚动进度（0-100）
    getScrollProgress() {
        const currentPosition = this.getCurrentPosition();
        const maxScroll = Math.max(
            document.body.scrollHeight,
            document.documentElement.scrollHeight
        ) - window.innerHeight;

        return Math.round((currentPosition.y / maxScroll) * 100);
    }

    // 等待滚动结束
    waitForScrollEnd() {
        return new Promise(resolve => {
            let lastPos = this.getCurrentPosition().y;
            const checkScrollEnd = setInterval(() => {
                const currentPos = this.getCurrentPosition().y;
                if (currentPos === lastPos) {
                    clearInterval(checkScrollEnd);
                    resolve();
                }
                lastPos = currentPos;
            }, 50); // 每50ms检查一次
        });
    }

    // 监听滚动事件
    addScrollListener(callback, options = {}) {
        const {
            throttleMs = 100,  // 节流时间（毫秒）
            passive = true     // 是否使用被动事件监听
        } = options;

        let lastCall = 0;
        const throttledCallback = (event) => {
            const now = Date.now();
            if (now - lastCall >= throttleMs) {
                callback({
                    position: this.getCurrentPosition(),
                    progress: this.getScrollProgress(),
                    event
                });
                lastCall = now;
            }
        };

        window.addEventListener('scroll', throttledCallback, { passive });
        return () => window.removeEventListener('scroll', throttledCallback);
    }
}

// 创建单例实例
const scrollManager = new ScrollManager();
export default scrollManager; 
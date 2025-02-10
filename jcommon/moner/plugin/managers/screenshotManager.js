// 截图管理类
class ScreenshotManager {
    constructor() {
        // 可以在这里添加配置项
        this.config = {
            format: 'jpeg',
            quality: 10,
            defaultFilename: 'screenshot.jpeg'
        };
    }

    // 捕获完整页面的截图
    async captureFullPage() {
        try {
            // 获取当前活动标签页
            const [activeTab] = await chrome.tabs.query({ active: true, currentWindow: true });
            
            // 注入测量页面尺寸和滚动位置的脚本
            const [{result}] = await chrome.scripting.executeScript({
                target: { tabId: activeTab.id },
                function: () => {
                    return {
                        width: Math.max(
                            document.documentElement.scrollWidth,
                            document.body.scrollWidth,
                            document.documentElement.clientWidth
                        ),
                        height: Math.max(
                            document.documentElement.scrollHeight,
                            document.body.scrollHeight,
                            document.documentElement.clientHeight
                        ),
                        viewportHeight: window.innerHeight,
                        originalScrollTop: window.scrollY
                    };
                }
            });

            const { height, viewportHeight, originalScrollTop } = result;
            const totalHeight = height;
            const screenshots = [];
            
            // 分段截图
            for (let currentPosition = 0; currentPosition < totalHeight; currentPosition += viewportHeight) {
                // 滚动到指定位置
                await chrome.scripting.executeScript({
                    target: { tabId: activeTab.id },
                    function: (scrollTo) => {
                        window.scrollTo(0, scrollTo);
                    },
                    args: [currentPosition]
                });

                // 等待页面重绘
                await new Promise(resolve => setTimeout(resolve, 150));

                // 捕获当前视口的截图
                const screenshot = await chrome.tabs.captureVisibleTab(null, {
                    format: this.config.format,
                    quality: this.config.quality
                });
                screenshots.push(screenshot);
            }

            // 恢复原始滚动位置
            await chrome.scripting.executeScript({
                target: { tabId: activeTab.id },
                function: (scrollTo) => {
                    window.scrollTo(0, scrollTo);
                },
                args: [originalScrollTop]
            });

            // 创建 canvas 合并图片
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            canvas.width = result.width;
            canvas.height = totalHeight;

            // 加载并绘制所有截图
            for (let i = 0; i < screenshots.length; i++) {
                const img = await this.loadImage(screenshots[i]);
                ctx.drawImage(img, 0, i * viewportHeight);
            }

            // 将合并后的图片转换为 blob 并下载
            canvas.toBlob(async (blob) => {
                await chrome.downloads.download({
                    url: URL.createObjectURL(blob),
                    filename: 't.jpeg',
                    saveAs: false
                });
                console.log('Full page screenshot saved successfully');
            }, 'image/jpeg', 0.9);

            return true;

        } catch (error) {
            console.error('Error capturing full page screenshot:', error);
            throw error;
        }
    }

    // 捕获当前可视区域的截图
    async captureVisibleArea(allowDownload = false, screenshot) {
        try {
            // 创建一个 canvas 来处理图片
            const img = await this.loadImage(screenshot);
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            let scale = 1.0/window.devicePixelRatio;
            canvas.width = img.width * scale;
            canvas.height = img.height * scale; 
            
            // 绘制原始图片
            ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
            
            // 设置网格线样式
            ctx.strokeStyle = 'rgba(200, 200, 200, 0.2)'; // 更透明的网格线
            ctx.lineWidth = 0.5;
            ctx.font = '10px Arial';
            ctx.fillStyle = 'rgba(100, 100, 100, 0.7)';
            
            const GRID_COUNT = 20; // 网格数量
            const rowHeight = canvas.height / GRID_COUNT;
            const columnWidth = canvas.width / GRID_COUNT;
            
            // 绘制水平线
            for (let i = 1; i < GRID_COUNT; i++) {
                const y = Math.round(rowHeight * i);
                ctx.beginPath();
                ctx.moveTo(0, y);
                ctx.lineTo(canvas.width, y);
                ctx.stroke();
            }
            
            // 绘制垂直线
            for (let i = 1; i < GRID_COUNT; i++) {
                const x = Math.round(columnWidth * i);
                ctx.beginPath();
                ctx.moveTo(x, 0);
                ctx.lineTo(x, canvas.height);
                ctx.stroke();
            }
            
            // 在交叉点绘制坐标
            ctx.font = '9px Arial';
            for (let i = 1; i < GRID_COUNT; i++) {
                for (let j = 1; j < GRID_COUNT; j++) {
                    const x = Math.round(columnWidth * i);
                    const y = Math.round(rowHeight * j);
                    
                    // 计算实际坐标值
                    const coordX = Math.round(canvas.width * (i / GRID_COUNT));
                    const coordY = Math.round(canvas.height * (j / GRID_COUNT));
                    
                    // 绘制小点
                    ctx.fillStyle = 'rgba(100, 100, 100, 0.5)';
                    ctx.beginPath();
                    ctx.arc(x, y, 1.5, 0, 2 * Math.PI);
                    ctx.fill();
                    
                    // 绘制坐标文本
                    ctx.fillStyle = 'rgba(100, 100, 100, 0.7)';
                    const coordText = `(${coordX},${coordY})`;
                    
                    // 根据位置调整文本显示方向，避免超出边界
                    const textX = x + 3;
                    const textY = y - 3;
                    
                    // 每隔一个交叉点显示坐标，避免文字重叠
                    if ((i % 2 === 1) && (j % 2 === 1)) {
                        ctx.fillText(coordText, textX, textY);
                    }
                }
            }

            // 将图片复制到剪贴板
            try {
                canvas.toBlob(async (blob) => {
                    const clipboardItem = new ClipboardItem({
                        'image/png': blob
                    });
                    await navigator.clipboard.write([clipboardItem]);
                    console.log('Screenshot copied to clipboard successfully');
                }, 'image/png');
            } catch (clipboardError) {
                console.error('Error copying to clipboard:', clipboardError);
            }

            // 只在 allowDownload 为 true 时下载截图
            if (allowDownload) {
                await chrome.downloads.download({
                    url: screenshot,
                    filename: this.config.defaultFilename,
                    saveAs: false
                });
                console.log('Visible area screenshot saved successfully');
            }

            return true;

        } catch (error) {
            console.error('Error capturing visible area screenshot:', error);
            throw error;
        }
    }

    // 获取当前可视区域的截图
    async getVisibleAreaScreenshot() {
        try {
            // 获取当前活动标签页的截图
            const screenshot = await chrome.tabs.captureVisibleTab(null, {
                format: this.config.format,
                quality: this.config.quality
            });
            
            return screenshot;
        } catch (error) {
            console.error('Error getting visible area screenshot:', error);
            throw error;
        }
    }

    // 辅助函数：将图片 URL 加载为 Image 对象
    loadImage(url) {
        return new Promise((resolve, reject) => {
            const img = new Image();
            img.onload = () => resolve(img);
            img.onerror = reject;
            img.src = url;
        });
    }
}

// 创建单例实例
window.screenshotManager = new ScreenshotManager(); 
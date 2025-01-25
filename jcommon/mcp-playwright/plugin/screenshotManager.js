// 捕获完整页面的截图
async function captureFullPage() {
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
                format: 'jpeg',
                quality: 90
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
            const img = await loadImage(screenshots[i]);
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

// 辅助函数：将图片 URL 加载为 Image 对象
function loadImage(url) {
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => resolve(img);
        img.onerror = reject;
        img.src = url;
    });
}

// 导出函数供其他文件使用
export {
    captureFullPage
}; 
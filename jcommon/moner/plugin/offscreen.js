// 监听来自background的消息
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'MERGE_SCREENSHOTS') {
        mergeScreenshots(message.data)
            .then(dataUrl => {
                sendResponse({ success: true, dataUrl });
            })
            .catch(error => {
                sendResponse({ success: false, error: error.message });
            });
        return true; // 保持消息通道开放
    }
});

// 合并截图的函数
async function mergeScreenshots({ screenshots, width, height, viewportHeight }) {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    canvas.width = width;
    canvas.height = height;

    // 加载并绘制所有截图
    for (let i = 0; i < screenshots.length; i++) {
        const img = await loadImage(screenshots[i]);
        ctx.drawImage(img, 0, i * viewportHeight);
    }

    return canvas.toDataURL('image/jpeg', 0.9);
}

// 加载图片的辅助函数
function loadImage(url) {
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => resolve(img);
        img.onerror = reject;
        img.src = url;
    });
} 
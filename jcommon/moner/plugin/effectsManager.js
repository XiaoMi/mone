// 特效状态管理
let effectInterval = null;
let currentEffect = null;

// 创建雨滴/雪花元素
function createParticle(type) {
    const particle = document.createElement('div');
    particle.className = `particle ${type}`;
    particle.style.left = Math.random() * 100 + 'vw';
    particle.style.animationDuration = (Math.random() * 1 + 0.5) + 's';
    particle.style.opacity = Math.random();
    return particle;
}

// 添加特效
function addEffect(type, tabId) {
    // 注入CSS
    chrome.scripting.insertCSS({
        target: { tabId },
        css: `
            .particle {
                position: fixed;
                pointer-events: none;
                z-index: 9999;
                animation: fall linear forwards;
            }
            
            .rain {
                width: 2px;
                height: 20px;
                background: linear-gradient(transparent, #00aaff);
            }
            
            .snow {
                width: 6px;
                height: 6px;
                background: white;
                border-radius: 50%;
                box-shadow: 0 0 5px white;
            }
            
            @keyframes fall {
                from {
                    transform: translateY(-5vh);
                }
                to {
                    transform: translateY(105vh);
                }
            }
        `
    });

    // 注入特效脚本
    chrome.scripting.executeScript({
        target: { tabId },
        function: (effectType) => {
            if (window._effectInterval) {
                clearInterval(window._effectInterval);
            }

            const container = document.createElement('div');
            container.id = 'effect-container';
            container.style.position = 'fixed';
            container.style.top = '0';
            container.style.left = '0';
            container.style.width = '100%';
            container.style.height = '100%';
            container.style.pointerEvents = 'none';
            container.style.zIndex = '9999';
            document.body.appendChild(container);

            window._effectInterval = setInterval(() => {
                const particle = document.createElement('div');
                particle.className = `particle ${effectType}`;
                particle.style.left = Math.random() * 100 + 'vw';
                particle.style.animationDuration = (Math.random() * 1 + 0.5) + 's';
                particle.style.opacity = Math.random();
                
                container.appendChild(particle);
                
                // 动画结束后移除粒子
                setTimeout(() => {
                    particle.remove();
                }, 2000);
            }, 50);
        },
        args: [type]
    });
}

// 移除特效
function removeEffect(tabId) {
    chrome.scripting.executeScript({
        target: { tabId },
        function: () => {
            if (window._effectInterval) {
                clearInterval(window._effectInterval);
                window._effectInterval = null;
            }
            const container = document.getElementById('effect-container');
            if (container) {
                container.remove();
            }
        }
    });
}

// 切换特效
async function toggleEffect(type) {
    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
    
    if (currentEffect === type) {
        // 如果当前特效与点击的特效相同，则移除特效
        removeEffect(tab.id);
        currentEffect = null;
        return false; // 返回false表示特效已关闭
    } else {
        // 如果有其他特效在运行，先移除
        if (currentEffect) {
            removeEffect(tab.id);
        }
        // 添加新特效
        addEffect(type, tab.id);
        currentEffect = type;
        return true; // 返回true表示特效已开启
    }
}

export {
    toggleEffect
}; 
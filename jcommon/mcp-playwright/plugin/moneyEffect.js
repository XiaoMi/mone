// Money effect animation class
class MoneyEffect {
    static #intervals = new Map(); // 存储每个tab的interval ID

    static async toggleEffect() {
        try {
            const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
            
            // Check if effect is already running
            const [{ result: isEffectActive }] = await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                func: () => !!document.getElementById('money-effect-container')
            });

            if (isEffectActive) {
                // Remove effect
                await chrome.scripting.executeScript({
                    target: { tabId: tab.id },
                    func: () => {
                        const container = document.getElementById('money-effect-container');
                        if (container) {
                            container.remove();
                        }
                        // 清理style标签
                        const style = document.getElementById('money-effect-style');
                        if (style) {
                            style.remove();
                        }
                        // 清理所有相关interval
                        const intervals = window._moneyEffectIntervals || [];
                        intervals.forEach(id => clearInterval(id));
                        window._moneyEffectIntervals = [];
                    }
                });
                
                // 清理stored interval
                if (this.#intervals.has(tab.id)) {
                    this.#intervals.delete(tab.id);
                }
                return false;
            } else {
                // Add effect
                await chrome.scripting.executeScript({
                    target: { tabId: tab.id },
                    func: () => {
                        // 创建容器
                        const container = document.createElement('div');
                        container.id = 'money-effect-container';
                        container.style.cssText = `
                            position: fixed;
                            top: 0;
                            left: 0;
                            width: 100%;
                            height: 100%;
                            pointer-events: none;
                            z-index: 9999;
                        `;
                        document.body.appendChild(container);

                        // 创建金钱元素的函数
                        function createMoney() {
                            const money = document.createElement('div');
                            money.innerHTML = '💰';
                            money.style.cssText = `
                                position: absolute;
                                font-size: ${Math.random() * 20 + 10}px;
                                left: ${Math.random() * 100}%;
                                animation: moneyFall ${Math.random() * 3 + 2}s linear;
                            `;
                            container.appendChild(money);

                            money.addEventListener('animationend', () => {
                                money.remove();
                            });
                        }

                        // 添加CSS动画
                        const style = document.createElement('style');
                        style.id = 'money-effect-style';
                        style.textContent = `
                            @keyframes moneyFall {
                                0% {
                                    transform: translateY(-20px) rotate(0deg);
                                    opacity: 1;
                                }
                                100% {
                                    transform: translateY(100vh) rotate(360deg);
                                    opacity: 0.3;
                                }
                            }
                        `;
                        document.head.appendChild(style);

                        // 存储intervals以便清理
                        window._moneyEffectIntervals = window._moneyEffectIntervals || [];
                        
                        // 创建金钱元素的interval
                        const interval = setInterval(() => {
                            if (!document.getElementById('money-effect-container')) {
                                clearInterval(interval);
                                return;
                            }
                            createMoney();
                        }, 200);

                        window._moneyEffectIntervals.push(interval);
                    }
                });
                return true;
            }
        } catch (error) {
            console.error('Money effect error:', error);
            return false;
        }
    }
}

// 导出
export default MoneyEffect;
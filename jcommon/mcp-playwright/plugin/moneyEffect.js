// Money effect animation class
export class MoneyEffect {
    static async toggleEffect() {
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
                }
            });
            return false;
        } else {
            // Add effect
            await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                func: () => {
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

                    function createMoney() {
                        const money = document.createElement('div');
                        money.innerHTML = '💰';
                        money.style.cssText = `
                            position: absolute;
                            font-size: ${Math.random() * 20 + 10}px;
                            left: ${Math.random() * 100}%;
                            animation: fall ${Math.random() * 3 + 2}s linear;
                        `;
                        container.appendChild(money);

                        money.addEventListener('animationend', () => {
                            money.remove();
                        });
                    }

                    // Add CSS animation
                    const style = document.createElement('style');
                    style.textContent = `
                        @keyframes fall {
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

                    // Create money at intervals
                    const interval = setInterval(() => {
                        if (!document.getElementById('money-effect-container')) {
                            clearInterval(interval);
                            return;
                        }
                        createMoney();
                    }, 200);
                }
            });
            return true;
        }
    }
} 
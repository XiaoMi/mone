import errorManager from './errorManager.js';

document.addEventListener('DOMContentLoaded', () => {
    const errorList = document.getElementById('errorList');
    const refreshBtn = document.getElementById('refreshBtn');
    const clearBtn = document.getElementById('clearBtn');

    // 渲染错误列表
    function renderErrors() {
        const errors = errorManager.getErrors();
        
        if (errors.length === 0) {
            errorList.innerHTML = '<div class="no-errors">暂无错误记录</div>';
            return;
        }

        errorList.innerHTML = errors.map(error => `
            <div class="error-item">
                <div class="error-header">
                    <span class="error-level level-${error.level}">${error.level.toUpperCase()}</span>
                    <span class="error-timestamp">${new Date(error.timestamp).toLocaleString()}</span>
                </div>
                <div class="error-message">${error.message}</div>
                ${error.stack ? `
                    <div class="error-details">
                        ${error.stack}
                    </div>
                ` : ''}
            </div>
        `).join('');
    }

    // 初始渲染
    renderErrors();

    // 添加错误监听器
    errorManager.addListener(() => {
        renderErrors();
    });

    // 刷新按钮事件
    refreshBtn.addEventListener('click', () => {
        renderErrors();
    });

    // 清除按钮事件
    clearBtn.addEventListener('click', () => {
        errorManager.clearErrors();
        renderErrors();
    });
}); 
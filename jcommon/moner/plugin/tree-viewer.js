document.addEventListener('DOMContentLoaded', async () => {
    try {
        // 从storage中获取DOM树数据
        const result = await chrome.storage.local.get('lastDomTreeData');
        const treeData = result.lastDomTreeData;
        
        if (!treeData) {
            document.getElementById('treeContainer').innerHTML = 
                '<p style="color: red;">No DOM tree data found</p>';
            return;
        }

        // 渲染树数据
        const treeContainer = document.getElementById('treeContainer');
        
        // 创建复制按钮
        const copyButton = document.createElement('button');
        copyButton.textContent = 'Copy HTML';
        copyButton.style.marginBottom = '10px';
        treeContainer.appendChild(copyButton);
        
        // 创建pre元素来保持格式
        const preElement = document.createElement('pre');
        treeContainer.appendChild(preElement);
        
        // 生成HTML字符串
        const htmlString = generateHtmlString(treeData, 0);
        preElement.textContent = htmlString;

        // 添加复制功能
        copyButton.addEventListener('click', () => {
            navigator.clipboard.writeText(htmlString)
                .then(() => {
                    const originalText = copyButton.textContent;
                    copyButton.textContent = 'Copied!';
                    setTimeout(() => {
                        copyButton.textContent = originalText;
                    }, 2000);
                })
                .catch(err => {
                    console.error('Failed to copy:', err);
                    copyButton.textContent = 'Copy failed';
                });
        });
    } catch (error) {
        console.error('Error loading tree data:', error);
        document.getElementById('treeContainer').innerHTML = 
            `<p style="color: red;">Error loading tree data: ${error.message}</p>`;
    }
});

function generateHtmlString(node, indent = 0) {
    if (!node) return '';

    const indentStr = '    '.repeat(indent);
    let html = '';

    // 开始标签
    if (node.tagName) {
        html += `${indentStr}<${node.tagName.toLowerCase()}`;
        
        // 添加属性
        if (node.attributes && typeof node.attributes === 'object') {
            for (const [key, value] of Object.entries(node.attributes)) {
                if (value !== null && value !== undefined) {
                    html += ` ${key}="${value}"`;
                }
            }
        }
        html += '>\n';
    }

    // 处理子节点
    if (node.children && Array.isArray(node.children)) {
        node.children.forEach(child => {
            html += generateHtmlString(child, indent + 1);
        });
    }

    // 处理文本内容
    if (node.textContent && typeof node.textContent === 'string' && node.textContent.trim()) {
        html += `${indentStr}    ${node.textContent.trim()}\n`;
    }

    // 结束标签
    if (node.tagName) {
        html += `${indentStr}</${node.tagName.toLowerCase()}>\n`;
    }

    return html;
} 
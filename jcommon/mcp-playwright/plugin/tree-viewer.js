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
        renderTree(treeData, treeContainer);
    } catch (error) {
        console.error('Error loading tree data:', error);
        document.getElementById('treeContainer').innerHTML = 
            `<p style="color: red;">Error loading tree data: ${error.message}</p>`;
    }
});

function renderTree(node, container) {
    // 添加空值检查
    if (!node) {
        console.warn('Encountered null node in tree data');
        return;
    }

    const nodeDiv = document.createElement('div');
    nodeDiv.className = 'tree-node';

    // 创建节点内容 - 添加tagName的空值检查
    let content = `<span class="tag-name">${node.tagName || 'UNKNOWN'}</span>`;
    
    // 添加属性信息 - 增加防护检查
    if (node.attributes && typeof node.attributes === 'object' && Object.keys(node.attributes).length > 0) {
        content += ' <span class="attributes">';
        for (const [key, value] of Object.entries(node.attributes)) {
            if (value !== null && value !== undefined) {
                content += `${key}="${value}" `;
            }
        }
        content += '</span>';
    }

    // 添加文本内容 - 增加防护检查
    if (node.textContent && typeof node.textContent === 'string' && node.textContent.trim()) {
        content += ` <span class="text-content">"${node.textContent.trim()}"</span>`;
    }

    nodeDiv.innerHTML = content;
    container.appendChild(nodeDiv);

    // 递归渲染子节点 - 增加防护检查
    if (node.children && Array.isArray(node.children) && node.children.length > 0) {
        const childContainer = document.createElement('div');
        childContainer.style.marginLeft = '20px';
        nodeDiv.appendChild(childContainer);
        
        node.children.forEach(child => {
            renderTree(child, childContainer);
        });
    }
} 
// 监听鼠标移动事件
document.addEventListener('mousemove', (event) => {
    // chrome.runtime.sendMessage({
    //     type: 'mousePosition',
    //     x: event.clientX,
    //     y: event.clientY
    // });
    //console.log("mousemove:" + event.clientX + "," + event.clientY);
});

// 添加脚本执行处理函数
function executeScriptInPage(code) {
    try {
        // 创建script标签
        const script = document.createElement('script');
        // 将代码包装在立即执行函数中
        script.textContent = `(function(){${code}})();`;
        // 将script标签添加到文档中
        document.body.appendChild(script);
        // 执行完后移除script标签
        //script.remove();
        return true;
    } catch (error) {
        console.error('Error executing script:', error);
        return { error: error.message };
    }
}

// 创建一个标记来追踪是否是确认后的点击
let isConfirmedClick = false;

// 监听鼠标点击事件
document.addEventListener('click', async (event) => {
    // 如果是确认后的点击，直接执行并重置标记
    if (isConfirmedClick) {
        isConfirmedClick = false;
        return;
    }

    if (true) {
        return;
    }
    
    // 阻止默认点击行为
    event.preventDefault();
    
    // 显示确认对话框
    const isConfirmed = confirm('是否确认执行此操作？');
    
    if (isConfirmed) {
        // 用户点击确认，发送消息
        chrome.runtime.sendMessage({
            type: 'mouseClick',
            x: event.clientX,
            y: event.clientY
        });
        console.log("click:" + event.clientX + "," + event.clientY);
        
        // 设置标记并触发原始点击
        isConfirmedClick = true;
        event.target.click();
    } else {
        // 获取窗口的宽度和高度
        const windowWidth = window.innerWidth;
        const windowHeight = window.innerHeight;
        
        console.log(
            "用户取消了操作:" +
            "x " + event.clientX + ",y " + event.clientY + 
            " | 窗口大小: 宽度 " + windowWidth + "px, 高度 " + windowHeight + "px"
        );

        // 发送消息给background script来执行截图
        chrome.runtime.sendMessage({
            type: 'captureScreen',
            width: windowWidth,
            height: windowHeight
        });
    }
});

// 获取元素的唯一选择器
function getSelector(element) {
    if (!(element instanceof Element)) return;
    
    // 1. 如果有id，直接返回
    if (element.id) {
        return '#' + element.id;
    }
    
    // 2. 尝试使用class组合
    if (element.className) {
        const classes = Array.from(element.classList)
            .filter(cls => cls && !cls.includes(' '))
            .join('.');
        if (classes) {
            const selector = '.' + classes;
            if (document.querySelectorAll(selector).length === 1) {
                return selector;
            }
        }
    }
    
    // 3. 生成完整路径
    let path = [];
    let current = element;
    while (current && current.nodeType === Node.ELEMENT_NODE) {
        let selector = current.nodeName.toLowerCase();
        if (current.id) {
            selector = '#' + current.id;
            path.unshift(selector);
            break;
        } else {
            let nth = 1;
            let sibling = current;
            while (sibling.previousElementSibling) {
                sibling = sibling.previousElementSibling;
                if (sibling.nodeName.toLowerCase() === selector) nth++;
            }
            if (nth !== 1) selector += `:nth-of-type(${nth})`;
        }
        path.unshift(selector);
        current = current.parentNode;
    }
    return path.join(' > ');
}

// 存储最后的右键点击坐标
let lastContextMenuClick = { x: 0, y: 0 };

// 监听右键点击事件
document.addEventListener('contextmenu', function(e) {
    // 存储点击的页面坐标
    lastContextMenuClick = {
        x: e.pageX,
        y: e.pageY
    };
});

// 监听来自background的消息
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.action === 'getElementPosition') {
        const element = document.querySelector(message.selector);
        if (element) {
            const rect = element.getBoundingClientRect();
            const scrollX = window.scrollX || window.pageXOffset;
            const scrollY = window.scrollY || window.pageYOffset;
            
            sendResponse({
                success: true,
                position: {
                    x: Math.round(rect.left + scrollX + rect.width / 2),
                    y: Math.round(rect.top + scrollY + rect.height / 2)
                }
            });
        } else {
            sendResponse({ success: false });
        }
        return true;
    }
    //截图
    if (message.type === 'takeScreenshot') {
        console.log('Taking screenshot from content script');
        screenshotManager.captureVisibleArea(false,message.data);
    }

    if (message.type === 'executeScript') {
        console.log('Executing script from content script');
        const result = executeScriptInPage(message.code);
        sendResponse(result);
        return true;
    }

    if (message.type === 'toggleSelector' && message.active) {
        // 使用存储的右键点击坐标
        const x = lastContextMenuClick.x;
        const y = lastContextMenuClick.y;
        
        // 这里可以使用准确的坐标进行元素定位和选择器生成
        console.log('Right click coordinates:', x, y);
        
        // 获取点击位置的元素
        const element = document.elementFromPoint(
            x - window.pageXOffset,
            y - window.pageYOffset
        );
        
        if (element) {
            // 生成选择器等后续操作...
            const selector = getSelector(element);
            // 发送选择器回background
            chrome.runtime.sendMessage({
                type: 'elementSelector',
                selector: selector
            });
        }
    }
});



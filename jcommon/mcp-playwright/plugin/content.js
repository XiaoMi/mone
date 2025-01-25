//alert("content.js");

// 监听鼠标移动事件
document.addEventListener('mousemove', (event) => {
    // chrome.runtime.sendMessage({
    //     type: 'mousePosition',
    //     x: event.clientX,
    //     y: event.clientY
    // });
    //console.log("mousemove:" + event.clientX + "," + event.clientY);
});

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
    
    let path = [];
    while (element.nodeType === Node.ELEMENT_NODE) {
        let selector = element.nodeName.toLowerCase();
        if (element.id) {
            selector += '#' + element.id;
            path.unshift(selector);
            break;
        } else {
            let sibling = element;
            let nth = 1;
            while (sibling.previousElementSibling) {
                sibling = sibling.previousElementSibling;
                if (sibling.nodeName.toLowerCase() === selector) nth++;
            }
            if (nth !== 1) selector += `:nth-of-type(${nth})`;
        }
        path.unshift(selector);
        element = element.parentNode;
    }
    return path.join(' > ');
}

// 监听右键菜单事件
document.addEventListener('contextmenu', (event) => {
    const selector = getSelector(event.target);
    console.log('Element selector:', selector);
    
    // 发送消息给background script
    chrome.runtime.sendMessage({
        type: 'elementSelector',
        selector: selector
    });
});

// 添加一个变量来跟踪菜单状态
let emailMenuActive = false;

// 监听来自background的消息
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'toggleEmailMenu') {
        emailMenuActive = message.active;
        // 在这里添加你的邮件菜单相关逻辑
        if (emailMenuActive) {
            // 激活邮件菜单功能
            console.log('Email menu activated');
            // 添加你的邮件菜单处理代码
        } else {
            // 停用邮件菜单功能
            console.log('Email menu deactivated');
            // 清理邮件菜单相关内容
        }
    }
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
});

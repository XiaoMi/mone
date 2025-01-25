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

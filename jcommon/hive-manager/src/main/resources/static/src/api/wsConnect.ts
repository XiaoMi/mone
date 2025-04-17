const WS_HOST = 'ws://0.0.0.0:8080/ws/agent/chat';

export function connectWebSocket(onClose: () => void) {
    const socket = new WebSocket(WS_HOST);
    let heartbeatInterval: any;

    const startHeartbeat = () => {
        heartbeatInterval = setInterval(() => {
            try {
                if (socket?.readyState === WebSocket.OPEN) {
                    socket?.send('ping');
                } else {
                    socket?.close();
                    clearInterval(heartbeatInterval);
                }
            } catch (error) {
                console.error('心跳包发送失败:', error);
                socket?.close();
                clearInterval(heartbeatInterval);
            }
        }, 10000); // 每30秒发送一次心跳包
    };

    socket.onopen = () => {
        console.log("连接成功.");
        startHeartbeat();
    };

    socket.onmessage = (event) => {
        console.log(event.data);
        // resultCodeHandler(event.data, host)
        // 处理接收到的消息
    };

    socket.onerror = (error) => {
        console.error("WebSocket error:", error);
        // 处理错误
    };

    socket.onclose = (event) => {
        clearInterval(heartbeatInterval);
        onClose();
        console.log("WebSocket connection closed:", event);
        // 处理连接关闭
    };

    return socket;
}


// 定义状态机类
class StateMachine {
    constructor(id) {
        this.id = id;
        this.messageQueue = [];
        this.currentState = 'OPENING_PAGE'; // 初始状态
        this.intervalId = null;
        this.stateChangeListeners = new Set(); // 添加监听器集合
        this.tabId = null; // 添加tabId
        
        // 定义所有可能的状态
        this.states = {
            OPENING_PAGE: 'OPENING_PAGE',       // 打开页面中
            PAGE_OPENED: 'PAGE_OPENED'          // 打开页面后
        };

        // 启动自动消息发送
        this.startAutoMessage();
    }

    // 开始自动发送消息
    startAutoMessage() {
        this.intervalId = setInterval(() => {
            this.addMessage({
                type: 'AUTO_MESSAGE',
                timestamp: new Date().toISOString(),
                content: `Auto message for state: ${this.currentState}`
            });
        }, 300000);
    }

    // 停止自动发送消息
    stopAutoMessage() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
            this.intervalId = null;
        }
    }

    // 添加消息到队列
    addMessage(message) {
        this.messageQueue.push({
            ...message,
            timestamp: new Date().toISOString()
        });
        this.processMessage();
    }

    // 处理消息队列
    processMessage() {
        while (this.messageQueue.length > 0) {
            const message = this.messageQueue.shift();
            this.handleMessage(message);
        }
    }

    // 处理单个消息
    handleMessage(message) {
        console.log(`[StateMachine ${this.id}] Processing message:`, message);
        console.log(`[StateMachine ${this.id}] Current state:`, this.currentState);

        // 保存当前状态用于比较
        const previousState = this.currentState;

        // 根据当前状态和消息类型进行状态转换
        switch (this.currentState) {
            case this.states.OPENING_PAGE:
                if (message.type === 'PAGE_LOADED') {
                    this.currentState = this.states.PAGE_OPENED;
                }
                break;

            case this.states.PAGE_OPENED:
                if (message.type === 'INFO_RECEIVED') {
                    this.currentState = this.states.OPENING_PAGE;
                }
                break;
        }

        // 只有状态发生改变时才发送通知
        if (previousState !== this.currentState && this.currentState === this.states.PAGE_OPENED) {
            this.notifyStateChange();
        }
    }
   

    // 添加状态变更监听器
    addStateChangeListener(listener) {
        this.stateChangeListeners.add(listener);
    }

    // 移除状态变更监听器
    removeStateChangeListener(listener) {
        this.stateChangeListeners.delete(listener);
    }

    // 修改通知状态变更方法
    notifyStateChange() {
        console.log(`[StateMachine ${this.id}] State changed to:`, this.currentState);
        
        // 创建状态更新对象，添加tabId
        const stateUpdate = {
            type: 'STATE_MACHINE_UPDATE',
            machineId: this.id,
            state: this.currentState,
            timestamp: new Date().toISOString(),
            tabId: this.tabId // 添加tabId
        };

        // 直接通知所有监听器
        this.stateChangeListeners.forEach(listener => {
            try {
                listener(stateUpdate);
            } catch (error) {
                console.error('Error notifying state change listener:', error);
            }
        });
    }

    // 添加设置tabId的方法
    setTabId(tabId) {
        this.tabId = tabId;
    }

    // 获取当前状态
    getCurrentState() {
        return this.currentState;
    }

    // 销毁状态机
    destroy() {
        this.stopAutoMessage();
        this.messageQueue = [];
    }
}

// 状态机管理器类
class StateManager {
    constructor() {
        this.machines = new Map();
        this.lastMachineId = 0;
        this.globalStateChangeListeners = new Set(); // 添加全局监听器集合
    }

    // 添加全局状态变更监听器
    addGlobalStateChangeListener(listener) {
        this.globalStateChangeListeners.add(listener);
        // 为所有现有的状态机添加监听器
        for (const machine of this.machines.values()) {
            machine.addStateChangeListener(listener);
        }
    }

    // 移除全局状态变更监听器
    removeGlobalStateChangeListener(listener) {
        this.globalStateChangeListeners.delete(listener);
        // 从所有状态机中移除监听器
        for (const machine of this.machines.values()) {
            machine.removeStateChangeListener(listener);
        }
    }

    // 修改创建状态机方法
    createMachine() {
        const machineId = `machine_${++this.lastMachineId}`;
        const machine = new StateMachine(machineId);
        
        // 为新创建的状态机添加所有全局监听器
        this.globalStateChangeListeners.forEach(listener => {
            machine.addStateChangeListener(listener);
        });
        
        this.machines.set(machineId, machine);
        console.log(`Created new state machine with ID: ${machineId}`);
        return machineId;
    }

    // 获取状态机
    getMachine(machineId) {
        return this.machines.get(machineId);
    }

    // 向指定状态机发送消息
    sendMessage(machineId, message) {
        const machine = this.machines.get(machineId);
        if (machine) {
            machine.addMessage(message);
            return true;
        }
        console.warn(`State machine ${machineId} not found`);
        return false;
    }

    // 销毁状态机
    destroyMachine(machineId) {
        const machine = this.machines.get(machineId);
        if (machine) {
            machine.destroy();
            this.machines.delete(machineId);
            console.log(`Destroyed state machine: ${machineId}`);
            return true;
        }
        return false;
    }

    // 获取所有状态机的状态
    getAllMachineStates() {
        const states = {};
        for (const [id, machine] of this.machines) {
            states[id] = machine.getCurrentState();
        }
        return states;
    }
}

// 创建单例实例
const stateManager = new StateManager();
export default stateManager; 
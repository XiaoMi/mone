// 错误管理类
export class ErrorManager {
    constructor() {
        this.errors = [];
        this.maxErrors = 100; // 最大保存错误数量
        this.listeners = new Set();
        
        // 错误等级定义
        this.ERROR_LEVELS = {
            INFO: 'info',
            WARNING: 'warning',
            ERROR: 'error',
            FATAL: 'fatal'
        };

        // 初始化错误页面URL
        this.errorPageUrl = chrome.runtime.getURL('error-page.html');
        
        // 绑定错误处理方法
        this.handleError = this.handleError.bind(this);
        this.handleUnhandledRejection = this.handleUnhandledRejection.bind(this);
        
        // 从 storage 加载错误记录
        chrome.storage.local.get(['errors'], (result) => {
            if (result.errors) {
                this.errors = result.errors;
            }
        });
        
        // 设置全局错误监听
        this.setupErrorListeners();
    }

    // 设置错误监听器
    setupErrorListeners() {
        window.addEventListener('error', this.handleError);
        window.addEventListener('unhandledrejection', this.handleUnhandledRejection);
    }

    // 处理常规错误
    handleError(event) {
        const error = {
            type: 'runtime',
            level: this.ERROR_LEVELS.ERROR,
            message: event.message,
            filename: event.filename,
            lineNumber: event.lineno,
            columnNumber: event.colno,
            stack: event.error?.stack,
            timestamp: new Date().toISOString()
        };
        
        this.logError(error);
    }

    // 处理未捕获的Promise错误
    handleUnhandledRejection(event) {
        const error = {
            type: 'promise',
            level: this.ERROR_LEVELS.ERROR,
            message: event.reason?.message || 'Unhandled Promise Rejection',
            stack: event.reason?.stack,
            timestamp: new Date().toISOString()
        };
        
        this.logError(error);
    }

    // 记录错误
    logError(error) {
        // 添加错误到数组
        this.errors.unshift(error);
        
        // 限制错误数量
        if (this.errors.length > this.maxErrors) {
            this.errors.pop();
        }
        
        // 控制台输出
        this.logToConsole(error);
        
        // 保存到 chrome.storage
        chrome.storage.local.set({ errors: this.errors }, () => {
            // 通知所有监听器
            this.notifyListeners(error);
        });
        
        // 如果是致命错误，打开错误页面
        if (error.level === this.ERROR_LEVELS.FATAL) {
            this.openErrorPage();
        }
    }

    // 控制台输出
    logToConsole(error) {
        const style = this.getConsoleStyle(error.level);
        console.group(`%c${error.level.toUpperCase()}: ${error.message}`, style);
        console.log('Timestamp:', error.timestamp);
        console.log('Type:', error.type);
        if (error.filename) {
            console.log('File:', error.filename);
            console.log('Location:', `Line ${error.lineNumber}, Column ${error.columnNumber}`);
        }
        if (error.stack) {
            console.log('Stack Trace:');
            console.log(error.stack);
        }
        console.groupEnd();
    }

    // 获取控制台样式
    getConsoleStyle(level) {
        const styles = {
            info: 'color: #2196F3; font-weight: bold;',
            warning: 'color: #FFC107; font-weight: bold;',
            error: 'color: #F44336; font-weight: bold;',
            fatal: 'color: #B71C1C; font-weight: bold; font-size: 1.2em;'
        };
        return styles[level] || styles.info;
    }

    // 添加错误监听器
    addListener(callback) {
        this.listeners.add(callback);
        return () => this.listeners.delete(callback);
    }

    // 通知所有监听器
    notifyListeners(error) {
        this.listeners.forEach(listener => {
            try {
                listener(error);
            } catch (err) {
                console.error('Error in error listener:', err);
            }
        });
    }

    // 打开错误页面
    openErrorPage() {
        chrome.tabs.create({ url: this.errorPageUrl });
    }

    // 获取所有错误
    getErrors() {
        return [...this.errors];
    }

    // 清除所有错误
    clearErrors() {
        this.errors = [];
        chrome.storage.local.remove(['errors'], () => {
            this.notifyListeners({ type: 'clear' });
        });
    }

    // 手动记录错误
    error(message, error = null) {
        this.logError({
            type: 'manual',
            level: this.ERROR_LEVELS.ERROR,
            message,
            stack: error?.stack,
            timestamp: new Date().toISOString()
        });
    }

    // 记录警告
    warning(message) {
        this.logError({
            type: 'manual',
            level: this.ERROR_LEVELS.WARNING,
            message,
            timestamp: new Date().toISOString()
        });
    }

    // 记录信息
    info(message) {
        this.logError({
            type: 'manual',
            level: this.ERROR_LEVELS.INFO,
            message,
            timestamp: new Date().toISOString()
        });
    }

    // 记录致命错误
    fatal(message, error = null) {
        this.logError({
            type: 'manual',
            level: this.ERROR_LEVELS.FATAL,
            message,
            stack: error?.stack,
            timestamp: new Date().toISOString()
        });
    }
}

// 创建单例实例
const errorManager = new ErrorManager();
export default errorManager; 
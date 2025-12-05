import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// 日志文件路径
const LOG_DIR = path.join(__dirname, '../../logs')
const LOG_FILE = path.join(LOG_DIR, `tui-${new Date().toISOString().split('T')[0]}.log`)

// 确保日志目录存在
if (!fs.existsSync(LOG_DIR)) {
  fs.mkdirSync(LOG_DIR, { recursive: true })
}

// 日志级别
export enum LogLevel {
  DEBUG = 'DEBUG',
  INFO = 'INFO',
  WARN = 'WARN',
  ERROR = 'ERROR',
}

// 是否启用调试模式
const DEBUG_MODE = process.env.DEBUG === 'true' || process.env.NODE_ENV === 'development'

class Logger {
  private writeToFile(level: LogLevel, message: string, data?: any) {
    const timestamp = new Date().toISOString()
    const logEntry = `[${timestamp}] [${level}] ${message}${data ? ` ${JSON.stringify(data)}` : ''}\n`

    try {
      fs.appendFileSync(LOG_FILE, logEntry)
    } catch (err) {
      // 如果写入失败，静默处理
    }
  }

  debug(message: string, data?: any) {
    if (DEBUG_MODE) {
      this.writeToFile(LogLevel.DEBUG, message, data)
      console.debug(`[DEBUG] ${message}`, data || '')
    }
  }

  info(message: string, data?: any) {
    this.writeToFile(LogLevel.INFO, message, data)
    if (DEBUG_MODE) {
      console.info(`[INFO] ${message}`, data || '')
    }
  }

  warn(message: string, data?: any) {
    this.writeToFile(LogLevel.WARN, message, data)
    console.warn(`[WARN] ${message}`, data || '')
  }

  error(message: string, data?: any) {
    this.writeToFile(LogLevel.ERROR, message, data)
    console.error(`[ERROR] ${message}`, data || '')
  }

  // API 请求日志
  apiRequest(method: string, url: string, data?: any) {
    this.info(`API Request: ${method} ${url}`, data)
  }

  apiResponse(method: string, url: string, status: number, data?: any) {
    this.info(`API Response: ${method} ${url} - ${status}`, data)
  }

  // WebSocket 日志
  wsConnect(url: string) {
    this.info(`WebSocket Connect: ${url}`)
  }

  wsMessage(direction: 'send' | 'receive', message: any) {
    this.debug(`WebSocket ${direction}: `, message)
  }

  wsDisconnect(url: string, reason?: string) {
    this.info(`WebSocket Disconnect: ${url}`, { reason })
  }

  // 获取日志文件路径
  getLogFilePath() {
    return LOG_FILE
  }
}

export const logger = new Logger()

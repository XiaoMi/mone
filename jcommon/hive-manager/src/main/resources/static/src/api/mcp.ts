import { Service } from '@/common/req'
import type { IResponse } from '@/common/req'

// MCP 服务器接口定义
export interface McpServer {
  status: boolean
  version?: string
  tools?: Record<string, McpTool>
}

// MCP 工具接口定义
export interface McpTool {
  description: string
  inputSchema?: string
}

// MCP 服务器列表类型
export type McpServerList = Record<string, McpServer>

// MCP 状态类型
export type McpStatusMap = Record<string, string>

// 获取 MCP 服务器列表
export const getMcp = () => {
  // Mock 数据
  const mockData: McpServerList = {
    "database-server": {
      status: true,
      version: "1.0.0",
      tools: {
        "query_database": {
          description: "Execute SQL queries on the database",
          inputSchema: JSON.stringify({
            type: "object",
            properties: {
              query: {
                type: "string",
                description: "SQL query to execute"
              },
              params: {
                type: "array",
                description: "Query parameters"
              }
            },
            required: ["query"]
          })
        },
        "get_schema": {
          description: "Get database schema information",
          inputSchema: JSON.stringify({
            type: "object",
            properties: {
              table_name: {
                type: "string",
                description: "Table name to get schema for"
              }
            }
          })
        }
      }
    },
    "file-server": {
      status: false,
      version: "2.1.0",
      tools: {
        "read_file": {
          description: "Read file contents",
          inputSchema: JSON.stringify({
            type: "object",
            properties: {
              path: {
                type: "string",
                description: "File path to read"
              },
              encoding: {
                type: "string",
                description: "File encoding (default: utf-8)"
              }
            },
            required: ["path"]
          })
        },
        "write_file": {
          description: "Write content to file",
          inputSchema: JSON.stringify({
            type: "object",
            properties: {
              path: {
                type: "string",
                description: "File path to write"
              },
              content: {
                type: "string",
                description: "Content to write"
              },
              encoding: {
                type: "string",
                description: "File encoding (default: utf-8)"
              }
            },
            required: ["path", "content"]
          })
        }
      }
    },
    "web-scraper": {
      status: true,
      version: "1.5.2",
      tools: {
        "scrape_url": {
          description: "Scrape content from a URL",
          inputSchema: JSON.stringify({
            type: "object",
            properties: {
              url: {
                type: "string",
                description: "URL to scrape"
              },
              selector: {
                type: "string",
                description: "CSS selector for content extraction"
              },
              wait_for: {
                type: "number",
                description: "Time to wait before scraping (ms)"
              }
            },
            required: ["url"]
          })
        }
      }
    }
  }

  return Promise.resolve({
    data: {
      code: 200,
      message: "success",
      data: { servers: mockData }
    }
  })
}

// 获取 MCP 服务器状态
export const getMcpStatus = (name?: string) => {
  const mockStatus: McpStatusMap = {
    "database-server": "1",
    "file-server": "0",
    "web-scraper": "1"
  }

  if (name) {
    return Promise.resolve(mockStatus[name] || "0")
  }

  return Promise.resolve(mockStatus)
}

// 获取指定服务器的工具列表
export const getTools = (name: string) => {
  const mockTools = {
    "database-server": {
      "query_database": {
        description: "Execute SQL queries on the database",
        inputSchema: JSON.stringify({
          type: "object",
          properties: {
            query: {
              type: "string",
              description: "SQL query to execute"
            },
            params: {
              type: "array",
              description: "Query parameters"
            }
          },
          required: ["query"]
        })
      },
      "get_schema": {
        description: "Get database schema information",
        inputSchema: JSON.stringify({
          type: "object",
          properties: {
            table_name: {
              type: "string",
              description: "Table name to get schema for"
            }
          }
        })
      }
    },
    "file-server": {
      "read_file": {
        description: "Read file contents",
        inputSchema: JSON.stringify({
          type: "object",
          properties: {
            path: {
              type: "string",
              description: "File path to read"
            },
            encoding: {
              type: "string",
              description: "File encoding (default: utf-8)"
            }
          },
          required: ["path"]
        })
      },
      "write_file": {
        description: "Write content to file",
        inputSchema: JSON.stringify({
          type: "object",
          properties: {
            path: {
              type: "string",
              description: "File path to write"
            },
            content: {
              type: "string",
              description: "Content to write"
            }
          },
          required: ["path", "content"]
        })
      }
    },
    "web-scraper": {
      "scrape_url": {
        description: "Scrape content from a URL",
        inputSchema: JSON.stringify({
          type: "object",
          properties: {
            url: {
              type: "string",
              description: "URL to scrape"
            },
            selector: {
              type: "string",
              description: "CSS selector for content extraction"
            }
          },
          required: ["url"]
        })
      }
    }
  }

  return Promise.resolve({ [name]: mockTools[name] || {} })
}

// 获取 MCP 服务器版本
export const getMcpVersion = (name: string) => {
  const mockVersions = {
    "database-server": "1.0.0",
    "file-server": "2.1.0",
    "web-scraper": "1.5.2"
  }

  return Promise.resolve(mockVersions[name] || "unknown")
}

// 重试 MCP 服务器连接
export const mcpRetryConnection = (name: string) => {
  // 模拟重试连接，随机成功或失败
  const success = Math.random() > 0.3

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (success) {
        resolve({ message: "连接成功" })
      } else {
        reject(new Error("连接失败，请检查服务器配置"))
      }
    }, 1000) // 模拟网络延迟
  })
}

// 打开 MCP 配置文件
export const openMcp = () => {
  // 在实际实现中，这里会调用后端 API 来打开配置文件
  // 目前模拟操作
  console.log("Opening MCP configuration file...")
  return Promise.resolve({ message: "配置文件已打开" })
}

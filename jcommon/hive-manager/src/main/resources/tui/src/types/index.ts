export interface User {
  id: number
  username: string
  cname: string
  avatar?: string
  token?: string
}

export interface Agent {
  id: number
  name: string
  description: string
  agentUrl: string
  isPublic: boolean
  image?: string
  group: string
  version: string
  ctime: string
  utime: string
  mcpToolMap?: string
}

export interface AgentInstance {
  ip: string
  port: number
  status: 'running' | 'stopped'
}

export interface AgentListItem {
  agent: Agent
  instances: AgentInstance[]
  isFavorite: boolean
}

export interface Task {
  id?: number
  taskUuid: string
  clientAgentId: number | null
  serverAgentId: number | null
  skillId: number | null
  title: string
  description: string
  status: 'pending' | 'running' | 'completed' | 'failed'
  ctime: string
  utime: string
}

export interface Message {
  type: 'md' | 'hello' | 'image' | 'audio'
  author: {
    username: string
    cname: string
    avatar: string
  }
  meta: {
    role: 'USER' | 'IDEA' | 'ASSISTANT'
  }
  data: {
    text?: string
    content?: string
    hello?: string
    links?: any[]
    files?: any[]
  }
}

export interface ApiResponse<T = any> {
  code: number
  message?: string
  data?: T
}

export type Screen = 'login' | 'agents' | 'chat' | 'tasks' | 'about'

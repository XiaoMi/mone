import { Service } from '@/common/req'
import type { IResponse } from '@/common/req'

export interface Agent {
  id: number
  name: string
  description: string
  createdAt: string
  agentUrl: string
  isPublic: boolean
  image: string
  group: string
  version: string
  profile: string
  goal: string
  constraints: string
  instances: Array<object>
  toolMap?: string
  mcpToolMap?: string
}

export interface CreateAgentRequest {
  name: string
  description: string
  agentUrl: string
  isPublic: boolean
}

export interface UpdateAgentRequest extends CreateAgentRequest {
  id: number
}

// 技能接口定义
export interface Skill {
  id: number
  name: string
  description: string
  createdAt: string
  agentId: number
}

// 创建技能请求参数接口
export interface CreateSkillRequest {
  name: string
  skillId: string
  description: string
  tags: string
  examples: string
  outputSchema: string
}

export interface Access {
  id: number
  agentId: number
  accessApp: string
  description: string
  accessKey: string
  ctime: number
  utime: number
}

// 获取Agent列表
export const getAgentList = (name: string = "", isFavorite: boolean = false) => {
  return Service<IResponse<{
    agent: Agent,
    instances: Array<any>
    isFavorite: boolean
}[]>>({
    url: '/v1/agents/list?name='+name+'&isFavorite='+isFavorite,
    method: 'get'
  })
}

// 创建Agent
export const createAgent = (data: CreateAgentRequest) => {
  return Service<IResponse<Agent>>({
    url: '/v1/agents/create',
    method: 'post',
    data
  })
}

// 更新Agent
export const updateAgent = (id: number, data: CreateAgentRequest) => {
  return Service<IResponse<Agent>>({
    url: `/v1/agents/${id}`,
    method: 'put',
    data
  })
}

// 删除Agent
export const deleteAgent = (id: number) => {
  return Service<IResponse<void>>({
    url: `/v1/agents/${id}`,
    method: 'delete'
  })
}

// 获取Agent详情
export const getAgentDetail = (id: number) => {
  return Service<IResponse<{
    agent: Agent,
    instances: Array<any>
    isFavorite: boolean
  }>>({
    url: `/v1/agents/${id}`,
    method: 'get'
  })
}

// 获取指定Agent的所有技能
export const getAgentSkills = (agentId: number) => {
  return Service<IResponse<Skill[]>>({
    url: `/v1/agents/${agentId}/skills`,
    method: 'get'
  })
}

// 删除技能
export const deleteSkill = (skillId: number) => {
  return Service<IResponse<void>>({
    url: `/v1/skills/${skillId}`,
    method: 'delete'
  })
}

// 更新技能
export const updateSkill = (skillId: number, data: Partial<Skill>) => {
  return Service<IResponse<Skill>>({
    url: `/v1/skills/${skillId}`,
    method: 'put',
    data
  })
}

// 创建技能
export const createSkill = (agentId: number, data: CreateSkillRequest) => {
  return Service<IResponse<Skill>>({
    url: `/v1/agents/${agentId}/skills`,
    method: 'post',
    data
  })
}

// 获取access列表
export const accessList = (agentId: number, params: {
  agentId: number
  accessApp: string
  description: string
}) => {
  return Service<IResponse<Access[]>>({
    url: `/agent/access/list/${agentId}`,
    method: 'get',
    params,
  })
}

// 删除access
export const deleteAccess = (agentId: number) => {
  return Service<IResponse<any>>({
    url: `/agent/access/${agentId}`,
    method: 'delete',
  })
}

// 创建access
export const createAccess = (data: {
  agentId: number
  accessApp: string
  description: string
}) => {
  return Service<IResponse<Access>>({
    url: `/agent/access/create`,
    method: 'post',
    data
  })
}

// 下限agent
export const offlineAgent = (data) => {
  return Service<IResponse<string>>({
    url: `/v1/agents/offline`,
    method: 'post',
    data
  })
}

// 清除历史记录
export const clearHistory = (data) => {
  return Service<IResponse<string>>({
    url: `/v1/agents/clearHistory`,
    method: 'post',
    data
  })
}

// 获取收藏列表
export const favoriteList = (userId: number | string, type: number | string) => {
  return Service<IResponse<{
    agent: Agent,
    instances: Array<any>
    isFavorite: boolean
}[]>>({
    url: `/favorite/list?userId=${userId}&type=${type}`,
    method: 'get',
  })
}


// 添加收藏
export const addFavorite = (data: {
  userId: number | string
  type: number | string
  targetId: number | string
}) => {
  return Service<IResponse<string>>({
    url: `/favorite/add`,
    method: 'post',
    data
  })
}

// 删除收藏
export const deleteFavorite = (data: {
  userId: number | string
  type: number | string
  targetId: number | string
}) => {
  return Service<IResponse<string>>({
    url: `/favorite/remove`,
    method: 'post',
    data
  })
}
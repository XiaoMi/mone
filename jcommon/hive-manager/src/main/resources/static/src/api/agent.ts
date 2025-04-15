import { Service } from '@/common/req'
import type { IResponse } from '@/common/req'

export interface Agent {
  id: number
  name: string
  description: string
  createdAt: string
  agentUrl: string
  isPublic: boolean
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

// 获取Agent列表
export const getAgentList = () => {
  return Service<IResponse<Agent[]>>({
    url: '/v1/agents/list',
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
  return Service<IResponse<Agent>>({
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

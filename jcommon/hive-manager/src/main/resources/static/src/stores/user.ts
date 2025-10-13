import type { Agent } from '@/api/agent'
import { defineStore } from 'pinia'
import { ref } from 'vue'

interface User {
  id: number
  username: string
  internalAccount: string
  // 添加其他用户字段
}

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const user = ref<User | null>(null)
  const agent = ref<Agent | null>(null)
  const instance = ref<Array<any> | null>(null)
  const selectedInstance = ref<any>(null)

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUser(newUser: User) {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  function setAgent(newAgent: Agent | null) {
    agent.value = newAgent
  }

  function getAgent() {
    return agent.value
  }

  function setInstance(newInstance: Array<any> | null) {
    instance.value = newInstance
  }

  function getInstance() {
    return instance.value
  }

  function getSelectedInstance() {
    return selectedInstance.value
  }

  function setSelectedInstance(newInstance: any) {
    selectedInstance.value = newInstance
  }

  function clearUser() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  function initUser() {
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')
    if (storedToken && storedUser) {
      token.value = storedToken
      user.value = JSON.parse(storedUser)
      if (!user.value?.internalAccount) {
        return false
      }
      return true
    }
    return false
  }

  return {
    setAgent,
    token,
    user,
    agent,
    getInstance,
    setToken,
    setUser,
    clearUser,
    initUser,
    setInstance,
    getSelectedInstance,
    setSelectedInstance,
    getAgent
  }
})

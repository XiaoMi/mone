import { defineStore } from 'pinia'
import { ref } from 'vue'

interface User {
  id: number
  username: string
  // 添加其他用户字段
}

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const user = ref<User | null>(null)

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUser(newUser: User) {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
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
      return true
    }
    return false
  }

  return {
    token,
    user,
    setToken,
    setUser,
    clearUser,
    initUser
  }
})

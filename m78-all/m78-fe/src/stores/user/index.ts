import { defineStore } from 'pinia'
import type { UserState } from './helper'
import { getLocalState, getUser, setLocalState } from './helper'

export const useUserStore = defineStore('user-store', {
  state: (): UserState => getLocalState(),
  actions: {
    async initUserInfo() {
      try {
        const userInfo = await getUser()
        this.$state.userInfo = { ...this.userInfo, ...userInfo }
        this.recordState()
      } catch (err) {
        console.error(err)
      }
    },

    async setZToken(token: string) {
      try {
        this.$state.userInfo.ztoken = token
        this.recordState()
      } catch (err) {
        console.error(err)
      }
    },

    recordState() {
      setLocalState(this.$state)
    }
  }
})

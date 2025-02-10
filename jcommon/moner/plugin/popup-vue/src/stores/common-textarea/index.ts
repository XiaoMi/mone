import { defineStore } from 'pinia'
import type { CommonTextareaState } from './helper'
import { getLocalSetting, setLocalSetting } from './helper'

export const useCommonTextareaStore = defineStore('common-textarea-store', {
  state: (): CommonTextareaState => getLocalSetting(),
  actions: {
    setEnterWithEnterShiftKey(enterWithShiftKey: boolean) {
      this.enterWithShiftKey = enterWithShiftKey
      this.recordState()
    },

    recordState() {
      setLocalSetting(this.$state)
    }
  }
})

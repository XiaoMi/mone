import { defineStore } from 'pinia'
import type { SettingsState } from './helper'
import { defaultSetting, getLocalState, removeLocalState, setLocalState } from './helper'
import { getUserConfig } from '@/api/index'

export const useSettingStore = defineStore('setting-store', {
  state: (): SettingsState => getLocalState(),
  actions: {
    async fetchModels() {
      const { code, data } = await getUserConfig({})
      let models: Record<string, string> = {}
      if (code == 0 && data && data.modelConfig) {
        const modelConfig = data.modelConfig
        models = { ...modelConfig }
      }
      const settings = {
        codeModel: models.codeModel || '',
        documentModel: models.documentModel || '',
        chatModel: models.chatModel || '',
        translateModel: models.translateModel || ''
      }
      this.updateSetting(settings)
    },

    updateSetting(settings: Partial<SettingsState>) {
      this.$state = { ...this.$state, ...settings }
      this.recordState()
    },

    resetSetting() {
      this.$state = defaultSetting()
      removeLocalState()
    },

    recordState() {
      setLocalState(this.$state)
    }
  }
})

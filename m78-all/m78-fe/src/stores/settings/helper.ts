import { ss } from '@/utils/storage'

const LOCAL_NAME = 'settingsStorage'

export interface SettingsState {
  chatModel: string
  translateModel: string
  documentModel: string
  codeModel: string
  temperature: number
  top_p: number
}

export function defaultSetting(): SettingsState {
  return {
    chatModel: '',
    translateModel: '',
    documentModel: '',
    codeModel: '',
    temperature: 0.8,
    top_p: 1
  }
}

export function getLocalState(): SettingsState {
  const localSetting: SettingsState | undefined = ss.get(LOCAL_NAME)
  return { ...defaultSetting(), ...localSetting }
}

export function setLocalState(setting: SettingsState): void {
  ss.set(LOCAL_NAME, setting)
}

export function removeLocalState() {
  ss.remove(LOCAL_NAME)
}

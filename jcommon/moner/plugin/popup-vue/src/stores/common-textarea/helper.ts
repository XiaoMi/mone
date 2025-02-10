import { ss } from '@/utils/storage'

const LOCAL_NAME = 'commonTextareaSetting'

export interface CommonTextareaState {
  enterWithShiftKey: boolean
}

export function defaultSetting(): CommonTextareaState {
  return { enterWithShiftKey: false }
}

export function getLocalSetting(): CommonTextareaState {
  const localSetting: CommonTextareaState | undefined = ss.get(LOCAL_NAME)
  return { ...defaultSetting(), ...localSetting }
}

export function setLocalSetting(setting: CommonTextareaState): void {
  ss.set(LOCAL_NAME, setting)
}

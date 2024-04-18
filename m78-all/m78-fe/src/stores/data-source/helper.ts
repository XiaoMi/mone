import { ss } from '@/utils/storage'

const LOCAL_NAME = 'DataSource'

export interface DataSource {
  meta: Record<string, string>
}

export function defaultState(): DataSource {
  return { meta: {} }
}

export function getLocalState(): DataSource {
  const localSetting: DataSource | undefined = ss.get(LOCAL_NAME)
  return { ...defaultState(), ...localSetting }
}

export function setLocalState(state: DataSource): void {
  ss.set(LOCAL_NAME, state)
}

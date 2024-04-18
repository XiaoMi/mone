import type { App } from 'vue'
import { createPinia } from 'pinia'

export const store = createPinia()

export function setupStore(app: App) {
  app.use(store)
}

export * from './app'
export * from './chat'
export * from './user'
export * from './prompt'
export * from './settings'
export * from './auth'
export * from './chatContext'
export * from './doc'

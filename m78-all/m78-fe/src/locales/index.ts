import type { App } from 'vue'
import { createI18n } from 'vue-i18n'
import { en, zhCn } from 'element-plus/es/locale/index.mjs'
import enUS from './en-US'
import zhCN from './zh-CN'
import { useAppStoreWithOut } from '@/stores/app'
import type { Language } from '@/stores/app/helper'

const appStore = useAppStoreWithOut()

const defaultLocale = appStore.language || 'en-US'

const i18n = createI18n({
  locale: defaultLocale,
  fallbackLocale: 'en-US',
  allowComposition: true,
  messages: {
    'en-US': enUS,
    'zh-CN': zhCN
  }
})

export const t = i18n.global.t

export function setLocale(locale: Language) {
  i18n.global.locale = locale
}

export function setupI18n(app: App) {
  app.use(i18n)
}

export function getElementPlusI18n(locale: Language) {
  if (locale == 'en-US') {
    return en
  } else if (locale == 'zh-CN') {
    return zhCn
  }

  return en
}

export default i18n

import { createI18n } from 'vue-i18n'
import enUS from './en-US'
import zhCN from './zh-CN'

// 定义支持的语言类型
type SupportedLocales = 'zh-CN' | 'en-US'

const i18n = createI18n({
  legacy: false,
  locale: 'zh-CN',
  fallbackLocale: 'en-US',
  messages: {
    'en-US': enUS,
    'zh-CN': zhCN
  },
  runtimeOnly: true,
  compilerOptions: {
    MODE: 'runtime'
  },
  escapeParameter: true,
  useScope: 'local'
})

export default i18n

export const { t } = i18n.global

export function setLocale(locale: SupportedLocales) {
  i18n.global.locale.value = locale
}


import { createApp } from "vue"
import App from '../App.vue'
import { createI18n } from 'vue-i18n'
import enLocale from './en'
import zhLocale from './zh'
const app = createApp(App)

export interface I18nInterface {
  install: any;
  t?: (word: string) => string;
  [propName: string]: any;
}


const messages = {
  en: {
    ...enLocale
  },
  zh: {
    ...zhLocale
  }
}

let locale = window.localStorage.getItem('apiLocaleLanguage')
let selectedLang = window.localStorage.getItem('selectedLang')

const i18n: I18nInterface = createI18n({
  locale: locale === null ? 'zh' : locale,
  selectedLang: selectedLang === null ? '简体中文' : selectedLang,
  messages
})

app.use(i18n)


i18n.t = i18n.global.t
export default i18n;

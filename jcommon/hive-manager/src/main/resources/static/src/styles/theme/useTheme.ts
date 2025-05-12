import { ref, watchEffect } from 'vue'
import { lightTheme, darkTheme, cyberpunkTheme } from './index'
import type { ThemeConfig } from './types'

const THEME_KEY = 'app-theme'
const CUSTOM_THEMES_KEY = 'custom-themes'

// 预设主题
const presetThemes = {
  light: lightTheme,
  dark: darkTheme,
  cyberpunk: cyberpunkTheme
} as const

// 获取保存的自定义主题
const loadCustomThemes = (): Record<string, ThemeConfig> => {
  try {
    const saved = localStorage.getItem(CUSTOM_THEMES_KEY)
    return saved ? JSON.parse(saved) : {}
  } catch {
    return {}
  }
}

// 合并预设主题和自定义主题
export const themes: Record<string, ThemeConfig> = {
  ...presetThemes,
  ...loadCustomThemes()
}

export const useTheme = () => {
  const currentTheme = ref<ThemeConfig>(themes.light)
  const customThemes = ref<Record<string, ThemeConfig>>(loadCustomThemes())
  console.log("customThemes.value", customThemes.value)

  // 加载保存的主题
  const loadSavedTheme = () => {
    const savedTheme = localStorage.getItem(THEME_KEY)
    if (savedTheme) {
      const theme = themes[savedTheme]
      if (theme) {
        currentTheme.value = theme
      }
    }
  }

  // 保存自定义主题
  const saveCustomTheme = (theme: ThemeConfig) => {
    const themeKey = theme.name.toLowerCase().replace(/\s+/g, '-')
    customThemes.value[themeKey] = theme
    localStorage.setItem(CUSTOM_THEMES_KEY, JSON.stringify(customThemes.value))

    // 更新themes对象
    Object.assign(themes, customThemes.value)

    // 切换到新主题
    setTheme(themeKey)
  }

  // 删除自定义主题
  const deleteCustomTheme = (themeName: string) => {
    const themeKey = themeName.toLowerCase().replace(/\s+/g, '-')
    if (customThemes.value[themeKey]) {
      // 如果当前主题被删除，先切换到light主题
      if (currentTheme.value.name === themeName) {
        setTheme('light')
      }

      // 删除主题
      delete customThemes.value[themeKey]
      delete (themes as Record<string, ThemeConfig>)[themeKey]
      localStorage.setItem(CUSTOM_THEMES_KEY, JSON.stringify(customThemes.value))

      return true
    }
    return false
  }

  // 设置主题
  const setTheme = (themeName: string) => {
    const theme = themes[themeName]
    if (theme) {
      currentTheme.value = theme
      localStorage.setItem(THEME_KEY, themeName)
    }
  }

  // 应用主题到CSS变量
  const applyTheme = (theme: ThemeConfig) => {
    const root = document.documentElement
    // 移除所有主题 class
    Object.keys(presetThemes).forEach(themeName => {
      root.classList.remove(themeName)
    })
    // 添加当前主题 class
    root.classList.add(theme.name)
    // 设置body背景色
    document.body.style.backgroundColor = theme.colors.background
    document.body.style.color = theme.colors.textPrimary
  }

  // 监听主题变化并应用
  watchEffect(() => {
    applyTheme(currentTheme.value)
  })

  // 初始化时加载保存的主题
  loadSavedTheme()

  return {
    currentTheme,
    setTheme,
    themes,
    customThemes,
    saveCustomTheme,
    deleteCustomTheme,
    isCustomTheme: (themeName: string) => {
      const themeKey = themeName.toLowerCase().replace(/\s+/g, '-')
      return !!customThemes.value[themeKey]
    }
  }
}

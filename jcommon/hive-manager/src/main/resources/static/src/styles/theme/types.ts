export interface ThemeColors {
  primary: string
  success: string
  warning: string
  danger: string
  info: string
  background: string
  textPrimary: string
  textRegular: string
  textSecondary: string
  borderColor: string
  borderColorLight: string
  borderColorLighter: string
  fillColor: string
  fillColorLight: string
  fillColorLighter: string
  // 聊天界面相关
  chatBackground: string
  chatBackgroundGradient: string
  chatParticleColor: string
  chatLinkColor: string
  chatBorderGlow: string
  chatWindowBackground: string
  chatGridColor: string
  // 聊天窗口相关
  chatWindowBorderColor: string
  chatWindowBoxShadow: string
  chatWindowBorderGlow: string
  chatWindowBorderGlowOpacity: string
  chatWindowBorderGlowAnimation: string
  chatWindowBeforeGradientLight: string
  chatWindowBeforeGradientDark: string
}

export interface ThemeConfig {
  name: string
  colors: ThemeColors
}

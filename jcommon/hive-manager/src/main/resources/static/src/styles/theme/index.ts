import type { ThemeConfig } from './types'

export const lightTheme: ThemeConfig = {
  name: 'light',
  colors: {
    primary: '#409EFF',
    success: '#67C23A',
    warning: '#E6A23C',
    danger: '#F56C6C',
    info: '#909399',
    background: '#ffffff',
    textPrimary: '#303133',
    textRegular: '#606266',
    textSecondary: '#909399',
    borderColor: '#DCDFE6',
    borderColorLight: '#E4E7ED',
    borderColorLighter: '#EBEEF5',
    fillColor: '#F5F7FA',
    fillColorLight: '#F5F7FA',
    fillColorLighter: '#FAFAFA',
    // 聊天界面
    chatBackground: '#ffffff',
    chatBackgroundGradient: '#f5f7fa', //'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
    chatParticleColor: '#646cff',
    chatLinkColor: '#646cff',
    chatBorderGlow: 'rgba(100, 108, 255, 0.08)',
    chatWindowBackground: 'rgba(255, 255, 255, 0.9)',
    chatGridColor: 'rgba(100, 108, 255, 0.1)',
    // 聊天窗口
    chatWindowBorderColor: 'rgba(100, 100, 255, 0.2)',
    chatWindowBoxShadow: '0 0 30px rgba(0, 100, 255, 0.2)',
    chatWindowBorderGlow: 'linear-gradient(45deg, #646cff, #9499ff, #646cff, #9499ff)',
    chatWindowBorderGlowOpacity: '0.5',
    chatWindowBorderGlowAnimation: 'glowing-border 20s linear infinite',
    chatWindowBeforeGradientLight: '#646cff',
    chatWindowBeforeGradientDark: '#9499ff'
  }
}

export const darkTheme: ThemeConfig = {
  name: 'dark',
  colors: {
    primary: '#409EFF',
    success: '#67C23A',
    warning: '#E6A23C',
    danger: '#F56C6C',
    info: '#909399',
    background: '#141414',
    textPrimary: '#FFFFFF',
    textRegular: '#E5EAF3',
    textSecondary: '#CFD3DC',
    borderColor: '#4C4D4F',
    borderColorLight: '#363637',
    borderColorLighter: '#1D1D1D',
    fillColor: '#262727',
    fillColorLight: '#1D1D1D',
    fillColorLighter: '#262727',
    // 聊天界面
    chatBackground: '#1a1a1a',
    chatBackgroundGradient: 'linear-gradient(135deg, #1a1a1a 0%, #2d3436 100%)',
    chatParticleColor: '#646cff',
    chatLinkColor: '#646cff',
    chatBorderGlow: 'rgba(100, 108, 255, 0.15)',
    chatWindowBackground: 'rgba(15, 15, 35, 0.7)',
    chatGridColor: 'rgba(100, 108, 255, 0.2)',
    // 聊天窗口
    chatWindowBorderColor: 'rgba(100, 100, 255, 0.2)',
    chatWindowBoxShadow: '0 0 30px rgba(0, 100, 255, 0.3)',
    chatWindowBorderGlow: 'linear-gradient(45deg, #00dbde, #fc00ff, #00dbde, #fc00ff)',
    chatWindowBorderGlowOpacity: '0.7',
    chatWindowBorderGlowAnimation: 'glowing-border 20s linear infinite',
    chatWindowBeforeGradientLight: '#00dbde',
    chatWindowBeforeGradientDark: '#fc00ff'
  }
}

export const cyberpunkTheme: ThemeConfig = {
  name: 'cyberpunk',
  colors: {
    primary: '#00f0ff',
    success: '#00ff9f',
    warning: '#ff00a0',
    danger: '#ff003c',
    info: '#b6b6b6',
    background: '#0d1117',
    textPrimary: '#ffffff',
    textRegular: '#e1e1e1',
    textSecondary: '#888888',
    borderColor: 'rgba(49, 232, 249, 0.3)',
    borderColorLight: 'rgba(49, 232, 249, 0.2)',
    borderColorLighter: 'rgba(49, 232, 249, 0.1)',
    fillColor: 'rgba(22, 27, 34, 0.4)',
    fillColorLight: 'rgba(22, 27, 34, 0.6)',
    fillColorLighter: 'rgba(22, 27, 34, 0.8)',
    // 聊天界面
    chatBackground: '#0f0f23',
    chatBackgroundGradient: 'linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%)',
    chatParticleColor: '#00ff9d',
    chatLinkColor: '#00ff9d',
    chatBorderGlow: 'rgba(0, 255, 157, 0.2)',
    chatWindowBackground: 'rgba(15, 15, 35, 0.8)',
    chatGridColor: 'rgba(0, 255, 157, 0.2)',
    // 聊天窗口
    chatWindowBorderColor: 'rgba(0, 255, 157, 0.3)',
    chatWindowBoxShadow: '0 0 40px rgba(0, 255, 157, 0.4)',
    chatWindowBorderGlow: 'linear-gradient(45deg, #00ff9d, #00b8ff, #00ff9d, #00b8ff)',
    chatWindowBorderGlowOpacity: '0.8',
    chatWindowBorderGlowAnimation: 'glowing-border 15s linear infinite',
    chatWindowBeforeGradientLight: '#00ff9d',
    chatWindowBeforeGradientDark: '#00b8ff'
  }
}

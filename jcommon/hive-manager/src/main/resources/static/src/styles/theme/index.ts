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
    backgroundGradient: 'linear-gradient(135deg, #b3b8d4 0%, #8b93b8 100%)',
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
    chatText: '#303133',
    chatBackground: '#ffffff',
    chatBackgroundGradient: '#f5f7fa', //'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
    chatParticleColor: '#646cff',
    chatLinkColor: '#646cff',
    chatBorderGlow: 'rgba(100, 108, 255, 0.08)',
    chatWindowBackground: 'rgba(255, 255, 255, 0.9)',
    chatGridColor: 'rgba(100, 108, 255, 0.1)',
    // 聊天窗口
    chatWindowBorderColor: 'rgba(0, 0, 0, 0.1)',
    chatWindowBoxShadow: '0 0 20px rgba(0, 0, 0, 0.05)',
    chatWindowBorderGlow: 'linear-gradient(45deg, #E4E7ED, #DCDFE6)',
    chatWindowBorderGlowOpacity: '0.3',
    chatWindowBorderGlowAnimation: 'none',
    chatWindowBeforeGradientLight: '#E4E7ED',
    chatWindowBeforeGradientDark: '#DCDFE6'
  }
}

export const darkTheme: ThemeConfig = {
  name: 'dark',
  colors: {
    primary: '#3B82F6',
    success: '#10B981',
    warning: '#F59E0B',
    danger: '#EF4444',
    info: '#6B7280',
    background: '#111827',
    backgroundGradient: '#60A5FA', //'linear-gradient(135deg, #1E293B 0%, #334155 100%)',
    textPrimary: '#F9FAFB',
    textRegular: '#E5E7EB',
    textSecondary: '#9CA3AF',
    borderColor: '#374151',
    borderColorLight: '#4B5563',
    borderColorLighter: '#6B7280',
    fillColor: '#1F2937',
    fillColorLight: '#374151',
    fillColorLighter: '#4B5563',
    // 聊天界面
    chatText: '#F9FAFB',
    chatBackground: '#111827',
    chatBackgroundGradient: 'linear-gradient(135deg, #111827 0%, #1F2937 100%)',
    chatParticleColor: '#3B82F6',
    chatLinkColor: '#60A5FA',
    chatBorderGlow: 'rgba(59, 130, 246, 0.15)',
    chatWindowBackground: 'rgba(17, 24, 39, 0.8)',
    chatGridColor: 'rgba(59, 130, 246, 0.1)',
    // 聊天窗口
    chatWindowBorderColor: 'rgba(59, 130, 246, 0.2)',
    chatWindowBoxShadow: '0 0 25px rgba(17, 24, 39, 0.3)',
    chatWindowBorderGlow: 'linear-gradient(45deg, #363637, #4C4D4F)',
    chatWindowBorderGlowOpacity: '1',
    chatWindowBorderGlowAnimation: 'none',
    chatWindowBeforeGradientLight: '#60A5FA',
    chatWindowBeforeGradientDark: '#3B82F6'
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
    backgroundGradient: 'linear-gradient(135deg, rgb(0, 240, 255), rgb(180, 0, 255))',
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
    chatText: '#ffffff',
    chatBackground: '#0f0f23',
    chatBackgroundGradient: 'linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%)',
    chatParticleColor: '#00dbde',
    chatLinkColor: '#00dbde',
    chatBorderGlow: 'rgba(0, 219, 222, 0.2)',
    chatWindowBackground: 'rgba(15, 15, 35, 0.7)',
    chatGridColor: 'rgba(0, 219, 222, 0.2)',
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

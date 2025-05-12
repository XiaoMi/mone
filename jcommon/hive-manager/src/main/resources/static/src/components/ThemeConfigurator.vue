<template>
  <el-drawer
    v-model="visible"
    title="主题配置"
    size="400px"
    :with-header="true"
  >
    <div class="theme-config">
      <el-form :model="customTheme" label-position="top">
        <el-form-item label="主题名称">
          <el-input v-model="customTheme.name" placeholder="请输入主题名称" />
        </el-form-item>

        <div class="color-section">
          <h3>基础颜色</h3>
          <el-form-item v-for="key in baseColors" :key="key" :label="colorLabels[key]">
            <el-color-picker
              v-model="customTheme.colors[key]"
              show-alpha
              :predefine="predefineColors"
            />
            <span class="color-value">{{ customTheme.colors[key] }}</span>
          </el-form-item>
        </div>

        <div class="color-section">
          <h3>文本颜色</h3>
          <el-form-item v-for="key in textColors" :key="key" :label="colorLabels[key]">
            <el-color-picker
              v-model="customTheme.colors[key]"
              show-alpha
              :predefine="predefineColors"
            />
            <span class="color-value">{{ customTheme.colors[key] }}</span>
          </el-form-item>
        </div>

        <div class="color-section">
          <h3>边框和填充颜色</h3>
          <el-form-item v-for="key in borderAndFillColors" :key="key" :label="colorLabels[key]">
            <el-color-picker
              v-model="customTheme.colors[key]"
              show-alpha
              :predefine="predefineColors"
            />
            <span class="color-value">{{ customTheme.colors[key] }}</span>
          </el-form-item>
        </div>
      </el-form>

      <div class="actions">
        <el-button @click="resetTheme">重置</el-button>
        <el-button type="primary" @click="saveTheme">保存主题</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useTheme } from '../styles/theme/useTheme'
import type { ThemeConfig, ThemeColors } from '../styles/theme/types'
import { ElMessage } from 'element-plus'

const visible = ref(false)
const { currentTheme, setTheme, saveCustomTheme } = useTheme()

// 预定义颜色
const predefineColors = [
  '#409EFF',
  '#67C23A',
  '#E6A23C',
  '#F56C6C',
  '#909399',
  '#FFFFFF',
  '#000000',
]

// 颜色分类
const baseColors = ['primary', 'success', 'warning', 'danger', 'info', 'background'] as const
const textColors = ['textPrimary', 'textRegular', 'textSecondary'] as const
const borderAndFillColors = [
  'borderColor',
  'borderColorLight',
  'borderColorLighter',
  'fillColor',
  'fillColorLight',
  'fillColorLighter'
] as const

// 颜色标签映射
const colorLabels: Record<keyof ThemeColors, string> = {
  primary: '主要颜色',
  success: '成功颜色',
  warning: '警告颜色',
  danger: '危险颜色',
  info: '信息颜色',
  background: '背景颜色',
  textPrimary: '主要文本',
  textRegular: '常规文本',
  textSecondary: '次要文本',
  borderColor: '边框颜色',
  borderColorLight: '浅色边框',
  borderColorLighter: '更浅边框',
  fillColor: '填充颜色',
  fillColorLight: '浅色填充',
  fillColorLighter: '更浅填充',
  // 聊天界面相关
  chatBackground: '聊天背景',
  chatBackgroundGradient: '聊天背景渐变',
  chatParticleColor: '粒子颜色',
  chatLinkColor: '链接颜色',
  chatBorderGlow: '边框发光',
  chatWindowBackground: '窗口背景',
  chatGridColor: '网格颜色',
  // 聊天窗口相关
  chatWindowBorderColor: '窗口边框颜色',
  chatWindowBoxShadow: '窗口阴影',
  chatWindowBorderGlow: '窗口边框发光',
  chatWindowBorderGlowOpacity: '边框发光透明度',
  chatWindowBorderGlowAnimation: '边框发光动画',
  chatWindowBeforeGradientLight: '窗口渐变亮色',
  chatWindowBeforeGradientDark: '窗口渐变暗色'
}

// 自定义主题数据
const customTheme = reactive<ThemeConfig>({
  name: '自定义主题',
  colors: { ...currentTheme.value.colors }
})

// 重置主题
const resetTheme = () => {
  Object.assign(customTheme.colors, currentTheme.value.colors)
  customTheme.name = '自定义主题'
}

// 保存主题
const saveTheme = () => {
  if (!customTheme.name.trim()) {
    ElMessage.warning('请输入主题名称')
    return
  }

  saveCustomTheme({
    name: customTheme.name,
    colors: { ...customTheme.colors }
  })

  ElMessage.success('主题保存成功')
  visible.value = false
}

// 暴露方法给父组件
defineExpose({
  show: () => visible.value = true
})
</script>

<style scoped>
.theme-config {
  padding: 20px;
}

.color-section {
  margin-bottom: 24px;
}

.color-section h3 {
  margin: 0 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--el-border-color-light);
  color: var(--el-text-color-primary);
}

.el-form-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.color-value {
  margin-left: 12px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-color-picker__trigger) {
  width: 60px;
  height: 32px;
}
</style>

<template>
  <div class="theme-switcher">
    <el-dropdown @command="handleThemeChange" trigger="click">
      <el-button type="primary" :icon="Brush" size="large">
        {{ themeNameMap[currentTheme.name] || currentTheme.name }}
        <el-icon class="el-icon--right"><arrow-down /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu class="theme-dropdown-menu">
          <el-dropdown-item
            v-for="(theme, name) in themes"
            :key="name"
            :command="name"
            :class="{ 'is-active': currentTheme.name === theme.name }"
          >
            <div class="theme-item">
              <el-icon v-if="currentTheme.name === theme.name"><check /></el-icon>
              <span>{{ themeNameMap[theme.name] || theme.name }}</span>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { Brush, Check, ArrowDown } from '@element-plus/icons-vue'
import { useTheme } from '../styles/theme/useTheme'

const { currentTheme, setTheme, themes } = useTheme()

const themeNameMap: Record<string, string> = {
  light: '明亮',
  dark: '暗黑',
  cyberpunk: '赛博朋克'
}

const handleThemeChange = (themeName: string) => {
  setTheme(themeName)
}
</script>

<style lang="scss" scoped>
.theme-switcher {
  display: inline-block;
  background: var(--el-color-background-gradient);

  :deep(.el-button) {
    background: var(--el-color-background-gradient);
    border-color: var(--el-color-primary);
    color: var(--el-text-color-primary);
    border-width: 0px;
    transition: all 0.2s;

    &:hover {
      background: var(--el-color-primary);
      color: var(--el-bg-color);
    }
    .el-icon {
      margin-right: 4px;
    }
  }
}

.theme-dropdown-menu {
  background: var(--el-color-background-gradient);
}

.theme-item {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 8px 12px;
  transition: all 0.2s;

  &:hover {
    background: var(--el-color-primary);
    color: var(--el-bg-color);
  }

  .el-icon {
    font-size: 16px;
    color: var(--el-color-primary);
  }
}

.el-dropdown-item.is-active {
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9, #f0f6ff);
  font-weight: 500;
}

:deep(.el-dropdown-menu) {
  padding: 4px 0;
  border-radius: 8px;
  box-shadow: none;
  border: none;
  color: var(--el-text-color-primary);
  background: var(--el-color-background-gradient);
}

:deep(.el-dropdown-menu__item) {
  padding: 0;
  line-height: normal;
  color: var(--el-text-color-primary);
}

:deep(.el-dropdown-menu__item:focus) {
  background: transparent;
}

:deep(.el-dropdown-menu__item--divided:before) {
  margin: 4px 0;
  background: var(--el-border-color-lighter);
}

:deep(.el-popper.is-light) {
  border: none;
}
</style>

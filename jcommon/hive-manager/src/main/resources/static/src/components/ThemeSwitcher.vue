<template>
  <div class="theme-switcher">
    <el-dropdown @command="handleThemeChange" trigger="click">
      <el-button type="primary" :icon="Brush">
        {{ currentTheme.name }}
        <el-icon class="el-icon--right"><arrow-down /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="(theme, name) in themes"
            :key="name"
            :command="name"
            :class="{ 'is-active': currentTheme.name === theme.name }"
          >
            <div class="theme-item">
              <el-icon v-if="currentTheme.name === theme.name"><check /></el-icon>
              <span>{{ theme.name }}</span>
              <el-icon
                v-if="isCustomTheme(theme.name)"
                class="delete-icon"
                @click.stop="handleDeleteTheme(theme.name)"
              >
                <Delete />
              </el-icon>
            </div>
          </el-dropdown-item>
          <el-dropdown-item divided>
            <div class="theme-item" @click.stop="showConfigurator">
              <el-icon><plus /></el-icon>
              <span>自定义主题</span>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 主题配置器 -->
    <ThemeConfigurator ref="configuratorRef" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Brush, Check, Delete, Plus } from '@element-plus/icons-vue'
import { useTheme } from '../styles/theme/useTheme'
import ThemeConfigurator from './ThemeConfigurator.vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const { currentTheme, setTheme, themes, deleteCustomTheme, isCustomTheme } = useTheme()
const configuratorRef = ref()

const handleThemeChange = (themeName: string) => {
  setTheme(themeName)
}

const handleDeleteTheme = async (themeName: string) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除主题"${themeName}"吗？此操作不可恢复。`,
      '删除主题',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    if (deleteCustomTheme(themeName)) {
      ElMessage.success(`主题"${themeName}"已删除`)
    } else {
      ElMessage.error('删除失败，该主题可能已不存在')
    }
  } catch {
    // 用户取消删除，不做任何操作
  }
}

const showConfigurator = () => {
  configuratorRef.value?.show()
}
</script>

<style lang="scss" scoped>
.theme-switcher {
  display: inline-block;

  background: var(--el-color-background-gradient);

  :deep(.el-button) {
    background-color: var(--el-color-background-gradient);
    border-color: var(--el-color-primary);
    color: var(--el-color-white);
    transition: all var(--el-transition-duration);

    &:hover {
      background-color: var(--el-color-primary-light-3);
      border-color: var(--el-color-primary-light-3);
    }

    .el-icon {
      margin-right: 4px;
    }
  }
}

.theme-item {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 8px 12px;
  color: var(--el-text-color-primary);
  transition: all var(--el-transition-duration);

  &:hover {
    background-color: var(--el-color-primary-light-9);
  }

  .el-icon {
    font-size: 16px;
    color: var(--el-color-primary);
  }
}

.delete-icon {
  margin-left: auto;
  color: var(--el-color-danger);
  font-size: 16px;
  opacity: 0;
  transition: all var(--el-transition-duration);

  &:hover {
    color: var(--el-color-danger-light-3);
  }
}

.el-dropdown-item:hover .delete-icon {
  opacity: 1;
}

.el-dropdown-item.is-active {
  color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
  font-weight: 500;
}

:deep(.el-dropdown-menu) {
  padding: 4px 0;
  border-radius: var(--el-border-radius-base);
  box-shadow: var(--el-box-shadow-light);
  border: 1px solid var(--el-border-color-light);
}

:deep(.el-dropdown-menu__item) {
  padding: 0;
  line-height: normal;
}

:deep(.el-dropdown-menu__item:focus) {
  background-color: transparent;
}

:deep(.el-dropdown-menu__item--divided:before) {
  margin: 4px 0;
  background-color: var(--el-border-color-lighter);
}
</style>

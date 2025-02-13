<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import LangSwitch from '@/components/LangSwitch.vue'
import ThemeSwitch from '@/components/ThemeSwitch.vue'
// import { useI18n } from 'vue-i18n'
import { t } from '@/locales/index'
import { ref, onMounted } from 'vue'
import { ElIcon, ElMessage } from 'element-plus'
import { MoreFilled } from '@element-plus/icons-vue'

const router = useRouter()
//const { t } = useI18n()
const activeTab = ref('home')
const autoRemoveHighlight = ref(false)

// 处理下拉菜单命令
const handleCommand = (command: string) => {
	if (command !== 'toggleHighlight') {
		handleSelect(command)
	}
}

const handleSelect = (key: string) => {
	activeTab.value = key
	router.push(`/${key === 'home' ? '' : key}`)
}

// 处理开关变化
const handleAutoRemoveHighlightChange = async (value: boolean) => {
	try {
		await chrome.runtime.sendMessage({
			type: 'setAutoRemoveHighlight',
			value
		})
		ElMessage.success(`${value ? '开启' : '关闭'}自动取消重绘效果`)
	} catch (error) {
		console.error('更新配置失败:', error)
		ElMessage.error('更新配置失败')
		// 还原开关状态
		autoRemoveHighlight.value = !value
	}
}

// 在组件挂载时读取配置
onMounted(async () => {
	try {
		const result = await chrome.storage.local.get(['autoRemoveHighlight']) as { autoRemoveHighlight: boolean }
		autoRemoveHighlight.value = result.autoRemoveHighlight ?? false
	} catch (error) {
		console.error('读取配置失败:', error)
	}
})
</script>

<template>
	<header class="app-header">
		<nav class="app-nav">
			<div class="nav-container">
				<el-tabs v-model="activeTab" @tab-click="(tab: any) => handleSelect(tab.props.name)">
					<el-tab-pane :label="t('nav.home')" name="home" />
				</el-tabs>
			</div>

			<div class="app-switches">
				<el-dropdown @command="handleCommand" trigger="click">
					<el-button type="primary" text>
						<el-icon><MoreFilled /></el-icon>
					</el-button>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item command="features">{{ t('nav.features') }}</el-dropdown-item>
							<el-dropdown-item command="preview">{{ t('nav.preview') }}</el-dropdown-item>
							<el-dropdown-item command="config">{{ t('nav.config') }}</el-dropdown-item>
							<el-dropdown-item divided>
								<el-tooltip
									content="开启后将自动取消页面重绘效果"
									placement="left"
									effect="light"
								>
									<div class="dropdown-switch-item">
										<span class="switch-label">自动取消重绘</span>
										<el-switch
											v-model="autoRemoveHighlight"
											@change="handleAutoRemoveHighlightChange"
										/>
									</div>
								</el-tooltip>
							</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>
				<ThemeSwitch class="app-theme-switch" />
			</div>
		</nav>
	</header>
	<div class="app-content">
		<RouterView />
	</div>
</template>

<style scoped>
.app-header {
	background-color: var(--el-bg-color);
	padding: 1rem;
	border-bottom: 1px solid var(--el-border-color-light);
}

.app-nav {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 0 20px;
}

.app-switches {
	display: flex;
	align-items: center;
	gap: 12px;
}

.app-theme-switch {
	margin: 0;
}

.app-content {
	flex: 1;
	overflow: hidden;
}

:deep(.el-tabs__header) {
	margin: 0;
}

:deep(.el-tabs__nav-wrap::after) {
	height: 0;
}

.nav-container {
	display: flex;
	align-items: center;
}

:deep(.el-dropdown .el-button) {
	padding: 8px;
	height: 40px;
}

.dropdown-switch-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	width: 100%;
	padding: 0;
}

.switch-label {
	color: var(--el-text-color-regular);
	margin-right: 12px;
	font-size: var(--el-font-size-base);
}

:deep(.el-dropdown-menu__item) {
	padding: 5px 12px;
}

:deep(.el-dropdown-menu__item.is-disabled) {
	cursor: default;
	background-color: transparent;
}

:deep(.el-dropdown-menu .el-switch) {
	margin-right: 8px;
}

:deep(.el-switch__label) {
	display: none;
}
</style>

<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import LangSwitch from '@/components/LangSwitch.vue'
import ThemeSwitch from '@/components/ThemeSwitch.vue'
// import { useI18n } from 'vue-i18n'
import { t } from '@/locales/index'
import { ref } from 'vue'
import { ElIcon } from 'element-plus'
import { MoreFilled } from '@element-plus/icons-vue'

const router = useRouter()
//const { t } = useI18n()
const activeTab = ref('home')

const handleSelect = (key: string) => {
	activeTab.value = key
	router.push(`/${key === 'home' ? '' : key}`)
}
</script>

<template>
	<header class="app-header">
		<nav class="app-nav">
			<div class="nav-container">
				<el-tabs v-model="activeTab" @tab-click="(tab) => handleSelect(tab.props.name)">
					<el-tab-pane :label="t('nav.home')" name="home" />
				</el-tabs>
			</div>

			<div class="app-switches">
				<el-dropdown @command="handleSelect" trigger="click">
					<el-button type="primary" text>
						<el-icon><MoreFilled /></el-icon>
					</el-button>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item command="features">{{ t('nav.features') }}</el-dropdown-item>
							<el-dropdown-item command="preview">{{ t('nav.preview') }}</el-dropdown-item>
							<el-dropdown-item command="config">{{ t('nav.config') }}</el-dropdown-item>
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
</style>

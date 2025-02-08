<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import LangSwitch from '@/components/LangSwitch.vue'
import ThemeSwitch from '@/components/ThemeSwitch.vue'
// import { useI18n } from 'vue-i18n'
import { t } from '@/locales/index'
import { ref } from 'vue'

const router = useRouter()
//const { t } = useI18n()
const activeTab = ref('home')

const handleTabClick = (tab: string) => {
	activeTab.value = tab
	router.push(`/${tab === 'home' ? '' : tab}`)
}
</script>

<template>
	<header class="app-header">
		<nav class="app-nav">
			<el-tabs v-model="activeTab" @tab-click="(tab) => handleTabClick(tab.props.name)">
				<el-tab-pane :label="t('nav.home')" name="home" />
				<el-tab-pane :label="t('nav.features')" name="features" />
				<el-tab-pane :label="t('nav.preview')" name="preview" />
			</el-tabs>
			<div class="app-switches">
				<ThemeSwitch class="app-theme-switch" />
				<!-- <LangSwitch class="app-lang-switch" /> -->
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
	gap: 1rem;
}

.app-theme-switch {
	margin-right: 10px;
}

.app-lang-switch {
	margin-left: 20px;
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
</style>

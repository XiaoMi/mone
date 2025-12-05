<template>
  <div class="header-container">
    <div>
      <LeftComp/>
    </div>
    <el-menu
      :default-active="activeIndex"
      class="el-menu-header"
      mode="horizontal"
      @select="handleSelect"
      router
    >
      <el-menu-item index="agents">AGENT 列表</el-menu-item>
      <el-menu-item index="tasks">TASK 列表</el-menu-item>
      <el-menu-item index="reportList">调用列表</el-menu-item>
    </el-menu>
    <div class="header-right">
      <el-dropdown @command="handleCommand">
        <span class="el-dropdown-link">
          {{userStore.user.username}}<el-icon class="el-icon--right"><arrow-down /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="token">token</el-dropdown-item>
            <el-dropdown-item command="inner">内部账号</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <ThemeSwitcher />
    </div>
  </div>
  <router-view />
  <TokenDialog ref="tokenDialogRef"/>
  <BindInner ref="bindInnerRef"/>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import LeftComp from './LeftComp.vue';
import { useUserStore } from '../stores/user';
import TokenDialog from './TokenDialog.vue';
import BindInner from './BindInner.vue';
import ThemeSwitcher from './ThemeSwitcher.vue';
const route = useRoute();
const router = useRouter();
const activeIndex = ref(route.path.substring(1) || 'agents');
const userStore = useUserStore();
const tokenDialogRef = ref(null);
const bindInnerRef = ref(null);
// 监听路由变化
watch(
  () => route.path,
  (newPath) => {
    activeIndex.value = newPath.substring(1) || 'agents';
  }
);

function handleSelect(key, keyPath) {
  activeIndex.value = key;
}

function handleCommand(command) {
  if (command === 'token') {
    tokenDialogRef.value.open();
  } else if (command === 'inner') {
    bindInnerRef.value.open();
  }
}
</script>

<style scoped>
.header-container {
  background-color: var(--el-color-chat-background);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px 12px;
}
.header-right {
  display: flex;
  align-items: center;
}
.header-right .el-dropdown {
  margin-right: 20px;
}
.el-menu-header {
  background-color: transparent;
  color: var(--el-color-primary);
  border-bottom: none;
  user-select: none;
  flex: 1;
}
.el-menu-header .el-menu-item {
  color: var(--el-menu-text);
  font-size: 20px;
  border-bottom: none !important;
}
.el-menu-header .el-menu-item:hover {
    color: var(--el-menu-text-hover);
}
.el-menu-header .el-menu-item.is-active {
  color: var(--el-menu-text-active) !important;
}
.el-menu-header .el-menu-item:hover,.el-menu-header .el-menu-item:focus {
  background-color: transparent;
}
.el-dropdown-link {
  outline: none !important;
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
}
.el-dropdown-menu {
  background: var(--el-color-background-gradient);
}
</style>

<template>
  <el-menu
      :default-active="activeIndex"
      class="el-menu-header"
      mode="horizontal"
      @select="handleSelect"
      router
    >
      <el-menu-item index="agents">AGENT 列表</el-menu-item>
      <el-menu-item index="tasks">TASK 列表</el-menu-item>
    </el-menu>
  <router-view />
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const activeIndex = ref(route.path.substring(1) || 'agents');

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
</script>

<style scoped>
.el-menu-header {
  background-color: transparent;
  color: var(--el-color-primary);
  position: absolute;
  top: 0;
  z-index: 10;
  width: 340px;
  left: 50%;
  transform: translateX(-50%);
  border-bottom: none;
  user-select: none;
}
.el-menu-header .el-menu-item {
  color: var(--el-color-primary);
  font-size: 18px;
  border-bottom: none !important;
  transition: transform 0.3s ease;
}
.el-menu-header .el-menu-item:hover {
    transform: scale(1.3);
    color: var(--el-color-primary);
}
.el-menu-header .el-menu-item.is-active {
  transform: scale(1.3);
  background: var(--el-color-background-gradient);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent !important;
}
.el-menu-header .el-menu-item:hover,.el-menu-header .el-menu-item:focus {
  background-color: transparent;
}
</style>

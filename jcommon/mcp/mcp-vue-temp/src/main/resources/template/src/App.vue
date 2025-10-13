<template>
  <div id="app">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>${PROJECT_NAME}</h1>
          <el-menu
            mode="horizontal"
            :default-active="$route.path"
            router
            class="nav-menu"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/about">关于</el-menu-item>
            <el-menu-item index="/counter">计数器</el-menu-item>
          </el-menu>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useCounterStore } from './stores/counter'

const counterStore = useCounterStore()

onMounted(() => {
  console.log('App mounted')
})
</script>

<style scoped>
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.nav-menu {
  border-bottom: none;
}

h1 {
  margin: 0;
  color: #409eff;
}
</style>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.el-header {
  background-color: #f5f5f5;
  border-bottom: 1px solid #e4e7ed;
}
</style>

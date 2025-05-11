<template>
  <div class="app-container">
    <header class="app-header">
      <div class="header-left">
        <h1>Hive Manager</h1>
      </div>
      <div class="header-right">
        <ThemeSwitcher />
      </div>
    </header>
    <main class="app-main">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import ThemeSwitcher from './components/ThemeSwitcher.vue'
</script>

<style lang="scss">
html,body {
  height: 100%;
  font-family: cursive;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100%;
}

#nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--el-border-color-light);
  background-color: var(--el-color-background);
}

.header-left h1 {
  margin: 0;
  font-size: 1.5rem;
  color: var(--el-color-primary);
}

.app-main {
  display: flex;
  flex: 1;
  // padding: 2rem;
  background-color: var(--el-color-background);
}

/* 全局过渡效果 */
.theme-transition {
  transition: all 0.3s ease;
}

/* 确保所有Element Plus组件使用CSS变量 */
:root {
  --el-transition-duration: 0.3s;
  --el-transition-function: ease;
}
</style>

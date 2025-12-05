<template>
    <div class="title-container">
        <h1>⚡ {{showTitle}}</h1>
        <div class="animated-underline"></div>
    </div>
</template>

<script setup>
import { useRoute } from "vue-router"
import { watch, computed } from "vue"
const route = useRoute();
const showTitle = computed(() => {
  if (route.path === '/agents') {
    return 'AGENT';
  } else if (route.path === '/tasks') {
    return 'TASK';
  } else if (route.path === '/reportList') {
    return 'REPORT';
  }
});
</script>

<style scoped>

.title-container {
  position: relative;
  margin-right: 20px;
  position: relative;
  width: 160px;
}

/* .title-container::after {
  content: '';
  position: absolute;
  right: -20px;
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 24px;
  background-color: #6e6e6e;
} */

.animated-underline {
  position: absolute;
  bottom: 0px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--el-color-background-gradient);
  animation: progressLine 3s ease-in-out infinite;
  box-shadow: 0 0 10px var(--el-color-background-gradient);
}

@keyframes progressLine {
  0% {
    width: 0;
    opacity: 0.6;
    left: 0;
  }
  50% {
    width: 100%;
    opacity: 1;
    left: 0;
  }
  51% {
    width: 100%;
    opacity: 1;
    left: 0;
  }
  100% {
    width: 0;
    opacity: 0.6;
    left: 100%;
  }
}
h1 {
  font-family: 'Orbitron', sans-serif;
  font-size: 1.6rem;
  background: var(--el-color-background-gradient) text;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
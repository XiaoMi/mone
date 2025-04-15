<template>
  <div class="accordion-loading">
    <el-icon color="#5897ff" size="24" class="rotating-icon"
      ><Loading
    /></el-icon>
    <p>连接中，请稍等...</p>
  </div>
</template>

<script setup>
import { ref } from "vue";
</script>

<style scoped>
.accordion-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  transform: translate(-50%, -50%);
  animation: accordion 2s ease-in-out infinite;
  font-size: 18px;
}

.rotating-icon {
  animation: rotate 2s linear infinite;
}

p {
  background: linear-gradient(90deg, #b8d2fa, #00ff00, #333333);
  -webkit-background-clip: text;
  color: transparent;
  animation: text-gradient 3s ease-in-out infinite;
  margin-left: 8px;
}

@keyframes accordion {
  0%,
  100% {
    transform: scaleY(1);
  }
  50% {
    transform: scaleY(0.1);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes text-gradient {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}
</style>

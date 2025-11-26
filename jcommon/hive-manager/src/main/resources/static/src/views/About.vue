<template>
  <div class="index-home">
    <div class="canvas-container">
      <canvas ref="bubbleCanvas"></canvas>
    </div>
    <div class="recommend-wrap">
      <Recommend />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import Recommend from "./Recommend.vue";

const bubbleCanvas = ref<HTMLCanvasElement | null>(null);

onMounted(() => {
  const canvas = bubbleCanvas.value;
  if (!canvas) return;

  const ctx = canvas.getContext("2d");
  if (!ctx) return;

  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;

  const bubbleCount = window.innerWidth > 1440 ? 12 : 6;
  const bubbles = Array.from({ length: bubbleCount }, () => createBubble(canvas));

  function createBubble(canvas: HTMLCanvasElement) {
    return {
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      radius: Math.random() * 10 + 20,
      dx: Math.random() * 2 - 1,
      dy: Math.random() * 2 - 1,
    };
  }

  function drawBubble(bubble: any) {
    ctx.beginPath();
    ctx.arc(bubble.x, bubble.y, bubble.radius, 0, Math.PI * 2);
    ctx.fillStyle = "rgba(89,146,255,0.5)";
    ctx.fill();
    ctx.closePath();
  }

  function updateBubble(bubble: any) {
    bubble.x += bubble.dx;
    bubble.y += bubble.dy;

    if (
      bubble.x + bubble.radius > canvas.width ||
      bubble.x - bubble.radius < 0
    ) {
      bubble.dx *= -1;
    }
    if (
      bubble.y + bubble.radius > canvas.height ||
      bubble.y - bubble.radius < 0
    ) {
      bubble.dy *= -1;
    }
  }

  function animate() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    bubbles.forEach((bubble) => {
      updateBubble(bubble);
      drawBubble(bubble);
    });
    requestAnimationFrame(animate);
  }

  animate();
});
</script>

<style scoped>
.index-home {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(to top right, #b1cdfb, #c3daff, #ffffff);
  /* background: linear-gradient(to bottom left,#ffa672, #ffb88f, #ffffff); */
}

.canvas-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  z-index: 1;
}

canvas {
  display: block;
}

.recommend-wrap {
  position: relative;
  z-index: 2;
}
</style>

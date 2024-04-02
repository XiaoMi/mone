<template>
  <div class="star-container">
    <div class="star-content">
      <div class="star-item">
        <div class="star star-gray" v-for="(item, index) in allNum" :key="index">
          <i class="iconfont icon-star-fill"></i>
        </div>
      </div>
      <div class="star-item" :style="'width:' + (100 * props.num) / props.total + '%;'">
        <div :class="'star  selected'" v-for="(item, index) in allNum" :key="index">
          <i class="iconfont icon-star-fill"></i>
        </div>
      </div>
    </div>
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  num: {
    type: Number,
    default: 0
  },
  total: {
    type: Number,
    default: 5
  }
})
const allNum = [1, 2, 3, 4, 5]
</script>

<style scoped lang="scss">
.star-container {
  display: flex;
  align-items: center;
  .star-content {
    position: relative;
    width: 100px;
    display: flex;
    align-items: center;
    .star-item {
      position: absolute;
      display: flex;
      align-items: center;
      overflow: hidden;
    }
  }

  .star {
    width: 16px;
    height: 16px;
    color: #727272;
    display: flex;
    align-items: center;
    margin: 0 2px;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-blend-mode: normal, lighten, soft-light;

    &.star-gray {
      background-image: linear-gradient(to left, #bdbbbe 0%, #9d9ea3 100%),
        radial-gradient(
          88% 271%,
          rgba(255, 255, 255, 0.25) 0%,
          rgba(254, 254, 254, 0.25) 1%,
          rgba(0, 0, 0, 0.25) 100%
        ),
        radial-gradient(50% 100%, rgba(255, 255, 255, 0.3) 0%, rgba(0, 0, 0, 0.3) 100%);
    }
    &.selected {
      background-image: linear-gradient(to right, #fa709a 0%, #fee140 100%);
    }
  }
}
</style>

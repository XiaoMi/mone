<!--
 * @Description: 
 * @Date: 2024-09-23 15:44:53
 * @LastEditTime: 2024-10-22 10:22:42
-->
<template>
  <!-- title -->
  <span v-if="type == 'BASE_COMPONENT_TITLE'" :style="size == 'small' ? '' : 'font-size:20px;'">{{
    content
  }}</span>
  <!-- text -->
  <span v-if="type == 'BASE_COMPONENT_TEXT'" :style="size == 'small' ? '' : 'font-size:14px;'">{{
    content
  }}</span>
  <!-- 按钮 -->
  <span v-if="type == 'BASE_COMPONENT_BUTTON'">
    <el-tag effect="dark" size="small" v-if="size == 'small'">+</el-tag>
    <el-button type="primary" v-else>Button</el-button>
  </span>
  <!-- 图片 -->
  <i
    v-else-if="type == 'BASE_COMPONENT_IMAGE'"
    class="iconfont icon-tuxiang"
    :style="size == 'small' ? 'font-size: 20px' : 'font-size: 40px;color:#ddd;'"
  ></i>
  <!-- 图标 -->
  <i
    v-else-if="type == 'BASE_COMPONENT_ICON'"
    class="iconfont icon-wanneng"
    :style="size == 'small' ? 'font-size: 20px' : 'font-size: 40px;color:#ddd;'"
  ></i>
  <!-- tag -->
  <span v-else-if="type == 'BASE_COMPONENT_TAG'"> <el-tag :size="props.size">Tag</el-tag></span>
  <!-- 评分 -->
  <BaseStar
    v-else-if="type == 'BASE_COMPONENT_SCORE'"
    :num="4.5"
    :size="size == 'small' ? 'mini' : ''"
  ></BaseStar>
  <!-- 分割线 -->
  <span v-else-if="type == 'BASE_COMPONENT_DIVIDER'" class="divider"></span>
</template>

<script setup lang="ts">
import BaseStar from '@/components/probot/BaseStar.vue'
const props = defineProps({
  size: {
    type: String,
    default: 'small'
  },
  content: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: ''
  }
})
</script>

<style scoped lang="scss">
.divider {
  padding: 0 10px;
  width: 100%;
  display: flex;
  align-items: center;
  &:after {
    content: '';
    display: inline-block;
    width: 100%;
    height: 2px;
    background-color: rgba(0, 0, 0, 0.3);
  }
}
</style>

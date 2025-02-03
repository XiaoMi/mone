<!--
 * @Description: 
 * @Date: 2024-10-16 20:01:10
 * @LastEditTime: 2024-10-17 10:42:26
-->
<template>
  <TitleFirst title="基础配置"></TitleFirst>
  <div v-if="slotData">
    <TitleSecond title="数据"></TitleSecond>
    <slot name="data"></slot>
    <Divider />
  </div>
  <div v-if="slotBase">
    <TitleSecond title="样式"></TitleSecond>
    <slot name="base"></slot>
    <Divider />
  </div>
  <div v-if="slotOperate">
    <TitleSecond title="操作"></TitleSecond>
    <slot name="operate"></slot>
    <Divider />
  </div>
  <TitleFirst title="高级配置"></TitleFirst>
  <slot name="high" v-if="slotHigh"></slot>
  <div v-else>暂无配置</div>
</template>

<script setup lang="ts">
import TitleFirst from './components/TitleFirst.vue'
import TitleSecond from './components/TitleSecond.vue'
import Divider from './components/Divider.vue'
import { useSlots } from 'vue'

const slotData = !!useSlots().data
const slotBase = !!useSlots().base
const slotOperate = !!useSlots().operate
const slotHigh = !!useSlots().high
</script>

<style scoped></style>

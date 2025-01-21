<template>
  <div class="no-tips">
    <template v-if="[0, 2].includes(props.status)">
      <div class="center-class div100">
        <div class="div600 border-gray center-class">
          <NotSucImg :status="props.status" failedWidth="80px" />
        </div>
      </div>
    </template>
    <template v-else>
      <template v-if="props.list">
        <!-- 如果是大于1 -->
        <template v-if="props.list.length > 1">
          <MultipleImg :result="props.list" ref="mulImgRef" />
        </template>
        <!-- 如果是等于1  -->
        <div v-else class="center-class div100">
          <div class="div600">
            <ImgItem :imgUrl="props.list[0]" />
          </div>
        </div>
      </template>
    </template>
  </div>
</template>

<script setup lang="ts">
import MultipleImg from './MultipleImg.vue'
import ImgItem from './ImgItem'
import NotSucImg from '@/views/probot-mode/components/history/NotSucImg.vue'

const props = defineProps({
  list: {
    type: Array,
    default: () => []
  },
  status: {}
})
</script>

<style scoped>
.no-tips {
  border: none;
  display: flex;
  flex-wrap: wrap;
  width: 100%;
  height: 100%;
}
.center-class {
  display: flex;
  justify-content: center;
  align-items: center;
}
.div100 {
  width: 100%;
  height: 100%;
}
.div600 {
  width: 600px;
  height: 600px;
}
.center-failed {
  width: 100%;
  height: 100%;
}
.border-gray {
  border: dashed 1px #dcdfe6;
}
</style>

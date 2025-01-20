<template>
  <div class="outputs" v-for="item in showArr" :key="item.name">
    <div class="flex-class p-r" v-if="item.isBatch">
      <EditOutBtn
        v-if="type == 'output'"
        :item="item"
        :index="activeNum - 1"
        :batch="true"
        :nodeData="nodeData"
        :type="type"
        class="p-a"
      />
      <span class="name-i">{{ item.name }}：</span>
      <BatchResFilterItem :item="item.value[activeNum - 1]" />
    </div>
    <div class="flex-class p-r" v-else>
      <EditOutBtn
        :item="item"
        :nodeData="nodeData"
        :type="type"
        v-if="type == 'output'"
        class="p-a"
      />
      <span class="name-i">{{ item.name }}：</span>
      <ResItemVal :item="item.value" />
    </div>
  </div>
</template>

<script setup>
// import { computed } from 'vue'
import EditOutBtn from './EditOutBtn.vue'
import ResItemVal from '@/views/workflow/work-flow/components/components/ResItemVal.vue'
import BatchResFilterItem from './BatchResFilterItem.vue'

const props = defineProps({
  showArr: {},
  activeNum: {},
  nodeData: {},
  type: {}
})
</script>

<style lang="scss" scoped>
.flex-class {
  display: flex;
}
.json-box {
  flex: 1;
}
.p-r {
  position: relative;
}
.p-a {
  position: absolute;
  right: 0;
  top: 0;
}
</style>

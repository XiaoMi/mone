<template>
  <div
    class="pop-side"
    v-if="show"
    :style="{ height: popHeight + 'px', ...transformObj }"
    @wheel="handleNoWheelFn"
  >
    <Sidebar
      :showDetail="false"
      @addClick="addClickFn"
      @addPluginFlow="addPluginFlow"
      :draggable="false"
    />
  </div>
</template>

<script setup lang="ts">
import Sidebar from '../../components/Sidebar.vue'
import { defineProps, defineEmits, computed, ref } from 'vue'
import { useVueFlow } from '@vue-flow/core'
import { handleNoWheel } from '@/views/workflow/work-flow/baseInfo.js'

const handleNoWheelFn = ref(handleNoWheel)

const { getEdge, getNode } = useVueFlow()

const popHeight = ref(380)
const props = defineProps({
  showPopSideBtn: {},
  modelValue: {},
  btnPosition: {},
  clickAddEdgeId: {}
})
const emits = defineEmits(['update:modelValue', 'addEdgeNode'])
const show = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const transformObj = computed(() => {
  const { btnPosition } = props
  const obj = {
    x: btnPosition.x,
    y: btnPosition.y - popHeight.value / 2
  }
  return {
    transform: `translate(${obj.x}px,${obj.y}px)`
  }
})

const addClickFn = (type) => {
  const curEdge = getEdge.value(props.clickAddEdgeId)
  const targetId = curEdge.target
  const nextNode = getNode.value(targetId)
  show.value = false
  emits('addEdgeNode', { curEdge, nextNode, type })
}

const addPluginFlow = (val) => {
  const curEdge = getEdge.value(props.clickAddEdgeId)
  const targetId = curEdge.target
  const nextNode = getNode.value(targetId)
  show.value = false
  emits('addEdgeNode', { curEdge, nextNode, type: val.type, pluginInfo: val })
}
</script>

<style scoped lang="scss">
.pop-side {
  z-index: 9;
  position: fixed;
  left: 0;
  top: 0;
  border: solid 1px #eee;
  background-color: #fff;
  :deep(.sidebar) {
    background-color: #fff;
  }
  :deep(.node-item) {
    margin: 0;
    box-shadow: none;
    background: transparent;
    &:hover {
      background-color: #eee;
    }
  }
}
.edgebutton {
  border-radius: 50%;
  width: 20px;
  height: 20px;
  color: #fff;
  position: absolute;
  left: -30px;
  top: 50%;
}
</style>

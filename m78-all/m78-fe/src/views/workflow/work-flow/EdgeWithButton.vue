<template>
  <!-- You can use the `BaseEdge` component to create your own custom edge more easily -->
  <BaseEdge :id="id" :style="style" :path="path[0]" :marker-end="markerEnd" />

  <!-- Use the `EdgeLabelRenderer` to escape the SVG world of edges and render your own custom label in a `<div>` ctx -->
  <EdgeLabelRenderer>
    <div
      :style="{
        pointerEvents: 'all',
        position: 'absolute',
        transform: `translate(-50%, -50%) translate(${path[1]}px,${path[2]}px)`
      }"
      class="nodrag nopan edge-item-custom"
      :class="showBtn ? 'edge-btn-active-class' : ''"
      @mouseenter="showBtnFn"
      @mouseleave="hideBtnFn"
    >
      <el-button class="edgebutton" @click="clickBtn" type="primary" ref="btnRef">
        <i class="edge-btn-icon">+</i>
      </el-button>
    </div>
  </EdgeLabelRenderer>
</template>
<script setup>
import { BaseEdge, EdgeLabelRenderer, getBezierPath, useVueFlow } from '@vue-flow/core'
import { computed, ref, defineEmits, onMounted } from 'vue'

const props = defineProps({
  id: {
    type: String,
    required: true
  },
  sourceX: {
    type: Number,
    required: true
  },
  sourceY: {
    type: Number,
    required: true
  },
  targetX: {
    type: Number,
    required: true
  },
  targetY: {
    type: Number,
    required: true
  },
  sourcePosition: {
    type: String,
    required: true
  },
  targetPosition: {
    type: String,
    required: true
  },
  markerEnd: {
    type: String,
    required: false
  },
  style: {
    type: Object,
    required: false
  },
  showBtn: {
    type: Boolean
  }
})
const btnRef = ref(null)
const { removeEdges } = useVueFlow()

const path = computed(() => getBezierPath(props))

const hovering = ref(false)
const showpop = () => {
  console.log('showPop')
}
// const innerShowBtn = ref(false)
const showBtnFn = (e) => {
  const target = e.currentTarget
  target.classList.add('edge-btn-active-class')
}
const hideBtnFn = (e) => {
  const target = e.currentTarget
  target.classList.remove('edge-btn-active-class')
}

const emits = defineEmits(['clickEdgeBtn'])
const clickBtn = () => {
  // 获取button元素
  const btnEleRef = btnRef.value
  // 如果这个dom中有元素id是'edge-btn-active'，则去掉这个id
  const activeBtn = document.getElementById('edge-btn-active')
  // 查看activeBtn的'edge-btn-active'这个Id
  if (activeBtn) {
    activeBtn.removeAttribute('id')
  }
  btnEleRef.$el.id = 'edge-btn-active'
  // btnEleRef增加class
  btnEleRef.$el.classList.add('edge-btn-active-class')
  // 触发父组件的clickEdgeBtn事件
  emits('clickEdgeBtn', { edgeId: props.id })
}
</script>

<script>
export default {
  inheritAttrs: false
}
</script>
<style lang="scss" scoped>
.edge-item-custom {
  z-index: 1;
  .edgebutton {
    border-radius: 50%;
    width: 15px;
    height: 15px;
    line-height: 15px;
    color: #fff;
    padding: 0;
    visibility: hidden;
  }
}
.edge-btn-active-class {
  .edgebutton {
    visibility: visible;
  }
}
.sidebar-box {
  height: 580px;
  overflow: hidden;
  :deep(.sidebar) {
    height: 100%;
  }
}
.edge-btn-icon {
  font-size: 14px;
}
</style>

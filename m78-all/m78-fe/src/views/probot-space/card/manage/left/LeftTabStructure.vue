<!--
 * @Description: 
 * @Date: 2024-09-05 17:46:33
 * @LastEditTime: 2024-10-12 16:28:46
-->
<template>
  <div class="card-tree-container">
    <p
      :class="['card-tree-title', currentItem.uniqueKey === cardList[0].uniqueKey && 'selected']"
      @click="handleCardClick"
    >
      Card
    </p>
    <el-tree
      :data="cardList[0]?.children[0].children"
      :props="defaultProps"
      @node-click="handleNodeClick"
      :allow-drop="allowDrop"
      :allow-drag="allowDrag"
      draggable
      default-expand-all
      node-key="uniqueKey"
      :expand-on-click-node="false"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type Node from 'element-plus/es/components/tree/src/model/node'
import type { AllowDropType } from 'element-plus/es/components/tree/src/tree.type'
import { useProbotCardStore } from '@/stores/card'
const cardStore = useProbotCardStore()
const cardList = computed(() => cardStore.cardList)
const currentItem = computed(() => cardStore.currentItem)
const dataTypeMap = computed(() => cardStore.dataTypeMap)

interface Tree {
  label?: string
  children?: Tree[]
  uniqueKey?: string
  type?: string
}

const defaultProps = {
  children: 'children',
  label: 'label',
  class: (data: Tree, node: Node) => {
    if (data.uniqueKey === currentItem.value.uniqueKey) {
      return 'selected'
    }
    return ''
  }
}

const handleCardClick = () => {
  cardStore.setCurrentItem(cardList.value[0])
}

const handleNodeClick = (data: Tree) => {
  data.uniqueKey && cardStore.setCurrentItem(data)
}
const allowDrop = (draggingNode: Node, dropNode: Node, type: AllowDropType) => {
  // 这个type是拖拽的，inner，next，prev
  if (type == 'inner') {
    if (dropNode.data.type == 'CARD_ROOT') {
      return true
    } else if (dropNode.data.type == 'container') {
      return true
    }
  } else {
    if (dropNode.data.type !== 'container') {
      return true
    }
  }
  return false
}
const allowDrag = (draggingNode: Node) => {
  return dataTypeMap.value[draggingNode.data.type]?.customizeType === 'outer'
}
</script>

<style lang="scss">
.card-tree-container {
  width: 100%;
  height: 100%;
  overflow: auto;
  .card-tree-title {
    background-color: #fff;
    color: rgb(96, 98, 102);
    font-size: 14px;
    padding: 6px 8px;
    &:hover {
      background-color: #f5f7fa;
    }
  }
  .selected {
    color: var(--oz-menu-active-color);
    background-color: #edecfc;
  }
}
</style>

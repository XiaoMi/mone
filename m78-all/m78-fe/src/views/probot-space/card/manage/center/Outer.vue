<!--
 * @Description: 
 * @Date: 2024-09-20 15:18:39
 * @LastEditTime: 2024-10-22 14:31:13
-->
<template>
  <VueDraggable
    class="drag-area"
    tag="ul"
    v-model="list"
    :group="{ name: 'g1', pull: canPut, put: true }"
    :sort="false"
    item-key="uniqueKey"
    filter=".inner"
    draggable=".outer "
  >
    <li
      v-for="(item, index) in modelValue"
      :class="[
        item.type == 'container' ? 'inner' : item.type + ' outer',
        cardStore.currentOverKey == item.uniqueKey && 'hover',
        currentItem.uniqueKey == item.uniqueKey && 'selected'
      ]"
      @click.stop="itemclick(item)"
      @mouseover.stop="mouseover(item)"
      @mouseleave="mouseleave"
      :data-key="item.uniqueKey"
      :style="[
        (item.type == 'CARD_ROOT' ||
          cardStore.elementTypes.layout.includes(item.type) ||
          item.type == 'container') && {
          'grid-row-gap': item.property?.form.rowGap + 'px',
          padding: item.property?.form.padding + 'px'
        },
        (item.type == 'CARD_ROOT' || cardStore.elementTypes.layout.includes(item.type)) && {
          'background-color':
            item.property?.background?.backgroundType === 'Color'
              ? item.property.background.backgroundColor
              : 'transparent',
          'background-image':
            item.property?.background?.backgroundType === 'Picture'
              ? 'linear-gradient(rgba(255, 255, 255, ' +
                (1 - Number(item.property.background.backgroundImageTransparency) / 100) +
                '), rgba(255, 255, 255, ' +
                (1 - Number(item.property.background.backgroundImageTransparency) / 100) +
                ')), url(' +
                item.property.background.backgroundImageUrl +
                ')'
              : '',
          'background-position-x':
            cardStore.horizontal[item.property?.background.backgroundImageHorizontalPosition],
          'background-position-y':
            cardStore.vertical[item.property?.background.backgroundImageVerticalPosition]
        },
        cardStore.elementTypes.layout.includes(item.type) && {
          'flex-flow': [
            'LAYOUT_SINGLE_ROW_1',
            'LAYOUT_MULTI_ROW_1_1_1',
            'LAYOUT_SIDESLIP'
          ].includes(item.type)
            ? 'row nowrap'
            : 'row wrap',
          'border-radius': item.property?.form.roundedSize + 'px'
        },
        item.type == 'container' && {
          width:
            item.parentType == 'LAYOUT_GRID'
              ? ' calc((100% - ' +
                props.rowGap * (Number(props.itemsPerRow) - 1) +
                'px) / ' +
                props.itemsPerRow +
                ')'
              : item.parentType == 'LAYOUT_ROW'
                ? '100%'
                : ['LAYOUT_SIDESLIP', 'LAYOUT_FLOAT'].includes(item.parentType || '')
                  ? item.property?.form.width === 'weight'
                    ? item.property?.form.weight + 'px'
                    : 'auto'
                  : '',
          height: item.children.length == 0 ? '40px' : 'auto',
          'margin-right':
            (index !== list?.length - 1 &&
              ['LAYOUT_SINGLE_ROW_1', 'LAYOUT_MULTI_ROW_1_1_1', 'LAYOUT_SIDESLIP'].includes(
                item.parentType || ''
              )) ||
            ((index + 1) % props?.itemsPerRow !== 0 &&
              ['LAYOUT_GRID', 'LAYOUT_FLOAT'].includes(item.parentType || ''))
              ? props.rowGap + 'px'
              : '',
          'vertical-align': item.property?.form.vertical,
          'text-align': item.property?.form.horizontal,
          'flex-direction': 'column',
          flex: ['LAYOUT_SINGLE_ROW_1', 'LAYOUT_MULTI_ROW_1_1_1'].includes(item.parentType || '')
            ? item.property?.form.width === 'weight'
              ? item.property?.form.weight
                ? item.property?.form.weight + ' ' + item.property?.form.weight + ' 0px'
                : '1 1 0px'
              : item.property?.form.width === 'auto' &&
                '0 ' + item.property.form.enableShrink + ' auto'
            : '0 0 auto',
          border:
            item.parentType == 'LAYOUT_SIDESLIP' && item.property.form.enableShowBorder == '1'
              ? '1px solid #e6e6e6ff'
              : 'none'
        },
        cardStore.elementTypes.base.includes(item.type) && {
          display: 'inline'
        }
      ]"
    >
      <!-- 内容 -->
      <Inner :content="item.property?.content?.value" :type="item.type" size="large"></Inner>
      <!-- 操作 -->
      <div
        class="card-opeartion"
        v-if="currentItem.uniqueKey == item.uniqueKey && item.type !== 'CARD_ROOT'"
      >
        <el-tooltip
          effect="dark"
          content="复制"
          placement="top"
          v-if="props.layoutType != '1' && item.type !== 'BASE_COMPONENT_TITLE'"
        >
          <el-link :underline="false"
            ><el-icon :size="16" @click.stop="copy(list, item, index)"><DocumentCopy /></el-icon
          ></el-link>
        </el-tooltip>
        <el-tooltip effect="dark" content="删除" placement="top">
          <el-link :underline="false"
            ><el-icon :size="16" @click.stop="remove(list, item, index)"><Delete /></el-icon
          ></el-link>
        </el-tooltip>
      </div>
      <Outer
        v-model="item.children"
        :rowGap="item.property?.form?.rowGap"
        :layoutType="item.property?.layoutType"
        :itemsPerRow="item.property?.itemsPerRow"
      ></Outer>
    </li>
  </VueDraggable>
</template>

<script setup lang="ts">
import { VueDraggable } from 'vue-draggable-plus'
import { computed } from 'vue'
import Inner from './Inner.vue'
import { useProbotCardStore } from '@/stores/card'
import { removeNodeByUniqueKey, deepCloneTree } from '../tree'
const cardStore = useProbotCardStore()
const currentItem = computed(() => cardStore.currentItem)
const cardList = computed(() => cardStore.cardList)

interface IList {
  children: IList[]
  property: {
    background: {}
    loopRending: {
      enableLoopRending: Number
      boundArrayVariable: string
    }
  }
  type: string
  uniqueKey: string
  className?: string
  content?: string
  row?: number
  column?: number
  parentType?: string
}

interface Props {
  modelValue: IList[]
  rowGap?: number
  layoutType?: string
  itemsPerRow?: string
}

const props = defineProps<Props>()

interface Emits {
  (e: 'update:modelValue', value: IList[]): void
}

const emits = defineEmits<Emits>()
const list = computed({
  get: () => props.modelValue,
  set: (value) => emits('update:modelValue', value)
})

const itemclick = (v: any) => {
  if (v.parentType != 'CARD_ROOT') {
    cardStore.setCurrentItem(v)
  }
}
const mouseover = (v: any) => {
  if (v.parentType != 'CARD_ROOT') {
    cardStore.setOverKey(v.uniqueKey)
  }
  return false
}
const mouseleave = () => {
  cardStore.setOverKey('')
}
const canPut = (to: any, from: any, draggedElement: any, event: any) => {
  // 实现您的逻辑，返回 true 允许放置，返回 false 禁止放置
  if (draggedElement.className.includes('inner')) {
    return false
  } else if (to.el.parentElement.className.includes('inner')) {
    return true
  } else {
    return false
  }
}
const copy = (parent: any, item: any, index: number) => {
  list.value.splice(index + 1, 0, deepCloneTree(item))
  parent.forEach((element, index) => {
    element.label = element.treeLabel + (index + 1)
  })
}
const remove = (parent: any, item: any, index: number) => {
  list.value.splice(index, 1)
  cardStore.setCurrentItem({})
  parent.forEach((element, index) => {
    element.label = element.treeLabel + (index + 1)
  })
  if (list.value.length === 0) {
    const newList = removeNodeByUniqueKey(cardList.value, parent.uniqueKey)
    cardStore.setCardList(newList)
  }
}
</script>

<style scoped lang="scss">
.drag-area {
  width: 100%;
  display: flex;
  box-sizing: border-box;
  overflow-wrap: break-word;
  flex-flow: inherit;
  row-gap: inherit;
  position: relative;

  .hover,
  .selected {
    outline: 1px dotted var(--oz-color-primary) !important;
  }
}
.inner {
  flex-flow: inherit;
  row-gap: inherit;
}
.CARD_ROOT {
  flex-flow: column wrap;
  border-radius: 5px;
  max-height: 100%;
  width: 100%;
  background-color: #ddd;
  & > .drag-area > .inner {
    outline: none !important;
    background-color: transparent;
  }
}
.LAYOUT_SIDESLIP {
  .drag-area {
    padding: 1px;
    overflow-x: auto;
    &::-webkit-scrollbar {
      display: none;
    }
  }
}

.card-opeartion {
  position: absolute;
  top: 0px;
  left: 0px;
  display: flex;
  align-items: center;
  justify-content: space-around;
  width: 50px;
  height: 35px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 0 5px #ddd;
  z-index: 999;
}
</style>

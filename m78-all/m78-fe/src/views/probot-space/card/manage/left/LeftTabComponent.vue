<!--
 * @Description: 
 * @Date: 2024-09-05 17:44:08
 * @LastEditTime: 2024-11-05 15:32:04
-->
<template>
  <div v-for="(item, key) in data" :key="key" class="card-component-module small">
    <h3>{{ item.title }}</h3>
    <el-row :gutter="10" style="margin: 0px">
      <el-col :span="12" v-for="(value, index) in item.children" :key="index">
        <el-row
          :class="['outer', value?.className]"
          @click="click(value)"
          :gutter="2"
          draggable="true"
          @dragstart="dragstart(value)"
        >
          <div v-if="value?.content" style="height: 20px" class="content-inner">
            <Inner :content="value?.content" :type="value.type" size="small"></Inner>
          </div>
          <el-col
            v-for="(v, i) in value.smallChildren"
            :key="i"
            :span="v.column"
            class="inner"
            :style="'height: ' + 10 * v.row + 'px'"
          >
          </el-col>
        </el-row>
        <p>{{ value?.title }}</p>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getElementTypes } from '@/api/probot-card'
import { useProbotCardStore } from '@/stores/card'
import { useRoute } from 'vue-router'
import Inner from '../center/Inner.vue'
import { generateUUID, addNodeToChildren, addNodeAfter } from '../tree'
import mitt from '@/utils/bus'
import { ElMessage } from 'element-plus'
const route = useRoute()

const cardStore = useProbotCardStore()
const dataTypeMap = computed(() => cardStore.dataTypeMap)
const cardList = computed(() => cardStore.cardList)
const currentItem = computed(() => cardStore.currentItem)
const dragItem = ref('')

const data = ref([])
onMounted(() => {
  Promise.all([
    getElementTypes({ type: 'ALL' }),
    getElementTypes({ type: 'LAYOUT' }),
    getElementTypes({ type: 'BASE_COMPONENT' })
  ]).then(([all, layout, base]) => {
    cardStore.setElementTypes({
      all: all.data,
      layout: layout.data,
      base: base.data
    })
    all.data.forEach((item, index) => {
      const typeData = dataTypeMap.value[item]
      const children = typeData?.children?.map((v, i) => {
        return {
          ...JSON.parse(JSON.stringify(cardStore.initSetting)),
          ...v,
          type: 'container',
          treeLabel: 'Colunmn',
          label: 'Colunmn' + (i + 1),
          row: typeData.row,
          workspaceId: route.params.workSpaceId,
          cardId: route.params.cardId,
          parentType: item,
          property: {
            ...JSON.parse(JSON.stringify(cardStore.initSetting.property)),
            form: {
              ...JSON.parse(JSON.stringify(cardStore.initSetting?.property?.form)),
              weight: typeData.weight
            }
          }
        }
      })
      dataTypeMap.value[item] = {
        ...JSON.parse(JSON.stringify(cardStore.initSetting)),
        treeLabelIndex: 0,
        ...typeData,
        children: children,
        smallChildren: (typeData?.smallChildren || typeData?.children)?.map((item) => {
          return { ...item, row: typeData.row }
        }),
        customizeType: 'outer',
        type: item,
        workspaceId: route.params.workSpaceId,
        cardId: route.params.cardId,
        property: {
          ...JSON.parse(JSON.stringify(cardStore.initSetting.property)),
          itemsPerRow: dataTypeMap.value[item].itemsPerRow,
          content: {
            value: typeData.content
          }
        }
      }
    })
    cardStore.setDataTypeMap(dataTypeMap.value)
    const arr = [
      {
        title: '布局组件',
        className: 'layout-component',
        children: layout.data.map((v) => dataTypeMap.value[v])
      },
      {
        title: '基础组件',
        className: 'base-component',
        children: base.data
          .filter((v) => v == 'BASE_COMPONENT_TITLE')
          .map((v) => dataTypeMap.value[v])
      }
    ]
    data.value = arr
    mitt.emit('getCardDetailData')
  })

  mitt.on('cardComponentDarg', (event) => {
    if (dragItem.value) {
      const inner = event.target?.className.includes('inner')
      const outer = event.target?.className.includes('outer')
      const main = event.target.closest('.card-main')
      if (inner) {
        update(dragItem.value, event.target?.dataset?.key)
      } else if (outer) {
        update(dragItem.value, event.target?.dataset?.key, 'true')
      } else if (main) {
        update(dragItem.value, cardList.value[0].children[0].uniqueKey)
      }
      dragItem.value = ''
    }
  })
})

const click = (v: any) => {
  if (cardList.value[0]?.children[0]?.uniqueKey) {
    let insertUniqueKey = currentItem.value.uniqueKey || cardList.value[0].children[0].uniqueKey
    if (dataTypeMap.value[currentItem.value.type]?.customizeType === 'outer') {
      insertUniqueKey = cardList.value[0].children[0].uniqueKey
    }
    update(v, insertUniqueKey)
  } else {
    ElMessage({
      type: 'error',
      message: '请检查数据~'
    })
  }
}
const update = (v: any, insertUniqueKey: string, targetUniqueKey?: string) => {
  if (
    cardList.value[0]?.children[0].children[0].type === 'BASE_COMPONENT_TITLE' &&
    v.type == 'BASE_COMPONENT_TITLE'
  ) {
    ElMessage({
      type: 'error',
      message: '只可以添加一个Title组件'
    })
  } else {
    let value = JSON.parse(JSON.stringify(v))
    // 子级关系
    value.children?.forEach((item, key) => {
      item.uniqueKey = generateUUID()
    })
    // 自己
    value.uniqueKey = generateUUID()
    dataTypeMap.value[value.type].treeLabelIndex = dataTypeMap.value[value.type].treeLabelIndex + 1
    cardStore.setDataTypeMap(dataTypeMap.value)
    const label = value.treeLabel + dataTypeMap.value[value.type].treeLabelIndex
    value.label = label
    // 父级关系
    let newList = {}
    if (value.type === 'BASE_COMPONENT_TITLE') {
      cardList.value[0]?.children[0].children.unshift(value)
      newList = cardList.value
    } else {
      if (targetUniqueKey) {
        newList = addNodeAfter(cardList.value, insertUniqueKey, value)
      } else {
        newList = addNodeToChildren(cardList.value, insertUniqueKey, value)
      }
    }
    cardStore.setCardList(newList)
  }
}

const dragstart = (value) => {
  dragItem.value = JSON.parse(JSON.stringify(value))
}
</script>

<style lang="scss">
.card-component-module {
  width: 100%;

  h3 {
    color: #666;
    line-height: 30px;
    padding-bottom: 4px;
  }

  p {
    color: #666;
    text-align: center;
    line-height: 30px;
  }

  .outer {
    width: 100%;
    background-color: #f1f1f1;
    display: flex;
    border: 1px solid #f1f1f1;
    border-radius: 5px;
    margin-left: 0px !important;
    margin-right: 0px !important;
    position: relative;
  }

  .content-inner {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #666;
    position: relative;
  }
  .inner {
    max-width: 100%;
    min-width: 8px;
    height: 100%;
    border-radius: 5px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #666;
    position: relative;
    float: left;
    background-color: #fff;
  }

  &.small .base-component {
    justify-content: center;
  }
  &.large .base-component {
    justify-content: left;
  }

  &.small {
    .outer {
      padding: 2px 2px;
      &:hover {
        border-color: var(--oz-color-primary);
      }
    }
    .inner {
      border: 1px solid #f1f1f1;
    }
  }
  &.large {
    .inner {
      background-color: #eee;
    }
  }
}
</style>

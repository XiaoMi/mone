<!--
 * @Description: 
 * @Date: 2024-09-05 14:45:40
 * @LastEditTime: 2024-10-17 16:04:23
-->
<template>
  <div class="card-manage">
    <div class="card-manage-head">
      <el-page-header @back="onBack">
        <template #content>
          <div class="card-manage-head-left">
            <div class="card-manage-head-left-info">
              <BaseInfo
                :data="{
                  describe: cardData?.card?.description,
                  name: cardData?.card?.name,
                  avatarUrl: false
                }"
                size="small"
              ></BaseInfo>
            </div>
            <el-button link :icon="Edit" @click="edit"></el-button>
          </div>
        </template>
        <template #extra>
          <el-button type="primary" plain @click="save">保存</el-button>
          <el-button type="primary" @click="push">发布</el-button>
        </template>
      </el-page-header>
    </div>
    <DragContainer>
      <template #left>
        <TabsContainer :activeName="leftActiveName" :tabsData="leftTabsData">
          <template #card>
            <TabsContainer :activeName="activeName" :tabsData="tabsData">
              <template #template>
                <LeftTabTemplate />
              </template>
              <template #component>
                <LeftTabComponent />
              </template>
              <template #structure>
                <LeftTabStructure v-if="cardList[0]?.children[0].children?.length" />
                <div v-else class="card-empty-container">
                  <el-empty
                    description="请先从组件列表添加组件"
                    :image="tipIcon"
                    :image-size="20"
                  />
                </div>
              </template>
            </TabsContainer>
          </template>
          <template #variable>
            <Variable></Variable>
          </template>
        </TabsContainer>
      </template>
      <template #center>
        <div
          class="card-main card-component-module large"
          @dragover.prevent
          @drop="drop"
          @dragenter.stop="dragEnter($event)"
        >
          <Center v-if="cardList[0]?.children[0].children?.length" v-model="cardList"></Center>
          <div v-else class="card-empty-container">
            <el-empty description="暂未添加元素" :image="emptyIcon" :image-size="200" />
          </div>
        </div>
      </template>
      <template #right>
        <div class="card-setting">
          <Right v-if="currentItem.type"></Right>
          <div v-else class="card-empty-container">
            <el-empty description="可选中一个元素查看相关配置" :image="tipIcon" :image-size="24" />
          </div>
        </div>
      </template>
    </DragContainer>
    <CardDialog
      v-model="cardDialogVisible"
      :data="cardData?.card"
      @onOk="cardDialogCallback"
      type="detail"
    ></CardDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { getCardDetail, addCardDetail, publishCard, updateCardDetail } from '@/api/probot-card'
import { generateUUID } from './tree'
import { useProbotCardStore } from '@/stores/card'
import mitt from '@/utils/bus'
import BaseInfo from '@/components/BaseInfo.vue'
import CardDialog from '../CardDialog.vue'
import DragContainer from './DragContainer.vue'
import TabsContainer from './TabsContainer.vue'
import LeftTabTemplate from './left/LeftTabTemplate.vue'
import LeftTabComponent from './left/LeftTabComponent.vue'
import LeftTabStructure from './left/LeftTabStructure.vue'
import Variable from './left/Variable.vue'
import Center from './center/Outer.vue'
import Right from './right/index.vue'
import { flatToTree, flattenTree } from '@/views/probot-space/card/manage/tree.ts'

const cardStore = useProbotCardStore()
const router = useRouter()
const route = useRoute()
const cardData = computed(() => cardStore.cardData)
const cardList = computed(() => cardStore.cardList)
const currentItem = computed(() => cardStore.currentItem)

const cardDialogVisible = ref(false)
const leftActiveName = ref('card')
const leftTabsData = [
  {
    name: 'card',
    label: '卡片'
  },
  {
    name: 'variable',
    label: '变量'
  }
]
const activeName = ref('component')
const tabsData = [
  {
    name: 'template',
    label: '模板'
  },
  {
    name: 'component',
    label: '组件'
  },
  {
    name: 'structure',
    label: '结构'
  }
]
const emptyIcon = import.meta.env.VITE_APP_STATIC_PATH + 'images/nodata.svg'
const tipIcon = import.meta.env.VITE_APP_STATIC_PATH + 'images/tip.svg'

const getCardDetailData = () => {
  return getCardDetail({
    cardId: String(route.params.cardId)
  })
}
const onBack = () => {
  router.push({
    name: 'AI Probot Space',
    params: {
      id: route.params.workSpaceId
    },
    query: {
      tab: 'card'
    }
  })
}
const edit = () => {
  cardDialogVisible.value = true
}
const cardDialogCallback = () => {
  getCardDetailData().then((res) => {
    cardStore.setCardInfo(res.data.card)
  })
}
const save = () => {
  // if (!cardData.value.elementMap[params.rootUniqueKey]?.id) {
  //   //todo,添加不了
  //   addCardDetail(params).then((res) => {
  //     console.log('没有值，进行添加')
  //   })
  // }
  updateCardDetail({
    ...cardData.value,
    elementMap: flattenTree(cardList.value)
  }).then((res) => {
    if (res.data) {
      ElMessage({
        type: 'success',
        message: '保存成功'
      })
    } else {
      ElMessage({
        type: 'error',
        message: res.message || '失败'
      })
    }
  })
  return Promise.resolve()
}
const push = () => {
  save().then(() => {
    publishCard({
      cardId: String(route.params.cardId)
    }).then((res) => {
      if (res.data) {
        ElMessage({
          type: 'success',
          message: '发布成功'
        })
      } else {
        ElMessage({
          type: 'error',
          message: res.message || '失败'
        })
      }
    })
  })
}
onMounted(() => {
  mitt.on('getCardDetailData', () => {
    getCardDetailData().then((res) => {
      let params = res.data || {}
      if (!params?.rootUniqueKey) {
        params.rootUniqueKey = generateUUID()
      }
      if (!params?.workspaceId) {
        params.workspaceId = route.params.workSpaceId
      }
      if (!params?.cardId) {
        params.cardId = route.params.cardId
      }
      if (!params.elementMap) {
        params.elementMap = {}
      }
      cardStore.setVariableList(Object.values(res.data.cardVariableMap))
      cardStore.setCardData(params)
      cardStore.setCardList(flatToTree(params.elementMap, cardData.value.rootUniqueKey))
    })
  })
})

const drop = (event) => {
  event.preventDefault()
  mitt.emit('cardComponentDarg', event)
}
const dragEnter = (event) => {
  event.preventDefault()
  cardStore.setOverKey(event.target?.dataset?.key)
}
</script>
<style lang="scss" scoped>
.card-manage {
  width: 100%;
  height: calc(100% + 60px);
  display: flex;
  flex-direction: column;
  overflow: auto;
  color: #333;
}

.card-manage-head {
  padding: 10px;
  border-bottom: 1px solid #ddd;

  &-left {
    display: flex;
    width: 100%;
    align-items: flex-start;

    &-info {
      width: auto;
      overflow: hidden;
    }
  }
}

.card-main {
  width: 348px;
  height: 100%;
  background-color: #fff;
  border: 1px dotted #ddd;
  border-radius: 5px;
  padding: 10px;
  margin: 0 auto;
  overflow: auto;
}

.card-empty-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-setting {
  padding: 10px;
  height: 100%;
  overflow: auto;
}
</style>

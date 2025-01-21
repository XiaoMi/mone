<!--
 * @Description:
 * @Date: 2024-03-07 11:04:19
 * @LastEditTime: 2024-08-15 17:41:14
-->
<template>
  <BindDialog v-model="dialogVisible" :data="bindData" :is-header="false">
    <template #filter><BindPlugFilter></BindPlugFilter></template>
    <template #list>
      <template v-if="infoData.length">
        <div v-for="(item, index) in infoData" :key="index" class="bind-list-item">
          <div class="bind-list-content">
            <BaseInfo :data="item" size="small">
              <div class="more-info">
                <p>创建于{{ dateFormat(item.gmtCreate, 'yyyy-mm-dd HH:MM:ss') }}</p>
              </div>
            </BaseInfo>
          </div>
          <div class="btn-container">
            <BaseLink
              :name="item.bind ? '已绑定' : '绑定'"
              @click="bindKnowlege(item)"
              :disabled="item.bind"
            ></BaseLink>
          </div>
        </div>
      </template>
      <el-empty v-else description="不好意思，还没有数据呐~" :image-size="80"></el-empty>
    </template>
  </BindDialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import BaseInfo from '@/components/BaseInfo.vue'
import BindDialog from '../components/BindDialog.vue'
import BindPlugFilter from '../flow/BindPlugFilter.vue'
import BaseLink from '@/components/probot/BaseLink.vue'
import { getKnowledgeList } from '@/api/probot-knowledge'
import { useProbotStore } from '@/stores/probot'
import dateFormat from 'dateformat'

const probotStore = useProbotStore()
const workspaceId = computed(() => probotStore.workspaceId)
const workspaceList = computed(() => probotStore.workspaceList)

const { setBindKnowlege } = probotStore

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})
const emits = defineEmits(['update:modelValue'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const bindData = ref({
  title: '绑定知识库',
  toCreateTip: '还没有知识库，去新建',
  toCreateRouter: {
    path: '/probot-space/',
    query: {
      tab: 'knowledge'
    }
  }
})
watch(
  () => workspaceList,
  (list) => {
    bindData.value.toCreateRouter.path =
      '/probot-space/' + (list?.value?.length ? list?.value[0]?.id : '')
  },
  {
    immediate: true,
    deep: true
  }
)
watch(
  () => workspaceId.value,
  (id) => {
    if (id) {
      bindData.value.toCreateRouter.path = '/probot-space/' + id
    }
  },
  {
    immediate: true,
    deep: true
  }
)
const infoData = ref<
  {
    id: string
    gmtCreate: string
    bind: boolean
    desc: string
  }[]
>([])

const bindKnowlege = (item: any) => {
  const bindKnowlege = probotStore.bindKnowlege
  const item1 = bindKnowlege.find((it) => it.id === item.id)
  if (!item1) {
    item.bind = true
    setBindKnowlege([...bindKnowlege, item])
  }
  // emits('update:modelValue', false)
}

const fetchKnowledgeMyList = async () => {
  const { code, data } = await getKnowledgeList({
    workSpaceId: workspaceId.value,
    pageSize:1000
  })
  if (code === 0) {
    const bindKnowlege = probotStore.bindKnowlege
    infoData.value = ( data?.records || []).map((it: { remark: string; id: string; knowledgeBaseName: string }) => {
      const item = bindKnowlege.find((item) => it.id == item.id)
      if (item) return item
      return {
        ...it,
        desc: it.remark,
        name:it.knowledgeBaseName
      }
    })
  } else {
    infoData.value = []
  }
}

const init = () => {
  fetchKnowledgeMyList()
}

init()
</script>

<style lang="scss" scoped>
.bind-list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
}
</style>

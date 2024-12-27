<template>
  <div class="manage-info">
    <div class="manage-info-name">
      <BaseInfo
        :data="{
          name: state.detail?.knowledgeBaseName,
          describe: state.detail.remark,
          avatarUrl: state.detail.avatarUrl
        }"
        size="small"
      />
    </div>
    <div class="manage-info-name">
      <span>{{ t('plugin.workspace') }}：</span>
      <span class="space-name" @click="handleJump">{{ workspaceName }}</span>
    </div>
    <div class="manage-info-btns">
      <el-button size="small" @click="init">刷新</el-button>
      <el-button size="small" @click="fileClick" :disabled="!state.detail.self">知识上传</el-button>
      <el-button size="small" @click.stop="askClick">检索调试</el-button>
      <el-button
        size="small"
        @click="remove(route.params, removeCallback)"
        :disabled="!state.detail.self"
        >删除</el-button
      >
    </div>
  </div>
  <div class="manage">
    <TableList :tableList="state.tableList" @update="getList" />
  </div>
  <File v-model="fileVisible" @update="getList"></File>
  <Ask v-model="askVisible"></Ask>
</template>

<script lang="ts" setup>
import { reactive, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { t } from '@/locales'
import { getSingleKnowledgeBase, getKnowledgeFileMyList } from '@/api/probot-knowledge'
import BaseInfo from '@/components/BaseInfo.vue'
import TableList from './TableList.vue'
import Ask from './Ask.vue'
import File from './File.vue'
import remove from '../remove'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()
const route = useRoute()
const router = useRouter()
const state = reactive<{
  detail: {
    knowledgeBaseName: string
    remark: string
    avatarUrl: string
  }
  tableList: []
}>({
  detail: {
    knowledgeBaseName: '',
    remark: '',
    avatarUrl: ''
  },
  tableList: []
})
const workspaceName = ref('')

const askVisible = ref(false)
const fileVisible = ref(false)

const askClick = () => {
  askVisible.value = true
}

const fileClick = () => {
  fileVisible.value = true
}

const handleJump = () => {
  router.push({
    path: `/probot-space/${state.detail?.workSpaceId}`,
    query: {
      tab: 'knowledge'
    }
  })
}

const removeCallback = () => {
  handleJump()
}

const getList = () => {
  getKnowledgeFileMyList({
    knowledgeBaseId: route.params.knowledgeBaseId + ''
  }).then((res) => {
    state.tableList = res?.data
  })
}

const getWorkspaceName = () => {
  probotStore.workspaceList.forEach((item) => {
    if (item?.id == state.detail?.workSpaceId) {
      workspaceName.value = item.name
    }
  })
}

const getDetail = () => {
  getSingleKnowledgeBase({
    id: route.params.id + ''
  }).then((res) => {
    if (res.code === 0) {
      state.detail = res.data
      getWorkspaceName()
    } else {
      state.detail = []
    }
  })
}
const init = () => {
  getDetail()
  getList()
}

onMounted(() => {
  if (route.params.id) {
    init()
  } else {
    ElMessage.error(t('common.wrong'))
  }
})

watch(
  () => probotStore.workspaceList,
  () => {
    getWorkspaceName()
  }
)
</script>

<style lang="scss" scoped>
.manage-info {
  border-radius: 5px;
  margin: 20px 50px;
  padding: 20px 30px;
  background-color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: space-between;
  &-name {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    font-size: 14px;
    flex: 1;
    overflow: hidden;
    position: relative;
    padding-left: 2px;
    span {
      white-space: nowrap;
      &:last-child {
        display: inline-block;
        max-width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
  .space-name {
    color: #00a9ff;
    cursor: pointer;
  }
  &-btns {
    white-space: nowrap;
  }
}
.manage {
  margin: 0 50px;
  padding: 20px 20px 20px;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 5px;
}
</style>

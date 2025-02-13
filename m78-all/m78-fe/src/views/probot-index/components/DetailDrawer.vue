<template>
  <el-drawer v-model="drawer" title="详情" direction="rtl" size="80%" class="connection-drawer">
    <el-descriptions class="margin-top" title="会话信息" :column="3" border>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">会话id</div>
        </template>
        {{ detail.id }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">会话名称</div>
        </template>
        {{ detail.title }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">用户</div>
        </template>
        {{ detail.userName }}
      </el-descriptions-item>
    </el-descriptions>
    <el-descriptions
      v-if="detail.zInfo"
      class="margin-top pt-[20px]"
      title="知识库信息"
      :column="3"
      border
    >
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">知识库id</div>
        </template>
        {{ detail.zInfo.id }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">知识库名称</div>
        </template>
        {{ detail.zInfo.name }}
      </el-descriptions-item>
    </el-descriptions>
    <div v-if="detail.zInfo" class="pt-[20px]">
      <h2>知识库文件</h2>
      <el-table class="pt-[10px]" :data="detail.zFiles" style="width: 100%">
        <el-table-column property="id" label="文件Id" />
        <el-table-column property="fileName" label="文件名" />
        <el-table-column property="fileType" label="文件类型" />
      </el-table>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useChatStore } from '@/stores/chat'
import { getMessagetopicDetail } from '@/api/chat'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  }
})

const emits = defineEmits(['update:modelValue'])

const chatStore = useChatStore()
const uuid = computed(() => chatStore.active)
const drawer = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const detail = ref<{
  id: string
  title: string
  userName: string
  zInfo?: {
    id: string
    name: string
  },
  zFiles: {
    id: string
    fileName: string
    fileType: string
  }[]
}>({
  id: '',
  title: '',
  userName: '',
  zInfo: undefined,
  zFiles: []
})
const getKnowledgeConfig = async () => {
  const { code, data } = await getMessagetopicDetail({
    topicId: String(uuid.value)
  })
  if (code == 0 && data) {
    const { zknowledgeBaseDTO, zknowledgeBaseFilesDTOS } = data?.knowledgeConfigDetail || {}
    detail.value = {
      id: data.chatTopic.id,
      title: data.chatTopic.title,
      userName: data.chatTopic.userName,
      zInfo: zknowledgeBaseDTO
        ? {
            id: zknowledgeBaseDTO.id as string,
            name: zknowledgeBaseDTO.name as string
          }
        : undefined,
      zFiles: (zknowledgeBaseFilesDTOS || []).map((it) => {
        return {
          id: it.id,
          fileName: it.fileName,
          fileType: it.fileType
        }
      })
    }
  }
}

const init = async () => {
  await getKnowledgeConfig()
}

init()
</script>

<style lang="scss" scoped>
.cell-item {
  display: flex;
  align-items: center;
}
</style>

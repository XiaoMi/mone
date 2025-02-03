<!--
 * @Description:
 * @Date: 2024-01-31 15:08:28
 * @LastEditTime: 2024-01-31 17:12:13
-->
<template>
  <el-drawer
    v-model="drawer"
    title="知识库绑定"
    direction="rtl"
    size="80%"
    class="connection-drawer"
  >
    <template #default>
      <div class="connection-container">
        <el-alert title="一次会话只允许绑定一个知识库内的知识" type="warning" />
        <div class="connection-head">
          <el-select
            v-model="knowledgeValue"
            placeholder="请选择知识库"
            size="large"
            style="width: 240px; margin-right: 10px"
            @change="knowledgeChange"
          >
            <el-option
              v-for="item in knowledgeOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-select
            v-model="fileValue"
            @change="fileChange"
            placeholder="请选择绑定的文件"
            size="large"
            style="width: 240px; margin-right: 10px"
          >
            <el-option
              v-for="item in fileOptions"
              :key="item.id"
              :label="item.fileName"
              :value="item.id"
            />
          </el-select>
          <!-- <el-upload
            v-model="fileList"
            class="upload-container"
            action="/"
            multiple
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :before-remove="beforeRemove"
            :limit="3"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">文件上传</el-button>
          </el-upload> -->
        </div>
        <div class="connection-content">
          <!-- <el-table :data="tableData" style="width: 100%">
            <el-table-column type="selection" width="55" />
            <el-table-column property="date" label="文件Id" />
            <el-table-column property="name" label="文件名" />
            <el-table-column property="address" label="处理状态" />
            <el-table-column property="address" label="创建时间" />
            <el-table-column property="address" label="文件类型" />
            <el-table-column property="address" label="文件大小" />
            <el-table-column label="操作" fixed="right" width="120">
              <template #default>
                <el-button link type="primary" size="small">知识解析</el-button>
              </template>
            </el-table-column>
          </el-table> -->
          <File
            :drawer="drawer"
            :knowledgeId="String(knowledgeValue)"
            :fileList="fileList"
            @updateFileList="updateFileList"
          ></File>
        </div>
      </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="cancelClick">取消</el-button>
        <el-button type="primary" @click="confirmClick" :loading="loading">{{
          loading ? '确定中 ...' : '确定'
        }}</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
// import type { UploadProps, UploadUserFile } from 'element-plus'
// import { ElMessage, ElMessageBox } from 'element-plus'
// import { onMounted } from 'vue'
import { useChatStore } from '@/stores/chat'
import { getKnowledgeMyList, getKnowledgeFileMyList, getMessagetopicDetail } from '@/api/chat'
import File from './File.vue'
import { ElMessage } from 'element-plus'

interface FileType {
  id: number
  fileName: string
  fileType: string
  loadType: number
}

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
const filesMap = new Map<string, FileType[]>()
const knowledgeValue = ref('')
const fileList = ref<FileType[]>([])
const knowledgeOptions = ref<
  {
    id: string
    name: string
  }[]
>([])
const fileValue = ref<string>('')
const fileOptions = ref<FileType[]>([])
const loading = ref(false)

function cancelClick() {
  drawer.value = false
  loading.value = false
}

async function confirmClick() {
  loading.value = true
  try {
    await chatStore.updateHistory(uuid.value!, {
      isEdit: false,
      knowledgeConfig: {
        knowledgeBaseId: knowledgeValue.value,
        fileIdList: fileList.value.map((it) => String(it.id))
      }
    })
    emits('update:modelValue', false)
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error(JSON.stringify(e))
  } finally {
    loading.value = false
  }
}

const getKnowledgeOptions = async () => {
  knowledgeOptions.value = []
  const { code, data } = await getKnowledgeMyList({})
  if (code == 0 && data) {
    knowledgeOptions.value = data
    if (!knowledgeValue.value) {
      knowledgeValue.value = data[0]?.id
    }
    getFileOptions()
  }
}

const getFileOptions = async () => {
  fileOptions.value = []
  const { code, data } = await getKnowledgeFileMyList({
    knowledgeBaseId: knowledgeValue.value
  })
  if (code == 0 && data) {
    fileOptions.value = data
  }
}

const knowledgeChange = () => {
  fileList.value = []
  getFileOptions()
}

const fileChange = (id: number) => {
  const index = fileList.value.findIndex((it) => it.id == id)
  if (index == -1) {
    const fileOption = fileOptions.value.find((it) => it.id == id)
    if (fileOption) {
      fileList.value.push({
        ...fileOption,
        loadType: 2
      })
    }
  } else {
    ElMessage.warning('文件已经添加')
  }
  fileValue.value = ''
}

const updateFileList = async (files: FileType[]) => {
  fileList.value = files
}

const updateKnowledgeConfig = async () => {
  const { code, data } = await getMessagetopicDetail({
    topicId: uuid.value + ''
  })
  knowledgeValue.value = ''
  fileList.value = []
  if (code == 0 && data?.knowledgeConfigDetail) {
    const { zknowledgeBaseDTO, zknowledgeBaseFilesDTOS } = data.knowledgeConfigDetail
    knowledgeValue.value = zknowledgeBaseDTO?.id
    fileList.value = (zknowledgeBaseFilesDTOS || []).map((it) => {
      return {
        id: it.id,
        fileName: it.fileName,
        fileType: it.fileType,
        loadType: 2
      }
    })
    filesMap.set(knowledgeValue.value, fileList.value)
  }
}

const init = async () => {
  await updateKnowledgeConfig()
  await getKnowledgeOptions()
}

init()
</script>
<style lang="scss">
.connection-drawer {
  &.oz-drawer {
    background-color: #fff;
  }
  .oz-table.is-scrolling-none th.oz-table-fixed-column--left,
  .oz-table.is-scrolling-none th.oz-table-fixed-column--right {
    background-color: #fff;
  }
}
</style>
<style scoped lang="scss">
.connection-container {
}
.connection-head {
  display: flex;
  padding: 10px 0;
}
.connection-content {
}
.upload-container {
  display: flex;
  align-items: center;
}
</style>

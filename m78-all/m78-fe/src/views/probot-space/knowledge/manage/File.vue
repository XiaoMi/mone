<!--
 * @Description: 
 * @Date: 2024-03-20 16:19:04
 * @LastEditTime: 2024-09-02 19:09:30
-->
<template>
  <el-drawer v-model="drawer" title="知识上传" direction="rtl" size="50%" @open="handleOpen">
    <!-- 上传区域 -->
    <div
      id="container"
      ref="container"
      v-show="fileData.fileList.length === 0"
      style="margin-bottom: 20px"
    >
      <FileUpload @update="getTable" @letterSubmit="letterSubmit"></FileUpload>
    </div>
    <el-card>
      <el-table :data="fileData.fileList" style="width: 100%">
        <el-table-column prop="name" label="文件名称"></el-table-column>
        <el-table-column prop="type" label="文件类型"></el-table-column>
        <el-table-column prop="size" label="文件大小" v-slot="{ row }">
          {{ row.size }}MB
        </el-table-column>
        <el-table-column fixed="right" label="操作" v-slot="{ row }" width="144px">
          <div style="padding-bottom: 4px">
            <el-button type="primary" size="mini" @click="methods.FileRead(row)"
              >知识解析</el-button
            >
          </div>
          <div>
            <el-button type="primary" size="mini" @click="methods.customFileRead(row)"
              >自定义知识解析</el-button
            >
          </div>
        </el-table-column>
      </el-table>
    </el-card>
  </el-drawer>
  <CustomFileRead v-model="customFileReadVisible" @submit="submitCustomFileRead"></CustomFileRead>
</template>

<script setup lang="ts">
import { ref, reactive, computed, defineEmits } from 'vue'
import { embedding } from '@/api/probot-knowledge'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import FileUpload from './FileUpload.vue'
import CustomFileRead from './CustomFileRead.vue'

const props = defineProps({
  modelValue: {}
})
const emits = defineEmits(['update:modelValue', 'update'])

const drawer = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})

const route = useRoute()
const container = ref()

const fileData = reactive({
  fileList: [] as Array<{
    id: number
    name: string
    type: string
    size: number
  }>
})
const customFileReadVisible = ref(false)
const customFileReadRow = ref({})

const methods = {
  //绑定进队列
  FilesAdded(val: any, filePath: string) {
    fileData.fileList.push({
      id: val.id,
      name: val.name,
      type: val.raw.type,
      size: parseInt((val.size / 1024 / 1024) * 100) / 100,
      filePath: filePath
    })
  },
  embeddingFile(params: any, row?: any) {
    embedding(params).then((res: any) => {
      if (res?.code === 0) {
        ElMessage({
          message: '文件已经在解析中',
          type: 'success',
          duration: 3000
        })
        fileData.fileList = fileData.fileList.filter((val) => {
          if (val.id == row.id) {
            return false
          } else {
            return true
          }
        })
        emits('update')
        drawer.value = false
      }
    })
  },
  // 知识解析
  FileRead(row: any) {
    this.embeddingFile(
      [
        {
          fileName: row.name,
          knowledgeBaseId: Number(route.params.knowledgeBaseId),
          fileStore: 'FileServer',
          filePath: row.filePath
        }
      ],
      row
    )
  },
  //自定义知识解析
  customFileRead(row: any) {
    customFileReadVisible.value = true
    customFileReadRow.value = row
  }
}
const letterSubmit = (value) => {
  console.log('val', value)
  methods.embeddingFile(
    [
      {
        fileName: value.fileName,
        knowledgeBaseId: Number(route.params.knowledgeBaseId),
        separator: value.separator,
        urlPath: value.urlPath
        // fileStore: 'FileServer',
        // filePath: row?.filePath
      }
    ]
    // row
  )
}
const submitCustomFileRead = (value: string) => {
  const row = customFileReadRow.value
  customFileReadVisible.value = false
  methods.embeddingFile(
    [
      {
        fileName: row?.name,
        knowledgeBaseId: Number(route.params.knowledgeBaseId),
        separator: value,
        fileStore: 'FileServer',
        filePath: row?.filePath
      }
    ],
    row
  )
}
const getTable = (uploader: any, files: any, filePath: string) => {
  fileData.fileList = []
  methods.FilesAdded(uploader, files, filePath)
}

const handleOpen = () => {
  fileData.fileList = []
}
</script>

<style scoped lang="scss">
.card-container {
  margin-top: 20px;
}
.file-upload-head {
  padding-left: 0px;
}
.file-card {
  height: calc(100% - 32px);
  display: flex;
  flex-direction: column;
}
</style>

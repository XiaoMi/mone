<template>
  <el-table :data="props.tableList" style="width: 100%" stripe>
    <el-table-column prop="id" label="文件id" width="70px"></el-table-column>
    <el-table-column prop="fileName" label="文件名称"></el-table-column>
    <!-- 状态值 1：解析中2：解析完成 -->
    <el-table-column prop="embeddingStatus" label="解析状态" v-slot="{ row }">{{
      row.embeddingStatus === 1 ? '解析中' : row.embeddingStatus === 2 ? '解析完成' : ''
    }}</el-table-column>
    <el-table-column prop="gmtCreate" label="创建时间" v-slot="{ row }">
      {{ dateFormat(row.gmtCreate, 'yyyy-mm-dd HH:MM:ss') }}
    </el-table-column>
    <el-table-column :label="t('plugin.operate')" width="180" fixed="right">
      <template #default="scoped">
        <el-button
          class="btn-item"
          text
          size="small"
          @click.stop="handleEdit(scoped.row)"
          :disabled="!scoped.row.self && !flag"
          >{{ t('common.edit') }}</el-button
        >
        <el-button
          class="btn-item"
          text
          size="small"
          @click.stop="handleDetail(scoped.row)"
          :disabled="!scoped.row.self && !flag"
          >详情</el-button
        >
        <el-button
          class="btn-item"
          text
          size="small"
          :type="scoped.row.self ? 'danger' : ''"
          @click.stop="handleDel(scoped.row)"
          :disabled="!scoped.row.self && !flag"
          >{{ t('common.delete') }}</el-button
        >
      </template>
    </el-table-column>
  </el-table>
  <el-dialog v-model="detailVisible" title="详情" width="800">
    <div class="detail-container">
      <el-carousel indicator-position="outside" trigger="click" arrow="always">
        <el-carousel-item v-for="(item, index) in detailData" :key="index">
          <div class="detail-content">{{ item.blockContent }}</div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
  <EditDrawer v-model="editVisible" :row="editRow" />
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { t } from '@/locales'
import { deleteKnowledgeFile, getBlockList } from '@/api/probot-knowledge'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import dateFormat from 'dateformat'
import EditDrawer from './EditDrawer.vue'

const route = useRoute()
const flag = route.name === 'AI Probot Knowledge Manage New'

const props = defineProps({
  tableList: {
    type: Array,
    default() {
      return []
    }
  }
})
const emits = defineEmits(['update'])

const detailVisible = ref(false)
const detailData = ref()
const editVisible = ref(false)
const editRow = ref({})

const handleDetail = (row: any) => {
  detailVisible.value = true
  detailData.value = ''
  getBlockList({
    knowledgeId: row.knowledgeBaseId,
    knowledgeFileId: row.id
  }).then((res: any) => {
    detailData.value = res.data
  })
}
const handleDel = (row) => {
  ElMessageBox.confirm('确定删除【' + row.fileName + '】这个文件吗？', t('plugin.warning'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancle'),
    type: 'warning'
  })
    .then((action) => {
      if (action === 'confirm') {
        deleteKnowledgeFile({
          knowledgeBaseId: route.params.knowledgeBaseId,
          fileId: row.id
        })
          .then((res) => {
            if (res.code === 0) {
              ElMessage.success(t('common.deleteSuccess'))
              emits('update')
            } else {
              ElMessage.error(res.message || t('common.deleteFailed'))
            }
          })
          .catch((e) => {
            console.log(e)
          })
      }
    })
    .catch(() => {})
}
const handleEdit = (row) => {
  editRow.value = row
  editVisible.value = true
}
</script>

<style lang="scss" scoped>
.oz-table {
  background-color: transparent;
  &:deep(tr) {
    background-color: #f5f7fa;
    th.oz-table__cell {
      background-color: #f5f7fa;
      color: #333;
      font-size: 12px;
    }
  }
  &:deep(tbody) {
    background-color: #f5f7fa;
    tr.oz-table__row {
      background-color: #f5f7fa;
      td {
        .btn-item {
          padding-left: 4px;
          padding-right: 4px;
        }
        .oz-tag {
          margin: 0 4px 4px 0;
        }
      }
    }
  }
}
.detail-container {
  background-color: #eee;
  border-radius: 10px;
}
.detail-content {
  padding: 10px 60px;
  line-height: 30px;
  white-space: pre-line;
  height: 100%;
  overflow-y: auto;
}
</style>

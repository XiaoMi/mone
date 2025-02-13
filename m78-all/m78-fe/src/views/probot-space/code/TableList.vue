<template>
  <el-table :data="props.tableList" style="width: 100%">
    <el-table-column :label="t('codeBase.name')" prop="name"> </el-table-column>
    <el-table-column :label="t('common.description')"  prop="desc"> </el-table-column>
    <el-table-column prop="creator" :label="t('plugin.creator')" > </el-table-column>
    <el-table-column prop="meta" :label="t('plugin.createTime')" v-slot="{ row }">
      {{ moment(row.ctime).format('YYYY-MM-DD HH:mm:ss') }}
    </el-table-column>
    <el-table-column prop="meta" :label="t('plugin.updateTime')" v-slot="{ row }">
      {{ row.utime ? moment(row.utime).format('YYYY-MM-DD HH:mm:ss') : '--' }}
    </el-table-column>
    <el-table-column :label="t('plugin.operate')" width="120" align="center" fixed="right">
      <template #default="scoped">
        <el-button
          class="btn-item"
          type="primary"
          text
          size="small"
          @click.stop="handleEdit(scoped.row)"
          >{{ t('common.edit') }}</el-button
        >
        <el-button
          class="btn-item"
          text
          size="small"
          type="primary"
          @click.stop="handleDel(scoped.row)"
          >{{ t('common.delete') }}</el-button
        >
      </template>
    </el-table-column>
  </el-table>
  <EditCodeGroup v-model="state.editVisible" :row="state.eidtRow" @onOk="emits('onOk')" />
</template>

<script lang="ts" setup>
import { deleteCode, getCodeById } from '@/api/probot-code'
import EditCodeGroup from './EditCodeGroup.vue'
import { reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { t } from '@/locales'
import moment from 'moment'

const props = defineProps({
  tableList: {
    type: Array,
    default() {
      return []
    }
  }
})

const emits = defineEmits(['onOk'])

const state = reactive({
  editVisible: false,
  eidtRow: {},
  groundingVisible: false,
  groundingData: {}
})

const handleEdit = (row: any) => {
  getCodeById({
    id: row.id
  }).then((res) => {
    state.eidtRow = res.data
    state.editVisible = true
  })
}

const handleDel = (row: any) => {
  ElMessageBox.confirm(t('codeBase.deleteTip', { name: row.name }), t('codeBase.warning'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancle'),
    type: 'warning'
  })
    .then((action) => {
      if (action === 'confirm') {
        deleteCode({
          id: row.id
        })
          .then((data) => {
            if (data.data) {
              ElMessage.success(t('common.deleteSuccess'))
              emits('onOk')
            } else {
              ElMessage.error(data.message || t('common.deleteFailed'))
            }
          })
          .catch((e) => {
            console.log(e)
          })
      }
    })
    .catch(() => {})
}
</script>

<style lang="scss" scoped>
.oz-table {
  &:deep(tbody) {
    tr.oz-table__row {
      td {
        .btn-item {
          padding-left: 4px;
          padding-right: 4px;
        }
      }
    }
  }
}
</style>

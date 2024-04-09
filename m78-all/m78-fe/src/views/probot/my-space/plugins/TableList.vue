<template>
  <el-table :data="props.tableList" style="width: 100%" @row-click="handleClickRow">
    <el-table-column :label="t('plugin.tablePluginName')" width="220" v-slot="{ row, $index }">
      <BaseInfo
        :data="{
          name: row.pluginOrgName || '----',
          describe: row.pluginOrgDesc,
          avatarUrl: row.avatarUrl || $index
        }"
        size="small"
      />
    </el-table-column>
    <el-table-column prop="apiUrl" :label="t('plugin.components')" width="440" v-slot="{ row }">
      <template v-if="row.plugins?.length">
        <div class="comp-list">
          <el-tag v-for="item in row.plugins" :key="item.id">{{ item.name }}</el-tag>
        </div>
      </template>
      <template v-else> {{ t('plugin.noCompTips') }} </template>
    </el-table-column>
    <el-table-column prop="meta" :label="t('plugin.status')" width="100" v-slot="{ row }">
      {{ E_PLUGIN.getEnum(row.status, 'value').label }}
    </el-table-column>
    <el-table-column prop="meta" :label="t('plugin.createTime')" width="170" v-slot="{ row }">
      {{ moment(row.createTime).format('YYYY-MM-DD HH:mm:ss') }}
    </el-table-column>
    <el-table-column prop="creator" :label="t('plugin.creator')" width="120"> </el-table-column>
    <el-table-column prop="meta" :label="t('plugin.updateTime')" width="170" v-slot="{ row }">
      {{ row.modifyTime ? moment(row.modifyTime).format('YYYY-MM-DD HH:mm:ss') : '--' }}
    </el-table-column>
    <el-table-column prop="modifier" :label="t('plugin.updater')" width="120"> </el-table-column>
    <el-table-column :label="t('plugin.operate')" width="220" align="center" fixed="right">
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
          type="primary"
          text
          size="small"
          @click.stop="handleOnline(scoped.row)"
          >{{
            scoped.row.status === E_PLUGIN.getEnum('PUB', 'key').value
              ? t('plugin.takeOffline')
              : t('plugin.publish')
          }}</el-button
        >
        <el-button
          class="btn-item"
          type="primary"
          text
          size="small"
          @click.stop="handleClickRow(scoped.row)"
          >{{ t('plugin.manage') }}</el-button
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
  <EditPluginGroup v-model="state.editVisible" :row="state.eidtRow" @onOk="emits('onOk')" />
</template>

<script lang="ts" setup>
import { deleteById, publishOrCancel } from '@/api/plugins'
import EditPluginGroup from './EditPluginGroup.vue'
import { reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { t } from '@/locales'
import BaseInfo from '@/components/BaseInfo.vue'
import { useRouter, useRoute } from 'vue-router'
import moment from 'moment'
import { E_PLUGIN } from './constants'

const props = defineProps({
  tableList: {
    type: Array,
    default() {
      return []
    }
  }
})

const emits = defineEmits(['onOk'])

const router = useRouter()
const route = useRoute()

const state = reactive({
  editVisible: false,
  eidtRow: {}
})

const handleClickRow = (row) => {
  router.push({
    path: `/probot-comp-list/${route.params.id || 0}/${row.id}`
  })
}

const handleOnline = (row) => {
  let publish = false
  if (row.status !== E_PLUGIN.getEnum('PUB', 'key').value) {
    publish = true
  }
  publishOrCancel({
    id: row.id,
    publish
  }).then((data) => {
    if (data.data) {
      ElMessage.success(
        publish ? t('plugin.successfullylistedSale') : t('plugin.successfullyTakenOffline')
      )
      emits('onOk')
    } else {
      ElMessage.error(data.message!)
    }
  })
}

const handleEdit = (row: any) => {
  state.eidtRow = row
  state.editVisible = true
}

const handleDel = (row: any) => {
  ElMessageBox.confirm(t('plugin.deletePlugin', { name: row.name }), t('plugin.warning'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancle'),
    type: 'warning'
  })
    .then((action) => {
      if (action === 'confirm') {
        deleteById(row.id)
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
        .comp-list {
          height: 54px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
        }
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
</style>

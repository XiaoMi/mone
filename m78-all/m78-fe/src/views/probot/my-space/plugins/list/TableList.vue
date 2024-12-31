<template>
  <el-table :data="props.tableList" style="width: 100%" stripe>
    <el-table-column prop="name" :label="t('plugin.compName')" width="160">
      <template #default="scoped">
        {{ scoped.row.name }}
      </template>
    </el-table-column>
    <el-table-column prop="type" :label="t('plugin.category')" width="80">
      <template #default="scoped"> {{ scoped.row.type }} </template>
    </el-table-column>
    <el-table-column prop="meta" :label="t('plugin.metadata')" min-width="340" />
    <el-table-column prop="debugStatus" :label="t('plugin.debugStatus')" width="120">
      <template #default="scoped">
        {{ E_DEBUG.getEnum(scoped.row.debugStatus, 'value').label }}
      </template>
    </el-table-column>
    <el-table-column prop="status" :label="t('plugin.enabledStatus')" width="120">
      <template #default="scoped">
        <el-switch
          @change="handleChangeStatus(scoped.row)"
          v-model="scoped.row.status"
          inline-prompt
          style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
          :active-text="E_COMP.getEnum('ENABLED', 'key').label"
          :inactive-text="E_COMP.getEnum('DISABLED', 'key').label"
          :active-value="E_COMP.getEnum('ENABLED', 'key').value"
          :inactive-value="E_COMP.getEnum('DISABLED', 'key').value"
        />
      </template>
    </el-table-column>
    <el-table-column prop="desc" :label="t('plugin.description')" width="220">
      <template #default="scoped">
        {{ scoped.row.desc }}
      </template>
    </el-table-column>
    <el-table-column prop="meta" :label="t('plugin.createTime')" width="170">
      <template #default="scoped">
        {{ scoped.row.createTime }}
      </template>
    </el-table-column>
    <el-table-column prop="meta" :label="t('plugin.creator')" width="130">
      <template #default="scoped"> {{ scoped.row.userName }} </template>
    </el-table-column>
    <el-table-column :label="t('plugin.operate')" width="180" align="center" fixed="right">
      <template #default="scoped">
        <el-button class="btn-item" text size="small" @click.stop="handleEdit(scoped.row)">{{
          t('common.edit')
        }}</el-button>
        <el-button class="btn-item" text size="small" @click.stop="handleDebug(scoped.row)">{{
          t('plugin.debugTitle')
        }}</el-button>
        <el-button
          class="btn-item"
          text
          size="small"
          type="danger"
          @click.stop="handleDel(scoped.row)"
          >{{ t('common.delete') }}</el-button
        >
      </template>
    </el-table-column>
  </el-table>
  <DebugDawer v-model="state.debug.visible" :id="state.debug.id" @onOk="emits('onOk')" />
  <EditComp
    v-model="state.visible"
    :orgId="route.params.plugin"
    :row="state.eidtRow"
    @onOk="emits('onOk')"
  />
</template>

<script lang="ts" setup>
import { reactive } from 'vue'
import { t } from '@/locales'
import { E_DEBUG, E_COMP } from '../constants'
import { deleteComp, compToggle } from '@/api/plugins'
import { ElMessage, ElMessageBox } from 'element-plus'
import DebugDawer from './DebugDawer.vue'
import EditComp from './EditComp.vue'
import { useRoute } from 'vue-router'

const route = useRoute()

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
  visible: false,
  debug: {
    visible: false,
    id: 0
  },
  eidtRow: {}
})

const handleEdit = (row) => {
  state.eidtRow = row
  state.visible = true
}

const handleDebug = (row) => {
  state.debug.visible = true
  state.debug.id = row.id
}

const handleChangeStatus = (row) => {
  console.log(row.status)
  compToggle({
    id: row.id,
    enable: !row.status
  }).then((data) => {
    if (data.data) {
      ElMessage.success(t('common.success'))
    } else {
      ElMessage.error(data.message)
    }
  })
}

const handleDel = (row) => {
  ElMessageBox.confirm(t('plugin.deletePlugin', { name: row.name }), t('plugin.warning'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancle'),
    type: 'warning'
  })
    .then((action) => {
      if (action === 'confirm') {
        deleteComp(row.id)
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
</style>

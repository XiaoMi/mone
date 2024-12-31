<template>
  <el-dropdown @command="handleCommand" ref="dropdown" :hide-on-click="false">
    <el-button size="small" plain>
      操作
      <el-icon class="el-icon--right"><arrow-down /></el-icon>
    </el-button>
    <template #dropdown>
      <el-dropdown-item command="addRow">
        {{ t('excle.addRow') }}
      </el-dropdown-item>
      <el-dropdown-item command="addCol" class="drop-item"
        >{{ t('excle.addCol') }} <span class="col-name">{{ t('excle.colName') }}：</span
        ><el-input
          v-model="colKey"
          :placeholder="t('excle.enterColNameTip')"
          size="small"
          class="col-key"
          @keyup.enter="enterInput"
          @click.stop.native="inputClick"
        />
      </el-dropdown-item>
    </template>
  </el-dropdown>
</template>
<script setup>
import { t } from '@/locales'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const colKey = ref('ColumnName')
const emits = defineEmits(['commandFn'])
const handleCommand = (command) => {
  if (command == 'addCol' && !colKey.value) {
    if (!colKey.value) {
      ElMessage.warning('请输入列名')
      return
    }
  }
  dropdown.value.handleClose()
  emits('commandFn', command, command == 'addCol' ? colKey.value : '')
}
const dropdown = ref()
const switchShow = () => {
  dropdown.value.handleOpen()
}
const inputClick = () => {
  // console.log('00000')
}
const enterInput = () => {
  if (!colKey.value) {
    ElMessage.warning('请输入列名')
    return
  }
  emits('commandFn', 'addCol', colKey.value)
  dropdown.value.handleClose()
}
</script>
<style lang="scss" scoped>
.el-dropdown-link {
  margin-left: 10px;
}
.col-key {
  width: 100px;
}
.drop-item {
  .col-name {
    position: relative;
    margin-left: 10px;
    &::before {
      content: '';
      position: absolute;
      left: -5px;
      top: 5px;
      width: 1px;
      height: 11px;
      background: #606266;
    }
  }
  &:hover {
    .col-name {
      &::before {
        background: #00a9ff;
      }
    }
  }
}

.el-icon--right {
  margin-left: 3px;
}
</style>

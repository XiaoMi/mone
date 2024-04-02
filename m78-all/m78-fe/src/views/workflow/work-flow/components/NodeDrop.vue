<template>
  <el-dropdown @command="commandFn" v-if="beginEnd.indexOf(nodeData.nodeType) < 0">
    <span class="el-dropdown-link">
      <el-icon><More /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="del">删除</el-dropdown-item>
        <el-dropdown-item command="rename">重命名</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
  <el-dialog title="重命名" v-model="showRename" append-to-body width="400">
    <el-input v-model="name" :maxlength="20"></el-input>
    <p class="err" v-if="showTips">不能为空</p>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showRename = false" size="small">取消</el-button>
        <el-button type="primary" @click="confirm" size="small"> 确认 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { useVueFlow } from '@vue-flow/core'
import { ref } from 'vue'

const { removeNodes, updateNode } = useVueFlow()
const props = defineProps({
  nodeData: {}
})
const name = ref('')
const beginEnd = ref(['begin', 'end'])
const showRename = ref(false)
const showTips = ref(false)

const renameFn = () => {
  showRename.value = true
  name.value = props.nodeData.nodeMetaInfo.nodeName
}
const commandFn = (command) => {
  switch (command) {
    case 'del':
      removeNodes(props.nodeData, true, true)
      break
    case 'rename':
      renameFn()
      break
    default:
      break
  }
}
const confirm = () => {
  showTips.value = !name.value
  if (!name.value) return
  updateNode(props.nodeData.id, {
    nodeMetaInfo: {
      nodeName: name.value
    }
  })
  showRename.value = false
}
</script>

<style lang="scss" scoped>
.err {
  font-size: 12px;
  color: #f56c6c;
}
</style>

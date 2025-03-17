<!--
 * @Description: 
 * @Date: 2024-03-23 16:48:25
 * @LastEditTime: 2024-07-17 11:29:58
-->
<template>
  <el-drawer
    class="edit-drawer"
    v-model="editFile"
    size="50%"
    direction="rtl"
    :show-close="false"
  >
    <template #header>
      <div class="editFile-header">
        <h3>文件内容</h3>
        <el-button type="primary" size="small" @click="handleEdit({})">新增</el-button>
      </div>
    </template>
    <template #default>
      <ul
        class="files-content-list"
        v-if="state.blockContentList.length > 0"
        v-loading="state.loading"
      >
        <li v-for="item in state.blockContentList" :key="item.id">
          <div class="btn-wrap">
            <el-button type="text" size="small" @click="handleEdit(item)">编辑</el-button>
            <el-popconfirm
              @confirm="handleDelBlock(item)"
              title="确认删除?"
              confirm-button-text="确定"
              cancel-button-text="取消"
            >
              <template #reference>
                <el-button type="text" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </div>
          <p>{{ item.blockContent }}</p>
        </li>
      </ul>
      <el-empty v-else></el-empty>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="emits('update:modelValue', false)">关闭</el-button>
      </div>
    </template>
  </el-drawer>
  <el-dialog
    v-model="state.updateBlockContent"
    width="500px"
    :title="state.fileContent.id ? '内容编辑' : '新增'"
    :before-close="handleCloseUpdate"
  >
    <el-input
      :autosize="{ minRows: 6, maxRows: 12 }"
      placeholder="请输入内容"
      v-model="state.fileContent.blockContent"
      :show-word-limit="true"
      type="textarea"
    ></el-input>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCloseUpdate">取消</el-button>
        <el-button type="primary" @click="handleSubmitFile"> 提交 </el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import { computed, reactive, watch } from 'vue'
import { getBlockList, updateBlock, deleteBlock } from '@/api/probot-knowledge'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  row: {
    type: Object,
    default: () => ({})
  }
})

const emits = defineEmits(['update:modelValue'])

const editFile = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const state = reactive({
  blockContentList: [],
  updateBlockContent: false,
  loading: false,
  fileContent: {
    fileId: undefined,
    blockContent: undefined
  }
})

const getList = () => {
  state.loading = true
  getBlockList({
    knowledgeId: props.row.knowledgeBaseId,
    knowledgeFileId: props.row.id
  })
    .then((data) => {
      if (data.data?.length) {
        state.blockContentList = data.data || []
      } else {
        state.blockContentList = []
      }
    })
    .catch((e) => {
      console.log(e)
    })
    .finally(() => {
      state.loading = false
    })
}

watch(
  () => props.row.id,
  (val) => {
    if (val) {
      getList()
      state.fileContent = {
        fileId: props.row.id,
        blockContent: undefined
      }
    }
  },
  {
    immediate: true,
    deep: true
  }
)

const handleEdit = (row) => {
  if (row.id) {
    state.fileContent = { ...row }
  }
  state.updateBlockContent = true
}

const handleCloseUpdate = () => {
  state.fileContent = {
    fileId: undefined,
    blockContent: undefined
  }
  state.updateBlockContent = false
}

const handleSubmitFile = () => {
  updateBlock({
    knowledgeId: props.row.knowledgeBaseId,
    knowledgeFileId: props.row.id,
    blockContent: state.fileContent.blockContent,
    blockId: state.fileContent?.blockId
  })
    .then((data) => {
      if (data.message == 'ok') {
        state.loading = true
        state.updateBlockContent = false
        ElMessage.success(state.fileContent.id ? '编辑成功' : '新增成功')
        if (state.fileContent.id) {
          getList()
        } else {
          setTimeout(() => {
            getList()
          }, 2000)
        }
      } else {
        ElMessage.error(data.message || '编辑失败')
      }
    })
    .catch((e) => {
      console.log(e)
    })
}

const handleDelBlock = (row) => {
  deleteBlock({
    knowledgeId: props.row.knowledgeBaseId,
    knowledgeFileId: props.row.id,
    knowledgeFileBlockId: row.blockId
  })
    .then((data) => {
      if (data.data) {
        getList()
        ElMessage.success('删除成功!')
      } else {
        ElMessage.error(data.message || '删除失败！')
      }
    })
    .catch((e) => {
      console.log(e)
    })
    .finally(() => {
      state.loading = false
    })
}
</script>
<style lang="scss">
.edit-drawer {
  .el-drawer__header {
    margin: 0;
    padding: 20px;
    border-bottom: 1px solid #ddd;
  }
  .el-drawer__footer {
    margin: 0;
    padding: 20px;
    border-top: 1px solid #ddd;
  }
  .editFile-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    h3 {
      color: #333;
    }
  }

  .files-content-list {
    font-size: 14px;
    color: #333;
    display: flex;
    align-items: flex-start;
    justify-content: flex-start;
    flex-flow: wrap;
    // column-count: 2; /* 将容器分为两列 */
    // column-gap: 0; /* 列之间没有间隙 */
    // column-width: 50%;
    li {
      margin-bottom: 12px;
      border: 1px solid rgb(232, 234, 246);
      width: calc(50% - 12px);
      &:nth-child(2n) {
        margin-left: 12px;
      }
      .btn-wrap {
        background: rgb(232, 234, 246);
        padding: 4px 8px;
        display: flex;
        align-items: center;
        justify-content: flex-end;
      }
      p {
        padding: 12px;
        white-space: pre-line;
        height: 260px;
        overflow-y: auto;
        &::-webkit-scrollbar {
          display: none;
        }
      }
    }
    .el-button + .el-button {
      margin-left: 0px;
    }
  }
}
</style>

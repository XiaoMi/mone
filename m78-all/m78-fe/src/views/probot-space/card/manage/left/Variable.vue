<!--
 * @Description: 
 * @Date: 2024-09-25 14:42:47
 * @LastEditTime: 2024-10-10 18:24:43
-->
<template>
  <div class="card-variable">
    <el-button type="primary" plain class="card-variable-btn" @click="create">新建变量</el-button>
    <div class="card-variable-list">
      <div v-for="(item, index) in variableList" :key="item.id + index" class="variable-item">
        <div class="variable-item-left">
          <span class="variable-name">{{ item.name }}</span>
          <el-tag type="info" size="small">{{ item.classType }}</el-tag>
        </div>
        <div class="variable-item-opeartion">
          <el-link :underline="false" @click="edit(item)"
            ><el-icon class="opeartion-btn"><Edit /></el-icon
          ></el-link>
          <el-link :underline="false" @click="remove(item)"
            ><el-icon class="opeartion-btn"><Delete /></el-icon
          ></el-link>
        </div>
      </div>
    </div>
    <VariableDialog
      v-model="variableVisible"
      :data="variableData"
      @onOk="variableDialogCallback"
    ></VariableDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted,computed } from 'vue'
import VariableDialog from './VariableDialog.vue'
import { getCardVariablesByCardId, deleteCardVariableById } from '@/api/probot-card'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useProbotCardStore } from '@/stores/card'
const cardStore = useProbotCardStore()
const variableList = computed(() => cardStore.variableList)
const route = useRoute()

const variableVisible = ref(false)
const variableData = ref({})
const create = () => {
  variableVisible.value = true
  variableData.value = {}
}
const getList = () => {
  getCardVariablesByCardId({ cardId: route.params?.cardId }).then((res) => {
    cardStore.setVariableList(res.data)
  })
}

onMounted(() => {
  getList()
})
const variableDialogCallback = () => {
  getList()
}
const edit = (item) => {
  variableVisible.value = true
  variableData.value = { ...item }
}
const remove = (item) => {
  ElMessageBox.confirm('确定删除吗?', 'Warning', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteCardVariableById({
      id: item.id
    }).then(() => {
      getList()
      ElMessage({
        type: 'success',
        message: '删除成功'
      })
    })
  })
}
</script>

<style scoped lang="scss">
.card-variable {
  .card-variable-btn {
    width: 100%;
  }
  .card-variable-list {
  }
  .variable-item {
    width: 100%;
    margin-top: 10px;
    padding: 10px;
    box-shadow:
      (0 0 #0000, 0 0 #0000),
      (0 0 #0000, 0 0 #0000),
      0 10px 15px -3px rgba(0, 0, 0, 0.1),
      0 4px 6px -4px rgba(0, 0, 0, 0.1);
    background-color: hsl(0 0% 100%);
    color: hsl(224 71.4% 4.1%);
    border: 1px solid rgb(229, 231, 235);
    border-radius: 2px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    transform-origin: center center;
    transition: all 0.3s;
  }
  .variable-item-left {
    display: flex;
    align-items: center;
    overflow: hidden;
    flex: 1;
  }
  .variable-name {
    font-size: 14px;
    color: #666;
    padding-right: 2px;
    max-width: 70px;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
  }
  .variable-item-opeartion {
    .opeartion-btn {
      margin-left: 4px;
    }
  }
}
</style>

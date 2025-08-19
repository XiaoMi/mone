<!--
 * @Description: 
 * @Date: 2024-03-07 10:34:41
 * @LastEditTime: 2024-08-15 16:21:56
-->
<template>
  <div class="filter-container">
    <div class="item-dropdown">
      <el-dropdown @command="handleCommand">
        <span class="el-dropdown-link">
          {{ scopeLabel }}
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="item in scopeOptions" :key="item.value" :command="item">{{
              item.label
            }}</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <el-form :model="form" inline>
      <el-form-item label="">
        <el-input v-model="form.name" placeholder="工作流名称" @keyup.enter="search" clearable />
      </el-form-item>
      <el-form-item label="">
        <el-select v-model="form.workSpaceId" placeholder="团队空间" clearable @change="search">
          <el-option
            v-for="item in workspaceList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="">
        <el-select v-model="form.orderFieldName" placeholder="" @change="search">
          <el-option
            v-for="item in orderOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <el-button type="primary" plain  @click="search">查询</el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useProbotStore } from '@/stores/probot'
import mittBus from '@/utils/bus'

const probotStore = useProbotStore()
const workspaceList = computed(() => probotStore.workspaceList)

const scopeLabel = ref('显示全部')
const scopeOptions = [
  {
    value: 'all',
    label: '显示全部'
  },
  {
    value: 'mine',
    label: '我的创建'
  },
  {
    value: 'favorite',
    label: '我的收藏'
  }
]
const orderOptions = [
  {
    value: 'ctime',
    label: '创建时间'
  },
  {
    value: 'utime',
    label: '更新时间'
  }
]
const form = reactive({
  scale: 'all',
  name: '',
  workSpaceId: '',
  orderFieldName: 'ctime'
})

const search = () => {
  mittBus.emit('filterFlowListData', form)
}
const handleCommand = (command: { label: string; value: string }) => {
  scopeLabel.value = command.label
  form.scale = command.value
  search()
}
</script>

<style scoped lang="scss">
.filter-container {
  display: flex;
  .item-dropdown {
    padding-top: 8px;
    padding-right: 20px;
  }
}
</style>

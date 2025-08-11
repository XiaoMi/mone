<!--
 * @Description: 
 * @Date: 2024-03-07 10:34:41
 * @LastEditTime: 2024-08-15 16:21:35
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
        <el-input v-model="form.name" placeholder="插件名称" @keyup.enter="search" />
      </el-form-item>
      <el-form-item label="">
        <el-select v-model="form.category" placeholder="插件类型" @change="search">
          <el-option
            v-for="item in categoryList['2']"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="">
        <el-select v-model="form.order" placeholder="Select" @change="search">
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
import mittBus from '@/utils/bus'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()
const categoryList = computed(() => probotStore.categoryList)

const scopeLabel = ref('显示全部')
const form = reactive({
  scope: 'all',
  name: '',
  category: '',
  order: 'popular'
})

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
    value: 'popular',
    label: '最受欢迎'
  },
  {
    value: 'latest',
    label: '最近发布'
  }
]

const search = () => {
  mittBus.emit('filterPlugData', form)
}
const handleCommand = (command: { label: string; value: string }) => {
  scopeLabel.value = command.label
  form.scope = command.value
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

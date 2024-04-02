<!--
 * @Description: 
 * @Date: 2024-03-11 14:49:51
 * @LastEditTime: 2024-03-28 16:23:12
-->
<template>
  <ProbotBaseTitle title="团队空间"></ProbotBaseTitle>
  <div class="space-wrap">
    <div class="space-container">
      <div class="space-head">
        <div class="space-filter">
          <el-form ref="formRef" :model="form" inline>
            <el-form-item prop="name" label="空间名称:">
              <el-input v-model="form.name" placeholder="请输入空间名称" clearable />
            </el-form-item>
          </el-form>
        </div>
        <el-button type="primary" @click="create" class="create-btn">新建</el-button>
      </div>
      <el-table
        :data="workspaceList.filter((v) => v?.name.indexOf(form.name) > -1)"
        style="width: 100%"
      >
        <el-table-column label="空间" v-slot="{ row }">
          <BaseInfo :data="row" size="small" />
        </el-table-column>
        <el-table-column prop="owner" label="管理员" />
        <el-table-column prop="creator" label="创建人" />
        <el-table-column prop="createTime" label="创建时间" v-slot="{ row }">
          {{ dateFormat(row.createTime, 'yyyy-mm-dd HH:MM:ss') }}
        </el-table-column>
        <el-table-column prop="updater" label="更新人" />
        <el-table-column prop="updateTime" label="更新时间" v-slot="{ row }">
          {{ dateFormat(row.updateTime, 'yyyy-mm-dd HH:MM:ss') }}
        </el-table-column>
        <el-table-column fixed="right" label="操作" v-slot="{ row }" width="100px">
          <el-button type="primary" class="btn-item" text size="small" @click="editClick(row)"
            >编辑</el-button
          >
          <el-button type="primary" class="btn-item" text size="small" @click="deleteClick(row)"
            >删除</el-button
          >
        </el-table-column>
      </el-table>
    </div>
    <ProbotTeamDialog
      v-model="state.createDialogVisible"
      @onOk="getList"
      :teamInfo="teamInfo"
    ></ProbotTeamDialog>
  </div>
</template>

<script lang="ts" setup>
import { reactive, computed, ref } from 'vue'
import { getWorkspaceList } from '@/api/probot'
import { useProbotStore } from '@/stores/probot'
import BaseInfo from '@/components/BaseInfo.vue'
import dateFormat from 'dateformat'
import ProbotTeamDialog from '@/components/ProbotTeamDialog.vue'
import ProbotBaseTitle from '@/components/ProbotBaseTitle.vue'
import { removeTeam } from '@/common/probot.ts'

const probotStore = useProbotStore()

const workspaceList = computed(() => probotStore.workspaceList)

const state = reactive({
  createDialogVisible: false
})
const form = reactive({
  name: ''
})
const teamInfo = ref()

const getList = () => {
  getWorkspaceList()
    .then((res) => {
      if (res?.data?.length) {
        probotStore.setWorkspaceList(res?.data)
      } else {
        probotStore.setWorkspaceList([])
      }
    })
    .catch((e) => {
      console.log(e)
    })
}

const create = () => {
  state.createDialogVisible = true
  teamInfo.value = {}
}

const deleteClick = (row: { id: number; type: string }) => {
  removeTeam(row.id, getList)
}
const editClick = (row: any) => {
  state.createDialogVisible = true
  teamInfo.value = row
}
</script>

<style lang="scss" scoped>
.space-wrap {
  padding: 20px;
  min-height: 300px;
}
.space-container {
  background: #fff;
  padding: 20px;
}
.space-head {
  width: 100%;
  align-items: center;
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  .create-btn {
    margin-right: 20px;
  }
}
.space-filter {
  flex: 1;
}
.btn-item {
  padding-left: 4px;
  padding-right: 4px;
}
</style>

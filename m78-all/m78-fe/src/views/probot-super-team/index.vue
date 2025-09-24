<!--
 * @Description: 
 * @Date: 2024-03-11 14:49:51
 * @LastEditTime: 2024-09-11 19:23:43
-->
<template>
  <ProbotBaseTitle title="超管空间"></ProbotBaseTitle>
  <div class="space-wrap">
    <div class="space-container">
      <div class="space-head">
        <div class="space-filter">
          <el-form ref="formRef" :model="form" inline @submit.native.prevent>
            <el-form-item prop="name" label="空间名称:">
              <el-input
                v-model="form.name"
                placeholder="请输入空间名称"
                clearable
                @keyup.enter="getList(1)"
              />
            </el-form-item>
            <el-form-item prop="owner" label="管理员:">
              <el-input
                v-model="form.owner"
                placeholder="请输入管理员"
                clearable
                @keyup.enter="getList(1)"
              />
            </el-form-item>
          </el-form>
        </div>
        <el-button type="primary" @click="create" class="create-btn">新建</el-button>
      </div>
      <el-table :data="state.tableList" style="width: 100%">
        <el-table-column label="空间" v-slot="{ row }" width="180px">
          <BaseInfo :data="row" size="small" @click="toDetail(row)" />
        </el-table-column>
        <el-table-column prop="owner" label="管理员" width="150px" />
        <el-table-column prop="creator" label="创建人" width="150px" />
        <el-table-column prop="createTime" label="创建时间" v-slot="{ row }" width="182px">
          {{ dateFormat(row.createTime, 'yyyy-mm-dd HH:MM:ss') }}
        </el-table-column>
        <el-table-column prop="updater" label="更新人" min-width="150px" />
        <el-table-column prop="updateTime" label="更新时间" v-slot="{ row }" width="180px">
          {{ dateFormat(row.updateTime, 'yyyy-mm-dd HH:MM:ss') }}
        </el-table-column>
        <el-table-column fixed="right" label="操作" v-slot="{ row }" width="280px">
          <div class="right" v-if="row.canOperate">
            <BaseLink
              name="成员管理"
              @click.stop="userClick(row)"
              icon="icon-chengyuanguanli"
            ></BaseLink>
            <BaseLink name="编辑" @click.stop="editClick(row)" icon="icon-bianji"></BaseLink>
            <div class="item-btn">
              <el-dropdown>
                <BaseLink name="更多操作" icon="icon-gengduocaozuo"></BaseLink>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click.stop="transferClick(row)">转让所有权</el-dropdown-item>
                    <el-dropdown-item @click.stop="deleteClick(row)">删除空间</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-table-column>
      </el-table>
      <div class="pager" v-if="state.total">
        <el-pagination
          small
          background
          layout="prev, pager, next"
          :total="state.total"
          :page-size="state.search.pageSize"
          :v-model:current-page="state.search.pageNum"
          hide-on-single-page
          @change="handleChangePage"
        />
      </div>
    </div>
    <UserList :visible="userListVisible" :teamInfo="teamInfo" @onCancel="userListVisible = false" />
    <ProbotTeamDialog v-model="state.createDialogVisible" :teamInfo="teamInfo" @onOk="getList" />
    <TransferUser v-model="transferDialogVisible" :teamInfo="teamInfo" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import BaseInfo from '@/components/BaseInfo.vue'
import dateFormat from 'dateformat'
import ProbotBaseTitle from '@/components/probot/ProbotBaseTitle.vue'
import BaseLink from '@/components/probot/BaseLink.vue'
import UserList from '../probot-space/components/User/UserList.vue'
import ProbotTeamDialog from '@/components/probot/ProbotTeamDialog.vue'
import TransferUser from '../probot-space/components/User/TransferUser.vue'
import { removeTeam } from '@/common/probot.ts'
import { superAdminWorkspace } from '@/api/probot-team'
import { useRouter } from 'vue-router'
const router = useRouter()

const state = reactive({
  createDialogVisible: false,
  tableList: [],
  search: {
    pageSize: 10,
    pageNum: 1
  },
  total: 0,
  loading: true
})
const form = reactive({
  name: '',
  owner: ''
})
const teamInfo = ref()
const userListVisible = ref(false)
const transferDialogVisible = ref(false)

const getList = (page = 1) => {
  state.loading = true
  state.search.pageNum = page
  superAdminWorkspace({
    ...state.search,
    name: form.name,
    owner: form.owner
  })
    .then((data) => {
      if (data?.data.records?.length) {
        state.tableList = data.data.records || []
        state.total = data.data.totalRow || 0
      } else {
        state.tableList = []
        state.total = 0
      }
    })
    .catch((e) => {
      console.log(e)
    })
}
onMounted(() => {
  getList(1)
})
const handleChangePage = (page: number) => {
  getList(page)
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
const userClick = (row: any) => {
  userListVisible.value = true
  teamInfo.value = row
}
const transferClick = (row: any) => {
  transferDialogVisible.value = true
  teamInfo.value = row
}
const toDetail = (row: any) => {
  const path = '/probot-space/' + row.id
  const { href } = router.resolve({
    path
  })
  window.open(href, '_blank') //打开新的窗口
}
</script>

<style lang="scss" scoped>
.space-wrap {
  padding: 10px 10px;
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
.right {
  display: flex;
  align-items: center;
}
.item-btn {
  margin-top: 4px;
}
.pager {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
</style>

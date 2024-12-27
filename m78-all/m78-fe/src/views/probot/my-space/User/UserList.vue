<template>
  <el-drawer v-model="state.show" :destroy-on-close="true" size="720px" @close="emits('onCancel')">
    <template #title>
      <strong class="user-title">{{ t('probot.userManager.member') }}</strong>
    </template>
    <el-form :model="state.form">
      <el-row :gutter="20">
        <el-col :span="7">
          <el-form-item :label="`${t('probot.userManager.role')}:`">
            <el-select v-model="state.form.type" style="width: 100%">
              <el-option key="all" value="" :label="t('probot.userManager.roleList.all')">{{
                t('probot.userManager.roleList.all')
              }}</el-option>
              <el-option
                v-for="item in state.userType"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item>
            <el-input
              v-model="state.form.username"
              :placeholder="t('probot.userManager.pleaceEnterUserName')"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item>
            <div class="btns">
              <el-button @click="getUsers">{{ t('probot.search') }}</el-button>
              <el-button type="primary" @click="state.editUser = true">{{
                t('probot.userManager.addUserName')
              }}</el-button>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-table :data="state.userList" size="small" :header-row-style="{ color: '#333' }">
      <el-table-column property="username" :label="t('probot.userManager.userName')" width="240" />
      <el-table-column property="role" :label="t('probot.userManager.role')" width="100">
        <template #default="scoped">
          {{ state.userType.filter((v) => v.value === scoped.row.role)[0].label }}
        </template>
      </el-table-column>
      <el-table-column property="createTime" :label="t('probot.userManager.joinTime')">
        <template #default="scoped">
          {{ moment(scoped.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
      </el-table-column>
      <el-table-column :label="t('probot.userManager.operate')" width="130" align="center">
        <template #default="scoped">
          <el-button type="text" size="small" @click="handleEdit(scoped.row)">{{
            t('probot.userManager.modify')
          }}</el-button>
          <el-button type="text" size="small" @click="handleDelete(scoped.row)">{{
            t('probot.userManager.remove')
          }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <div class="drawer-footer">
        <el-button @click="emits('onCancel')">{{ t('probot.userManager.close') }}</el-button>
      </div>
    </template>
  </el-drawer>
  <EditUser
    :visible="state.editUser"
    :user-type="state.userType.filter((v) => v.value !== ERole.creator)"
    @on-cancel="
      () => {
        ;(state.editUser = false), (state.modifyUser = {})
      }
    "
    :modifyUser="state.modifyUser"
    :teamInfo="props.teamInfo"
    @getList="getUsers"
  />
</template>

<script lang="ts" setup>
import { reactive, watch } from 'vue'
import EditUser from './EditUser.vue'
import { deleteUser, getUserList } from '@/api/probot'
import { ElMessage, ElMessageBox } from 'element-plus'
import { t } from '@/locales'
import moment from 'moment'
import { ERole } from './interface'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  teamInfo: {
    type: Object,
    default() {
      return {}
    }
  }
})

const emits = defineEmits(['onCancel'])

const state = reactive({
  show: false,
  editUser: false,
  modifyUser: {},
  form: {
    username: undefined,
    type: ''
  },
  userType: [
    {
      label: t('probot.userManager.roleList.creator'),
      value: ERole.creator
    },
    {
      label: t('probot.userManager.roleList.manager'),
      value: ERole.manager
    },
    {
      label: t('probot.userManager.roleList.member'),
      value: ERole.member
    }
  ],
  userList: []
})

const getUsers = () => {
  getUserList({
    workspaceId: props.teamInfo?.id,
    username: state.form.username,
    role: state.form.type
  })
    .then((data) => {
      if (data.code === 0) {
        state.userList = (data.data || []).filter((v) => v.role !== ERole.super)
      } else {
        ElMessage.error(data.message!)
      }
    })
    .catch((e) => {
      console.log(e)
    })
}

const handleEdit = (row: any) => {
  state.editUser = true
  state.modifyUser = row
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    t('probot.userManager.confirmDelUser', { name: row.username }),
    t('probot.userManager.warning'),
    {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancle'),
      type: 'warning'
    }
  )
    .then((action) => {
      if (action === 'confirm') {
        deleteUser({
          workspaceId: props.teamInfo?.id,
          username: row.username
        })
          .then((data) => {
            if (data.data) {
              getUsers()
              ElMessage.success(t('common.deleteSuccess'))
            } else {
              ElMessage.error(data.message!)
            }
          })
          .catch((e) => {
            console.log(e)
          })
      }
    })
    .catch(() => {})
}

watch(
  () => props.visible,
  (val) => {
    state.show = val
    if (!val) {
      state.form = {
        username: undefined,
        type: ''
      }
    } else {
      getUsers()
    }
  }
)
</script>

<style lang="scss" scoped>
.user-title {
  color: #333;
  font-size: 16px;
}
.btns {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.pager {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>

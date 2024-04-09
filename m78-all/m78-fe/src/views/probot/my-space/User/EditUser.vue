<template>
  <el-dialog
    v-model="state.show"
    :show-close="false"
    :title="
      props.modifyUser?.username
        ? t('probot.userManager.editUser')
        : t('probot.userManager.addUser')
    "
    width="400"
  >
    <el-form :model="state.form" :rules="rules" ref="formRef">
      <el-form-item :label="`${t('probot.userManager.role')}:`" prop="userType">
        <el-select v-model="state.form.userType" style="width: 100%">
          <el-option
            v-for="item in props.userType"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="`${t('probot.userManager.user')}:`" prop="user">
        <el-select
          :disabled="!!props.modifyUser?.username"
          v-model="state.form.user"
          multiple
          filterable
          remote
          :placeholder="t('probot.userManager.pleaceEnterKeyword')"
          :remote-method="searchMember"
          style="width: 100%"
          class="user-select-multiple"
        >
          <el-option
            v-for="item in state.userList"
            :key="item.username"
            :label="item.user"
            :value="item.username"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="emits('onCancel')">{{ t('common.cancle') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ t('common.confirm') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, watch, ref } from 'vue'
import Lodash from 'lodash'
import { t } from '@/locales'
import { addUser, searchUser, updateUserRole } from '@/api/probot'
import { ElMessage } from 'element-plus'

const formRef = ref()

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  userType: {
    type: Array,
    defaule() {
      return []
    }
  },
  modifyUser: {
    type: Object,
    default() {
      return {}
    }
  },
  teamInfo: {
    type: Object,
    default() {
      return {}
    }
  }
})

const emits = defineEmits(['onCancel', 'getList'])
const state = reactive({
  show: false,
  form: {
    userType: 0,
    user: [] as any[]
  },
  userList: []
})

const rules = {
  user: [
    {
      required: true,
      message: t('probot.userManager.pleaceSelectUser'),
      trigger: ['blur', 'change']
    }
  ],
  userType: [
    {
      required: true,
      message: t('probot.userManager.pleaceSelectRole'),
      trigger: ['blur', 'change']
    }
  ]
}

const getUserList = (word: string) => {
  searchUser({ keyword: word })
    .then((data) => {
      if (data.data?.length) {
        state.userList = data.data || []
      } else {
        state.userList = []
      }
    })
    .catch((e) => {
      console.log(e)
    })
}

const searchMember = Lodash.debounce(getUserList, 500)

const handleSubmit = () => {
  formRef.value.validate((bool: boolean) => {
    if (bool) {
      if (!props.modifyUser?.username) {
        addUser({
          workspaceId: props.teamInfo?.id,
          username: state.form.user,
          role: state.form.userType
        })
          .then((data) => {
            if (data.data) {
              ElMessage.success(t('probot.userManager.addSucc'))
              emits('getList')
              emits('onCancel')
            } else if (data.code === 400000500) {
              ElMessage.error(t('probot.userManager.noPermission'))
            } else {
              ElMessage.error(data.message!)
            }
          })
          .catch((e) => {
            console.log(e)
          })
      } else {
        updateUserRole({
          workspaceId: props.teamInfo?.id,
          username: state.form.user[0],
          role: state.form.userType
        })
          .then((data) => {
            if (data.data) {
              ElMessage.success(t('probot.userManager.editSucc'))
              emits('getList')
              emits('onCancel')
            } else if (data.code === 400000500) {
              ElMessage.error(t('probot.userManager.noPermission'))
            } else {
              ElMessage.error(data.message!)
            }
          })
          .catch((e) => {
            console.log(e)
          })
      }
    }
  })
}

watch(
  () => props.visible,
  (val) => {
    state.show = val
    if (!val) {
      state.form.user = []
      state.form.userType = 0
    } else {
      if (props.modifyUser?.username) {
        state.form.user = [props.modifyUser.username]
        state.form.userType = props.modifyUser.role
      }
    }
  }
)
</script>

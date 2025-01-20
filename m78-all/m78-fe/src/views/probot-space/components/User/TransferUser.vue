<template>
  <el-dialog v-model="dialogVisible" :title="t('probot.userManager.transfer')" width="400">
    <el-form :model="state.form" :rules="rules" ref="formRef">
      <el-form-item :label="`${t('probot.userManager.user')}:`" prop="user">
        <el-select
          v-model="state.form.user"
          filterable
          remote
          :placeholder="t('probot.userManager.pleaceEnterKeyword')"
          :remote-method="searchMember"
          style="width: 100%"
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
        <el-button @click="emits('update:modelValue', false)">{{ t('common.cancle') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ t('common.confirm') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, watch, ref, computed } from 'vue'
import Lodash from 'lodash'
import { searchUser } from '@/api/probot'
import { transferTeam } from '@/api/probot-team'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'

const formRef = ref()

const props = defineProps({
  modelValue: {
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
const emits = defineEmits(['update:modelValue'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const state = reactive({
  show: false,
  form: {
    user: undefined
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
  ]
}

const getUser = (word: string) => {
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

const searchMember = Lodash.debounce(getUser, 500)

const handleSubmit = () => {
  formRef.value.validate((bool: boolean) => {
    if (bool) {
      transferTeam({
        workspaceId: props.teamInfo?.id,
        username: state.form.user
      })
        .then((data) => {
          if (data.data) {
            ElMessage.success(t('probot.userManager.transferSucc'))
            emits('update:modelValue', false)
          } else if (data.code === 400000500) {
            ElMessage.error(t('probot.userManager.noPermission'))
          } else {
            ElMessage.error(data.message || t('probot.userManager.transferErr'))
          }
        })
        .catch((e) => {
          console.log(e)
        })
    }
  })
}

watch(
  () => props.modelValue,
  (val) => {
    if (!val) {
      state.form.user = undefined
    }
  }
)
</script>

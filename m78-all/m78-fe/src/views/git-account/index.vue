<!--
 * @Description: 
 * @Date: 2024-11-14 14:24:34
 * @LastEditTime: 2024-11-14 16:42:02
-->
<template>
  <ProbotBaseTitle title="Gitlab 账号"></ProbotBaseTitle>
  <div class="git-account-wrap">
    <div class="git-account-container">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" status-icon>
        <el-form-item label="用户：" prop="user">
          <el-input
            v-model="form.user"
            autocomplete="off"
            placeholder="请输入user"
            maxlength="50"
            show-word-limit
            disabled
          />
        </el-form-item>
        <el-form-item label="Gitlab账号：" prop="gitlabAccount">
          <el-input
            v-model="form.gitlabAccount"
            autocomplete="off"
            placeholder="请输入Git账号"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div class="git-account-btn">
        <el-button type="primary" @click="sure" :disabled="loading"> 确定 </el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { addGitlabMapping, searchGitlab } from '@/api/git-account'
import { submitForm } from '@/common/formMethod'
import ProbotBaseTitle from '@/components/probot/ProbotBaseTitle.vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

interface RuleForm {
  gitlabAccount: string
  user: string
}

const formRef = ref<FormInstance>()
const form = reactive<RuleForm>({
  gitlabAccount: '',
  user: computed(() => userStore.userInfo.fullAccount).value
})
const rules = reactive<FormRules<RuleForm>>({
  gitlabAccount: [{ required: true, message: '请输入gitlabAccount', trigger: 'blur' }],
  user: [{ required: true, message: '请输入user', trigger: 'blur' }]
})
const loading = ref(false)
onMounted(() => {
  searchGitlab([{ user: form.user }]).then((res) => {
    form.gitlabAccount = res?.data?.gitlabAccount
  })
})
const sure = () => {
  submitForm(formRef.value, form).then(() => {
    loading.value = true
    addGitlabMapping([
      {
        ...form
      }
    ])
      .then((data) => {
        if (data.code == 0) {
          ElMessage.success('映射成功！')
        } else {
          ElMessage.error(data.message || '映射失败！')
        }
      })
      .catch((e) => {
        console.log(e)
      })
      .finally(() => {
        loading.value = false
      })
  })
}
</script>

<style lang="scss" scoped>
.git-account-wrap {
  padding: 10px 10px;
  min-height: 300px;
}
.git-account-container {
  background: #fff;
  padding: 20px;
  width: 100%;
}
.git-account-btn {
  text-align: center;
}
</style>

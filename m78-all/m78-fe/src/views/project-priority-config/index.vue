<!--
 * @Description:
 * @Date: 2024-11-14 14:24:34
 * @LastEditTime: 2024-12-10 21:08:25
-->
<template>
  <ProbotBaseTitle title="项目优先级配置"></ProbotBaseTitle>
  <div class="priority-config-wrap">
    <div class="priority-config-container">
      <el-form ref="formRef" :model="form" label-position="top">
        <el-form-item
          label="用户："
          prop="account"
          :rules="[
            {
              required: true,
              message: '用户名不能为空',
              trigger: 'blur'
            }
          ]"
        >
          <el-input
            v-model="form.account"
            autocomplete="off"
            placeholder="请输入用户"
            maxlength="50"
            show-word-limit
            disabled
          />
        </el-form-item>
        <el-form-item label="项目优先级配置：" prop="priorityList">
          <div class="priority-header">
            <div class="priority-select">优先级</div>
            <div class="project-adress">项目地址</div>
            <div class="priority-owners">Owner</div>
            <div class="priority-icon-container"></div>
          </div>
          <div class="priority-item" v-for="(domain, index) in form.priorityList" :key="index">
            <el-form-item
              :prop="'priorityList.' + index + '.projectPriority'"
              :rules="{
                required: true,
                message: '请选择优先级',
                trigger: 'change'
              }"
              class="priority-select"
            >
              <el-select v-model="domain.projectPriority" placeholder="请选择优先级">
                <el-option
                  v-for="(item, index) in projectPriorityOptions"
                  :key="item.value + index"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              :prop="'priorityList.' + index + '.projectAddress'"
              :rules="{
                required: true,
                message: '项目地址不能为空',
                trigger: 'blur'
              }"
              class="project-adress"
            >
              <el-input
                v-model="domain.projectAddress"
                placeholder="https://xxx/项目组/项目名称 (项目主页）,例：https://xxx/mione-test/dfztest1"
              />
            </el-form-item>
            <el-form-item
              :prop="'priorityList.' + index + '.owners'"
              :rules="{
                required: false,
                message: '请选择owner',
                trigger: 'change'
              }"
              class="priority-owners"
            >
              <el-select
                v-model="domain.owners"
                filterable
                remote
                :reserve-keyword="false"
                placeholder="请选择owner"
                :remote-method="searchMember"
                style="width: 100%"
              >
                <el-option
                  v-for="(item, index) in userList"
                  :key="item.username + item.user + index"
                  :label="item.user"
                  :value="item.username + '#' + item.user"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <div class="priority-icon-container">
              <div
                class="priority-icon"
                @click="addGitlab(index)"
                v-if="form.priorityList.length < 3"
              >
                <el-icon><Plus /></el-icon>
              </div>
              <div
                class="priority-icon"
                @click="deleteGitlab(index)"
                v-if="form.priorityList.length > 1"
              >
                <el-icon><Minus /></el-icon>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <div class="priority-config-btn">
        <el-button type="primary" @click="sure" :disabled="loading"> 确定 </el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import ProbotBaseTitle from '@/components/probot/ProbotBaseTitle.vue'
import { submitForm } from '@/common/formMethod'
import { useUserStore } from '@/stores/user'
import {
  getProjectPriorityConfigByAccount,
  projectPriorityConfig
} from '@/api/project-priority-config'
import { searchUser } from '@/api/probot'
import Lodash from 'lodash'

const userStore = useUserStore()

interface RuleForm {
  priorityList: Array<{
    projectPriority: number | undefined
    projectAddress: string
    owners: string
  }>
  account: string
}

const projectPriorityOptions = ref([
  {
    value: 1,
    label: '1(高)'
  },
  {
    value: 2,
    label: '2(中)'
  },
  {
    value: 3,
    label: '3(低)'
  }
])
const formRef = ref<FormInstance>()
const form = reactive<RuleForm>({
  priorityList: [
    {
      projectPriority: undefined,
      projectAddress: '',
      owners: ''
    }
  ],
  account: computed(() => userStore.userInfo.fullAccount).value
})

const loading = ref(false)
const userList = ref([])

const getUserList = async (word: string) => {
  searchUser({ keyword: word })
    .then((data) => {
      if (data.data?.length) {
        userList.value = data.data || []
      } else {
        userList.value = []
      }
    })
    .catch((e) => {
      console.log(e)
    })
}
const searchMember = Lodash.debounce(getUserList, 100)

onMounted(() => {
  getProjectPriorityConfigByAccount([form.account]).then((res) => {
    if (res?.data?.length) {
      form.priorityList = res?.data.map((item: any) => ({
        projectPriority: item.projectPriority,
        projectAddress: item.projectAddress,
        owners: item.owners.indexOf('#') > -1 ? item.owners.split('#')[1] : item.owners
      }))
    }
  })
})
const sure = () => {
  submitForm(formRef.value, form).then(() => {
    loading.value = true
    const priorityList = form.priorityList.map((item) => ({
      projectPriority: item.projectPriority,
      projectAddress: item.projectAddress,
      owners: [item.owners]
    }))
    projectPriorityConfig([
      {
        account: form.account,
        priorityList
      }
    ])
      .then((data) => {
        if (data.code == 0) {
          ElMessage.success('配置成功！')
        } else {
          ElMessage.error(data.message || '配置失败！')
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
//添加
const addGitlab = (index: number) => {
  form.priorityList.splice(index + 1, 0, {
    projectPriority: undefined,
    projectAddress: '',
    owners: ''
  })
}
//删除
const deleteGitlab = (index: number) => {
  form.priorityList.splice(index, 1)
}
</script>

<style lang="scss" scoped>
.priority-config-wrap {
  padding: 10px 10px;
  min-height: 300px;
}
.priority-config-container {
  background: #fff;
  padding: 20px;
  width: 100%;
}
.priority-config-btn {
  text-align: center;
}
.priority-item {
  display: flex;
  width: 100%;
  align-items: center;
  padding: 10px;
  background: #f5f7fa;
  .priority-select {
    margin-right: 10px;
    width: 200px;
  }
  .project-adress {
    flex: 1;
  }
  .priority-owners {
    width: 200px;
    margin-left: 10px;
  }
  .priority-icon-container {
    display: flex;
    width: 140px;
  }
  .priority-icon {
    margin-left: 10px;
    background: #eee;
    width: 30px;
    height: 30px;
    border-radius: 50%;
    text-align: center;
    cursor: pointer;
    &:hover {
      box-shadow: inset 0 0 5px #ddd;
    }
  }
}
.priority-tip {
  width: 100%;
  font-size: 12px;
  color: #f56c6c;
  line-height: 14px;
}
.priority-header {
  display: flex;
  width: 100%;
  align-items: center;
  padding: 0 10px;
  color: #606266;

  .priority-select {
    margin-right: 10px;
    width: 200px;
  }
  .project-adress {
    flex: 1;
  }
  .priority-owners {
    width: 200px;
    margin-left: 10px;
  }
  .priority-icon-container {
    width: 140px;
  }
}
</style>

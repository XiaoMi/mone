<!--
 * @Description: 
 * @Date: 2024-11-14 14:24:34
 * @LastEditTime: 2024-12-10 14:57:08
-->
<template>
  <ProbotBaseTitle title="员工管理"></ProbotBaseTitle>
  <div class="employee-config-wrap">
    <div class="employee-config-container">
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
        <el-form-item label="员工配置：" prop="employeeList" style="width: 100%">
          <div v-if="form.employeeList?.length" style="width: 100%">
            <div class="employee-item">
              <div class="column1">姓名</div>
              <!-- <div class="column2">是否参与排名</div> -->
              <div class="column3">级别</div>
              <div class="column4">角色</div>
              <div class="column5">代码占比</div>
              <div class="column6">文档占比</div>
            </div>
            <div class="employee-item" v-for="(domain, index) in form.employeeList" :key="index">
              <el-form-item
                :prop="'employeeList.' + index + '.oprid'"
                :rules="{
                  required: true,
                  message: '姓名不能为空',
                  trigger: 'blur'
                }"
                class="column1"
              >
                <el-input v-model="domain.oprid" placeholder="请输入姓名" />
              </el-form-item>
              <!-- <el-form-item
                :prop="'employeeList.' + index + '.status'"
                :rules="{
                  required: true,
                  message: '请选择是否参与排名',
                  trigger: 'blur'
                }"
                class="column2"
              >
                <el-select v-model="domain.status" placeholder="请选择是否参与排名">
                  <el-option
                    v-for="item in statusOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
              </el-form-item> -->
              <el-form-item
                :prop="'employeeList.' + index + '.level'"
                :rules="{
                  required: false,
                  message: '请选择级别',
                  trigger: 'blur'
                }"
                class="column3"
              >
                <el-select
                  v-model="domain.level"
                  placeholder="请选择级别"
                  @change="(val) => handleLevelChange(val, index)"
                >
                  <el-option
                    v-for="item in levelOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item
                :prop="'employeeList.' + index + '.frontOrBack'"
                :rules="{
                  required: false,
                  message: '请选择前端或后端',
                  trigger: 'blur'
                }"
                class="column4"
              >
                <el-select v-model="domain.frontOrBack" placeholder="请选择前端或后端">
                  <el-option
                    v-for="item in frontOrBackOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item
                :prop="'employeeList.' + index + '.codeProportion'"
                :rules="{
                  required: false,
                  validator: validateCodeProportion,
                  trigger: 'blur'
                }"
                class="column5"
              >
                <el-input
                  v-model="domain.codeProportion"
                  :placeholder="getCodePlaceholder(domain.level)"
                  :disabled="domain.level === 1"
                  @change="codeProportionChange(domain.codeProportion, index)"
                >
                  <template #append>%</template>
                </el-input>
              </el-form-item>

              <el-form-item
                :prop="'employeeList.' + index + '.docProportion'"
                :rules="{
                  required: false,
                  validator: validateDocProportion,
                  trigger: 'blur'
                }"
                class="column6"
              >
                <el-input v-model="domain.docProportion" placeholder="请输入文档占比" disabled>
                  <template #append>%</template>
                </el-input>
              </el-form-item>
            </div>
          </div>
          <el-empty :description="description" v-else class="empty-container" />
        </el-form-item>
      </el-form>
      <div class="employee-config-btn" v-if="form.employeeList?.length">
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
import { getEmployeeStatusByAccount, configureEmployeeStatus } from '@/api/employee-manage'

const userStore = useUserStore()

interface RuleForm {
  employeeList: Array<{
    oprid: string
    status: number | undefined
    level: number | undefined
    frontOrBack: number | undefined
    codeProportion: string
    docProportion: string
  }>
  account: string
}

const statusOptions = ref([
  {
    value: 0,
    label: '不参与'
  },
  {
    value: 1,
    label: '参与'
  }
])
const frontOrBackOptions = ref([
  {
    value: 0,
    label: '后端'
  },
  {
    value: 1,
    label: '前端'
  }
])
const levelOptions = ref([
  {
    value: 1,
    label: '1（13到15）'
  },
  {
    value: 2,
    label: '2（16到17）'
  },
  {
    value: 3,
    label: '3（18到19）'
  },
  {
    value: 4,
    label: '4（20以上）'
  }
])
const formRef = ref<FormInstance>()
// 更新表单项
const form = reactive<RuleForm>({
  employeeList: [
    {
      oprid: '',
      status: undefined,
      level: undefined,
      frontOrBack: undefined,
      codeProportion: '',
      docProportion: ''
    }
  ],
  account: computed(() => userStore.userInfo.fullAccount).value
})

const loading = ref(false)
const description = ref('')
onMounted(() => {
  getEmployeeStatusByAccount([form.account]).then((res) => {
    if (res?.code == 0 && res?.data?.length) {
      form.employeeList = res?.data
      form.employeeList.forEach((item) => {
        if (item.level === 1) {
          item.codeProportion = '100'
          item.docProportion = '0'
        }
      })
      description.value = ''
    } else {
      form.employeeList = []
      description.value = res?.message || '无数据'
    }
  })
})
const sure = () => {
  submitForm(formRef.value, form).then(() => {
    loading.value = true
    const employeeList = form.employeeList.map((item) => {
      return {
        oprid: item.oprid,
        status: item.status === undefined ? 1 : item.status,
        level: item.level,
        frontOrBack: item.frontOrBack,
        codeProportion: item.codeProportion,
        docProportion: item.docProportion
      }
    })
    configureEmployeeStatus([
      {
        account: form.account,
        employeeStatusDtoList: employeeList
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
// 修改代码占比变化处理函数
const codeProportionChange = (value: string, index: number) => {
  if (value) {
    const codeProp = Number(value) || 0
    const level = form.employeeList[index].level

    // 级别1固定100%
    if (level === 1) {
      form.employeeList[index].codeProportion = '100'
      form.employeeList[index].docProportion = '0'
      return
    }

    // 级别2不能小于70%
    if (level === 2 && codeProp < 70) {
      ElMessage.warning('级别2的代码占比不能小于70%')
      form.employeeList[index].codeProportion = ''
      form.employeeList[index].docProportion = ''
      return
    }

    // 检查是否超过100%
    if (codeProp > 100) {
      ElMessage.warning('代码占比不能超过100%')
      form.employeeList[index].codeProportion = ''
      form.employeeList[index].docProportion = ''
      return
    }

    // 自动计算文档占比
    form.employeeList[index].docProportion = String(100 - codeProp)
  } else {
    form.employeeList[index].docProportion = ''
  }
}

// 修改表单验证规则
const validateCodeProportion = (rule: any, value: string, callback: any) => {
  if (value) {
    const num = Number(value)
    if (isNaN(num)) {
      callback(new Error('请输入数字'))
      return
    }

    // 获取当前验证的行索引
    const index = Number(rule.field.match(/\d+/)[0])
    const level = form.employeeList[index].level

    // 级别1固定100%
    if (level === 1 && num !== 100) {
      callback(new Error('级别1的代码占比必须为100%'))
      return
    }

    // 级别2不低于70%
    if (level === 2 && num < 70) {
      callback(new Error('级别2的代码占比不低于70%'))
      return
    }

    if (num > 100) {
      callback(new Error('代码占比不能超过100%'))
      return
    }
  }
  callback()
}

// 修改文档占比验证规则
const validateDocProportion = (rule: any, value: string, callback: any) => {
  if (value) {
    const num = Number(value)
    if (isNaN(num)) {
      callback(new Error('请输入数字'))
      return
    }
    if (num < 0 || num > 50) {
      callback(new Error('文档占比必须在0-50%之间'))
      return
    }
  }
  callback()
}
const handleLevelChange = (level: number, index: number) => {
  if (level === 1) {
    // 级别1时固定为100%
    form.employeeList[index].codeProportion = '100'
    form.employeeList[index].docProportion = '0'
  } else {
    // 切换到其他级别时清空
    form.employeeList[index].codeProportion = ''
    form.employeeList[index].docProportion = ''
  }
}

// 添加获取placeholder的方法
const getCodePlaceholder = (level: number | undefined) => {
  switch (level) {
    case 1:
      return '级别1固定为100%'
    case 2:
      return '级别2代码占比不低于70%'
    case 3:
    case 4:
      return '请输入代码占比'
    default:
      return '请选择级别'
  }
}
</script>

<style lang="scss" scoped>
.employee-config-wrap {
  padding: 10px 10px;
  min-height: 300px;
}
.employee-config-container {
  background: #fff;
  padding: 20px;
  width: 100%;
}
.employee-config-btn {
  text-align: center;
}
.employee-item {
  display: flex;
  width: 100%;
  align-items: center;
  padding: 10px;
  background: #f5f7fa;
  :deep(.oz-select) {
    width: 100%;
  }
  .column1 {
    width: 20%;
    padding-right: 10px;
  }
  // .column2 {
  //   width: 15%;
  //   padding-right: 10px;
  // }
  .column3 {
    width: 15%;
    padding-right: 10px;
  }
  .column4 {
    width: 15%;
    padding-right: 10px;
  }
  .column5 {
    width: 25%;
    padding-right: 10px;
  }
  .column6 {
    width: 25%;
  }
  .employee-icon-container {
    display: flex;
  }
  .employee-icon {
    margin-right: 10px;
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
.employee-tip {
  width: 100%;
  font-size: 12px;
  color: #f56c6c;
  line-height: 14px;
}
.empty-container {
  margin: 0 auto;
}
</style>

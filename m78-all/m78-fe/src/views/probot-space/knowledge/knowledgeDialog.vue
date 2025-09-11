<!--
 * @Description: 
 * @Date: 2024-03-06 11:30:12
 * @LastEditTime: 2024-09-11 19:23:28
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="props.data.id ? '编辑知识库' : '创建知识库'"
    width="500"
    :draggable="true"
    :append-to-body="true"
  >
    <div class="create-dialog-container">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" status-icon>
        <el-form-item label="名称：" prop="name">
          <el-input
            v-model="form.name"
            autocomplete="off"
            placeholder="请输入名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="描述：" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            autocomplete="off"
            placeholder="请输入描述"
            :autosize="{ minRows: 4 }"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <BaseAvatar
          :name="form.name"
          :remark="form.remark"
          v-model="form.avatarUrl"
          tips="输入知识库 名称和介绍后，点击自动生成头像"
        ></BaseAvatar>
        <!--公开私有 -->
        <el-form-item label="权限：" prop="auth">
          <el-radio-group v-model="form.auth">
            <el-radio :label="0">私有</el-radio>
            <el-radio :label="1">公开</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="类型：" prop="type">
          <el-select v-model="form.type" placeholder="请选择" filterable clearable>
            <el-option v-for="(item, key) in typeOptions" :key="key" :label="item" :value="key">
            </el-option>
          </el-select>
        </el-form-item>
        <!-- 标签 -->
        <el-form-item label="标签：">
          <div class="labels-wrap" v-if="form.labels.length">
            <el-form-item
              v-for="(domain, index) in form.labels"
              :key="index"
              :prop="'labels.' + index + '.key'"
              class="labels-form"
            >
              <div class="labels-container">
                <div class="labels-item">
                  <el-input v-model="domain.key" placeholder="请输入key" @change="keyChange" />
                  <div>：</div>
                  <el-input
                    v-model="domain.value"
                    placeholder="请输入value"
                    @change="valueChange"
                  />
                  <div class="labels-icon-container">
                    <div class="labels-icon" @click="addLabels(index)">
                      <el-icon><Plus /></el-icon>
                    </div>
                    <div class="labels-icon" @click="deleteLabels(index)">
                      <el-icon><Minus /></el-icon>
                    </div>
                  </div>
                </div>
                <div class="labels-tip">{{ domain.tip }}</div>
              </div>
            </el-form-item>
          </div>
          <el-button type="primary" @click="addLabels(0)" v-else
            ><el-icon><Plus /></el-icon>添加labels</el-button
          >
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sure" :disabled="loading"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useRoute } from 'vue-router'
import { create, update, createKnowledgeBaseBindDefaultBot } from '@/api/probot-knowledge'
import { submitForm, resetForm } from '@/common/formMethod'
import BaseAvatar from '@/components/probot/BaseAvatar.vue'

const route = useRoute()
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: () => ({})
  }
})
const emits = defineEmits(['update:modelValue', 'onOk'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

interface RuleForm {
  type: string
  name: string
  avatarUrl: string
  remark: string
  auth: number
  labels: Array<{
    tip: string
    key: string
    value?: string
  }>
}

const formInit = {
  avatarUrl: Math.floor(Math.random() * 10) + '',
  type: '',
  name: '',
  remark: '',
  auth: 0,
  labels: []
}
const formRef = ref<FormInstance>()
const form = ref<RuleForm>(formInit)
const rules = reactive<FormRules<RuleForm>>({
  name: [{ required: true, message: '请输入知识库名称', trigger: 'blur' }],
  avatarUrl: [{ required: true, message: '请选择知识库图标', trigger: 'change' }],
  remark: [{ required: true, message: '请输入知识库描述', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
})
const loading = ref(false)
const typeOptions = {
  normal: '普通'
}

//添加
const addLabels = (index: number) => {
  form.value.labels.splice(index + 1, 0, {
    key: '',
    value: ''
  })
}
//删除
const deleteLabels = (index: number) => {
  form.value.labels.splice(index, 1)
}
const keyChange = () => {
  checkKey()
}
const valueChange = () => {
  checkKey()
}
const checkKey = () => {
  let res = true
  let obj = {}
  form.value.labels.forEach((v) => {
    v.tip = ''
    if (!v.key) {
      v.tip = 'key不能为空'
      res = false
    } else if (obj[v.key] !== undefined) {
      v.tip = 'key值不能重复'
      res = false
    }
    obj[v.key] = v.value
  })
  return res
}

const setFormValue = (val: any) => {
  if (val.id) {
    let labels = []
    const labelsObj = val?.labels
    for (let key in labelsObj) {
      labels.push({
        key: key,
        value: labelsObj[key]
      })
    }
    form.value = {
      name: val?.knowledgeBaseName,
      remark: val?.remark,
      auth: val?.auth ? Number(val?.auth) : formInit.auth,
      labels: labels,
      avatarUrl: val?.avatarUrl,
      type: val?.type
    }
  } else {
    form.value = { ...formInit }
  }
}
/**  watch */
watch(
  () => props.data,
  (val) => {
    resetForm(formRef.value)
    setFormValue(val)
  },
  {
    immediate: true,
    deep: true
  }
)
const sure = () => {
  submitForm(formRef.value, form).then(() => {
    loading.value = true
    let request
    if (route.name === 'AI Probot Knowledge New') {
      request = props.data.id ? update : createKnowledgeBaseBindDefaultBot
    } else {
      request = props.data.id ? update : create
    }
    let labels = ''
    form.value.labels?.forEach((v: any) => {
      if (labels) {
        labels += ','
      }
      labels += v.key + '=' + v.value
    })
    request({
      ...props.data,
      ...form.value,
      labels,
      status: '0',
      workSpaceId: route.params.id
    })
      .then((data) => {
        if (data.data) {
          ElMessage.success(props.data.id ? '编辑成功！' : '创建成功！')
          emits('update:modelValue', false)
          emits('onOk', !props.data?.id ? data.data : '')
        } else {
          ElMessage.error(data.message || '创建失败')
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

<style lang="scss">
.create-dialog-container {
  .oz-input,
  .oz-select {
    width: 100%;
  }
}
</style>

<style lang="scss" scoped>
.labels-wrap {
  padding: 10px 0;
  width: 100%;
  background: #f5f7fa;
}
.labels-form {
  padding: 10px;
}
.labels-container {
  width: 100%;
}
.labels-item {
  display: flex;
  width: 100%;
  .labels-icon-container {
    display: flex;
  }
  .labels-icon {
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
.labels-tip {
  width: 100%;
  font-size: 12px;
  color: #f56c6c;
  line-height: 14px;
}
</style>

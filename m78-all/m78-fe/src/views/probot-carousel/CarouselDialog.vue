<!--
 * @Description: 
 * @Date: 2024-09-13 16:18:40
 * @LastEditTime: 2024-09-13 20:11:52
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="props.data?.id ? '编辑' : '新建'"
    width="500"
    :draggable="true"
    :append-to-body="true"
  >
    <div class="carouse-dialog-container">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" status-icon>
        <el-form-item label="botId：" prop="botId">
          <el-select
            v-model="form.botId"
            filterable
            remote
            reserve-keyword
            placeholder="请选择Bot名字搜索"
            :remote-method="botRemoteMethod"
            :loading="botLoading"
            style="width: 100%;"
          >
            <el-option
              v-for="item in botOptions"
              :key="item.botId"
              :label="item.botName"
              :value="item.botId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="名称：" prop="title">
          <el-input
            v-model="form.title"
            autocomplete="off"
            placeholder="请输入名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="推荐理由：" prop="recommendReasons">
          <div style="width: 100%">
            <el-form-item
              v-for="(domain, index) in form.recommendReasons"
              :key="index"
              :prop="'recommendReasons.' + index + '.value'"
              style="padding-bottom: 2px"
            >
              <el-input
                v-model="domain.value"
                autocomplete="off"
                placeholder="请输入推荐理由"
                maxlength="50"
                show-word-limit
                type="textarea"
              />
            </el-form-item>
          </div>
        </el-form-item>
        <el-form-item prop="type" label="类型:">
          <el-select v-model="form.type" style="width: 100%" placeholder="请选择类型" clearable>
            <el-option
              v-for="item in typeOptions"
              :key="item?.id"
              :label="item?.name"
              :value="item?.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="displayStatus" label="状态:">
          <el-select
            v-model="form.displayStatus"
            style="width: 100%"
            placeholder="请选择状态"
            clearable
          >
            <el-option
              v-for="item in statusOptions"
              :key="item?.id"
              :label="item?.name"
              :value="item?.id"
            />
          </el-select>
        </el-form-item>
        <FormItemUpload
          v-model="form.backgroundUrl"
          prop="backgroundUrl"
          :limit="1"
          title="背景图上传"
          :require="true"
          @change="backgroundUrlChange"
        ></FormItemUpload>
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
import { add, update } from '@/api/probot-carousel'
import { submitForm, resetForm } from '@/common/formMethod'
import FormItemUpload from '@/views/probot-mode/components/FormItemUpload.vue'
import { getProbotList } from '@/api/probot'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default() {
      return {}
    }
  },
  typeOptions: {
    type: Object,
    default() {
      return {}
    }
  },
  statusOptions: {
    type: Object,
    default() {
      return {}
    }
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
  botId: string
  title: string
  recommendReasons: Array<{
    value: string
  }>
  type: number | undefined
  displayStatus: number | undefined
  backgroundUrl: Array<string>
}

const formRef = ref<FormInstance>()
const form = reactive<RuleForm>({
  botId: '',
  title: '',
  recommendReasons: [],
  type: 1,
  displayStatus: undefined,
  backgroundUrl: []
})
const rules = reactive<FormRules<RuleForm>>({
  botId: [{ required: true, message: '请输入botId', trigger: 'blur' }],
  title: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  displayStatus: [{ required: true, message: '请选择状态', trigger: 'change' }],
  backgroundUrl: [{ required: true, message: '请上传背景图', trigger: 'change' }]
})
const loading = ref(false)
const botLoading = ref(false)
const botOptions = ref([])
const getRecommendReasons = (val) => {
  const arr = [{}, {}, {}]
  for (var i = 0; i < 3; i++) {
    arr[i] = {
      value: val[i] || ''
    }
  }
  return arr
}

watch(
  () => props.modelValue,
  (val) => {
    resetForm(formRef.value)
    if (val && props.data?.id) {
      form.botId = props.data?.botId
      form.title = props.data?.title
      form.recommendReasons = getRecommendReasons(props.data?.recommendReasons || [])
      form.type = props.data?.type
      form.displayStatus = props.data?.displayStatus
      form.backgroundUrl = [props.data?.backgroundUrl]
    } else {
      form.botId = ''
      form.title = ''
      form.recommendReasons = getRecommendReasons([])
      form.type = 1
      form.displayStatus = undefined
      form.backgroundUrl = []
    }
  }
)

const sure = () => {
  submitForm(formRef.value, form).then(() => {
    loading.value = true
    let request = props.data?.id ? update : add
    request({
      ...props.data,
      ...form,
      recommendReasons: form.recommendReasons.map((item) => item.value),
      backgroundUrl: form.backgroundUrl[0]
    })
      .then((data) => {
        if (data.data) {
          ElMessage.success(props.data?.id ? '编辑成功！' : '创建成功！')
          emits('onOk')
          dialogVisible.value = false
        } else {
          ElMessage.error(data.message || (props.data?.id ? '编辑失败！' : '创建失败！'))
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
const backgroundUrlChange = () => {
  formRef.value?.validateField('backgroundUrl')
}

const botRemoteMethod = (query: string) => {
  if (query) {
    botLoading.value = true
    getProbotList({
      name: query,
      pageSize: 1000,
      pageNum: 1
    }).then((res) => {
      botLoading.value = false
      botOptions.value = res?.data?.records
    })
  } else {
    botOptions.value = []
  }
}
</script>

<style lang="scss">
.carouse-dialog-container {
}
</style>

<template>
  <el-drawer v-model="drawer" title="发布" direction="rtl" size="50%" @open="handleOpen">
    <el-form
      ref="ruleFormRef"
      :model="ruleForm"
      :rules="rules"
      label-width="10px"
      class="probot-deploy-form"
      size="large"
      status-icon
      label-position="left"
    >
      <BaseGroup
        title="是否公开配置"
        tooltip="通过公开配置选项，用户可以看到你的Probot编排细节，更好地理解聊天助手的内部工作原理。这种透明度和可定制性有助于提高用户对聊天助手的信任度和满意度。"
        ><el-form-item label="" prop="permissions" label-position="left">
          <el-switch v-model="ruleForm.permissions" /> </el-form-item
      ></BaseGroup>

      <div class="config-group">
        <BaseGroup title="分类标签"
          ><el-form-item prop="categoryIds">
            <el-select
              v-model="ruleForm.categoryIds"
              placeholder="请选择分类标签"
              clearable
              multiple
            >
              <el-option
                v-for="(item, index) in categoryList['']"
                :key="index + item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select> </el-form-item
        ></BaseGroup>
      </div>
      <BaseGroup
        title="发布记录"
        :btn="{
          name: 'AI生成',
          icon: 'icon-AIshengcheng',
          click: optimize
        }"
      >
        <el-form-item prop="versionRecord">
          <el-input
            maxlength="1000"
            show-word-limit
            v-model="ruleForm.versionRecord"
            type="textarea"
            :autosize="{ minRows: 5 }"
            placeholder="请输入发布记录"
          /> </el-form-item
      ></BaseGroup>

      <BaseGroup
        title="发布平台"
        tooltip="使用第三方平台发布您的Probot，即表示您已充分理解并同意遵循该平台的服务条款。"
      >
        <div>
          <el-form-item prop="desc" label="" label-width="10px">
            <div class="card">
              <div class="card-left"><el-checkbox v-model="ruleForm.feishu"></el-checkbox></div>
              <div class="card-right">
                <div class="card-right-head">
                  <BaseIconPlatform :iconType="1"></BaseIconPlatform>
                  <h3 class="name">飞书</h3>
                  <el-link
                    type="primary"
                    :underline="false"
                    class="openId-link"
                    @click="showLetterOpenIdDialog"
                    >配置openId</el-link
                  >
                  <span class="openId-txt" v-if="letterOpenId">({{ letterOpenId }})</span>
                </div>
                <div class="desc">
                  <span>将机器人发布到飞书，在飞书里直接与之对话，提高沟通和协作效率</span>
                </div>
              </div>
            </div>
          </el-form-item>
        </div>
        <div>
          <el-form-item prop="desc" label="" label-width="10px">
            <div class="card">
              <div class="card-left"><el-checkbox v-model="ruleForm.weixin"></el-checkbox></div>
              <div class="card-right">
                <div class="card-right-head">
                  <BaseIconPlatform :iconType="2"></BaseIconPlatform>
                  <h3 class="name">微信公众号</h3>
                  <el-link
                    type="primary"
                    :underline="false"
                    class="openId-link"
                    @click="showOpenIdDialog"
                    >配置openId</el-link
                  >
                  <span class="openId-txt" v-if="openId">({{ openId }})</span>
                </div>
                <div class="desc"><span>在微信公众号里内嵌机器人，随时随地与之对话</span></div>
              </div>
            </div>
          </el-form-item>
        </div>
      </BaseGroup>
    </el-form>
    <template #footer>
      <div style="flex: auto">
        <el-button type="primary" @click="submitForm(ruleFormRef)" size="large"> 发 布 </el-button>
      </div>
    </template>
  </el-drawer>
  <!-- ai优化弹窗 -->
  <DeployOptimizeDialog
    v-model="optimizeDialogVisible"
    :data="ruleForm.versionRecord"
    @use="useOptimize"
  ></DeployOptimizeDialog>
  <!-- 微信-配置openId -->
  <DeployOpenIdDialog
    v-model="openIdVisible"
    :data="openId"
    @openidChange="openidChange"
  ></DeployOpenIdDialog>
  <!-- 飞书-配置openId -->
  <DeployLetterOpenIdDialog
    v-model="letterOpenIdVisible"
    :data="letterOpenId"
    @openidChange="letterOpenidChange"
  ></DeployLetterOpenIdDialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import BaseGroup from './BaseGroup.vue'
import BaseIconPlatform from '@/views/probot/components/BaseIconPlatform.vue'
import { publishBot } from '@/api/probot'
import { useProbotStore } from '@/stores/probot'
import DeployOptimizeDialog from './DeployOptimizeDialog.vue'
import DeployOpenIdDialog from './DeployOpenIdDialog.vue'
import DeployLetterOpenIdDialog from './DeployLetterOpenIdDialog.vue'

const emits = defineEmits(['update:modelValue', 'update'])

const props = defineProps<{
  modelValue: boolean
  data: Object
}>()

const drawer = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emits('update:modelValue', value)
  }
})

const probotStore = useProbotStore()

const categoryList = computed(() => probotStore.categoryList)
const optimizeDialogVisible = ref(false) // ai 优化
const openIdVisible = ref(false)
const openId = ref('')
const letterOpenIdVisible = ref(false)
const letterOpenId = ref('')

interface RuleForm {
  permissions: boolean
  publishImChannel: number[]
  versionRecord: string
  categoryIds: number[]
  m78: boolean
  weixin: boolean
  feishu: boolean
}

const ruleFormRef = ref<FormInstance>()
const ruleForm = reactive<RuleForm>({
  permissions: false,
  publishImChannel: [],
  versionRecord: '',
  categoryIds: [],
  m78: false,
  weixin: false,
  feishu: false
})

const rules = reactive<FormRules<RuleForm>>({
  categoryIds: [{ required: true, message: '请至少选择一个标签', trigger: 'change' }],
  versionRecord: [
    { required: true, message: '请输入发布记录', trigger: 'change' },
    { min: 3, max: 1000, message: '发布记录字数在3到1000', trigger: 'blur' }
  ]
})

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      const items: number[] = []
      if (ruleForm.feishu) items.push(1)
      if (ruleForm.weixin) items.push(2)
      if (ruleForm.weixin && !openId.value) {
        ElMessage.error('选择微信请配置openId')
        return false
      }
      publishBot({
        botId: probotStore.createdRobtId,
        categoryIds: ruleForm.categoryIds,
        versionRecord: ruleForm.versionRecord,
        publishImChannel: items,
        permissions: ruleForm.permissions ? 1 : 0,
        openId: JSON.stringify({
          anonymousLetter: ruleForm.feishu ? letterOpenId.value : '',
          weChat: ruleForm.weixin ? openId.value : ''
        })
      }).then(
        ({ code, message }) => {
          if (code === 0) {
            emits('update')
            drawer.value = false
            ElMessage.success('成功')
          } else {
            ElMessage.error(message || '失败')
          }
        },
        (e) => {
          console.error('e', e)
          ElMessage.error('失败')
        }
      )
    } else {
      console.error('error submit!', fields)
    }
  })
}
const openidChange = (val: string) => {
  openId.value = val
}
const letterOpenidChange = (val: string) => {
  letterOpenId.value = val
}
const optimize = () => {
  optimizeDialogVisible.value = true
}
const useOptimize = (data: string) => {
  ruleForm.versionRecord = data
}
const showLetterOpenIdDialog = () => {
  letterOpenIdVisible.value = true
}
const showOpenIdDialog = () => {
  openIdVisible.value = true
}
const handleOpen = () => {
  const { publishRecordDTOS = [], botCategory = [], botInfo = {} } = props.data
  ruleForm.permissions = botInfo.permissions ? true : false
  ruleForm.categoryIds = probotStore.categoryList['']
    ?.filter((item) => botCategory.some((item2) => item2 == item.name))
    ?.map((item) => item.id)
  if (publishRecordDTOS?.length) {
    const val = publishRecordDTOS[0]
    ruleForm.publishImChannel = []
    ruleForm.versionRecord = val.versionRecord
    ruleForm.weixin = val.publishImChannel.indexOf('2') > -1 ? true : false
    ruleForm.feishu = val.publishImChannel.indexOf('1') > -1 ? true : false
  } else {
    ruleForm.publishImChannel = []
    ruleForm.versionRecord = ''
    ruleForm.weixin = false
    ruleForm.feishu = false
  }
}
</script>

<style lang="scss" scoped>
.probot-deploy-form {
  :deep(.oz-select) {
    width: 100%;
  }
  :deep(.oz-tag.oz-tag--info),
  :deep(.oz-tag .oz-tag__close) {
    color: #666;
  }
  .card {
    display: flex;
    &-left {
      padding-right: 10px;
    }
    &-right {
      &-head {
        display: flex;
        align-items: center;
        .name {
        }
        .openId-link {
          margin: 0 4px 0 14px;
        }
        .openId-txt {
          font-size: 12px;
          color: rgb(107, 114, 128);
        }
      }
      .desc {
        font-size: 14px;
        line-height: 24px;
        font-weight: 500;
        color: rgb(107, 114, 128);
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
}
</style>

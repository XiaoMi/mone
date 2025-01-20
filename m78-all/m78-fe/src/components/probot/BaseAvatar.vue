<!--
 * @Description:
 * @Date: 2024-03-19 11:11:46
 * @LastEditTime: 2024-09-11 19:22:25
-->
<template>
  <el-form-item label="图标：" prop="avatarUrl">
    <div :class="'icon-container' + (props.disabled ? ' disabled' : '')">
      <BaseIconUpload
        :index="avatarUrl"
        :base64="base64Img"
        @upload="onUploadBotAvatar"
        @error="onUploadBotAvatarError"
        :disabled="props.disabled || generating"
      ></BaseIconUpload>
      <div class="generate" v-if="false">
        <el-tooltip :content="props.tips" placement="bottom" effect="light">
          <div :class="generateAvatarClassName" @click="generateAvatar">
            <i class="iconfont icon-AIshengcheng"></i>
            <span v-if="generating">生成中</span>
            <span v-else>普通生成</span>
          </div>
        </el-tooltip>
        <el-tooltip
          :content="props.tips + '(耗时较长，预计30s到1分钟)'"
          placement="bottom"
          effect="light"
        >
          <div
            :class="generateAvatarClassName"
            @click="generateAvatar({ type: 'wanx' })"
            style="margin-left: 10px"
          >
            <i class="iconfont icon-AIshengcheng"></i>
            <span v-if="generating">生成中</span>
            <span v-else>精美生成</span>
          </div>
        </el-tooltip>
      </div>
    </div>
  </el-form-item>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import BaseIconUpload from '@/components/BaseIconUpload.vue'
import { createBotAvatar, uploadBotAvatar } from '@/api/probot'
import { generateImage, uploadPluginImg } from '@/api/plugins'

const props = defineProps({
  tips: {
    type: String,
    default: '输入Probot 名称和介绍后，点击自动生成头像。'
  },
  modelValue: {
    type: String,
    default: ''
  },
  remark: {
    type: String,
    default: ''
  },
  id: {
    type: String,
    default: ''
  },
  name: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  type: {
    type: String,
    default: 'bot'
  }
})
const emits = defineEmits(['update:modelValue'])

const avatarUrl = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const generating = ref(false)
const base64Img = ref('')
const botId = ref<number | null | string>(null)

const generateAvatarClassName = computed(() => {
  return {
    'create-icon': true,
    'create-icon-disabled': generating.value
  }
})

const generateAvatar = async (params?: { type: string }) => {
  if (generating.value || props.disabled) return
  try {
    generating.value = true
    let response = {} as any
    const data = {
      name: props.name,
      desc: props.remark,
      ...params
    }
    if (props.type === 'bot') {
      response = await createBotAvatar(data)
    } else {
      response = await generateImage(data)
    }
    if (response.code === 0) {
      base64Img.value = response.data.base64
      avatarUrl.value = response.data.url
    } else {
      ElMessage.error(response.message || '出错了')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('出错了')
  } finally {
    generating.value = false
  }
}

const onUploadBotAvatar = async (base64: string) => {
  base64Img.value = base64
  try {
    let response = {} as any
    if (props.type === 'bot') {
      response = await uploadBotAvatar({
        base64: base64,
        botId: botId.value
      })
    } else {
      response = await uploadPluginImg({
        base64: base64
      })
    }

    if (response.code === 0) {
      avatarUrl.value = response.data
    } else {
      console.error(response.message)
      ElMessage.error(response.message || '出错了')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('出错了')
  }
}

const onUploadBotAvatarError = async (e: Error) => {
  console.error(e)
  ElMessage.error('出错了')
}

watch(
  () => props.id,
  (id) => {
    botId.value = id
    if (typeof avatarUrl.value === 'string') {
      base64Img.value = avatarUrl.value
    } else if (props.type === 'plugin') {
      base64Img.value = props.modelValue
    }
  },
  {
    deep: true,
    immediate: true
  }
)
</script>

<style scoped lang="scss">
.icon-container {
  display: flex;
  width: 100%;
  padding-bottom: 10px;
  &.disabled {
    .create-icon {
      cursor: no-drop !important;
    }
  }

  .generate {
    flex: 1;
    height: 100px;
    align-items: center;
    background-color: #f0f0f5;
    border-radius: 8px;
    box-sizing: border-box;
    display: flex;
    margin-left: 16px;
    padding: 14px 13px;
    .create-icon {
      align-items: center;
      background-color: #f7f7fa;
      border: 1px solid rgba(29, 28, 35, 0.12);
      border-radius: 8px;
      color: #4d53e8;
      cursor: pointer;
      display: flex;
      flex-direction: column;
      height: 68px;
      justify-content: center;
      width: 68px;

      &-disabled {
        opacity: 0.8;
        cursor: not-allowed;
      }

      &:hover {
        border: 1px solid rgba(29, 28, 35, 0.2);
      }
    }
    .iconfont {
      font-size: 16px;
      line-height: 20px;
    }
    span {
      font-size: 12px;
      line-height: 20px;
    }
  }
}
</style>

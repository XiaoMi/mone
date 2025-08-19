<!--
 * @Description: v
 * @Date: 2024-08-14 17:19:32
 * @LastEditTime: 2024-08-14 22:21:42
-->
<template>
  <div class="api-code-container">
    <div class="head">
      <h1>{{ props.data.title }}</h1>
      <el-button type="primary" plain size="small" :icon="DocumentCopy" @click.stop="handleCopy" ></el-button>
    </div>
    <div class="methods-container" v-if="props.data.methods">
      <span class="methods">{{ props.data.methods }}</span>
      <span class="url">{{ props.data.url }}</span>
    </div>
    <div class="main">{{ props.data.main }}</div>
  </div>
</template>

<script setup lang="ts">
import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'
import { DocumentCopy } from '@element-plus/icons-vue'
import { t } from '@/locales'

const { toClipboard } = useClipboard()

const props = defineProps({
  data: {
    type: Object,
    default() {
      return {}
    }
  }
})

const handleCopy = () => {
  return toClipboard(props.data.main)
    .then(() => {
      ElMessage.success(t('common.copySuccess'))
    })
    .catch(() => {
      ElMessage.error(t('common.copyError'))
    })
}
</script>

<style scoped lang="scss">
.api-code-container {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  background-color: #000;
  border-radius: 10px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
  .head {
    width: 100%;
    line-height: 30px;
    font-size: 16px;
    font-weight: bold;
    color: #fff;
    padding: 10px 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .methods-container {
    padding: 10px 20px;
    width: 100%;
    line-height: 30px;
    font-size: 14px;
    color: #fff;
    border-top: 1px solid #333;
    .methods {
      font-size: 12px;
      color: rgb(56, 189, 248);
    }
    .url {
      padding-left: 10px;
      font-size: 12px;
    }
  }
  .main {
    width: 100%;
    overflow: auto;
    font-size: 14px;
    color: #fff;
    line-height: 30px;
    white-space: pre-wrap;
    word-break: break-all;
    border-top: 1px solid #333;
    padding: 10px 20px;
    font-family:
      ui-monospace,
      SFMono-Regular,
      Menlo,
      Monaco,
      Consolas,
      Liberation Mono,
      Courier New,
      monospace;
  }
}
</style>

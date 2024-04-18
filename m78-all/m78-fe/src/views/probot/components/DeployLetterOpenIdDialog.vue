<!--
 * @Description:
 * @Date: 2024-03-27 20:33:08
 * @LastEditTime: 2024-03-27 20:49:05
-->
<template>
  <el-dialog v-model="dialogVisible" title="配置飞书openId" width="500" @open="open">
    <div class="openid-container">
      <div class="openid-content">
        <span class="openid-content-title">open id :</span>
        <el-input v-model="openid" style="flex: 1" />
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="sure"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElButton, ElDialog } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: String,
    default: ''
  }
})
const emits = defineEmits(['update:modelValue', 'openidChange'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const openid = ref(props.data)
const open = () => {
  openid.value = props.data
}
const sure = () => {
  emits('openidChange', openid.value)
  dialogVisible.value = false
}
</script>

<style scoped lang="scss">
.openid-container {
  .openid-content {
    display: flex;
    align-items: center;
    padding: 0 20px 20px 20px;
    justify-content: space-between;
    &-title {
      padding-right: 10px;
    }
  }
  .openid-describe {
    border-top: 1px solid #ddd;
    padding: 20px 20px;
    dd {
      display: flex;
      line-height: 20px;
      padding-top: 20px;
    }
    img {
      height: 200px;
      vertical-align: top;
    }
  }
}
</style>

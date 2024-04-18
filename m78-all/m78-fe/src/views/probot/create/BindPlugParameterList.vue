<!--
 * @Description:
 * @Date: 2024-03-06 19:26:26
 * @LastEditTime: 2024-03-15 20:22:07
-->
<template>
  <el-dialog v-model="dialogVisible" width="500" title="参数列表">
    <div class="bind-dialog-content" v-if="parameterData">
      <div class="parameter-module">
        <h3>请求头</h3>
        <dl>
          <dt>
            <span>键</span>
            <span>值</span>
          </dt>
          <dd v-for="(item, key) in parameterData?.http_headers" :key="key">
            <span> {{ key }}</span>
            <span> {{ item }}</span>
          </dd>
        </dl>
      </div>
      <div class="parameter-module">
        <h3>入参</h3>
        {{ parameterData?.input }}
        <dl>
          <dt>
            <span>参数名称</span>
            <span>描述</span>
          </dt>
          <dd v-for="(item, key) in parameterData?.input" :key="key">
            <span> {{ item.name }}</span>
            <span> {{ item.desc }}</span>
          </dd>
        </dl>
      </div>
      <div class="parameter-module">
        <h3>出参</h3>
        <dl>
          <dt>
            <span>参数名称</span>
            <span>描述</span>
          </dt>
          <dd v-for="(item, key) in parameterData?.output" :key="key">
            <span> {{ item.name }}</span>
            <span> {{ item.desc }}</span>
          </dd>
        </dl>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElDialog } from 'element-plus'

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
const emits = defineEmits(['update:modelValue'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const parameterData = ref()

watch(
  () => props.data,
  (val) => {
    parameterData.value = val
  }
)
</script>

<style lang="scss">
.bind-dialog-content {
  .parameter-module {
    padding-bottom: 10px;
    h3 {
      font-size: 16px;
      line-height: 24px;
      padding: 10px 4px;
      color: rgb(71, 85, 105);
    }
    dl {
    }
    dt,
    dd {
      display: flex;
      align-items: center;
      justify-content: space-between;
      span {
        font-size: 14px;
        line-height: 20px;
        padding: 6px 0 6px 6px;
        width: 50%;
        color: rgb(71, 85, 105);
      }
    }
    dt {
      span {
        background-color: rgba(0, 0, 0, 0.06);
      }
    }
    dd {
      border-top: 1px solid #eee;
      span {
      }
    }
  }
}
</style>

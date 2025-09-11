<template>
  <div class="mode-container">
    <div class="left">
      <div class="form-container">
        <div class="form-content">
          <slot name="left" />
        </div>
        <div class="form-btn">
          <el-button
            type="primary"
            @click="emits('onSubmit')"
            color="#262845"
            :loading="props.loading"
            >立即生成</el-button
          >
        </div>
      </div>
    </div>
    <div class="center">
      <div class="center-container" v-if="curItem">
        <template v-if="Object.keys(curItem).length == 0">
          <EmptyBox />
        </template>
        <template v-else>
          <!-- 运行中 -->
          <LoadingBox v-if="curItem.runStatus == 0" :loading="curItem.runStatus == 0" />
          <template v-else>
            <!-- 运行失败 -->
            <template v-if="curItem.runStatus == 2 && curItem.rstMessage">
              <div class="div600 flex-center">
                <div class="err-msg">错误原因：{{ curItem.rstMessage }}</div>
              </div>
            </template>
            <!-- 运行成功 -->
            <template v-else>
              <CenterImgs
                v-if="props.type == 'img'"
                :list="curItem.multiModalResourceOutput"
                :status="curItem.runStatus"
              />
              <TextItem v-else :text="curItem.multiModalResourceOutput[0]" />
            </template>
          </template>
        </template>
      </div>
    </div>
    <div class="right">
      <History ref="hisRef" @setActiveItem="setActive" :type="historyType" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useSlots, defineProps, watch, ref, computed, onUnmounted } from 'vue'
import ImgItem from './components/ImgItem.vue'
import MultipleImg from './components/MultipleImg.vue'
import TextItem from './components/TextItem.vue'
import LoadingBox from './components/LoadingBox.vue'
import EmptyBox from './components/EmptyBox.vue'
import CenterImgs from './components/CenterImgs.vue'
import History from '@/views/probot-mode/components/history/History.vue'
import { getTaskInfo } from '@/api/probot-mode.ts'
import { ElMessage } from 'element-plus'

const mulImgRef = ref(null)

const emits = defineEmits(['onSubmit', 'changeHistory'])
const hisRef = ref(null)
const props = defineProps({
  type: {
    default: 'text'
  },
  loading: {
    default: false
  },
  historyType: {},
  apiName: {}
})
const curItem = ref({})
const setActive = (val) => {
  curItem.value = val
  emits('changeHistory', curItem.value)
}
const timer = ref(false)
const submitFn = async (params) => {
  hisRef.value.initList({
    multiModalResourceOutput: [],
    runStatus: 0,
    setting: params
  })
  //  关闭上一次未完成的请求
  clearTimeout(timer.value)
  const res = await props.apiName(params)
  const { code, data } = res
  if (code == 0) {
    // 数据提交成功
    loopNum.value = 0
    loopFn(data)
  } else {
    // 失败了
    const msg = res.message
    const errObj = {
      runStatus: 2,
      rstMessage: msg
    }
    // 更新历史数据
    hisRef.value.updateFirst(errObj)
    curItem.value = errObj
    ElMessage.error(msg || 'error')
  }
}
const loopNum = ref(0)
const loopFn = async (taskId) => {
  const { data } = await getTaskInfo({ taskId })
  const { runStatus } = data
  clearTimeout(timer.value)
  //  成功
  if (runStatus != 0) {
    curItem.value = data
    hisRef.value.updateFirst(data)
  } else {
    // runStatus 0 进行中 2 失败  1成功
    if (loopNum.value > 20) {
      return
    }
    timer.value = setTimeout(() => {
      loopNum.value = loopNum.value + 1
      loopFn(taskId)
    }, 1000 * 10)
  }
}
onUnmounted(() => {
  clearTimeout(timer.value)
})
// 暴露方法给父组件
defineExpose({
  submitFn
})
</script>

<style lang="scss" scoped>
.mode-container {
  flex: 1;
  display: flex;
}
.left {
  width: 350px;
  background: #fff;
  padding: 10px 0;

  .form-container {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  .form-content {
    flex: 1;
    overflow: auto;
    padding: 0 10px;
  }
  .form-btn {
    text-align: right;
    padding-bottom: 10px;
    padding-right: 20px;
    padding: 10px 20px 10px 0;
  }
}
.center {
  flex: 1;
  background: #fff;
  padding: 10px;
  border-left: 1px solid #eee;
  .center-container {
    padding: 10px;
    height: 100%;
    box-shadow:
      (0 0 #0000, 0 0 #0000),
      (0 0 #0000, 0 0 #0000),
      0 10px 15px -3px rgba(0, 0, 0, 0.1),
      0 4px 6px -4px rgba(0, 0, 0, 0.1);
  }
}
.right {
  border-left: 1px solid #eee;
  // width: 150px;
  background: #fff;
  // padding: 10px;
  .right-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    h2 {
      font-size: 14px;
    }
  }
}

.center-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.tips {
  display: flex;
  justify-content: center;
  align-items: center;
}

.smile-img {
  width: 70px;
}
.div600 {
  width: 600px;
  height: 600px;
  border: dashed 1px #dcdfe6;
  padding: 10px;
  font-size: 16px;
  line-height: 25px;
  overflow-y: auto;
  /* word-break: break-all; */
}
.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}
.err-msg {
  color: #f89898;
  font-size: 12px;
  // width: 100px;
}
</style>

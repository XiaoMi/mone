<!--
 * @Description: 
 * @Date: 2024-03-20 15:59:51
 * @LastEditTime: 2024-04-16 14:25:37
-->
<template>
  <el-drawer v-model="drawer" title="知识检索" direction="rtl" size="50%">
    <!-- 头部 -->
    <div class="ask-input-container">
      <el-input
        v-model="askInput"
        placeholder="请输入知识进行检索"
        clearable
        @keyup.enter="searchData"
        @clear="searchData"
      >
        <template #suffix>
          <el-icon class="el-input__icon" @click="searchData"><Search /></el-icon>
        </template>
      </el-input>
    </div>
    <!-- 主要内容 -->
    <div class="main-content">
      <div v-if="loading">
        <div class="serach-tip">
          <el-icon class="right-icon"><Check /></el-icon>正在搜索：<b>{{ askInput }}</b>
        </div>
        <div class="serach-tip">
          <el-icon class="right-icon"><Check /></el-icon>正在为你生成答案
          <div class="loading">
            <div class="item"></div>
            <div class="item"></div>
            <div class="item"></div>
            <div class="item"></div>
            <div class="item"></div>
            <div class="item"></div>
            <div class="item"></div>
            <div class="item"></div>
          </div>
        </div>
      </div>
      <!-- 新内容 -->
      <ul v-else-if="answerData?.length">
        <li v-for="(item, index) in answerData" :key="index">
          <MarkdownMessage
            :message="item?.content"
            v-if="item?.content"
            class="markdown-container"
          ></MarkdownMessage>
          <el-empty description="未匹配到数据" v-else />
        </li>
      </ul>
      <div v-else-if="errorMsg">
        <MarkdownMessage :message="errorMsg" class="markdown-container"></MarkdownMessage>
      </div>
      <el-empty description="请输入知识进行检索" v-else />
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import { ref, watch, computed } from 'vue'
import { Search, Check } from '@element-plus/icons-vue'
import { queryKnowledge } from '@/api/probot-knowledge'
import { useRoute } from 'vue-router'
import MarkdownMessage from '@/components/common/markdown-message/index.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  }
})

const emits = defineEmits(['update:modelValue'])

const drawer = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
/**  data */
const askInput = ref()
const loading = ref(false)
const answerData = ref([]) //表格数据
const route = useRoute()
const errorMsg = ref('')

/**  methods */
// 获取表格数据
const getData = (): void => {
  const {
    params: { knowledgeBaseId }
  } = route
  if (askInput.value) {
    loading.value = true
    answerData.value = []
    errorMsg.value = ''
    queryKnowledge({
      queryText: askInput.value || '',
      knowledgeId: knowledgeBaseId
    })
      .then((res: any) => {
        if (res.data) {
          answerData.value = res.data
        } else {
          errorMsg.value = res.message
        }
      })
      .catch((error) => {
        errorMsg.value = error.message
      })
      .finally(() => {
        loading.value = false
      })
  } else {
    answerData.value = []
  }
}
//搜索数据
const searchData = () => {
  getData()
}

//监听路由
watch(
  () => route.path,
  () => {
    answerData.value = []
    askInput.value = ''
  },
  {
    immediate: true,
    deep: true
  }
)
</script>
<style lang="scss">
.markdown-container {
  flex: 1;
  overflow: auto;
  background-color: #f7f7f7;
  margin-bottom: 10px;
}
.ask-input-container {
  display: flex;
  padding: 20px 20px;
  background-color: #fff;
  .el-input__icon {
    cursor: pointer;
    &:hover {
      color: #1890ff;
    }
  }
}
.serach-tip {
  font-size: 14px;
  display: flex;
  align-items: center;
  position: relative;
  color: rgb(96, 98, 102);
  line-height: 28px;
}
.right-icon {
  font-size: 22px;
  color: #67c23a;
  padding-right: 6px;
}

/* 设置位置 */
.loading {
  position: absolute;
  /* 居中 */
  top: 50%;
  transform: translate(140px, -50%);
  /* 高度 */
  height: 40px;
  /* 弹性布局 */
  display: flex;
  /* 设置子项在y轴方向居中，应该是设置起点在中间，非常有用，不然动画很怪 */
  align-items: center;
}

/* 小竖条 */
.item {
  height: 6px;
  width: 2px;
  background: #ddd;
  /* 加margin，使竖条之间有空隙 */
  margin: 0px 2px;
  /* 圆角 */
  border-radius: 5px;
  /* 动画：名称、时间、循环 */
  animation: loading 1s infinite;
}

/* 设置动画 */
@keyframes loading {
  0% {
    height: 0px;
  }

  50% {
    height: 6px;
  }

  100% {
    height: 0px;
  }
}

/* 为每一个竖条设置延时 */
.item:nth-child(2) {
  animation-delay: 0.1s;
}

.item:nth-child(3) {
  animation-delay: 0.2s;
}

.item:nth-child(4) {
  animation-delay: 0.3s;
}

.item:nth-child(5) {
  animation-delay: 0.4s;
}

.item:nth-child(6) {
  animation-delay: 0.5s;
}

.item:nth-child(7) {
  animation-delay: 0.6s;
}

.item:nth-child(8) {
  animation-delay: 0.7s;
}
</style>

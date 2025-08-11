<template>
  <div class="history-wrap" :class="selecting ? 'selecting-list-box' : ''">
    <div class="history" v-if="historyList.length > 0">
      <div class="top">
        <h2 class="his-title">历史记录</h2>
        <div class="sel-btn" :class="selecting ? 'active' : ''" @click="switchSel">
          <i class="iconfont icon-list-select"></i>
        </div>
      </div>
      <ul class="ul-box" v-infinite-scroll="loadMore">
        <li
          v-for="(item, index) in historyList"
          :key="index"
          class="img-wrap"
          :class="index == active ? 'active' : ''"
          @click="clickActive(index)"
        >
          <div class="wrap">
            <div class="loading-box" v-if="[0, 2].includes(item.runStatus)">
              <NotSucImg :status="item.runStatus" :item="item" />
            </div>
            <template v-else>
              <template v-if="item.multiModalResourceOutput">
                <div
                  class="shadow"
                  v-if="item.multiModalResourceOutput?.length > 1 && index == active"
                ></div>
                <template v-if="showContent == 'img'">
                  <img
                    :src="item.multiModalResourceOutput[0] || EmptyImg"
                    alt=""
                    class="img-item"
                  />
                </template>

                <template v-if="showContent == 'text'">
                  <div class="text-box">
                    {{ item.multiModalResourceOutput[0] }}
                  </div>
                </template>
                <i
                  v-if="item.multiModalResourceOutput?.length > 1 && index == active"
                  class="img-num"
                  >{{ item.multiModalResourceOutput.length }}</i
                >
              </template>
            </template>
            <ItemRadio v-if="selecting" v-model="item.checked" />
          </div>
        </li>
      </ul>
    </div>
    <div class="del-box" v-if="selecting">
      <el-button type="danger" class="del-btn" @click="delFn" :loading="delLoading">删除</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, defineProps, defineExpose, onUnmounted, nextTick } from 'vue'
import { getHistory, getHisDetails, delHis } from '@/api/probot-mode.ts'
import loadingImg from '@/assets/loading.gif'
import { ElMessage } from 'element-plus'
import NotSucImg from './NotSucImg.vue'
import EmptyImg from '@/assets/empty.png'
import ItemRadio from './ItemRadio.vue'

const emits = defineEmits(['setActiveItem'])
const historyList = ref([])
const active = ref(-1)
const props = defineProps({
  type: {
    type: Number,
    default: 3
  },
  showContent: {
    default: 'img'
  },
  // 是否正在生成
  gening: {
    type: Boolean,
    default: false
  }
})
const clickActive = (val) => {
  setActive(val)
}
const setActive = (val: any) => {
  active.value = val
  const emptyVal = {}
  emits('setActiveItem', historyList.value[val] || emptyVal)
}

const regParams = ref({
  pageNum: 1,
  pageSize: 20
})
const loading = ref(false)
const noMore = ref(true)
const getList = async () => {
  try {
    loading.value = true
    const { data } = await getHistory({ ...regParams.value, type: props.type })
    const list = data?.list || []
    loading.value = false
    // 将每个元素的newList的multiModalResourceOutput(数组)中每个元素如果是''过滤掉
    const transformedList = list.map((item) => {
      const imgUrls = item.multiModalResourceOutput || []
      const multiModalResourceOutput = imgUrls.filter((imgUrl) => imgUrl)
      return {
        ...item,
        multiModalResourceOutput
      }
    })
    const newList = [...historyList.value, ...transformedList]
    noMore.value = list.length < regParams.value.pageSize ? true : false
    historyList.value = newList
    if (regParams.value.pageNum == 1) {
      clearTimeFn()
      judgeLoopFn(getFailedIds(historyList.value))
    }
  } catch (error) {
    console.log('error', error)
    loading.value = false
  }
}

const loadMore = () => {
  if (loading.value || noMore.value) return
  regParams.value.pageNum += 1
  getList()
}

const clearTimeFn = () => {
  clearTimeout(timer.value)
  loopNum.value = 0
}

const getFailedIds = (arr) => {
  return arr.filter((item) => item.runStatus == 0 && item.id)
}

const judgeLoopFn = (failedIds) => {
  if (failedIds.length == 0) return
  const ids = failedIds.map((item) => item.id)
  loopFn(ids)
}
const loopNum = ref(0)
const timer = ref(null)
// 我要定义一个loopFn，参数是ids(是个数组)； 每隔10秒调用一下getHisDetails这个方法，获取到详情
const loopFn = (ids) => {
  if (loopNum.value > 20) {
    return
  }
  timer.value = setTimeout(async () => {
    try {
      loopNum.value = loopNum.value + 1
      const { data } = await getHisDetails(ids)
      data.forEach((ele) => {
        const toChangeIndex = historyList.value.findIndex((hisItem) => hisItem.id == ele.id)
        historyList.value[toChangeIndex] = ele
      })
      clearTimeout(timer.value)
      judgeLoopFn(getFailedIds(data))
    } catch (error) {
      console.log('err', error)
    }
  }, 10000)
}

const updateHistory = (item) => {
  const index = historyList.value.findIndex((item) => item.id === item.id)
  if (index > -1) {
    historyList.value[index] = item
  } else {
    historyList.value.unshift(item)
  }
}
const updateFirst = (val) => {
  historyList.value[0] = val
}
const initList = (val) => {
  historyList.value = [val]
  setActive(0)
  regParams.value.pageNum = 1
  getList()
}
const selecting = ref(false)
const switchSel = () => {
  selecting.value = !selecting.value
}
const delLoading = ref(false)
const delFn = async () => {
  delLoading.value = true
  const delItems = historyList.value.filter((item) => item.checked && item.id)
  const ids = delItems.map((item) => item.id)
  if (ids.length == 0) {
    ElMessage.warning('请选择要删除的选项！')
    return
  }
  setActive(-1)

  const res = await delHis(ids)
  delLoading.value = false
  afterDel(res.data || [])
}
const afterDel = (res) => {
  // 根据成功结果删除选中数据
  const newList = historyList.value.filter((item) => !res.includes(item.id))
  setListReset(newList)
}
const setListReset = (newList) => {
  switchSel()
  historyList.value = newList.map((item) => {
    return {
      ...item,
      checked: false
    }
  })
}
// 使用 mousedown 事件代替 click 事件。mousedown 事件在鼠标按下时就会触发，而不是在释放时。这通常可以捕获到下拉选项的选择操作。
const handleGlobalClick = (event: MouseEvent) => {
  // 使用 nextTick 确保 DOM 已更新
  nextTick(() => {
    // 添加一个小的延时
    setTimeout(() => {
      const historyWrap = document.querySelector('.history-wrap')
      if (historyWrap && !historyWrap.contains(event.target as Node)) {
        if (selecting.value) {
          setListReset(historyList.value)
        }
      }
    })
  })
}
// 暴露方法给父组件
defineExpose({
  initList,
  updateHistory,
  updateFirst
})
onMounted(() => {
  getList()
  document.addEventListener('mousedown', handleGlobalClick)
})
onUnmounted(() => {
  clearTimeout(timer.value)
  document.removeEventListener('mousedown', handleGlobalClick)
})
</script>

<style scoped lang="scss">
.history-wrap {
  height: 100%;
  width: 100%;
  overflow: hidden;
  position: relative;
  &.selecting-list-box {
    padding-bottom: 43px;
  }
  .del-box {
    position: absolute;
    bottom: 0px;
    padding: 5px 10px;
    background: #fff;
    width: 100%;
    border-top: solid 1px #eee;
    .del-btn {
      width: 100%;
    }
  }
}
.history {
  height: 100%;
  background: #fff;
  // overflow-y: auto;
}

.img-item {
  width: 100%;
  height: 100%;
  -o-object-fit: cover;
  object-fit: cover;
}
.img-wrap {
  position: relative;
  width: 134px;
  height: 134px;
  cursor: pointer;
  margin-top: 15px;

  .wrap {
    height: 100%;
    width: 100%;
    overflow: hidden;
    border-radius: 10px;
    border: solid 3px #eee;
  }
  .img-num {
    position: absolute;
    right: 0;
    bottom: 0;
    padding: 2px 5px;
    font-size: 12px;
    background: #69696f;
    border-radius: 4px;
    color: #fff;
  }
  .shadow {
    position: absolute;
    top: -5px;
    height: 5px;
    width: calc(100% - 14px);
    background-image: linear-gradient(to right, #fddb92 0%, #b6d8da 25%, #b6d8da 75%, #fddb92 100%);
    left: 50%;
    transform: translate(-50%, 0);
    border-radius: 4px 4px 0 0;
  }
}
.active .wrap {
  border-color: #69696f;
}
.loading-box {
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.text-box {
  padding: 3px 6px;
  display: -webkit-box;
  -webkit-line-clamp: 7; /* 指定显示的行数 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  /* 其他样式 */
  width: 124px; /* 指定容器宽度 */
  line-height: 1.5;
  word-break: break-all;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-bottom: solid 1px #eee;
  .his-title {
    font-size: 14px;
  }
}
.sel-btn {
  padding: 2px 4px;
  border: solid 1px #333333;
  border-radius: 4px;
  cursor: pointer;
  .iconfont {
    margin-right: 0;
    font-size: 12px;
  }
  &.active {
    color: #3ca9ff;
    border-color: #3ca9ff;
  }
}
.ul-box {
  height: calc(100% - 42px);
  overflow-y: auto;
  overflow-x: hidden;
  padding: 0 10px 10px;
  &::-webkit-scrollbar {
    width: 6px;
  }
  /* 滑块颜色 */
  &::-webkit-scrollbar-thumb {
    background-color: #d5d5d5;
    border-radius: 10px;
  }
}
</style>

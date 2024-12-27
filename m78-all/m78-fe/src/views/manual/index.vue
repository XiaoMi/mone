<!--
 * @Description: 
 * @Date: 2024-01-23 19:56:01
 * @LastEditTime: 2024-01-29 17:37:37
-->
<template>
  <div class="manual-wrap">
    <div class="manual-container">
      <div class="manual-left">
        <el-affix :offset="61" target=".manual-container">
          <el-menu :default-openeds="defaultOpeneds" router :default-active="route.path">
            <MenuItem :data="data" :path="route.path"></MenuItem>
          </el-menu>
        </el-affix>
      </div>
      <div class="manual-center">
        <div class="breadcrumb-container">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/manual' }">文档</el-breadcrumb-item>
            <el-breadcrumb-item v-if="pathArr[0]" :to="{ path: '/manual/' + pathArr[0] }">{{
              data[pathArr[0]]?.title
            }}</el-breadcrumb-item>
            <el-breadcrumb-item
              v-if="pathArr[1]"
              :to="{ path: '/manual/' + pathArr[0] + '-' + pathArr[1] }"
              >{{ data[pathArr[0]]?.children[pathArr[1]]?.title }}</el-breadcrumb-item
            >
            <el-breadcrumb-item v-if="pathArr[2]">{{
              data[pathArr[0]]?.children[pathArr[1]]?.children[pathArr[2]]?.title
            }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <ContentItem :data="contentData" :path="route.path"></ContentItem>
      </div>
      <div class="manual-right"></div>
    </div>
    <div class="manual-bottom"><CommonFoot></CommonFoot></div>
  </div>
</template>

<script setup lang="ts">
import { reactive, shallowRef, onMounted, nextTick, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import MenuItem from './components/MenuItem.vue'
import ContentItem from './components/ContentItem.vue'
import Code1 from './components/Code1.vue'
import Code2 from './components/Code2.vue'
import Document1 from './components/Document1.vue'
import Document2 from './components/Document2.vue'
import Document3 from './components/Document3.vue'
import Document4 from './components/Document4.vue'
import Chat1 from './components/Chat1.vue'
import Translate1 from './components/Translate1.vue'
import Translate2 from './components/Translate2.vue'
import Translate3 from './components/Translate3.vue'
import Translate4 from './components/Translate4.vue'
import Translate5 from './components/Translate5.vue'
import CommonFoot from '@/components/CommonFoot.vue'

const route = useRoute()
const manualData = reactive([
  {
    title: 'M78星云使用文档'
  },
  {
    title: '项目地址'
  },
  {
    title: '核心功能',
    children: [
      {
        title: '一、AI Code 编程助手',
        children: [
          {
            title: '功能介绍',
            componentName: shallowRef(Code1)
          },
          {
            title: '插件配置',
            componentName: shallowRef(Code2)
          }
        ]
      },
      {
        title: '二、AI Document 文件助手',
        componentName: shallowRef(Document1),
        children: [
          {
            title: '文档上传',
            componentName: shallowRef(Document2)
          },
          {
            title: '文档分析',
            componentName: shallowRef(Document3)
          },
          {
            title: '文档列表',
            componentName: shallowRef(Document4)
          }
        ]
      },
      {
        title: '三、AI Chat 聊天助手',
        children: [
          {
            title: '功能介绍',
            componentName: shallowRef(Chat1)
          }
        ]
      },
      {
        title: '四、AI Translate翻译助手',
        children: [
          {
            title: '定制化翻译风格',
            componentName: shallowRef(Translate1)
          },
          {
            title: '文字翻译',
            componentName: shallowRef(Translate2)
          },
          {
            title: '图片翻译',
            componentName: shallowRef(Translate3)
          },
          {
            title: '文档翻译',
            componentName: shallowRef(Translate4)
          },
          {
            title: '网站翻译',
            componentName: shallowRef(Translate5)
          }
        ]
      }
    ]
  }
])
const menuOpeneds = []
const dealData = (
  data: Array<{
    deep: Number
    parentIndex: Number | String
    index: Number | String
    children: Array<any>
  }>,
  parentIndex?: Number | String,
  deep = 0
) => {
  data.forEach((item, key) => {
    item.deep = deep + 1
    item.parentIndex = parentIndex || ''
    item.index = parentIndex !== undefined ? parentIndex + '-' + key : key
    if (item.children) {
      menuOpeneds.push('/manual/' + item.index)
      dealData(item.children, item.index, Number(item.deep))
    }
  })
}
dealData(manualData)
const data = [...manualData]
const defaultOpeneds = [...menuOpeneds]
const contentData = ref([...manualData])
const pathArr = ref<Array<string>>([])

watch(
  () => route.path,
  (val) => {
    const arr = val.split('/manual/')
    if (arr[1]) {
      pathArr.value = arr[1].split('-')
    } else {
      pathArr.value = []
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.manual-wrap {
  height: 100%;
  overflow: auto;
  display: flex;
  flex-direction: column;
}
.manual-container {
  background-color: #fff;
  padding: 0 60px;
  flex: 1;
  display: flex;
  .manual-left {
    width: 261px;
    background-color: #fff;
  }
  .manual-center {
    padding: 0px 0px 0px 40px;
    background-color: #fff;
    flex: 1;
  }
  .manual-right {
    width: 100px;
    background-color: #fff;
  }
}
.manual-bottom {
  padding-top: 80px;
  background-color: #fff;
}
.breadcrumb-container {
  padding: 20px 0;
}
</style>
<style lang="scss">
.manual-container {
  font-size: 16px;
  line-height: 30px;
  .oz-menu {
    background-color: #f8f8f8;
    border-right: none;
    padding-top: 10px;
  }
  .oz-menu-item {
    height: 50px;
    line-height: 50px;
  }
  .oz-sub-menu__title {
    height: 40px;
    line-height: 40px;
  }
  .oz-sub-menu .oz-menu-item {
    height: 40px;
    line-height: 40px;
  }
  .title-tip {
  }
  // 小圆点
  .module-dots {
    position: relative;
    padding-left: 20px;
    &::before {
      content: '';
      width: 5px;
      height: 5px;
      border-radius: 50%;
      background-color: var(--oz-color-primary);
      position: absolute;
      top: 12px;
      left: 5px;
    }
  }

  .serial-number-title span {
    color: var(--oz-color-primary);
  }
  .red-text {
    color: #d83931;
  }
  .img-container {
    padding: 10px 60px 10px 10px;
  }
  img {
    max-width: 100%;
  }
}
</style>

<template>
  <TooltipMenu :name="t('probot.menu1')" ref="tooltipRef">
    <div class="probot-menu">
      <div class="probot-module" @click="handleProbot">
        <p class="probot-title">PROBOT库</p>
        <div class="probot-content">
          <div class="probot-icon"><i class="iconfont icon-APP-robot1"></i></div>
          <div class="probot-main">
            <h3>Probot商店是提供各种不同类型 AI 机器人的在线平台，根据您的需求和兴趣探索试用吧</h3>
            <div class="link-container" v-if="categoryList['1']?.length">
              <div class="link-icon"><i class="iconfont icon-tag"></i></div>
              <div class="link-main">
                <div class="link-content">
                  <template v-for="(item, index) in categoryList['1']" :key="index">
                    <span class="link-item" @click.stop="handleProbot(item)">
                      <el-link type="primary" :underline="false">{{ item.name + ',' }}</el-link>
                    </span>
                  </template>
                </div>
                <span class="link-item">
                  <el-link type="primary" :underline="false">更多</el-link>
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="probot-module" @click="handlePlugin">
        <p class="probot-title">插件库</p>
        <div class="probot-content">
          <div class="probot-icon"><i class="iconfont icon-chajianku-chajianku"></i></div>
          <div class="probot-main">
            <h3>
              插件库提供各种不同类型的组件，根据您的需求搜索查看，使用它们编排出个性化的Probot吧
            </h3>
            <div class="link-container" v-if="categoryList['2']?.length">
              <div class="link-icon"><i class="iconfont icon-tag"></i></div>
              <div class="link-main">
                <div class="link-content">
                  <template v-for="(item, index) in categoryList['2']" :key="index">
                    <span class="link-item" @click.stop="handlePlugin(item)">
                      <el-link type="primary" :underline="false">{{ item.name + ',' }}</el-link>
                    </span>
                  </template>
                </div>
                <span class="link-item">
                  <el-link type="primary" :underline="false">更多</el-link>
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </TooltipMenu>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { t } from '@/locales'
import TooltipMenu from '@/views/probot/components/TooltipMenu.vue'
import { useRouter } from 'vue-router'
import { getCategoryTypeList, getCategoryList } from '@/api/probot-classification'
import { useProbotStore } from '@/stores/probot'

const router = useRouter()
const probotStore = useProbotStore()

const categoryTypeList = computed(() => probotStore.categoryTypeList)
const categoryList = computed(() => probotStore.categoryList)

onMounted(() => {
  getCategoryTypeList().then((res) => {
    probotStore.setCategoryTypeList(res?.data)
    Object.keys(categoryTypeList.value).forEach((item, key) => {
      getCategoryList({
        type: item
      }).then(({ data }) => {
        probotStore.setCategoryList({
          [item]: data?.length ? data : []
        })
      })
    })
  })
  //所有数据
  getCategoryList({
    type: ''
  }).then(({ data }) => {
    probotStore.setCategoryList({
      '': data?.length ? data : []
    })
  })
})

const handleProbot = (item?: { id?: number }) => {
  router.push({
    path: '/probot-list',
    name: 'AI Probot List',
    query: {
      category: item?.id
    }
  })
}
const handlePlugin = (item?: { id?: number }) => {
  router.push({
    path: '/probot-plugin-list',
    name: 'AI Probot Plugin List',
    query: {
      category: item?.id
    }
  })
}
</script>

<style scoped lang="scss">
.probot-menu {
  width: 400px;
  padding: 10px;

  .probot-module {
    width: 100%;
    cursor: pointer;
    padding-bottom: 10px;

    .probot-title {
      font-size: 16px;
      line-height: 30px;
      padding-bottom: 10px;
      color: hsl(224 71.4% 4.1%);
    }

    .probot-content {
      width: 100%;
      display: flex;
      padding: 10px;
      border: 1px solid rgb(229, 231, 235);
      background-color: hsl(0 0% 100%);
      border-radius: 10px;
      color: hsl(224 71.4% 4.1%);
    }

    &:hover {
      .probot-content {
        box-shadow:
          (0 0 #0000, 0 0 #0000),
          (0 0 #0000, 0 0 #0000),
          0 10px 15px -3px rgba(0, 0, 0, 0.1),
          0 4px 6px -4px rgba(0, 0, 0, 0.1);
      }
    }
  }

  .probot-icon {
    width: 80px;
    height: 60px;
    text-align: center;
    line-height: 60px;

    i {
      font-size: 70px;
      background: -webkit-linear-gradient(#eee, #8ec5fc);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }

  .probot-main {
    padding: 0 10px;
    flex: 1;
    overflow: hidden;

    h3 {
      font-size: 14px;
      line-height: 24px;
      font-weight: 500;
      color: rgb(107, 114, 128);
      width: 100%;
    }
  }

  .link-container {
    width: 100%;
    padding: 6px 0px;
    display: flex;
    align-items: center;

    .link-icon {
      font-size: 12px;
      line-height: 20px;
      margin-right: 8px;
    }
    .link-main {
      display: flex;
      align-items: center;
      .link-content {
        // text-overflow: ellipsis;
        width: 200px;
        margin-right: 10px;
        white-space: nowrap;
        overflow: hidden;
        flex: 1;
      }

      .link-item {
        line-height: 20px;
        padding: 6px 0px;
        margin-right: 8px;
        color: rgb(73, 173, 255);
      }

      .oz-link {
        font-size: 12px;
        color: rgb(73, 173, 255);
        border-bottom: 1px solid rgb(73, 173, 255, 0.9);
        opacity: 0.8;

        &:hover {
          opacity: 1;
        }
      }
    }
  }
}
</style>

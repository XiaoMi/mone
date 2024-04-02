<!--
 * @Description:
 * @Date: 2024-03-01 16:18:44
 * @LastEditTime: 2024-03-26 16:21:28
-->
<template>
  <div class="probot-container">
    <div class="head">
      <h1>欢迎来到AI PROBOT~</h1>
      <p>AI PROBOT是一个低门槛、快速便捷的人工智能聊天工具创作平台。</p>
      <p>
        无需编写复杂代码或拥有专业技能，轻松编排各种组件，创造出个性化、多媒体互动的AI
        PROBOT，并一键发布到飞书、微信服务号等渠道。
      </p>
      <p>无需等待，尽情探索各种组件的组合，开始您的创作之旅！让您的AI聊天工具梦想成真！</p>
    </div>
    <div class="btn">
      <el-button type="primary" size="large" @click="create"><span>创建PROBOT</span></el-button>
      <el-button type="primary" plain size="large" color="#40a3ff" @click="handleMoreProbot"
        ><span>PROBOT库</span></el-button
      >
    </div>
    <div class="probot-library library-container">
      <div class="title">
        <h1>PROBOT库</h1>
        <el-link type="primary" @click="handleMoreProbot">更多...</el-link>
      </div>
      <BaseList type="probot" :loading="botLoading" :data="botList"></BaseList>
    </div>
    <div class="plugin-library library-container">
      <div class="title">
        <h1>插件库</h1>
        <el-link type="primary" @click="handleMorePlugin">更多...</el-link>
      </div>
      <BaseList
        type="plugin"
        :loading="pluginsLoading"
        :data="pluginsList"
        @onJump="handleJump"
      ></BaseList>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import BaseList from '@/components/BaseList.vue'
import { onBeforeMount, ref } from 'vue'
import { getProbotList } from '@/api/probot'
import { pluginsList4home } from '@/api/plugins'

const router = useRouter()

const botLoading = ref(false)
const botList = ref([])
const pluginsLoading = ref(false)
const pluginsList = ref([])

const create = () => {
  router.push({
    path: '/probot-create',
    name: 'AI Probot Create'
  })
}

const handleMoreProbot = () => {
  router.push({
    path: '/probot-list',
    name: 'AI Probot List'
  })
}

const handleMorePlugin = () => {
  router.push({
    path: '/probot-plugin-list',
    name: 'AI Probot Plugin List'
  })
}

const handleJump = (item) => {
  router.push({
    path: `/plugin-detail`,
    name: 'AI Probot Plugin Detail',
    query: {
      id: item.id
    }
  })
}

onBeforeMount(() => {
  botLoading.value = true
  getProbotList({
    pageNum: 1,
    pageSize: 4
  })
    .then((res) => {
      if (res?.data?.records?.length) {
        botList.value = res?.data?.records || []
      } else {
        botList.value = []
      }
    })
    .catch((e) => {
      console.log(e)
    })
    .finally(() => {
      botLoading.value = false
    })
  pluginsLoading.value = true
  pluginsList4home({
    pageSize: 4,
    pageNum: 1,
    orgOnly: true
  })
    .then(({ data }) => {
      if (data?.records?.length) {
        pluginsList.value = data.records.map((v: any) => ({
          ...v,
          userName: v.creator,
          name: v.pluginOrgName,
          desc: v.pluginOrgDesc
        }))
      } else {
        pluginsList.value = []
      }
    })
    .catch((e) => {
      console.log(e)
    })
    .finally(() => {
      pluginsLoading.value = false
    })
})
</script>

<style scoped lang="scss">
.probot-container {
  width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .head {
    line-height: 30px;
    padding: 46px 20px 16px;

    h1 {
      font-size: 30px;
      font-weight: 500;
      text-align: center;
      padding-bottom: 40px;
    }

    p {
      padding: 0px 0px;
      font-size: 16px;
      padding-top: 6px;
      line-height: 32px;
      text-align: center;
    }
  }

  .btn {
    padding: 20px 0px 0;
    text-align: center;
    .oz-button {
      font-size: 16px;
    }
  }
}

.library-container {
  padding-top: 30px;

  .title {
    display: flex;
    align-items: center;
    padding: 10px 0px;

    h1 {
      line-height: 30px;
      font-size: 20px;
      padding-right: 20px;
      color: rgb(71, 85, 105);
      padding-left: 10px;
    }

    .oz-link {
      color: rgb(73, 173, 255);
    }
  }
}
</style>

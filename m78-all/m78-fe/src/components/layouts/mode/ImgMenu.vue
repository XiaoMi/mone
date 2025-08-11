<template>
  <div class="mode-index">
    <el-menu :default-active="state.active" class="menu-box" :router="true">
      <el-menu-item-group v-for="item in menu" :key="item.index" :title="item.name">
        <el-menu-item v-for="child in item.children" :key="child.index" :index="child.index">
          <div class="item-inner">
            <i class="iconfont" :class="child.icon"></i>
            {{ child.name }}
          </div>
        </el-menu-item>
      </el-menu-item-group>
    </el-menu>
    <RouterView />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import './common.scss'

const route = useRoute()
const state = reactive({
  active: route.path
})
const menu = ref([
  {
    name: '图片理解',
    index: 1,
    children: [
      {
        name: '图片理解',
        index: '/probot-mode-image',
        icon: 'icon-tuxianglijie2'
      }
    ]
  },
  {
    name: '图片创作',
    index: 2,
    children: [
      {
        name: '办公图表',
        index: '/probot-mode-image/office-chart',
        icon: 'icon-db-table'
      },
      {
        name: '文本生图',
        index: '/probot-mode-image/text-to-images',
        icon: 'icon-zhonghui1'
      },
      {
        name: '涂鸦生图',
        index: '/probot-mode-image/graffiti',
        icon: 'icon-tuya'
      },
      {
        name: '背景生成',
        index: '/probot-mode-image/background',
        icon: 'icon-zhonghui2'
      },
      {
        name: '风格重绘',
        index: '/probot-mode-image/redraw',
        icon: 'icon-jubuzhonghui'
      },
      {
        name: '图文融合',
        index: '/probot-mode-image/image-text',
        icon: 'icon-tuwenjiehe'
      },
      {
        name: '艺术字',
        index: '/probot-mode-image/word-art',
        icon: 'icon-yishuzi'
      }
    ]
  }
  // {
  //   name: '图片编辑',
  //   index: 3,
  //   children: [
  //     {
  //       name: '图片扩展',
  //       index: '/probot-mode-image/extend',
  //       icon: 'icon-kuozhan1'
  //     }
  //   ]
  // }
])
onMounted(() => {
  state.active = route.path
})
</script>

<style lang="scss"></style>

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
    name: '声音理解',
    index: 1,
    children: [
      {
        name: '声音理解',
        index: '/probot-mode-voice',
        icon: 'icon-shengyinlijie'
      }
    ]
  },
  {
    name: '声音创作',
    index: 2,
    children: [
      {
        name: '音频生成',
        index: '/probot-mode-voice/audio',
        icon: 'icon-filesoundo'
      },
      {
        name: '声音复刻',
        index: '/probot-mode-voice/sound-copy',
        icon: 'icon-shengyinfuke2'
      }
    ]
  }
])
onMounted(() => {
  state.active = route.path
})
</script>

<style lang="scss"></style>

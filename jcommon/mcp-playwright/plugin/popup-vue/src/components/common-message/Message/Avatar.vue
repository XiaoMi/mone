<script lang="ts" setup>
import { computed } from 'vue'
import { isString } from '@/utils/is'
import { UserFilled } from '@element-plus/icons-vue'
import Logo2 from './robot.png'

interface Props {
  image?: boolean
  avatar?: string
  avatarUrl?: string
  username?: string
}

const props = defineProps<Props>()

const avatar = computed(() => props.avatar || 'robot')
const name = computed(() => props.username || 'robot')
</script>

<template>
  <template v-if="props.username">
    <el-avatar>{{ props.username.toUpperCase().substring(0, 1) }}</el-avatar>
  </template>
  <template v-else-if="image">
    <el-avatar v-if="isString(avatar) && avatar.length > 0" :src="avatar" />
    <el-avatar v-else-if="isString(name) && name.length > 0">{{
      name.toUpperCase().substring(0, 1)
    }}</el-avatar>
    <el-avatar v-else :icon="UserFilled" />
  </template>
  <template v-else-if="props.avatar">
    <el-avatar :src="avatar" />
  </template>
  <div
    v-else
    style="
      background-color: rgb(238, 238, 238);
      width: 32px;
      height: 32px;
      display: flex;
      justify-content: center;
      align-items: center;
    "
  >
    <img :src="Logo2" style="width: 20px" />
  </div>
</template>

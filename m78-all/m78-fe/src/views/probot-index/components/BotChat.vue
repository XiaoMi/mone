<!--
 * @Description: 
 * @Date: 2024-08-22 20:08:18
 * @LastEditTime: 2024-08-23 11:25:20
-->
<template>
  <div class="bot-chat-wrap">
    <div class="bot-chat" v-loading="state.loading">
      <dl>
        <dt>我的收藏</dt>
        <dd v-for="(item, key) in state.listData" :key="key" @click="emits('botClick',item)">
         
          <BaseInfo
            :data="{
              name: item.botInfo.name || '----',
              avatarUrl: item.botInfo.avatarUrl || '10'
            }"
            size="mini"
          >
          <span class="desc">{{ '@'+item.botInfo.creator }} </span>
          </BaseInfo>
        </dd>
      </dl>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeMount, reactive } from 'vue'
import BaseInfo from '@/components/BaseInfo.vue'
import { getProbotList } from '@/api/probot'

const emits = defineEmits(['botClick'])
const state = reactive({
  listData: [],
  search: {
    status: '',
    category: '',
    name: '',
    pageSize: 1000,
    pageNum: 1
  },
  loading: true
})

const getList = () => {
  state.loading = true
  getProbotList({
    ...state.search,
    isMyCollect: true
  })
    .then(({ data }) => {
      if (data?.records?.length) {
        state.listData = data?.records
      } else {
        state.listData = []
      }
    })
    .catch((e) => {
      console.log(e)
    })
    .finally(() => {
      state.loading = false
    })
}


onBeforeMount(() => {
  getList()
})
</script>

<style scoped lang="scss">
.bot-chat {
  &-wrap {
    position: absolute;
    bottom: 90px;
    left: 0px;
    width: 100%;
    padding: 0 40px;
    z-index: 2;
  }
  background-color: #fff;
  padding: 10px;
  border-radius: 5px;
  min-height: 100px;
  dl{}
  dt{
    font-size: 13px;
    height: 30px;
    line-height: 30px;
    color: #666;
    padding-left: 4px;
    margin-bottom: 4px;
  }
  dd{
    padding: 6px 10px;
    cursor: pointer;
    border-radius: 5px;
    &:hover{
      background-color: rgba(6,7,8,0.1);
    }
    .desc{
      color: #666;
      padding-left: 6px;
    }
  }
}
</style>

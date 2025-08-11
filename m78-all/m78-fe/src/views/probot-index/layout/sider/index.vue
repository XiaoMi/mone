<script setup lang="ts">
import { reactive } from 'vue'
import { useChatStore } from '@/stores'
import MyBtn from '@/components/MyBtn.vue'
import List from './List.vue'
import { t } from '@/locales'

const chatStore = useChatStore()
// const dialogVisible = ref(false)

const form = reactive({
  title: t('chat.newChat'),
  desc: ''
})

function handleAdd() {
  chatStore.addHistory(form)
  // dialogVisible.value = true
}

// async function submit() {
//   try {
//     chatStore.addHistory(form)
//     dialogVisible.value = false
//   } catch (e) {
//     console.error(e)
//   }
// }
</script>

<template>
  <div class="sider flex flex-col h-full">
    <main class="flex flex-col flex-1 min-h-0">
      <div class="sider-header">
        <MyBtn
          class="flex-1"
          bgColor="var(--oz-color-primary)"
          iconClass="icon-icon-test"
          @click="handleAdd"
          :text="$t('chat.newChatButton')"
        />
      </div>
      <div class="flex flex-1 min-h-0 pb-4 overflow-hidden">
        <div class="flex-1 overflow-y-auto">
          <h3 class="sider-list-header">聊天列表</h3>
          <List />
        </div>
      </div>
    </main>
    <!-- <el-dialog :title="$t('chat.newChatButton')" v-model="dialogVisible">
      <el-form label-width="60px">
        <el-form-item label="名称" prop="title">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit"> {{ $t('chat.newChatButton') }}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog> -->
  </div>
</template>

<style lang="scss" scoped>
.sider {
  margin: 0 10px;

  &-header {
    display: flex;
    padding: 10px 0;
    justify-content: center;
  }

  &-list-header {
    margin: 20px 0;
  }
}
</style>

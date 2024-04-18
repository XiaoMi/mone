<script setup lang="ts">
import { computed, ref } from 'vue'
import { useChatStore } from '@/stores'
import { debounce } from '@/utils/functions/debounce'
import { Delete, Edit } from '@element-plus/icons-vue'
import KnownledgeBase from '@/views/chat/knowledge-base/index.vue'
import DetailDrawer from '@/views/chat/components/DetailDrawer.vue'
import { t } from '@/locales'

const chatStore = useChatStore()

const dataSources = computed(() => chatStore.history)

async function handleSelect({ uuid }: Chat.History) {
  if (isActive(uuid)) return

  if (chatStore.active) chatStore.updateHistory(chatStore.active, { isEdit: false })
  await chatStore.setActive(uuid)
}

function handleEdit({ uuid }: Chat.History, isEdit: boolean) {
  chatStore.updateHistory(uuid, { isEdit })
}

async function handleDelete(index: number) {
  chatStore.deleteHistory(index)
}

const handleDeleteDebounce = debounce(handleDelete, 600)

async function handleEnter({ uuid, title }: Chat.History, isEdit: boolean) {
  chatStore.updateHistory(uuid, { isEdit })
}

function isActive(uuid: number) {
  return chatStore.active === uuid
}

const connectionVisible = ref(false)
const handleConnection = () => {
  connectionVisible.value = true
}

const showDetail = ref(false)
const handleTopicDetail = () => {
  showDetail.value = true
}
</script>

<template>
  <div class="flex flex-col gap-2 text-sm">
    <template v-if="!dataSources.length">
      <div class="flex flex-col items-center mt-4 text-center text-neutral-300">
        <el-empty :description="t('common.noData')"></el-empty>
      </div>
    </template>
    <template v-else>
      <template v-for="(item, index) of dataSources" :key="index">
        <div
          class="relative flex items-center gap-3 px-3 py-3 break-all text-[#fff] rounded-md cursor-pointer group dark:hover:bg-[#00a9ff]"
          :class="
            isActive(item.uuid)
              ? [
                  'border-[#4b9e5f]',
                  'bg-[#00a9ff]',
                  'dark:bg-[#00a9ff]',
                  'dark:border-[#24272e]',
                  'pr-14'
                ]
              : ['bg-[#80d4ff]', 'hover:bg-[#00a9ff]']
          "
          @click="handleSelect(item)"
        >
          <div class="chat-item">
            <div class="chat-item-top">
              <el-icon size="18"><ChatDotRound /></el-icon>
              <div
                class="relative pl-[8px] flex-1 overflow-hidden break-all text-ellipsis whitespace-nowrap"
              >
                <el-input
                  text
                  v-if="item.isEdit"
                  v-model="item.title"
                  @keyup.enter="handleEnter(item, false)"
                />
                <span v-else>{{ item.title }}</span>
              </div>
            </div>
            <div class="chat-item-bottom pt-[10px]" v-if="isActive(item.uuid)">
              <div>
                <el-button link style="color: #fff" @click="handleTopicDetail">详情</el-button>
              </div>
              <div class="absolute z-10 flex visible right-1">
                <el-tooltip effect="dark" content="绑定知识库" placement="top">
                  <el-icon size="18" style="margin: 0 10px" @click="handleConnection"
                    ><Connection
                  /></el-icon>
                </el-tooltip>
                <template v-if="item.isEdit">
                  <el-tooltip effect="dark" content="保存" placement="top">
                    <el-icon size="18" style="margin: 0 10px" @click="handleEnter(item, false)"
                      ><Edit
                    /></el-icon>
                  </el-tooltip>
                </template>
                <template v-else>
                  <el-tooltip effect="dark" content="编辑" placement="top">
                    <el-icon size="18" @click="handleEdit(item, true)"><Edit /></el-icon>
                  </el-tooltip>
                  <el-popconfirm
                    :title="t('chat.deleteHistoryConfirm')"
                    @confirm="handleDeleteDebounce(index)"
                  >
                    <template #reference>
                      <div>
                        <el-tooltip effect="dark" content="编辑" placement="top">
                          <el-icon size="18" style="margin: 0 10px"><Delete /></el-icon>
                        </el-tooltip>
                      </div>
                    </template>
                  </el-popconfirm>
                </template>
              </div>
            </div>
          </div>
        </div>
      </template>
    </template>
  </div>
  <DetailDrawer v-if="showDetail" v-model="showDetail" />
  <KnownledgeBase v-if="connectionVisible" v-model="connectionVisible" />
</template>

<style scoped lang="scss">
.chat-item {
  &-top {
    display: flex;
    align-items: center;
  }
  &-bottom {
    display: flex;
  }
}
</style>

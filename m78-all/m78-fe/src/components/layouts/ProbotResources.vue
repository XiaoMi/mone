<template>
  <TooltipMenu :name="t('probot.menu2')" ref="tooltipRef">
    <div class="probot-resources">
      <div class="left">
        <el-menu router class="resources-menu" @select="handleSelect" :default-active="activeIndex">
          <el-menu-item index="/probot-my-collect">
            <i class="iconfont icon-aixin1"></i>
            <span>我的收藏</span></el-menu-item
          >
          <el-menu-item index="">
            <i class="iconfont icon-chuangjiantuandui1"></i>
            <span>{{ t('probot.createTeam') }}</span></el-menu-item
          >
          <el-menu-item index="/probot-team">
            <i class="iconfont icon-tuanduikongjian"></i>
            <span>{{ t('probot.teamSpace') }}</span></el-menu-item
          >
        </el-menu>
      </div>
      <div class="right">
        <div class="head">
          <el-input
            v-model.trim="input"
            style="max-width: 400px"
            placeholder="请输入内容进行搜索"
            size="large"
            clearable
          >
            <template #prepend
              ><el-icon class="search-icon"> <Search /> </el-icon
            ></template>
          </el-input>
        </div>
        <ul class="info-list" v-if="workspaceList?.length">
          <li
            v-for="(item, index) in workspaceList.filter((v) => v?.name.indexOf(input) > -1)"
            :key="index"
            @click="spaceClick(item)"
          >
            <BaseInfo :data="item" size="small"></BaseInfo>
          </li>
        </ul>
        <el-empty :image-size="80" v-else />
      </div>
    </div>
  </TooltipMenu>
  <ProbotTeamDialog v-model="createTeamDialogVisible" @onOk="getList"></ProbotTeamDialog>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { t } from '@/locales'
import TooltipMenu from '@/views/probot/components/TooltipMenu.vue'
import ProbotTeamDialog from '@/components/ProbotTeamDialog.vue'
import BaseInfo from '@/components/BaseInfo.vue'
import { getWorkspaceList } from '@/api/probot'
import { useProbotStore } from '@/stores/probot'

const route = useRoute()
const router = useRouter()
const probotStore = useProbotStore()

const tooltipRef = ref()
const activeIndex = ref('1')
const input = ref('')
const createTeamDialogVisible = ref(false)
const workspaceList = computed(() => probotStore.workspaceList)

const getList = () => {
  getWorkspaceList()
    .then((res) => {
      if (res?.data?.length) {
        probotStore.setWorkspaceList(res?.data)
      } else {
        probotStore.setWorkspaceList([])
      }
    })
    .catch((e) => {
      console.log(e)
    })
}

onBeforeMount(() => {
  getList()
})

const handleSelect = (key: string, keyPath: string[]) => {
  if (!key) {
    createTeamDialogVisible.value = true
    router.replace({
      path: route.path,
      query: route.query
    })
  }
  tooltipRef.value.hide()
}

const spaceClick = (item: { id: number }) => {
  router.push({
    path: '/probot-space/' + item.id
  })
  tooltipRef.value.hide()
}
</script>

<style scoped lang="scss">
.probot-resources {
  display: flex;
  padding: 6px 10px 6px 0;
  .left {
    .resources-menu {
      height: 100%;
      .oz-menu-item {
        height: 40px;
        line-height: 40px;
        .iconfont {
          font-size: 20px;
          padding-right: 10px;
        }
        &:hover {
          color: #40a3ff;
        }
      }
    }
  }
  .right {
    padding-top: 6px;
    padding-left: 10px;
    .head {
      .search-icon {
        color: rgba(0, 0, 0, 0.3);
      }
    }
    ul.info-list {
      padding-top: 10px;
      max-height: 65vh;
      overflow-y: auto;
      width: 300px;
      &::-webkit-scrollbar {
        display: none;
      }
    }
    li {
      width: 100%;
      margin-bottom: 10px;
      padding: 10px;
      color: hsl(224 71.4% 4.1%);
      display: flex;
      justify-content: space-between;
      cursor: pointer;
      border-radius: 10px;
      border: 1px solid #fff;
      &:hover {
        box-shadow:
          (0 0 #0000, 0 0 #0000),
          (0 0 #0000, 0 0 #0000),
          0 10px 15px -3px rgba(0, 0, 0, 0.1),
          0 4px 6px -4px rgba(0, 0, 0, 0.1);
        background-color: hsl(0 0% 100%);
        border-color: rgb(229, 231, 235);
      }
    }
  }
}
</style>

<!--
 * @Description: 
 * @Date: 2024-03-11 14:49:51
 * @LastEditTime: 2024-09-18 11:20:50
-->
<template>
  <ProbotBaseTitle title="轮播图管理"></ProbotBaseTitle>
  <div class="space-wrap">
    <div class="space-container">
      <div class="space-head">
        <div class="space-filter">
          <el-form ref="formRef" :model="form" inline @submit.native.prevent>
            <el-form-item prop="title" label="名称:">
              <el-input
                v-model="form.title"
                placeholder="请输入名称"
                clearable
                @keyup.enter="getList(1)"
              />
            </el-form-item>
            <el-form-item props="type" label="类型:">
              <el-select
                v-model="form.type"
                style="width: 100%"
                placeholder="请选择类型"
                @change="getList(1)"
                clearable
              >
                <el-option
                  v-for="item in typeOptions"
                  :key="item?.id"
                  :label="item?.name"
                  :value="item?.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item props="displayStatus" label="状态:">
              <el-select
                v-model="form.displayStatus"
                style="width: 100%"
                placeholder="请选择状态"
                @change="getList(1)"
                clearable
              >
                <el-option
                  v-for="item in statusOptions"
                  :key="item?.id"
                  :label="item?.name"
                  :value="item?.id"
                />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
        <el-button type="primary" @click="create" class="create-btn">新建</el-button>
      </div>
      <el-table :data="state.tableList" style="width: 100%">
        <el-table-column label="Bot" v-slot="{ row }" >
          <BaseInfo
            :data="{
              name: row.botName || '----',
              describe: '@' + row.botCuser,
              avatarUrl: row.botAvatar
            }"
            size="small"
            @click="toDetail(row)"
          />
        </el-table-column>
        <el-table-column prop="title" label="标题"  />
        <el-table-column prop="type" label="类型" v-slot="{ row }" width="100px">
          {{ typeOptions.find((item) => item.id == row.type)?.name }}
        </el-table-column>
        <!-- <el-table-column prop="botPermissions" label="权限" /> -->
        <el-table-column prop="displayStatus" label="状态" v-slot="{ row }" width="80px">
          <el-switch
            @change="handleChangeStatus(row)"
            v-model="row.displayStatus"
            inline-prompt
            style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
            active-text="禁用"
            inactive-text="启用"
            :active-value="0"
            :inactive-value="1"
          />
        </el-table-column>
        <el-table-column prop="ctime" label="创建时间" v-slot="{ row }" width="182px">
          {{ dateFormat(row.ctime, 'yyyy-mm-dd HH:MM:ss') }}
        </el-table-column>
        <el-table-column prop="utime" label="更新时间" v-slot="{ row }" width="180px">
          {{ dateFormat(row.utime, 'yyyy-mm-dd HH:MM:ss') }}
        </el-table-column>
        <el-table-column fixed="right" label="操作" v-slot="{ row }" width="90px">
          <div class="right">
            <BaseLink name="编辑" @click.stop="editClick(row)" icon="icon-bianji"></BaseLink>
          </div>
        </el-table-column>
      </el-table>
      <div class="pager" v-if="state.total">
        <el-pagination
          small
          background
          layout="prev, pager, next"
          :total="state.total"
          :page-size="state.search.pageSize"
          :v-model:current-page="state.search.pageNum"
          @change="handleChangePage"
        />
      </div>
    </div>
    <CarouselDialog
      v-model="state.carouselDialogVisible"
      :data="dialogData"
      @onOk="getList"
      :typeOptions="typeOptions"
      :statusOptions="statusOptions"
    />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import BaseInfo from '@/components/BaseInfo.vue'
import dateFormat from 'dateformat'
import ProbotBaseTitle from '@/components/probot/ProbotBaseTitle.vue'
import BaseLink from '@/components/probot/BaseLink.vue'
import CarouselDialog from './CarouselDialog.vue'
import { getListByAdmin, updateDisplayStatus } from '@/api/probot-carousel'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'

const typeOptions = [
  {
    id: 1,
    name: 'probot广场'
  }
]
const statusOptions = [
  {
    id: 0,
    name: '禁用'
  },
  {
    id: 1,
    name: '启用'
  }
]

const form = reactive({
  title: '',
  type: '',
  displayStatus: ''
})

const router = useRouter()

const state = reactive({
  carouselDialogVisible: false,
  tableList: [],
  search: {
    pageSize: 10,
    pageNum: 1
  },
  total: 0,
  loading: true
})
const dialogData = ref()

const getList = (page = 1) => {
  state.loading = true
  state.search.pageNum = page
  getListByAdmin({
    ...state.search,
    ...form
  })
    .then((data) => {
      if (data?.data.list?.length) {
        state.tableList = data.data.list || []
        state.total = data.data.totalPage * state.search.pageSize || 0
      } else {
        state.tableList = []
        state.total = 0
      }
    })
    .catch((e) => {
      console.log(e)
    })
}
onMounted(() => {
  getList(1)
})
const handleChangePage = (page: number) => {
  getList(page)
}
const create = () => {
  state.carouselDialogVisible = true
  dialogData.value = {}
}

const editClick = (row: any) => {
  state.carouselDialogVisible = true
  dialogData.value = row
}
const toDetail = (row: any) => {
  const path = '/probot-edit/' + row.botId
  const { href } = router.resolve({
    path
  })
  window.open(href, '_blank') //打开新的窗口
}

const handleChangeStatus = (row) => {
  updateDisplayStatus({
    id: row.id,
    displayStatus: row.displayStatus ? 1 : 0
  }).then((data) => {
    if (data.data) {
      ElMessage.success(t('common.success'))
    } else {
      ElMessage.error(data.message)
    }
  })
}
</script>

<style lang="scss" scoped>
.space-wrap {
  padding: 10px 10px;
  min-height: 300px;
}
.space-container {
  background: #fff;
  padding: 20px;
}
.space-head {
  width: 100%;
  align-items: center;
  display: flex;
  justify-content: space-between;
  .create-btn {
    margin-right: 20px;
  }
}
.space-filter {
  flex: 1;
}
.btn-item {
  padding-left: 4px;
  padding-right: 4px;
}
.right {
  display: flex;
  align-items: center;
}
.item-btn {
  margin-top: 4px;
}
.pager {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
</style>

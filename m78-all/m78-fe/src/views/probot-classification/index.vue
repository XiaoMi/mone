<!--
 * @Description: 
 * @Date: 2024-03-11 14:49:51
 * @LastEditTime: 2024-08-15 11:18:13
-->
<template>
  <ProbotBaseTitle title="分类管理"></ProbotBaseTitle>
  <div class="category-wrap">
    <div class="category-container">
      <div class="category-head">
        <div class="category-filter">
          <el-form ref="formRef" :model="form" inline>
            <el-form-item prop="type" label="分类类型:">
              <el-select v-model="form.type" placeholder="请选择分类类型" clearable>
                <el-option
                  v-for="(item, index) in categoryTypeList"
                  :key="index"
                  :label="item"
                  :value="index"
                />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
        <el-button type="primary" @click="create" class="create-btn">新建</el-button>
      </div>
      <el-table :data="categoryList[form.type]" style="width: 100%">
        <el-table-column prop="id" label="id" />
        <el-table-column label="类型" v-slot="{ row }">
          {{ categoryTypeList[row.type] }}
        </el-table-column>
        <el-table-column prop="name" label="name" />
        <el-table-column fixed="right" label="操作" v-slot="{ row }" width="112px">
          <el-button
            type="primary"
            class="btn-item"
            text
            size="small"
            @click="deleteCategoryClick(row)"
            >删除</el-button
          >
        </el-table-column>
      </el-table>
    </div>
  </div>
  <CreateClass
    v-model="state.createDialogVisible"
    @onOk="getList"
    :categoryOptions="categoryTypeList"
  ></CreateClass>
</template>

<script lang="ts" setup>
import { onMounted, reactive, computed } from 'vue'
import { t } from '@/locales'
import CreateClass from './CreateClass'
import { getCategoryList, deleteCategory } from '@/api/probot-classification'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useProbotStore } from '@/stores/probot'
import ProbotBaseTitle from '@/components/probot/ProbotBaseTitle.vue'

const probotStore = useProbotStore()
const categoryTypeList = computed(() => probotStore.categoryTypeList)
const categoryList = computed(() => probotStore.categoryList)

const state = reactive({
  createDialogVisible: false
})
const form = reactive({
  type: ''
})

const getList = (type: string) => {
  getCategoryList({
    type
  }).then(({ data }) => {
    probotStore.setCategoryList({
      [type]: data?.length ? data : []
    })
  })
  getCategoryList({
    type: ''
  }).then(({ data }) => {
    probotStore.setCategoryList({
      '': data?.length ? data : []
    })
  })
}

const create = () => {
  state.createDialogVisible = true
}

const deleteCategoryClick = (row: { id: string; type: string }) => {
  ElMessageBox.confirm(t('common.confirmDel'), 'Warning', {
    type: 'warning',
    confirmButtonText: t('common.yes'),
    cancelButtonText: t('common.no'),
    title: t('common.delete')
  }).then(async () => {
    deleteCategory({
      categoryId: row.id
    })
      .then((res) => {
        if (res.code === 0) {
          ElMessage.success(t('common.deleteSuccess'))
          getList(row.type)
        }
      })
      .catch(() => {
        ElMessage.error(t('common.deleteFail'))
      })
  })
}
onMounted(() => {})
</script>

<style lang="scss" scoped>
.category-wrap {
  padding: 10px;
  min-height: 300px;
}
.category-container {
  background: #fff;
  padding: 20px;
}
.category-head {
  width: 100%;
  align-items: center;
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  background: #fff;
  .create-btn {
    margin-right: 20px;
  }
}
.category-filter {
}
.btn-item {
  padding-left: 4px;
  padding-right: 4px;
}
</style>

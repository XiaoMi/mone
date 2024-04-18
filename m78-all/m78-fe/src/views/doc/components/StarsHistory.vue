<template>
  <CommonHistory
    @closeHistory="closeHistory"
    :historyData="{
      title: props.dataType == 'history' ? t('excle.myDocs') : t('excle.myFavorites')
    }"
  >
    <template #content>
      <div class="list-box-container" v-loading="loading">
        <DocStatusMap />
        <div v-if="tableData.length > 0" class="list-container">
          <ul class="list-box">
            <li v-for="item in tableData" :key="item.id" class="list-item">
              <span
                @click="showItem(item)"
                :type="retType(item)"
                size="small"
                link
                class="tag-btn"
                :class="`status-${item.status}`"
              >
                {{ item.originalFileName }}
              </span>
              <div class="btn-box">
                <FavoriteBtn
                  :favoriteData="{
                    favorite: item?.favorite,
                    id: item.id
                  }"
                  @onSuccess="favoriteSuccess"
                />
                <el-tooltip effect="dark" :content="t('common.delete')" placement="bottom">
                  <el-button link size="small" @click="deleteItem(item)" type="primary">
                    <el-icon class="action-icon">
                      <Delete />
                    </el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </li>
          </ul>
        </div>
        <p v-else class="center-class margin-top-10">{{ t('common.noData') }}</p>
      </div>
    </template>
  </CommonHistory>
</template>
<script setup lang="ts">
import CommonHistory from '@/components/CommonHistory.vue'
import { myfavorite, docHistory, delDocApi } from '@/api/excle'
import { onMounted, ref } from 'vue'
import { ElMessage, type UploadProps } from 'element-plus'
import DocStatusMap from './DocStatusMap.vue'
import { computed } from 'vue'
import { useExcleStore } from '@/stores/doc'
import { t } from '@/locales'
import FavoriteBtn from './FavoriteBtn.vue'

const docStore = useExcleStore()
const docStatusArr = computed(() => docStore.value)

const retType = (listItem) => {
  const filer = docStatusArr.value.filter((item) => listItem.status === item.code)
  if (filer.length > 0) {
    return filer[0]?.tagType
  } else {
    return 'danger'
  }
}
const props = defineProps({
  // 'history' 'favorite'
  dataType: {
    type: String,
    default: 'history'
  }
})
const loading = ref(false)
const emits = defineEmits(['closeHistory', 'showItem', 'delItemSuccess'])
const closeHistory = () => {
  emits('closeHistory')
}
const tableData = ref([])
const showItem = (item) => {
  if (item.status != 1) {
    ElMessage.warning(
      `该文档${[0, 2].indexOf(item.status) > -1 ? '解析中,请稍后!' : '解析失败，无法查看！'}`
    )
    return
  }
  emits('showItem', item)
  closeHistory()
}
const init = () => {
  loading.value = true
  const apiName = props.dataType == 'history' ? docHistory : myfavorite
  apiName()
    .then(({ code, data }) => {
      if (code != 0) return
      if (data && Array.isArray(data)) {
        tableData.value = data
      } else {
        data = []
      }
    })
    .finally(() => {
      loading.value = false
    })
}
const deleteItem = (row) => {
  loading.value = true

  delDocApi(row.id)
    .then((res) => {
      if (res.code != 0) return
      ElMessage.success(t('common.deleteSuccess'))
      emits('delItemSuccess', row.id)
      init()
    })
    .catch(() => {
      ElMessage.error(t('common.deleteFail'))
    })
    .finally(() => {
      loading.value = false
    })
}
const favoriteSuccess = () => {
  init()
}
onMounted(() => {
  init()
})
defineExpose({
  init
})
</script>
<style lang="scss" scoped>
.list-box-container {
  height: calc(100% - 37px);
  .list-container {
    height: calc(100% - 20px);
    overflow: auto;
  }
  .list-box {
    border: solid 1px #eee;
    border-radius: 3px;
  }
}
.list-item {
  padding: 0 5px;
  border-bottom: solid 1px #eee;
  height: 40px;
  line-height: 40px;
  .btn-box {
    display: inline-block;
    vertical-align: top;
  }
}
.list-item:hover {
  background: #f4f4f4;
}
.list-item:last-child {
  border: none;
}

.center-class {
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: solid 1px #dcdfe6;
  border-radius: 3px;
  color: #909399;
}
.tag-btn {
  display: inline-block;
  width: 220px;
  font-size: 14px;
  border: none;
  // background: transparent;
  cursor: not-allowed;
  color: #d50031;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.status-1 {
  cursor: pointer;

  color: #636569;
}
.status-2 {
  cursor: not-allowed;
  color: #00a9ff;
}
.action-icon {
  font-size: 15px;
}
</style>

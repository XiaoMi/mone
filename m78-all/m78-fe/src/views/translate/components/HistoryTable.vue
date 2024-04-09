<!--
 * @Description: 
 * @Date: 2024-01-16 18:58:43
 * @LastEditTime: 2024-01-24 14:32:31
-->
<template>
  <CommonHistory
    @closeHistory="closeHistory"
    :historyData="{
      title: props.title
    }"
  >
    <template #content>
      <el-table :data="props.data" style="width: 100%; height: 100%" size="small">
        <el-table-column>
          <template #default="scope">
            <div class="translate-history" @click="rowClick(scope.row)">
              <div class="translate-history-top">
                {{ scope.row.fromLanguage }}&nbsp;->&nbsp;{{ scope.row.toLanguage }}
                <div>
                  <FavoriteBtn
                    :favoriteData="{
                      id: scope.row.uuid,
                      favorite: scope.row.favorite
                    }"
                    @onSuccess="handleFavorite(scope.row)"
                  />
                  <el-button link type="primary" size="small" @click="handleDelete(scope.row)">
                    <el-icon class="delete-icon">
                      <Delete />
                    </el-icon>
                  </el-button>
                </div>
              </div>
              <div class="translate-history-bottom">
                {{ scope.row.fromText }}<br />->&nbsp;{{ scope.row.toText }}
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-dialog v-model="dialogVisible" :title="t('translate.delTitle')" width="30%">
        <span>{{ t('translate.delTip') }}</span>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">{{ t('translate.delCancle') }}</el-button>
            <el-button type="primary" @click="sureDelete"> {{ t('translate.delSure') }} </el-button>
          </span>
        </template>
      </el-dialog>
    </template>
  </CommonHistory>
</template>

<script lang="ts" setup>
import { ref, defineProps } from 'vue'
import { ElMessage } from 'element-plus'
import FavoriteBtn from './FavoriteBtn.vue'
import { fetchTranslateHistoryDelete } from '@/api/translate'
import CommonHistory from '@/components/CommonHistory.vue'
import { t } from '@/locales'
import mitt from '@/utils/bus'

const props = defineProps({
  title: String,
  data: {},
  uuid: String
})

const emits = defineEmits(['updateData', 'close'])

const closeHistory = () => {
  emits('close')
}

const dialogVisible = ref(false)
const row = ref({})

const handleFavorite = (row) => {
  if (props.uuid === row.uuid) {
    mitt.emit('translateData', {
      favorite: !row.favorite
    })
  }
  emits('updateData')
}
const handleDelete = (row) => {
  dialogVisible.value = true
  row.value = row
}
const sureDelete = () => {
  fetchTranslateHistoryDelete({
    uuid: row.value.uuid
  }).then((res) => {
    if (res.data === true) {
      ElMessage({
        type: 'success',
        message: t('translate.delSuccess')
      })
      emits('updateData')
      dialogVisible.value = false
    } else {
      ElMessage({
        type: 'error',
        message: t('translate.delError')
      })
    }
  })
}
const rowClick = (row) => {
  //复原
  mitt.emit('translateData', {
    origin: row.fromText,
    result: row.toText,
    uuid: row.uuid,
    favorite: row.favorite
  })
  mitt.emit('translateLang', {
    fromLanguage: row.fromLanguage,
    toLanguage: row.toLanguage
  })
  // todo:requirements
}
</script>
<style lang="scss" scoped>
.translate-history {
  padding: 10px 0;
  cursor: pointer;
  &-top {
    display: flex;
    justify-content: space-between;
    padding-bottom: 10px;
    .delete-icon {
      font-size: 15px;
    }
  }
  &-bottom {
  }
}
</style>

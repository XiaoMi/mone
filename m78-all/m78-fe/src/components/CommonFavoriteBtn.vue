<template>
  <el-tooltip :content="props.favoriteData?.favorite ? removeText : addFavText">
    <el-button
      type="danger"
      text
      size="small"
      :disabled="!props.favoriteData?.id"
      @click.stop="collectFn"
      class="favorite-icon"
    >
      <i
        class="iconfont"
        :class="{
          'icon-aixin-xian': !props.favoriteData?.favorite,
          'icon-love': props.favoriteData?.favorite
        }"
      ></i>
    </el-button>
  </el-tooltip>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'
const props = defineProps({
  favoriteData: {
    type: Object,
    required: true
  }
})
const emits = defineEmits(['onSuccess', 'onError'])
const removeText = computed(() => {
  return t('excle.RemoveFav')
})
const cancleFavFailedText = computed(() => {
  return t('excle.cancleFavfailed')
})
const addFavSucText = computed(() => {
  return t('excle.favSucTips')
})
const addFavFailText = computed(() => {
  return t('excle.favFailTips')
})
const addFavText = computed(() => {
  return t('excle.favorite')
})
const collectFn = () => {
  if (props.favoriteData?.favorite) {
    props.favoriteData
      .fetchUnfavorite(props.favoriteData?.id)
      .then((res) => {
        if (res.code === 0) {
          ElMessage.success(removeText.value)
          emits('onSuccess')
        } else {
          ElMessage.error(res.message || cancleFavFailedText.value)
          emits('onError')
        }
      })
      .catch((e) => {
        console.log(e)
      })
  } else {
    props.favoriteData
      .fetchFavorite(props.favoriteData?.id)
      .then((res) => {
        if (res.code === 0) {
          ElMessage.success(addFavSucText.value)
          emits('onSuccess')
        } else {
          ElMessage.error(res.message || addFavFailText.value)
          emits('onError')
        }
      })
      .catch((e) => {
        console.log(e)
      })
  }
}
</script>
<style scoped lang="scss">
.favorite-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
.iconfont {
  font-size: 16px;
}
</style>

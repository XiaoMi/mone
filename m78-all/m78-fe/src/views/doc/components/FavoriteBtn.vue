<template>
  <CommonFavoriteBtn
    :favoriteData="{
      favorite: props.favoriteData?.favorite,
      id: props.favoriteData.id,
      fetchFavorite: favorite,
      fetchUnfavorite: unfavorite
    }"
    @onSuccess="emits('onSuccess')"
    @onError="emits('onError')"
  ></CommonFavoriteBtn>
</template>
<script setup lang="ts">
import CommonFavoriteBtn from '@/components/CommonFavoriteBtn.vue'
import { favorite, unfavorite } from '@/api/excle'
const props = defineProps({
  favoriteData: {}
})
const emits = defineEmits(['onSuccess', 'onError'])
</script>

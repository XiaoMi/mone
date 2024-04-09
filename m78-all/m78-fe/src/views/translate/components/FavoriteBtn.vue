<!--
 * @Description: 
 * @Date: 2024-01-16 19:01:17
 * @LastEditTime: 2024-01-17 11:06:40
-->
<template>
  <CommonFavoriteBtn
    :favoriteData="{
      favorite: props.favoriteData?.favorite,
      id: props.favoriteData.id,
      fetchFavorite: fetchTranslateFavorite,
      fetchUnfavorite: fetchTranslateUnfavorite
    }"
    @onSuccess="emits('onSuccess')"
    @onError="emits('onError')"
  ></CommonFavoriteBtn>
</template>
<script setup lang="ts">
import CommonFavoriteBtn from '@/components/CommonFavoriteBtn.vue'
import { fetchTranslateFavorite, fetchTranslateUnfavorite } from '@/api/translate'

const props = defineProps({
  favoriteData: {
    type: Object,
    required: true
  }
})
const emits = defineEmits(['onSuccess', 'onError'])
</script>

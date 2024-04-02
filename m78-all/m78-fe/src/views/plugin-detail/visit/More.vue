<!--
 * @Description:
 * @Date: 2024-03-05 18:11:47
 * @LastEditTime: 2024-03-14 19:57:43
-->
<template>
  <el-drawer
    v-model="drawer"
    :with-header="false"
    direction="ttb"
    class="visit-more-drawer"
    modal-class="visit-more-modal"
  >
    <div class="visit-more">
      <Rates v-if="showRate" :bot-id="props.data?.id" :type="1" />
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Rates from '@/components/Rates.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: () => ({})
  },
  showRate: {
    type: Boolean,
    default: () => true
  }
})
const emits = defineEmits(['update:modelValue'])

const drawer = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const route = useRoute()
const router = useRouter()

const permissions = ref(props.data?.botInfo?.permissions)
const platform = ref([])

const goView = () => {
  router.push({
    path: '/probot-view/' + route.params.id
  })
}
watch(
  () => props.data,
  ({ botInfo, publishRecordDTOS }) => {
    permissions.value = botInfo?.permissions
    if (publishRecordDTOS.length) {
      platform.value =
        publishRecordDTOS[0] && publishRecordDTOS[0]?.publishImChannel
          ? JSON.parse(publishRecordDTOS[0].publishImChannel)
          : ''
    }
  }
)
</script>

<style lang="scss">
.visit-more-modal {
  top: 82px !important;
}
.visit-more-drawer {
  background-color: rgb(219, 239, 255);
  height: 370px !important;
}
</style>

<style lang="scss" scoped>
.visit-more {
  padding: 0px 0px 10px 50px;
  background-color: rgb(219, 239, 255);
}
</style>

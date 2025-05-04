<!--
 * @Description:
 * @Date: 2024-03-05 18:11:47
 * @LastEditTime: 2024-08-15 10:27:15
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
      <div class="visit-more-top">
        <div class="remark">{{ data?.botInfo?.remark }}</div>
        <div class="visit-more-top-right">
          <div>
            <span class="visit-label">配置是否公开：</span>
            <div class="radio-container">
              <div class="radio-content" v-if="permissions">
                是
                <el-link type="primary" @click="goView" :underline="false" class="ml4">
                  (去查看)</el-link
                >
              </div>
              <div v-else>否</div>
            </div>
          </div>
          <div>
            <span class="visit-label">AI模型：</span>
            {{ data?.botSetting?.aiModel }}
          </div>
        </div>
      </div>
      <div class="visit-more-bottom">
        <span>发布平台</span>
        <div class="visit-publishing-platform">
          <BaseIconPlatform
            :iconType="item"
            v-for="(item, index) in platform"
            :key="index"
          ></BaseIconPlatform>
        </div>
      </div>
      <div>
        <Rates v-if="showRate" :bot-id="props.data?.botId" :type="0" />
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BaseIconPlatform from '@/components/probot/BaseIconPlatform.vue'
import Rates from '@/components/probot/Rates.vue'

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
  &-top {
    display: flex;
    justify-content: space-between;
    padding-bottom: 10px;
    .remark {
      width: 70%;
      padding-top: 12px;
    }
    &-right {
      flex: 1;
      line-height: 40px;
      & > div {
        display: flex;
        align-items: center;
      }
      span.visit-label {
        display: inline-flex;
        width: 110px;
        text-align: center;
      }
      .radio-container {
        display: inline-flex;
      }
      .radio-content {
        display: inline-flex;
        text-align: center;
      }
    }
  }
  &-bottom {
    border-top: 1px solid #ddd;
    padding-top: 10px;
    display: flex;
    align-items: center;
    .visit-publishing-platform {
      padding-left: 20px;
      display: flex;
      align-items: center;
    }
  }
}
</style>

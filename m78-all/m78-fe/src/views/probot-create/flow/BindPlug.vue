<!--
 * @Description: 
 * @Date: 2024-03-07 11:04:19
 * @LastEditTime: 2024-08-30 10:36:06
-->
<template>
  <BindDialog v-model="dialogVisible" :data="bindData">
    <template #filter><BindPlugFilter></BindPlugFilter></template>
    <template #list>
      <template v-if="props.data.length">
        <div v-for="(item, index) in props.data" :key="index" class="bind-list-item">
          <el-collapse v-model="activeNames" @change="handleChange" class="collapse-container">
            <el-collapse-item :name="index">
              <template #title>
                <div class="bind-list-content">
                  <BaseInfo :data="item" size="small">
                    <div class="more-info">
                      <p>{{ item.pluginCnt || '0' }}个组件</p>
                      <p>
                        {{
                          item.botRefCnt
                            ? item.botRefCnt + '个probot引用了该插件'
                            : '没有probot引用该插件'
                        }}
                      </p>
                      <p>发布于{{ item.createTime }}</p>
                    </div>
                  </BaseInfo>
                </div>
              </template>
              <div v-for="(v, i) in item?.plugins" :key="i" class="child-container">
                <div class="child-content">
                  <h3>{{ v.name }}</h3>
                  <p>{{ v.describe }}</p>
                </div>
                <div class="btn-container">
                  <BaseLink
                    :name="v.bind ? '已绑定' : '绑定'"
                    @click="bindClick(v)"
                    :disabled="v.bind"
                  ></BaseLink>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </template>
      <el-empty v-else description="不好意思，还没有数据呐~" :image-size="80"></el-empty>
    </template>
  </BindDialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import BaseInfo from '@/components/BaseInfo.vue'
import BindDialog from '../components/BindDialog.vue'
import BindPlugFilter from './BindPlugFilter.vue'
import BaseLink from '@/components/probot/BaseLink.vue'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()
const workspaceId = computed(() => probotStore.workspaceId)
const workspaceList = computed(() => probotStore.workspaceList)

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Array,
    default: () => []
  }
})
const emits = defineEmits(['update:modelValue', 'update'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const activeNames = ref(['1'])
const bindData = ref({
  title: '绑定插件',
  toCreateTip: '还没有插件，去新建',
  toCreateRouter: {
    path: '/probot-space/',
    query: {
      tab: 'plug'
    }
  }
})
watch(
  () => workspaceList,
  (list) => {
    bindData.value.toCreateRouter.path =
      '/probot-space/' + (list?.value?.length ? list?.value[0]?.id : '')
  },
  {
    immediate: true,
    deep: true
  }
)
watch(
  () => workspaceId.value,
  (id) => {
    if (id) {
      bindData.value.toCreateRouter.path = '/probot-space/' + id
    }
  },
  {
    immediate: true,
    deep: true
  }
)

const handleChange = (val: string[]) => {
  console.log(val)
}

const bindClick = (value: any) => {
  props.data.forEach((item: any) => {
    item.plugins.forEach((v: any) => {
      if (v.id == value.id) {
        v.bind = true
      }
    })
  })
  let selected = props.data.map((item) => {
    const selectedPlugins = item?.plugins?.filter((v: any) => v.bind)
    return {
      ...item,
      plugins: selectedPlugins
    }
  })
  selected = selected.filter((item) => item.plugins.length)
  emits('update', selected)
}
</script>

<style lang="scss">
.bind-list-item {
  .oz-collapse {
    border-top: none;
  }
  .oz-collapse-item__header {
    height: auto;
  }
  .oz-collapse-item__content {
    padding-bottom: 10px;
  }
}
</style>
<style lang="scss" scoped>
.bind-list-item {
  .collapse-container {
    width: 100%;
  }
  .child-container {
    border-bottom: 1px solid #ddd;
    display: flex;
    justify-content: space-between;
    margin: 0px 0px 0 55px;
    padding: 5px 10px;
    font-size: 14px;
    line-height: 24px;
    color: rgba(0, 0, 0, 0.7);
    &:last-child {
      border: none;
    }
    .child-content {
      h3,
      p {
        font-size: 14px;
        color: rgb(107, 114, 128);
      }
    }
  }
}
</style>

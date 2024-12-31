<!--
 * @Description: 
 * @Date: 2024-01-11 17:33:19
 * @LastEditTime: 2024-01-29 15:00:36
-->
<template>
  <div class="lang-select">
    <el-dropdown @command="handleCommand" trigger="click" popper-class="lang-menu-container">
      <el-button class="dropdown-link"
        ><el-icon><ArrowDown /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="input">
            <el-input
              v-model="input"
              :placeholder="t('translate.langPlaceholder')"
              @click.stop=""
              @input="inputEvent"
          /></el-dropdown-item>
          <div class="lang-menu-content">
            <el-dropdown-item
              :command="item"
              v-for="(item, index) in allLangData"
              :key="index"
              :class="active === item ? 'active' : ''"
              >{{ item }}</el-dropdown-item
            >
          </div>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { t } from '@/locales'

interface Props {
  current: []
  allLang: Array<string>
}
const props = defineProps<Props>()
const emit = defineEmits(['selected'])

const allLangData = ref(props.allLang)
const active = ref()
const input = ref('')

const handleCommand = (item: string) => {
  if (item !== 'input') {
    emit('selected', item)
  }
}

const inputEvent = (query: string) => {
  allLangData.value = props.allLang.filter((item) =>
    item.toLowerCase().includes(query.toLowerCase())
  )
}

watch(
  () => props.allLang,
  (val) => {
    allLangData.value = val
  },
  {
    immediate: true,
    deep: true
  }
)
watch(
  () => props.current,
  (val) => {
    if (val?.length) {
      active.value = val[0].value
    } else {
      active.value = ''
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.lang-select {
}
.dropdown-link {
  border: none !important;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  outline: none;
  .oz-icon {
    font-size: 20px;
  }
}
</style>
<style lang="scss">
.lang-menu-container {
  .lang-menu-content {
    max-height: 340px;
    overflow: auto;
  }
  .active {
    color: var(--oz-menu-active-color);
  }
}
</style>

<template>
  <div class="edit-textarea">
    <el-input
      v-model="inputValue"
      type="textarea"
      :rows="props.rows"
      :disabled="props.disabled"
      :placeholder="props.placeholder"
    >
    </el-input>
    <div class="el-icon-full-screen" @click="openDrawer">
      <svg
        t="1732688688602"
        viewBox="0 0 1024 1024"
        version="1.1"
        xmlns="http://www.w3.org/2000/svg"
        p-id="2337"
        width="16"
        height="16"
      >
        <path
          d="M95.500388 368.593511c0 11.905658-9.637914 21.543572-21.543573 21.543572-11.877311 0-21.515225-9.637914-21.515225-21.543572V188.704684c0-37.502824 15.307275-71.575684 39.997343-96.265751s58.762928-39.997342 96.265751-39.997343h179.888827c11.905658 0 21.543572 9.637914 21.543572 21.515225 0 11.905658-9.637914 21.543572-21.543572 21.543573H188.704684c-25.625512 0-48.926586 10.488318-65.821282 27.383014s-27.383014 40.19577-27.383014 65.821282v179.888827z m559.906101-273.093123c-11.877311 0-21.515225-9.637914-21.515226-21.543573 0-11.877311 9.637914-21.515225 21.515226-21.515225h179.917174c37.502824 0 71.547337 15.307275 96.237404 39.997343s40.025689 58.762928 40.02569 96.265751v179.888827c0 11.905658-9.637914 21.543572-21.543572 21.543572-11.877311 0-21.515225-9.637914-21.515226-21.543572V188.704684c0-25.625512-10.488318-48.926586-27.411361-65.821282-16.894696-16.894696-40.19577-27.383014-65.792935-27.383014h-179.917174z m273.12147 559.906101c0-11.877311 9.637914-21.515225 21.515226-21.515226 11.905658 0 21.543572 9.637914 21.543572 21.515226v179.917174c0 37.474477-15.335622 71.547337-40.02569 96.237404s-58.734581 39.997342-96.237404 39.997343h-179.917174c-11.877311 0-21.515225-9.637914-21.515226-21.515225s9.637914-21.543572 21.515226-21.543573h179.917174c25.597165 0 48.898239-10.488318 65.792935-27.383014 16.923043-16.894696 27.411361-40.19577 27.411361-65.792935v-179.917174z m-559.934448 273.093123c11.905658 0 21.543572 9.666261 21.543572 21.543573s-9.637914 21.515225-21.543572 21.515225H188.704684c-37.502824 0-71.575684-15.307275-96.265751-39.997343s-39.997342-58.762928-39.997343-96.237404v-179.917174c0-11.877311 9.637914-21.515225 21.515225-21.515226 11.905658 0 21.543572 9.637914 21.543573 21.515226v179.917174c0 25.597165 10.488318 48.898239 27.383014 65.792935s40.19577 27.383014 65.821282 27.383014h179.888827z"
          fill="currentColor"
          p-id="2338"
        ></path>
      </svg>
    </div>
    <el-drawer title="编辑" v-model="drawerVisible" direction="rtl" size="50%">
      <el-input
        v-model="inputValue"
        type="textarea"
        :disabled="props.disabled"
        :placeholder="props.placeholder"
        :autosize="{ minRows: 20 }"
        class="prompt-editor"
      />
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  placeholder: {
    type: String,
    default: ''
  },
  rows: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['update:modelValue'])

const inputValue = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const drawerVisible = ref(false)

function openDrawer() {
  drawerVisible.value = true
}
</script>

<style lang="scss" scoped>
.edit-textarea {
  width: 100%;
  position: relative;

  :deep(.el-icon-full-screen) {
    display: block;
    position: absolute;
    right: 10px;
    top: 10px;
    color: var(--oz-color-primary);
    cursor: pointer;
  }

  :deep(.oz-textarea__inner) {
    &::-webkit-scrollbar {
        display: none;
    }
  }
}
.prompt-editor {
  height: 100%;
  :deep(.oz-textarea__inner) {
    height: 100% !important;
  }
}
</style>

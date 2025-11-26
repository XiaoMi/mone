<template>
  <div ref="element"></div>
</template>

<script setup lang="ts">
import { ref, nextTick } from "vue";
import TypeIt from "typeit";
import type {
  Message as TypeMessage,
} from "@/stores/chat-context"

const props = defineProps({
  message: {
    type: Object as () => TypeMessage,
    required: true,
  },
});

const instance: any = ref();
const element = ref();

nextTick(() => {
  const setTypewriterCode = function (code: TypeMessage) {
    instance.value = new (TypeIt as any)(element.value, {
      speed: 10, //打字的速度,每步之间以毫秒为单位测量。
      lifeLike: false, // 使打字速度不规则，就好像真人在做一样。
      html: true,
      cursor: false,
    });
    instance.value.type(code.data.text).flush();
    instance.value.go();
  };

  setTypewriterCode(props.message);
});
</script>

<style></style>

<template>
  <el-empty :image-size="imageSize" :description="description" :image="imageUrl">
	<!-- {{children}} -->
  <slot></slot>
	</el-empty>
</template>
<script lang="ts">
import svg_404 from "./images/404.svg"
import empty from "./images/empty.svg"
import i18n from '../../lang'
import { defineComponent, reactive, toRefs, onMounted } from "vue"

export default defineComponent({
  props:{
    imageSize:{
      type: Number,
      default: 360
    },
    description: {
      type: String,
      default: i18n.t('noData')
    },
    type: {
      type: String,
      default: 'empty'
    }
  },
  setup(props, ctx){
    const state = reactive({
      imageUrl: empty,
      imageSize: props.imageSize,
      description: props.description
    })
    onMounted(() => {
      switch (props.type) {
        case '404':
          state.imageUrl = svg_404
          break;
        default:
          state.imageUrl = empty
          break;
      }
    })
    return {
      ...toRefs(state)
    }
  }
});
</script>

<style lang="scss" scope>
  .el-empty{
    padding: 0;
  }
</style>
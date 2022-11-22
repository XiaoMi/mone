<template>
	<div class="wangeditor-container">
		<Editor
      v-model="html"
      :defaultConfig="editorConfig"
      :mode="mode"
      @onCreated="onCreated"
      @onFocus="handleFocus"
    />
	</div>
</template>
<script lang="ts">
import '@wangeditor/editor/dist/css/style.css'
import { Editor } from '@wangeditor/editor-for-vue'
import { SlateNode } from '@wangeditor/editor'
import debounce from "@/common/debounce"
import { defineComponent, reactive, toRefs, watch, nextTick, onBeforeUnmount } from "vue"
export default defineComponent({
  components: {
    Editor
  },
  props: {
    defaultValue: {
      default: ''
    }
  },
  setup(props, ctx){
    const state = reactive({
      editor: null,
      mode: "simple",
      html: '',
      editorConfig: {
        readOnly: true,
        autoFocus: false
      }
    })

    // method
    const onCreated = (editor) => {
      state.editor = Object.seal(editor) // 一定要用 Object.seal() ，否则会报错
      if (props.defaultValue !== state.html) {
      	state.html = props.defaultValue
      }
      handleHeaders()
      // 触发focus 设置只读
      editor.focus()
    }
    const handleHeaders = debounce(function () {
      nextTick(() => {
        let headers = state.editor.getElemsByTypePrefix('header')
        if (headers && headers.length) {
          headers = headers.map(v => {
            return {
              type: v.type,
              id: v.id,
              content: SlateNode.string(v)
            }
          })
          ctx.emit("onHeaders", headers)
        }
      })
    }, 500, false)

    const handleFocus = (editor) => {
      editor.disable()
    }

    watch(() => props.defaultValue, (val) => {
      if (state.editor) {
        state.html = val
      }
    },{
      immediate: true
    })

    onBeforeUnmount(() => {
      state.html = ""
      if (state.editor == null) return
      state.editor.destroy() // 组件销毁时，及时销毁编辑器
    })

    return{
      handleFocus,
      onCreated,
      ...toRefs(state)
    }
  },
})
</script>
<style scoped>
@import url("../../plugins/normal.css");
.wangeditor-container >>> ul li {
	list-style: initial
}
.wangeditor-container >>> ol li {
	list-style: auto
}
.wangeditor-container >>> a{
	color: #5897ff !important;
	cursor: pointer;
}
</style>

<template>
  <div class="j-json-editor-container">
    <div :id="jsoneditorId" class="jsoneditor-container"></div>
    <span v-if="canResize" @mousedown.stop="handleDown($event)" class="lashen-img" />
  </div>
</template>

<script lang="ts">
// https://github.com/josdejong/jsoneditor/blob/master/docs/api.md
import JSONEditor from 'jsoneditor/dist/jsoneditor.min.js'
import 'jsoneditor/dist/jsoneditor.min.css'
import { defineComponent, reactive, toRefs, watch, onMounted, onBeforeMount, onBeforeUnmount} from "vue"

export default defineComponent({
  props: {
    content: {
      default: () => {
        return {}
      }
    },
    jsonEditorOptions: {
      type: Object,
      default: () => {
        return {}
      }
    },
    jsoneditorId: {
      type: String,
      default: ''
    },
    templates: Array
  },
  setup(props, ctx){
    const state = reactive({
      jsoneditor: null,
      jsoneditorId: props.jsoneditorId || 'jsoneditorId'+new Date().getTime() + ((Math.random() * 1000).toFixed(0) + ''),
      options: {
        mode: 'code',
        modes: ['code', 'form', 'text', 'tree'], // 'code', 'form', 'text', 'tree', 'view', 'preview'
        name: 'Parameters',
        onEditable: () => true,
        search: false,
        navigationBar: false,
        mainMenuBar: true,
        statusBar: false,
        showLineNumbers: true,
        indentation: 2,
        canResize: false // 如果设置为true，则外部父级高度不能为固定值
      },
      moveData: {
        y1: null,
        y2: null,
        initHeight: 150
      }
    })

    watch(() => props.content, (newVal, oldVal) => {
      if (newVal !== oldVal && state.jsoneditor) {
        state.jsoneditor.update(newVal)
      }
    })

    watch(() => state.moveData, (newVal)=> {
      if (newVal.y2 !== null) {
        let reduce = newVal.y2 - newVal.y1 + newVal.initHeight
        if (reduce > newVal.initHeight) {
          (document.querySelector('.j-json-editor-container') as any).style.height = `${reduce}px`
        }
      }
    },{
      immediate: true,
      deep: true
    })

    const handleDown = (e):void => {
      state.moveData.y1 = e.pageY
      document.onmousemove = mouseMove
    }
    const mouseMove = (e):void => {
      state.moveData.y2 = e.pageY
    }
    const handleUp = (e):void => {
      document.onmousemove = null
      if (state.moveData.y1 && state.moveData.y2) {
        let initHeight = state.moveData.y2 - state.moveData.y1 + state.moveData.initHeight
        state.moveData = {
          y1: null,
          y2: null,
          initHeight: initHeight >= 300 ? initHeight : 300
        }
      }
    }

    onMounted(() => {
      const options = {
        onChange: () => {
          if (state.jsoneditor) {
            try {
              const json = state.jsoneditor.getText()
              // that.$emit('json-change', json)
            } catch (error) {}
          }
        },
        onChangeText: () => {
          if (state.jsoneditor) {
            try {
              let json = state.jsoneditor.getText()
              try {
                json = JSON.parse(json)
              } catch (error) {}
              if ((typeof json === 'object') || !json) {
                ctx.emit('json-change', json)
              }
            } catch (error) {}
          }
        },
        onValidationError: function (errors) {
          if (errors.length) {
            try {
              ctx.emit('json-error', errors)
            } catch (error) {}
          }
        },
        onError: function (error) {
          console.log(error)
        },
        templates: props.templates,
        ...state.options,
        ...props.jsonEditorOptions
      }
      state.jsoneditor = new JSONEditor(document.querySelector(`#${state.jsoneditorId}`), options, props.content || {})
      window.addEventListener('mouseup', handleUp, true)
    })
    onBeforeUnmount(() => {
      window.removeEventListener('mouseup', handleUp, true)
    })

    onBeforeMount(() => {
      if (state.jsoneditor) {
        state.jsoneditor.destroy()
        state.jsoneditor = null
      }
    })

    return {
      handleDown,
      canResize: props.jsonEditorOptions.canResize || state.options.canResize,
      ...toRefs(state)
    }
  }
})
</script>

<style scoped>
  .jsoneditor-container {
    width: 100%;
    height: 100%;
  }
  >>> .ace_gutter {
    z-index: auto;
  }
	>>> .jsoneditor-poweredBy {
		display: none;
	}
.j-json-editor-container{
  position: relative;
  height: 100%;
}
.j-json-editor-container .lashen-img{
  position: absolute;
  right: 0;
  bottom: 0;
  z-index: 100;
  width: 16px;
  height: 16px;
  cursor: pointer;
}
.j-json-editor-container .lashen-img::before{
  position: absolute;
  width: 10px;
  height: 1px;
  background: #333;
  content: '';
  transform: rotate(-45deg);
  right: 1px;
  bottom: 4px;
  opacity: 0.8;
}
.j-json-editor-container .lashen-img::after{
  position: absolute;
  width: 4px;
  height: 1px;
  background: #333;
  content: '';
  transform: rotate(-45deg);
  right: 2px;
  bottom: 2px;
  opacity: 0.8;
}
</style>

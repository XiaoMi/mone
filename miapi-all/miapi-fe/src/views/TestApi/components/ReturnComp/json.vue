<template>
	<div class="test-return-json-container">
    <p class="test-return-status">
      <span v-if="apiTestProtocol !== protocol_type.Dubbo">{{apiTestResponse.status || '0'}}</span>
      <span v-else />
      <span>Size : {{apiTestResponse.size || '0'}}b | Time : {{apiTestResponse.cost || '0'}}ms</span>
    </p>
    <div class="test-return-json">
      <JsonEditor :key="moveData.initHeight" :content="defaultContent" :jsonEditorOptions="jsonEditorOptions"/>
      <span @mousedown.stop="handleDown($event)" class="lashen-img" />
    </div>
  </div>
</template>

<script>
import JsonEditor from '@/components/JsonEditor'
import { mapGetters } from 'vuex'
import { PROTOCOL_TYPE } from '@/views/constant'

export default {
  name: 'ResponseJson',
  components: {
    JsonEditor
  },
  computed: {
    ...mapGetters([
      'apiTestResponse',
      'apiTestProtocol'
    ]),
    protocol_type () {
      return PROTOCOL_TYPE
    }
  },
  data () {
    return {
      jsonEditorOptions: {
        mainMenuBar: false,
        onEditable: () => false
      },
      defaultContent: {},
      moveData: {
        y1: null,
        y2: null,
        initHeight: 300
      }
    }
  },
  watch: {
    apiTestResponse: {
      handler (val) {
        if (val) {
          let defaultContent = val.content || {}
          try {
            defaultContent = JSON.parse(defaultContent)
          } catch (error) {}
          this.defaultContent = defaultContent
        } else {
          this.defaultContent = {}
        }
      }
    },
    moveData: {
      handler (newVal) {
        if (newVal.y2 !== null) {
          let reduce = newVal.y2 - newVal.y1 + newVal.initHeight
          if (reduce > 300) {
            document.querySelector('.test-return-json').style.height = `${reduce}px`
          }
        }
      },
      deep: true
    }
  },
  mounted () {
    window.addEventListener('mouseup', this.handleUp, true)
  },
  destroyed () {
    window.removeEventListener('mouseup', this.handleUp, true)
  },
  methods: {
    handleDown (e) {
      this.moveData.y1 = e.pageY
      document.onmousemove = this.mouseMove
    },
    mouseMove (e) {
      this.moveData.y2 = e.pageY
    },
    handleUp (e) {
      document.onmousemove = null
      if (this.moveData.y1 && this.moveData.y2) {
        let initHeight = this.moveData.y2 - this.moveData.y1 + this.moveData.initHeight
        this.moveData = {
          y1: null,
          y2: null,
          initHeight: initHeight >= 300 ? initHeight : 300
        }
      }
    }
  }
}
</script>
<style>
.test-return-json-container .test-return-status {
  height: 32px;
  margin: 0 0 20px;
  padding: 0 20px;
  background: rgba(112, 182, 3, 1);
  color: #fff;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: 3px;
}
.test-return-json-container .test-return-json{
	height: 300px;
  position: relative;
}
.test-return-json-container .test-return-json .jsoneditor {
	border-color: #e6e6e6;
}
.test-return-json-container .test-return-json .ace_gutter {
	background: #f1f0f0;
}
.test-return-json-container .test-return-json .lashen-img{
  position: absolute;
  right: 0;
  bottom: 0;
  z-index: 100;
  width: 16px;
  height: 16px;
  cursor: pointer;
}
.test-return-json-container .test-return-json .lashen-img::before{
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
.test-return-json-container .test-return-json .lashen-img::after{
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

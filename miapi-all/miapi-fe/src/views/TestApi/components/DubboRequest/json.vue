<template>
	<div class="dubbo-request-json">
    <JsonEditor :key="isTestRequestEnd" :content="defaultContent" :jsonEditorOptions="jsonEditorOptions" @json-error="handleError" @json-change="handleChange"/>
  </div>
</template>

<script>
import JsonEditor from '@/components/JsonEditor'
import { mapGetters } from 'vuex'

export default {
  name: 'DubboResponseJson',
  components: {
    JsonEditor
  },
  computed: {
    ...mapGetters([
      'apiTestParameter',
      'isTestRequestEnd'
    ])
  },
  data () {
    return {
      jsonEditorOptions: {
        mainMenuBar: false
      },
      defaultContent: []
    }
  },
  watch: {
    isTestRequestEnd: {
      handler () {
        this.defaultContent = this.apiTestParameter || {}
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleChange (val) {
    	this.$store.dispatch('apitest/changeApiTestTarget', { parameter: val })
    },
    handleError () {
    	this.$store.dispatch('apitest/changeApiTestTarget', { parameter: [] })
    }
  }
}
</script>
<style>
.dubbo-request-json{
	height: 200px;
  width: 100%;
}
.dubbo-request-json .jsoneditor {
	border-color: #e6e6e6;
}
.dubbo-request-json .ace_gutter {
	background: #f1f0f0;
}
</style>

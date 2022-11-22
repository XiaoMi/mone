<template>
	<div class="dubbo-request-json">
    <JsonEditor :content="defaultContent" :jsonEditorOptions="jsonEditorOptions" @json-error="handleError" @json-change="handleChange"/>
  </div>
</template>

<script>
import JsonEditor from '@/components/JsonEditor'
import { mapGetters } from 'vuex'

export default {
  name: 'GRPCResponseJson',
  components: {
    JsonEditor
  },
  computed: {
    ...mapGetters([
      'grpcParams',
      'isTestRequestEnd'
    ])
  },
  data () {
    return {
      jsonEditorOptions: {
        mainMenuBar: false
      },
      defaultContent: {}
    }
  },
  watch: {
    isTestRequestEnd: {
      handler (val) {
        this.defaultContent = this.grpcParams.parameter || {}
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleChange (val) {
    	this.$store.dispatch('apitest/changeGrpcParam', { parameter: val || {} })
    },
    handleError () {
    	this.$store.dispatch('apitest/changeGrpcParam', { parameter: {} })
    }
  }
}
</script>
<style>
.dubbo-request-json{
	height: 200px;
}
.dubbo-request-json .jsoneditor {
	border-color: #e6e6e6;
}
.dubbo-request-json .ace_gutter {
	background: #f1f0f0;
}
</style>

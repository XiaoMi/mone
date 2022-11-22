<template>
	<div class="raw-json-container">
		<JsonEditor :content="content" @json-error="handleChangeJsonError" @json-change="handleChangeJson" :jsonEditorOptions="jsonEditorOptions"/>
	</div>
</template>

<script>
import JsonEditor from '@/components/JsonEditor'
import { mapGetters } from 'vuex'

export default {
  name: 'RawJson',
  components: {
    JsonEditor
  },
  data () {
    return {
      jsonEditorOptions: {
        mainMenuBar: false
      },
      content: {}
    }
  },
  computed: {
    ...mapGetters([
      'apiTestJsonBody'
    ])
  },
  mounted () {
    this.content = this.apiTestJsonBody || {}
  },
  methods: {
    handleChangeJson (val) {
    	this.$store.dispatch('apitest/changeApiTestTarget', { jsonBody: val })
    },
    handleChangeJsonError (errs) {
    	this.$store.dispatch('apitest/changeApiTestTarget', { jsonBody: {} })
      // this.$emit('changeIsError', !!errs.length)
    }
  }
}
</script>
<style>
.raw-json-container{
	height: 300px;
}
.raw-json-container .jsoneditor {
	border-color: #e6e6e6;
}
.raw-json-container .ace_gutter {
	background: #f1f0f0;
}
</style>

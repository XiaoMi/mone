<template>
	<div class="return-json-container">
		<JsonEditor :content="defaultContent" @json-error="handleChangeJsonError" @json-change="handleChangeJson" :jsonEditorOptions="jsonEditorOptions"/>
	</div>
</template>

<script>
import JsonEditor from '@/components/JsonEditor'
import { mapGetters } from 'vuex'

export default {
  name: 'ReturnJson',
  components: {
    JsonEditor
  },
  data () {
    return {
      jsonEditorOptions: {
        mainMenuBar: false
      },
      defaultContent: {},
      jsonContent: {}
    }
  },
  computed: {
    ...mapGetters([
      'mockData'
    ])
  },
  watch: {
    jsonContent: {
      handler (val) {
        this.$store.dispatch('addmock/changeAddMockData', {
          mockJsonData: val
        })
      },
      deep: false
    },
    "mockData.requestTime": {
      handler () {
        this.init()
      }
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      let defaultContent = this.mockData.mockJsonData || {}
      try {
        defaultContent = JSON.parse(defaultContent)
      } catch (error) {}
      this.defaultContent = defaultContent
    },
    handleChangeJson (val) {
      this.jsonContent = val
      // this.disabledSubmit = false
      this.$emit('changeIsError', false)
    },
    handleChangeJsonError (errs) {
      this.$emit('changeIsError', !!errs.length)
    }
  }
}
</script>
<style>
.return-json-container{
	height: 300px;
}
.return-json-container .jsoneditor {
	border-color: #e6e6e6;
}
.return-json-container .ace_gutter {
	background: #f1f0f0;
}
</style>

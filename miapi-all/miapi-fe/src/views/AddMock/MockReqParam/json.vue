<template>
	<div class="return-json-container">
		<JsonEditor :content="defaultContent" @json-error="handleChangeJsonError" @json-change="handleChangeJson" :jsonEditorOptions="jsonEditorOptions"/>
	</div>
</template>

<script>
import JsonEditor from '@/components/JsonEditor'
import { mapGetters } from 'vuex'

export default {
  name: 'MockRequestJson',
  components: {
    JsonEditor
  },
  data () {
    return {
      jsonEditorOptions: {
        mainMenuBar: false,
        onEditable: () => true
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
          mockRequestRaw: JSON.stringify(val)
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
      let defaultContent = this.mockData.mockRequestRaw || {}
      try {
        defaultContent = JSON.parse(defaultContent)
      } catch (error) {}
      this.defaultContent = defaultContent
    },
    handleChangeJson (val) {
      if (this.mockData.isDefault) {
        return
      }
      this.jsonContent = val
      this.$emit('changeIsError', false)
    },
    handleChangeJsonError (errs) {
      if (this.mockData.isDefault) {
        return
      }
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

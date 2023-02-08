<template>
	<div class="mock-return-container">
		<el-radio-group v-model="radioActive" size="small">
      <el-radio-button :label="radioType.FORM" >{{$i18n.t('formEntry')}}</el-radio-button>
      <el-radio-button :label="radioType.JSON">{{$i18n.t('jsonEntry')}}</el-radio-button>
    </el-radio-group>
		<div class="return-wrap">
			<ReturnTable v-show="radioActive === radioType.FORM" :formJsonData="formJsonData" @changeFormJsonData="changeFormJsonData"/>
			<ReturnJson v-show="radioActive === radioType.JSON" @changeIsError="changeIsError"/>
		</div>
	</div>
</template>
<script>
import ReturnTable from './table.vue'
import ReturnJson from './json.vue'
import { mapGetters } from 'vuex'
import { RADIO_TYPE } from '@/views/ApiList/constant'

export default {
  name: 'MockRetuenParam',
  components: {
    ReturnTable,
    ReturnJson
  },
  data () {
    return {
      radioActive: RADIO_TYPE.FORM,
      formJsonData: []
    }
  },
  computed: {
    ...mapGetters([
      'mockData'
    ]),
    radioType () {
      return RADIO_TYPE
    }
  },
  watch: {
    radioActive (val, old) {
      if (val !== old) {
        this.$store.dispatch('addmock/changeAddMockData', { mockDataType: val })
      }
    },
    "mockData": {
      handler (val) {
        if (val) {
          this.radioActive = val.mockDataType
        }
      },
      immediate: true,
      deep: true
    },
    "mockData.requestTime": {
      handler () {
        if (this.mockData.mockRule && this.mockData.mockRule.length) {
          this.formJsonData = this.mockData.mockRule
        }
      }
    },
    formJsonData: {
      handler (val) {
        this.$store.dispatch('addmock/changeAddMockData', {
          mockRule: val
        })
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    changeIsError (bool) {
      this.$emit('changeIsError', bool)
    },
    changeFormJsonData (arr) {
      this.formJsonData = arr
    }
  }
}
</script>

<style>
.mock-return-container .return-wrap{
	margin-top: 10px;
}
</style>

<template>
	<div class="mock-request-container">
		<el-radio-group v-model="radioActive" size="small">
      <el-radio-button :label="radioType.FORM" >{{$i18n.t('formEntry')}}</el-radio-button>
      <el-radio-button :disabled="mockData.isDefault" :label="radioType.JSON">{{$i18n.t('jsonEntry')}}</el-radio-button>
    </el-radio-group>
		<div class="request-wrap">
			<MockReqTable v-show="radioActive === radioType.FORM"/>
			<MockRequestJson v-show="radioActive === radioType.JSON" @changeIsError="changeIsError"/>
		</div>
	</div>
</template>
<script>
import MockReqTable from './table.vue'
import MockRequestJson from './json.vue'
import { mapGetters } from 'vuex'
import { RADIO_TYPE } from '@/views/ApiList/constant'

export default {
  name: 'MockRetuenParam',
  components: {
    MockReqTable,
    MockRequestJson
  },
  data () {
    return {
      radioActive: RADIO_TYPE.FORM
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
        this.$store.dispatch('addmock/changeAddMockData', { mockRequestParamType: val })
      }
    },
    "mockData": {
      handler (val) {
        if (val) {
          this.radioActive = val.mockRequestParamType
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    changeIsError (bool) {
      this.$emit('changeIsError', bool)
    }
  }
}
</script>

<style>
.mock-request-container .request-wrap{
	margin-top: 10px;
}
</style>

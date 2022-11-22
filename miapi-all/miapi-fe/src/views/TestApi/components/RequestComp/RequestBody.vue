<template>
	<div class="request-body">
		<div class="import-btn">
			<el-radio-group v-model="radio">
				<el-radio :label="radioType.FORM">Form-data</el-radio>
				<el-radio :label="radioType.RAW">Raw</el-radio>
			</el-radio-group>
		</div>
		<el-table
			v-if="radio === radioType.FORM"
			ref="multipleBodyTable"
			:data="tableData"
			tooltip-effect="dark"
			style="width: 100%"
			border
      @select="handleSelect"
			@select-all="handleSelectAll">
			<el-table-column
				type="selection"
        :selectable="handleSelectable"
        align='center'
				width="55">
			</el-table-column>
			<el-table-column
				width="260">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.name')}}</span>
        </template>
				<template #default="scope"><el-input :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="scope.row.paramKey"/></template>
			</el-table-column>
			<el-table-column
				prop="paramType"
				width="200px"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterType')}}</span>
        </template>
				<template #default="scope">
					<el-select
						v-model="scope.row.paramType"
						:placeholder="$i18n.t('placeholder.pleaseEnterType')">
						<el-option
							v-for="(name, value) in dataType"
              :disabled="disabledOption(value)"
							:key="value"
							:label="name"
							:value="value">
						</el-option>
					</el-select>
				</template>
			</el-table-column>
			<el-table-column
				prop="headerValue">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
        </template>
				<template #default="scope"><el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.paramValue"/></template>
			</el-table-column>
			<el-table-column
        width="90"
        align="center">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope"><el-button @click="handledelete(scope)" text type="primary">{{$i18n.t('btnText.delete')}}</el-button></template>
			</el-table-column>
		</el-table>
		<div v-else>
			<RawJson/>
		</div>
	</div>
</template>

<script>
import { HEADER, DATA_TYPE, DATA_TYPE_KEY } from '@/views/constant'
import RawJson from './json.vue'
import { mapGetters } from 'vuex'
import { RADIO_TYPE } from '@/views/TestApi/constant'
import debounce from '@/common/debounce'

export default {
  name: 'RequestBody',
  components: { RawJson },
  data () {
    return {
      radio: RADIO_TYPE.FORM,
      tableData: [],
      multipleSelection: [],
      isFromStore: false
    }
  },
  computed: {
    ...mapGetters([
      'apiTestBody',
      'isTestRequestEnd',
      "apiTestBodyType"
    ]),
    headers () {
      return HEADER
    },
    dataType () {
      return DATA_TYPE
    },
    radioType () {
      return RADIO_TYPE
    }
  },
  watch: {
    'tableData': {
      handler: function (val, oldVal) {
        if (this.tableData.every(item => !!item.paramKey && !!item.paramType)) {
          this.tableData.push({
            paramKey: '',
            paramValue: '',
            paramType: undefined
          })
        }
        this.multipleSelection = this.multipleSelection.filter(v => v.paramKey && v.paramValue && v.paramType)
        this.renderSelect()
      },
      immediate: true,
      deep: true
    },
    multipleSelection: {
      handler (val) {
        if (!this.isFromStore) {
    	    this.$store.dispatch('apitest/changeApiTestTarget', { body: val })
        } else {
          this.isFromStore = false
        }
      },
      deep: true
    },
    apiTestBodyType: {
      handler (newVal, old) {
        if (newVal !== old) {
          this.radio = newVal
        }
      },
      immediate: true,
      deep: true
    },
    radio: {
      handler (val) {
    		this.$store.dispatch('apitest/changeApiTestTarget', { apiTestBodyType: val })
        if (val === RADIO_TYPE.FORM) {
      		this.multipleSelection = [...this.apiTestBody]
          this.renderSelect()
        }
      },
      immediate: true
    },
    isTestRequestEnd: {
      handler () {
        this.init()
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    init () {
      this.isFromStore = true
      if (this.apiTestBody && Array.isArray(this.apiTestBody)) {
        this.radio = RADIO_TYPE.FORM
        this.tableData = [...this.apiTestBody]
        this.multipleSelection = [...this.apiTestBody].filter(v => v.paramKey && v.paramValue)
      } else if (this.apiTestBody && (Object.prototype.toString.call(this.apiTestBody) === '[object Object]')) {
        this.radio = RADIO_TYPE.RAW
      }
    },
    handleSelectable (row) {
      return row.paramKey && row.paramValue && row.paramType
    },
    renderSelect: debounce(function (val) {
      this.$nextTick(() => {
        this.tableData.forEach(v => {
          for (let i = 0; i < this.multipleSelection.length; i++) {
            let item = this.multipleSelection[i]
            if (v.paramKey === item.paramKey && v.paramValue === item.paramValue && v.paramType === item.paramType) {
              this.$refs.multipleBodyTable && this.$refs.multipleBodyTable.toggleRowSelection(v, true)
              break
            } else {
              this.$refs.multipleBodyTable && this.$refs.multipleBodyTable.toggleRowSelection(v, false)
            }
          }
        })
      })
    }, 200, false),
    handleSelect (val) {
      this.multipleSelection = val.filter(v => v.paramKey && v.paramValue && v.paramType)
    },
    handleSelectAll (val) {
      this.multipleSelection = val.filter(v => v.paramKey && v.paramValue && v.paramType)
    },
    handledelete (scope) {
      if (this.tableData.length > 1) {
        this.multipleSelection = this.multipleSelection.filter(item => !(item.paramKey === scope.row.paramKey && item.paramValue === scope.row.paramValue && item.paramType === scope.row.paramType))
        this.tableData = this.tableData.filter(item => !(item.paramKey === scope.row.paramKey && item.paramValue === scope.row.paramValue && item.paramType === scope.row.paramType))
      }
    },
    disabledOption (type) {
      return type === DATA_TYPE_KEY.json || type === DATA_TYPE_KEY.object || type === DATA_TYPE_KEY.array
    },
    renderHeader (h, { column, $index }) {
      return h('span', {}, [
        h('span', { style: { color: 'rgba(0, 0, 0, 0.65)', 'font-size': '14px' } }, column.label)
      ])
    }
  }
}
</script>
<style scoped>
.request-body{
  padding: 0 20px;
}
.request-body .import-btn {
	height: 50px;
	line-height: 50px;
	border-top: none;
	border-bottom: none;
}
.request-body .import-btn .btn {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.65);
	font-weight: normal;
}
.request-body >>> .el-table .el-table__header th {
  background: rgba(250, 250, 250, 1);
}
.request-body >>> .el-table td, .request-body >>> .el-table th.is-leaf{
	border-color: #e6e6e6;
	height: 50px;
}
.request-body >>> .el-table .cell{
  padding-left: 16px;
  padding-right: 16px;
}
</style>

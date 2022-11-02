<template>
  <div class="api-list-container">
		<el-table
			ref="apiList"
			:data="renderList"
			class="api-table"
			header-cell-class-name="table-header-cell"
			style="width: 100%">
			<el-table-column
				show-overflow-tooltip
				prop="apiName"
			>
      <template #header>
        <span class="common-table-title">{{$i18n.t('ApiClass.name')}}</span>
      </template>
			<template #default="scope">
				<span @click="handleGoDetail(scope.row)" style="color: #1890FF;cursor: pointer;">{{scope.row.apiName}}</span>
			</template>
			</el-table-column>
			<el-table-column
				prop="apiProtocol"
				show-overflow-tooltip
				:filters="apiProtocolFilters"
				:filter-method="filterHandler"
			>
      <template #header>
        <span class="common-table-title">{{$i18n.t('ApiClass.type')}}</span>
      </template>
			<template #default="scope">
				<span>{{protocol[scope.row.apiProtocol]}}</span>
			</template>
			</el-table-column>
			<el-table-column
				prop="apiURI"
				width="340"
			>
      <template #header>
        <span class="common-table-title">{{$i18n.t('ApiClass.interfacePath')}}</span>
      </template>
			<template #default="scope">
				<div class="api_url">
					<span v-if="scope.row.apiEnv" class="api-env">{{scope.row.apiEnv}}</span>
					<span v-if="`${scope.row.apiProtocol}`!==protocol_type.Dubbo && `${scope.row.apiProtocol}`!==protocol_type.Grpc" :class="{'request_type': true, [requestType[scope.row.apiRequestType]]:true}">{{requestType[scope.row.apiRequestType]}}</span>
					<span>{{scope.row.apiURI}}</span>
				</div>
			</template>
			</el-table-column>
			<el-table-column
				prop="apiStatus"
				show-overflow-tooltip
				:filters="apiStatusFilters"
				:filter-method="filterHandler"
			>
      <template #header>
        <span class="common-table-title">{{$i18n.t('ApiClass.interfaceStatus')}}</span>
      </template>
			<template #default="scope">
				<el-dropdown @command="handleCommand($event,scope.row)">
					<span class="el-dropdown-link">
						<var :class="{not:Number(scope.row.apiStatus) === showStatus.NOT,disabled:Number(scope.row.apiStatus) === showStatus.DISABLED}">·</var>{{apiStatus[scope.row.apiStatus]}}<el-icon :size="12"><ArrowDown /></el-icon>
					</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="v in Object.keys(apiStatus)" :key="v" :command="Number(v)">{{apiStatus[v]}}</el-dropdown-item>
            </el-dropdown-menu>
          </template>
				</el-dropdown>
			</template>
			</el-table-column>
			<el-table-column
				prop="apiUpdateTime"
				show-overflow-tooltip
			>
      <template #header>
        <span class="common-table-title">{{$i18n.t('uptateTime')}}</span>
      </template>
			<template #default="scope">
					{{moment(new Date(scope.row.apiUpdateTime)).format('YYYY-MM-DD HH:mm:ss')}}
				</template>
			</el-table-column>
      <template #empty>
        <div>
          <Empty/>
        </div>
      </template>
		</el-table>
		<p v-if="showPage" class="page-bar">
			<el-pagination
				background
				:pager-count="5"
				:page-size="pageSize"
				:current-page="pageNo"
				:page-sizes="[5, 10, 20, 50]"
				layout="prev, pager, next"
				@current-change="handleChangePage"
				:total="apiCount">
			</el-pagination>
		</p>
	</div>
</template>
<script>

import { getRecyclingStationApiList, getApiList, getAllApiList, searchApi, removeApi, deleteApi, addStar, removeStar, editApiStatus } from '@/api/apilist'
import debounce from '@/common/debounce'
import { PROTOCOL, PROTOCOL_TYPE, REQUEST_TYPE, API_STATUS, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { getApiListByIndex } from '@/api/apiindex'
import { mapGetters } from 'vuex'
import { PATH } from '@/router/constant'
import { GROUP_TYPE } from '../../constant'
import Empty from '@/components/Empty'
import moment from 'moment'

const STATUS = {
  'NOT': 0,
  'DISABLED': 2
}

export default {
  name: 'TableList',
  components: {
    Empty
  },
  data () {
    return {
      apiStatusFilters: [],
      apiProtocolFilters: [],
      pageNo: 1,
      pageSize: 15
    }
  },
  mounted () {
    if (this.groupID === '' || this.groupID === undefined || this.groupID === null) {
      this.$store.dispatch('apilist/changeGroupId', -1)
    	this.$store.dispatch('apilist/changeGroupDesc', '')
    }
    this.apiStatusFilters = Object.keys(API_STATUS).map(key => {
      return { text: API_STATUS[key], value: Number(key) }
    })
    this.apiProtocolFilters = Object.keys(PROTOCOL).map(key => {
      return { text: PROTOCOL[key], value: Number(key) }
    })
  },
  computed: {
    ...mapGetters([
      'groupID',
      'apiList',
      'indexApiList',
      'indexGroupID',
      'groupType',
      'apiCount'
    ]),
    protocol () {
      return PROTOCOL
    },
    protocol_type () {
      return PROTOCOL_TYPE
    },
    requestType () {
      return REQUEST_TYPE
    },
    apiStatus () {
      return API_STATUS
    },
    showStatus () {
      return STATUS
    },
    path () {
      return PATH
    },
    showPage () {
      return (this.groupType !== GROUP_TYPE.INDEX) && (this.apiList && this.apiList.length)
    },
    renderList () {
      if (this.groupType === GROUP_TYPE.API) {
        return this.apiList
      }
      return this.indexApiList
    },
    moment () {
      return moment
    }
  },
  watch: {
    groupID: {
      handler: debounce(function (newVal) {
        if (newVal === undefined) {
          return
        }
        if (this.groupType === GROUP_TYPE.API) {
          this.pageNo = 1
          this.init()
        }
      }, 200),
      immediate: true
    },
    indexGroupID: {
      handler: debounce(function (newVal) {
        if (!newVal) {
          return
        }
        if (this.groupType === GROUP_TYPE.INDEX) {
          this.pageNo = 1
          this.$store.dispatch('apiindex/getApiList', { indexGroupID: newVal, projectID: this.$utils.getQuery('projectID') })
        }
      }, 200),
      immediate: true
    }
  },
  methods: {
    handleChangePage (page) {
      this.pageNo = page
      this.init()
      try {
        this.$emit('turnPage')
      } catch (error) {}
    },
    init () {
      if (this.groupType === GROUP_TYPE.API) {
        this.$store.dispatch("apilist/apiList", { projectID: this.$utils.getQuery("projectID"), pageNo: this.pageNo, pageSize: this.pageSize })
      } else {
        this.$store.dispatch("apiindex/getApiList", { projectID: this.$utils.getQuery("projectID") })
      }
      if (this.$refs.apiList && this.$refs.apiList.bodyWrapper) {
        this.$refs.apiList.bodyWrapper.scrollTop = 0
      }
    },
    handleGoDetail (row) {
    	let projectID = this.$utils.getQuery('projectID')
      let query = {
        projectID,
        apiID: row.apiID,
        apiProtocol: row.apiProtocol
      }
      if (row.projectID && (Number(projectID) !== Number(row.projectID))) {
        query.indexProjectID = row.projectID
      }
      this.$router.push({ path: PATH.API_DETAIL, query: query })
    },
    filterHandler (value, row, column) {
      const property = column['property']
      return row[property] === value
    },
    handleCommand (t, r) {
      editApiStatus({
        apiID: r.apiID,
        projectID: this.$utils.getQuery('projectID'),
        status: t
      }).then(data => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.init()
        }
      }).catch(e => {})
    }
  }
}
</script>
<style scoped>
.api-list-container {
  position: relative;
  height: 100%;
}
.api-list-container >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border: 1px solid rgba(232, 232, 232, 1);
	border-right: none;
	border-left: none;
}
.api-list-container >>> .table-header-cell .el-table__column-filter-trigger {
	margin-left: 4px;
}
.api-list-container >>> .table-header-cell:first-child{
	border-left: 1px solid rgba(232, 232, 232, 1);
	border-top-left-radius: 4px;
}
.api-list-container >>> .table-header-cell:last-child{
	border-right: 1px solid rgba(232, 232, 232, 1);
	border-top-right-radius: 4px;
}
.api-list-container .page-bar {
	text-align: right;
	padding-top: 10px;
}
.api-table{
	font-size: 14px;
  padding-bottom: 4px;
}
.api-table::after{
	content: '';
	width: 100%;
	height: 1px;
	background: #fff;
	position: absolute;
	left: 0;
	bottom: 0;
	z-index: 6;
}
.api-table >>> th>.cell {
	padding-left: 16px;
}
.api-table >>> .el-table__row td{
	padding: 15px 0;
}
.api-table >>> .el-table__row .cell {
	padding-left: 16px;
}
.api-table .api_url {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.api-table .api_url span{
	display: inline-block;
}
.api-table .api_url span.api-env {
  background: #00A854;
  color: #fff;
  font-size: 12px;
  border-radius: 3px;
  display: inline-block;
  height: 18px;
  line-height: 18px;
  text-align: center;
  padding: 0 4px;
	white-space: nowrap;
	margin-right: 8px;
}
.api-table .api_url .request_type {
	border-radius: 3px;
	margin-right: 8px;
	font-size: 12px;
	height: 18px;
	padding: 0 4px;
	line-height: 18px;
	color: #1890FF;
	background-color: #eef8ff;
	border: 1px solid #3c9cf7;
	text-align: center;
	white-space: nowrap;
}
.api-table .api_url .request_type.GET{
	background-color: rgba(246, 255, 237, 1);
	border: 1px solid rgba(183, 235, 143, 1);
	color: #52C41A;
}
.api-table .api_url .request_type.PUT{
	background-color: rgba(246, 255, 237, 1);
	border: 1px solid rgba(183, 235, 143, 1);
	color: #52C41A;
}
.api-table .api_url .request_type.DELETE{
	background-color: rgba(240, 54, 7, 0.1);
	border: 1px solid rgba(240, 54, 7, 1);
	color: rgba(240, 54, 7, 1);
}
.api-table .el-dropdown-link {
	cursor: pointer;
}
.api-table .el-dropdown-link var {
	font-size: 34px;
	color: #00A854;
	font-style: normal;
	display: inline-block;
	vertical-align: middle;
}
.api-table .el-dropdown-link var.not{
	color: #F04134;
}
.api-table .el-dropdown-link var.disabled{
	color: #ccc;
}
</style>

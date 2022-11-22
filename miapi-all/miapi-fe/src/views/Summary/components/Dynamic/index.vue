<template>
	<div id="d-dynamic" class="d-dynamic">
		<!-- <h4>项目动态</h4> -->
    <template v-if="tableData.length">
      <ul ref="dynamic-list">
        <li v-for="(v,index) in tableData" :key="index">
          <div class="dynamic-left">
            <el-image :src="loadImg(v.opUsername)" fit="cover"></el-image>
            <div>
              <p>{{v.opUsername}} {{handleType(v)}} <span>{{handleTarget(v)}}</span>  {{v.opDesc}}</p>
              <p>{{handleTime(v)}}</p>
            </div>
          </div>
          <!-- <el-button size="medium">变更记录</el-button> -->
        </li>
      </ul>
      <div class="pagination-wrap">
        <el-pagination
          background
          layout="prev, pager, next"
          hide-on-single-page
          @current-change="handleChangePage"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="logCount">
        </el-pagination>
      </div>
    </template>
    <template v-else >
      <Empty/>
    </template>
	</div>
</template>

<script>
import { getProjectLogList } from '@/api/projectdynamic'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import Empty from "@/components/Empty"
import moment from "moment"

export default {
  name: 'Dynamic',
  components: {
    Empty
  },
  data () {
    return {
      currentPage: 1,
      pageSize: 20,
      logCount: 0,
      tableData: [],
      imgResult: [],
      ranNum: 23,
      imgObj: {},
      curentIndex: 0
    }
  },
  computed: {
    scrollContainer () {
      return document.getElementsByClassName('summary-doc-container')[1]
    }
  },
  mounted () {
    let arr = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
    let result = []

    for (let j = 0; j < this.ranNum; j++) {
      arr.push(j)
    }

    for (let i = 0; i < this.ranNum; i++) {
      let ran = Math.floor(Math.random() * (arr.length - i))
      result.push(arr[ran])
      arr[ran] = arr[arr.length - i - 1]
    };

    this.imgResult = result.map(index => require(`./img/${index}.jpeg`))
    this.getList()
  },
  methods: {
    loadImg (opUsername) {
      if (this.imgObj[opUsername]) {
        return this.imgObj[opUsername]
      } else {
        let img = this.imgResult[this.curentIndex % this.ranNum]
        this.curentIndex += 1
        this.imgObj[opUsername] = img
        return img
      }
    },
    handleChangePage (page) {
      this.getList(page)
      this.currentPage = page
      document.querySelector('#d-dynamic').scrollTo({ top: 0 })
    },
    getList (page) {
      page = page || this.currentPage
      getProjectLogList({ projectID: this.$utils.getQuery('projectID'), page, pageSize: this.pageSize }).then((response) => {
        if (response.message === AJAX_SUCCESS_MESSAGE) {
          let res = response.data || {}
          this.tableData = res.logList || []
          this.logCount = res.logCount || 0
          this.$refs['dynamic-list'].scrollTop = 0
        }
      }).catch(e => {})
    },
    handleTime (item) {
      if (Object.prototype.toString.call(item.opTime) === '[object String]') {
        return moment(new Date(item.opTime)).format("YYYY-MM-DD HH:mm:ss")
      }
      return `${item.opTime.date.year}-${item.opTime.date.month}-${item.opTime.date.day} ${item.opTime.time.hour}:${item.opTime.time.minute}:${item.opTime.time.second}`
    },
    handleType (item) {
      switch (item.opType) {
        case 0:
          return this.$i18n.t('dynamicList.add')
        case 1:
          return this.$i18n.t('dynamicList.modify')
        case 2:
          return this.$i18n.t('dynamicList.delete')
        case 3:
          return this.$i18n.t('dynamicList.other')
        default:
          return ''
      }
    },
    handleTarget (item) {
      switch (item.opTarget) {
        case 0:
          return this.$i18n.t('dynamicList.project')
        case 1:
          return 'API'
        case 2:
          return this.$i18n.t('dynamicList.apiGrouping')
        case 3:
          return this.$i18n.t('dynamicList.statusCode')
        case 4:
          return this.$i18n.t('dynamicList.statusCodeGrouping')
        case 5:
          return this.$i18n.t('dynamicList.projectEnvironment')
        case 6:
          return this.$i18n.t('dynamicList.teamCooperation')
        case 7:
          return this.$i18n.t('dynamicList.documentGrouping')
        case 8:
          return this.$i18n.t('dynamicList.documentation')
        default:
          return ''
      }
    }
  }
}
</script>

<style scoped>
.d-dynamic {
	padding: 0px 24px 12px;
  height: 100%;
  overflow-y: auto;
}
.d-dynamic::-webkit-scrollbar{
	display: none;
}
.d-dynamic h4 {
	margin-bottom: 20px;
}
/* .d-dynamic ul{
	max-height: calc(100vh - 242px);
	overflow-y: auto;
}
.d-dynamic ul::-webkit-scrollbar{
	display: none;
} */
.d-dynamic ul li{
	display: flex;
	align-items: center;
	justify-content: space-between;
	border-bottom: 1px solid #e6e6e6;
	padding: 20px 40px 20px 0;
}
.d-dynamic ul li .dynamic-left {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.d-dynamic ul li .dynamic-left .el-image {
	width: 40px;
	height: 40px;
	border-radius: 50%;
	margin-right: 16px;
  /* filter: blur(2px); */
}
.d-dynamic ul li .dynamic-left p {
	margin: 0;
	padding: 0;
	font-size: 14px;
}
.d-dynamic ul li .dynamic-left p:last-child{
	color: #aaa;
	margin-top: 4px;
}
.d-dynamic ul li .dynamic-left p span {
	font-weight: 650;
	color: #1890FF;
}
.d-dynamic .pagination-wrap {
	text-align: right;
	padding: 10px 0 0;
}
</style>

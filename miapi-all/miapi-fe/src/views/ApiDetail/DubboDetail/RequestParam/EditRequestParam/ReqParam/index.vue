<template>
	<div class="req-param dubbo">
		<el-table
			:data="tableData"
			border
			row-key="random"
      :indent="24"
			header-cell-class-name="table-header-cell"
			default-expand-all
			class="base-table"
			:tree-props="{children: 'itemValue', hasChildren: 'hasChildren'}"
			style="width: 100%">
			<el-table-column
				prop="itemName"
				class-name="table-paramKey"
				width="220">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.name')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="scope.row.isRoot" :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="scope.row.itemName" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="exampleValue"
				class-name="table-paramKey"
				width="220">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterName')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="scope.row.isRoot" :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="scope.row.exampleValue" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="itemClassStr"
				width="140px"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterType')}}</span>
        </template>
				<template #default="scope">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnterType')" v-model="scope.row.itemClassStr" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="required"
				:width="isEN ? '86px' : '54px'"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.required')}}</span>
        </template>
				<template #default="scope">
          <div style="width: 100%; text-align: center">
					  <el-checkbox v-model="scope.row.required"></el-checkbox>
          </div>
				</template>
			</el-table-column>
			<el-table-column
				prop="defaultValue"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
        </template>
				<template #default="scope">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.defaultValue" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="desc"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.instruction')}}</span>
        </template>
				<template #default="scope">
					<el-input :rows="1" type="textarea" :placeholder="$i18n.t('placeholder.enterDesc')" v-model="scope.row.desc" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				width="84px">
        <template #header>
          <span class="common-table-title">{{$i18n.t('addTo')}}</span>
        </template>
				<template #default="scope">
					<el-button :disabled="disabledChildrenTree(scope)" @click="handleAddChildren(scope)" text type="primary" size="small">{{$i18n.t('addSubdata')}}</el-button>
				</template>
			</el-table-column>
			<el-table-column
				:width="isEN ? '86px' : '54px'"
				align="center">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope">
          <el-button :disabled="scope.row.isRoot" size="small" text type="primary" @click="handledelete(scope)">{{$i18n.t('btnText.delete')}}</el-button>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>
<script>

export default {
  name: 'ReqParam',
  data () {
    return {
      isEN: false,
      tableData: [{
        random: 'root',
        allowableValues: [],
        defaultValue: "",
        desc: "",
        exampleValue: "",
        itemClassStr: '',
        itemName: 'root',
        itemTypeStr: '',
        itemValue: [{
          random: new Date().getTime(),
          allowableValues: [],
          defaultValue: "",
          desc: "",
          exampleValue: "",
          itemClassStr: '',
          itemName: '',
          itemTypeStr: '',
          itemValue: [],
          required: false
        }],
        required: false,
        isRoot: true
      }]
    }
  },
  computed: {
    dubboParam () {
      return this.$store.getters.dubboParam
    }
  },
  watch: {
    tableData: {
      handler: function (val, oldVal) {
        if (this.tableData[0].itemValue.every(item => !!item.itemClassStr)) {
          this.tableData[0].itemValue.push({
            random: new Date().getTime(),
            allowableValues: [],
            defaultValue: "",
            desc: "",
            exampleValue: "",
            itemClassStr: '',
            itemName: '',
            itemTypeStr: '',
            itemValue: [],
            required: false
          })
        }

        let arr = [...val].map(item => {
          let obj = { ...item }
          delete obj.random
          obj.itemValue = this.handleReserveValue(obj.itemValue, 'delete')
          return obj
        })

        this.$store.dispatch('apilist.add/changeDubboParam', {
          paramsLayerList: arr
        })
      },
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    if (this.dubboParam.paramsLayerList && this.dubboParam.paramsLayerList.length) {
      let tableData = []
      let children = []
      if (this.dubboParam.paramsLayerList[0] && this.dubboParam.paramsLayerList[0].itemName === 'root') {
        tableData.push({
          ...this.dubboParam.paramsLayerList[0],
          random: 'root',
          isRoot: true
        })
        children = this.dubboParam.paramsLayerList[0].itemValue || []
      } else {
        tableData.push({
          random: 'root',
          allowableValues: [],
          defaultValue: "",
          desc: "",
          exampleValue: "",
          itemClassStr: '',
          itemName: '',
          itemTypeStr: '',
          itemValue: [],
          required: false,
          isRoot: true
        })
        children = this.dubboParam.paramsLayerList
      }
      tableData[0].itemValue = children.map((item, index) => {
        let obj = {
          ...item,
          random: new Date().getTime() + (index + 1) * Math.random() * 10
        }
        if (Array.isArray(item.itemValue) && item.itemValue.length) {
          obj.itemValue = this.handleReserveValue(item.itemValue)
        }
        return obj
      })
      if (tableData[0].itemValue.every(item => !!item.itemClassStr)) {
        tableData[0].itemValue.push({
          random: new Date().getTime(),
          allowableValues: [],
          defaultValue: "",
          desc: "",
          exampleValue: "",
          itemClassStr: '',
          itemName: '',
          itemTypeStr: '',
          itemValue: [],
          required: false
        })
      }
      this.tableData = tableData
    }
  },
  methods: {
    handleReserveValue (arr, type) {
      let values = []
      for (let i = 0; i < arr.length; i++) {
        let obj = { ...arr[i] }
        if (type === 'delete') {
          delete obj.random
        } else {
          obj.random = new Date().getTime() + (i + 1) * Math.random() * 1000
        }
        if (Array.isArray(arr[i].itemValue) && arr[i].itemValue.length) {
          obj.itemValue = this.handleReserveValue(arr[i].itemValue, type)
        }
        values.push(obj)
      }
      return values
    },
    disabledChildrenTree (scope) {
      return !scope.row.itemName || !scope.row.itemClassStr || scope.row.isRoot
    },
    handleAddChildren (scope) {
      this.handleDeep(this.tableData, scope.row.random)
    },
    handleDeep (arr, target) {
      for (let i = 0; i < arr.length; i++) {
        if (arr[i].random === target) {
          arr[i].itemValue.push({
            random: new Date().getTime(),
            allowableValues: [],
            defaultValue: "",
            desc: "",
            exampleValue: "",
            itemClassStr: '',
            itemName: '',
            itemTypeStr: '',
            itemValue: [],
            required: false
          })
          break
        };
        if (arr[i].itemValue) {
          this.handleDeep(arr[i].itemValue, target)
        };
      }
    },
    handledelete (scope) {
      this.tableData = this.handleDeleteDeep(this.tableData, scope.row.random)
    },
    renderHeader (h, { column, $index }) {
      return h('span', {}, [
        h('span', { style: { color: 'rgba(0, 0, 0, 0.64)', fontSize: '14px' } }, column.label)
      ])
    },
    handleDeleteDeep (arr, target) {
      let array = [].concat(arr)
      for (let i = 0; i < array.length; i++) {
        if (array[i].random === target) {
          array.splice(i, 1)
          break
        };
        if (array[i].itemValue && array[i].itemValue.length) {
          array[i].itemValue = this.handleDeleteDeep(array[i].itemValue, target)
        };
      }
      return array
    }
  }
}
</script>
<style scoped>
.req-param.dubbo {
  margin-top: -20px;
}
.req-param .base-table >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.req-param .icon-modify {
	cursor: pointer;
	color: #1890FF;
	font-size: 14px;
}
.req-param .active_btn{
	background: #646669;
	border-color: #1890FF;
	color: #FFF;
}
.req-param .base-table >>> .cell {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
</style>

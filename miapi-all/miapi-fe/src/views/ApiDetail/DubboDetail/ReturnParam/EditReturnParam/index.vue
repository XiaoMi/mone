
<template>
  <div class="return-param">
		<!-- <el-button @click="handleOpenVisible" type="primary" size="small">导入Json</el-button> -->
    <el-table
			:data="tableData"
			border
			class="return-param-table"
			header-cell-class-name="table-header-cell"
			row-key="random"
      :indent="24"
			default-expand-all
			:tree-props="{children: 'itemValue', hasChildren: 'hasChildren'}"
			style="width: 100%">
			<el-table-column
				prop="itemName"
				width="220">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.name')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="scope.row.isRoot" :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="scope.row.itemName" autocomplete="off"></el-input>
				</template>
			</el-table-column>
      <el-table-column
				prop="exampleValue">
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
		<el-dialog
      :destroy-on-close="true"
      :center="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="importDialog.title"
      v-model="importDialog.visible"
			width="540px"
      append-to-body
    >
      <ImportDialog @handleOk="init" @handleChangeVisible="handleChangeVisible"/>
			<template #title>
        <div>{{importDialog.title}}</div>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import ImportDialog from './ImportDialog'
import { mapGetters } from 'vuex'

export default {
  name: 'EditReturnParam',
  components: { ImportDialog },
  data () {
    return {
      isEN: false,
      tableData: [],
      importDialog: {
        title: 'JSON转换返回参数',
        visible: false
      }
    }
  },
  computed: {
    ...mapGetters([
      'dubboParam'
    ])
  },
  methods: {
    init () {
      if (this.dubboParam.responseLayer && this.dubboParam.responseLayer.length) {
        let tableData = []
        let children = []
        if (this.dubboParam.responseLayer[0] && this.dubboParam.responseLayer[0].itemName === 'root') {
          tableData.push({
            ...this.dubboParam.responseLayer[0],
            random: 'root',
            isRoot: true
          })
          children = this.dubboParam.responseLayer[0].itemValue || []
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
          children = this.dubboParam.responseLayer
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
      } else {
        this.tableData = [{
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
    handleOpenVisible () {
      this.handleChangeVisible()
    },
    handledelete (scope) {
      this.tableData = this.handleDeleteDeep(this.tableData, scope.row.random)
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
    },
    handleChangeVisible (title) {
      this.importDialog = {
        title: title || this.importDialog.title,
        visible: !this.importDialog.visible
      }
    },
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
    disabledChildrenTree (scope) {
      return !scope.row.itemName || !scope.row.itemClassStr || scope.row.isRoot
    }
  },
  watch: {
    tableData: {
      handler: function (val) {
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
          responseLayer: arr
        })
      },
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    this.init()
  }
}
</script>
<style scoped>
.return-param h4{
	font-weight: normal;
	margin: 0 0 10px;
}
.return-param .return-param-table >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.return-param .return-param-table {
	margin: 10px 0 0;
}
.return-param .icon-modify {
	cursor: pointer;
	color: #1890FF;
	font-size: 14px;
}
.return-param .return-param-table >>> .cell {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
</style>

<template>
	<div class="controller-api-list">
		<ul>
			<li>
				<div :class="{'controller-label': true, 'isDubbo': isDubbo}">{{isDubbo ? $i18n.t('serviceName'):'Controller'}}：</div>
				<span>{{controllers.moduleClassName}}</span>
			</li>
			<li>
				<div :class="{'controller-label': true, 'isDubbo': isDubbo}">
					<p>{{isDubbo ? $i18n.t('funcName'):$i18n.t('ApiClass.path')}}：</p>
					<p class="has-selected">{{$i18n.t('controllerTable.hasSelected')}}{{checkedApis.length}}{{$i18n.t('controllerTable.items')}}</p>
				</div>
				<div>
					<el-checkbox class="all-select" :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">{{$i18n.t('selectAll')}}</el-checkbox>
					<el-checkbox-group v-model="checkedApis" @change="handleCheckedApisChange">
						<el-checkbox class="api-item" v-for="item in controllers.apiList" :checked="item.checked" :label="item.apiName" :key="item.apiName">{{item.apiName}}</el-checkbox>
					</el-checkbox-group>
				</div>
			</li>
		</ul>
		<div class="btns-wrap">
			<el-button @click="handleCancel">{{$i18n.t('btnText.cancel')}}</el-button>
			<el-button type="primary" @click="handleOk">{{$i18n.t('btnText.ok')}}</el-button>
		</div>
	</div>
</template>

<script>
export default {
  name: 'SelectApiDialog',
  data () {
    return {
      checkAll: false,
      checkedApis: [],
      isIndeterminate: false
    }
  },
  props: {
    controllers: {
      type: Object,
      default () {
        return {}
      }
    },
    isDubbo: {
      type: Boolean,
      default: false
    }
  },

  watch: {
    controllers: {
      handler (val) {
        if (val.apiList) {
          let arr = []
          val.apiList.forEach(item => {
            if (item.checked) {
              arr.push(item.apiName)
            }
          })
          this.handleCheckedApisChange(arr)
        }
      },
      immediate: true
    }
  },

  methods: {
    handleCheckAllChange (val) {
      this.checkedApis = val ? this.controllers.apiList.map(item => item.apiName) : []
      this.isIndeterminate = false
    },
    handleCheckedApisChange (value) {
      let checkedCount = value.length
      this.checkAll = checkedCount === this.controllers.apiList.length
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.controllers.apiList.length
    },
    handleCancel () {
      this.$emit('onCancel')
    },
    handleOk () {
      this.$emit('onOk', { serviceName: this.controllers.moduleClassName, paths: this.checkedApis })
      this.handleCancel()
    }
  }
}
</script>

<style scoped>
.controller-api-list{
	padding-top: 20px;
}
.controller-api-list li{
	display: flex;
	align-items: flex-start;
	justify-content: flex-start;
	margin-bottom: 20px;
}
.controller-api-list li .controller-label {
	margin-right: 8px;
	font-size: 14px;
	color: rgba(0, 0, 0, 0.84);
	line-height: 22px;
  white-space: nowrap;
  min-width: 78px;
}
.controller-api-list li .controller-label.isDubbo {
  min-width: 50px
}
.controller-api-list li .controller-label .has-selected {
	font-size: 12px;
	color: rgba(0, 0, 0, 0.4);
	margin-top: 12px;
}
.controller-api-list li >>> .el-checkbox__label {
	color: rgba(0, 0, 0, 0.64);
	font-weight: normal;
}
.controller-api-list li .all-select {
  height: 23px;
  margin-bottom: 6px;
}
.controller-api-list li .api-item{
  margin-bottom: 16px;
}
.controller-api-list .btns-wrap {
	text-align: right;
}
</style>

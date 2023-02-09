<template>
	<div class="add-env-menu-container">
		<div class="menus-title">
			<span>{{$i18n.t('environmentList')}}</span>
			<span class="add-btn" @click.stop="handleAdd"><el-icon :size="12"><Plus /></el-icon>{{$i18n.t('environment')}}</span>
		</div>
		<el-menu
      :default-active="activeTab"
      @select="handleSelect">
      <el-menu-item v-for="item in list" :key="item.id" :index="`${item.id}`">
        <template #title>
          <span class="add-env-menu-item-title">
            <em>{{item.envName}}</em>
            <el-tooltip v-if="!item.sysDefault" effect="dark" :content="$i18n.t('placeholder.deleteEnvironment')" placement="top">
              <el-icon :size="12" @click.stop="handleDeleteEnv(item)"><Delete /></el-icon>
            </el-tooltip>
          </span>
        </template>
      </el-menu-item>
    </el-menu>
	</div>
</template>
<script>
import { mapGetters } from 'vuex'
import { deleteApiEnv, getApiEnvById } from '@/api/apitest'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
export default {
  name: 'EnvLeftMenu',
  data () {
    return {
      activeTab: '',
      list: []
    }
  },
  computed: {
    ...mapGetters([
      'hostEnvList',
      'envData'
    ])
  },
  watch: {
    hostEnvList: {
      handler (val) {
        if (val && val.length) {
        	this.list = val || []
          this.changeSelectEnv(val[0])
        }
      },
      immediate: true,
      deep: true
    },
    activeTab (val) {
      if (val && (Number(val) !== this.envData.id)) {
        this.$store.dispatch('addEnv/changeAddEnvData', {
          id: Number(val)
        })
      }
    }
  },
  methods: {
    handleSelect (index, _) {
      this.activeTab = index
      let arr = this.list.filter(v => v.id === Number(index))
      if (arr.length) {
        this.changeSelectEnv(arr[0])
      }
    },
    changeSelectEnv (obj) {
      this.activeTab = `${obj.id}`
      let headers = obj.headers
      try {
        headers = JSON.parse(headers)
      } catch (error) {}
      if (!obj.isAdd) {
        getApiEnvById({
          envID: obj.id
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            headers = data.data.headers
            try {
              headers = JSON.parse(headers)
            } catch (error) {}
            this.$store.dispatch('addEnv/changeAddEnvData', {
              httpDomain: data.data.httpDomain,
              envName: data.data.envName,
              id: data.data.id,
              isAdd: false,
              headers: headers || []
            })
          }
        }).catch(e => {})
      } else {
        this.$store.dispatch('addEnv/changeAddEnvData', {
          httpDomain: obj.httpDomain,
          envName: obj.envName,
          id: obj.id,
          isAdd: !!obj.isAdd,
          headers: headers || []
        })
      }
    },
    handleAdd () {
      let obj = {
        envDesc: "",
        envName: this.$i18n.t('newEnvironment'),
        httpDomain: "http://",
        id: new Date().getTime(),
        projectId: this.$utils.getQuery('projectID'),
        sysDefault: false,
        isAdd: true,
        headers: []
      }
      this.list.unshift(obj)
      this.changeSelectEnv(obj)
    },
    handleDeleteEnv (row) {
      if (row.isAdd) {
        this.list.shift()
        if (this.list.length) {
      		this.changeSelectEnv(this.list[0])
        } else {
          this.$store.dispatch('addEnv/resetAddEnvData')
        }
        return
      }
      let projectID = this.$utils.getQuery('projectID')
      this.$confirm(`${this.$i18n.t('mockWarn.deleteTip')} ${row.envName}, ${this.$i18n.t('mockWarn.continue')}?`, this.$i18n.t('prompt'), {
        confirmButtonText: this.$i18n.t('btnText.ok'),
        cancelButtonText: this.$i18n.t('btnText.cancel'),
        type: 'warning'
      }).then(() => deleteApiEnv({ projectID, envID: row.id })).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message({
            type: 'success',
            message: this.$i18n.t('successDeleted')
          })
          this.$store.dispatch('apitest/getHostEnvList', projectID)
        }
      }).catch((e) => {})
    }
  }
}
</script>
<style scoped>
.add-env-menu-container {
	width: 212px;
	height: 100%;
	border-right: 1px solid #e6e6e6;
	overflow-y: auto;
}
.add-env-menu-container::-webkit-scrollbar{
	display: none;
}
.add-env-menu-container .menus-title {
	display: flex;
	align-items: center;
	justify-content: space-between;
	font-size: 14px;
	color: #555555;
	user-select: none;
	background: rgba(250, 250, 250, 1);
	height: 48px;
	border-bottom: 1px solid rgba(233, 233, 233, 1);
	padding: 0 16px 0 20px;
}
.add-env-menu-container .menus-title .add-btn {
	color: #108EE9;
	cursor: pointer;
}
.add-env-menu-container .menus-title .add-btn i {
	margin-right: 4px;
	vertical-align: 0px;
}
.add-env-menu-container .el-menu {
	border-right: none;
}
.add-env-menu-container .el-menu .el-menu-item {
	height: 45px;
	line-height: 45px;
}
.add-env-menu-container .el-menu .el-menu-item.is-active {
	background: rgba(230, 247, 255, 1);
	border-right: 2px solid rgba(16, 142, 233, 1);
}
.add-env-menu-container .el-menu .el-menu-item .add-env-menu-item-title{
	width: 100%;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.add-env-menu-container .el-menu .el-menu-item .add-env-menu-item-title em {
	font-style: normal;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	word-break: keep-all;
}
.add-env-menu-container .el-menu .el-menu-item .add-env-menu-item-title i {
	display: none;
}
.add-env-menu-container .el-menu .el-menu-item:hover .add-env-menu-item-title i {
	display: inline-block;
}
</style>

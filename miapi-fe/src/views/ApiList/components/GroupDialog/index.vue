<template>
	<div class="g-group-dialog">
		<el-form :label-position="isEN ? 'top': 'left'" :model="{groupName, group}" label-width="auto">
			<!-- <el-row v-if="group" :gutter="20">
				<el-col :span="24">
					<el-form-item label="分组:">
						<el-select style="width: 100%" size="medium" v-model="group" placeholder="默认分组">
              <el-option v-for="(v, index) in groupList" :key="index" :label="v.groupName" :value="v.groupID"></el-option>
            </el-select>
					</el-form-item>
				</el-col>
			</el-row> -->

			<el-row :gutter="20">
				<el-col :span="24">
					<el-form-item :label="`${$i18n.t('name')}:`">
						<el-input size="medium" :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="groupName" autocomplete="off"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="24">
					<el-form-item :label="`${$i18n.t('description')}:`">
						<el-input size="medium" :placeholder="$i18n.t('placeholder.enterDesc')" v-model="groupDesc" autocomplete="off"></el-input>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>

		<div class="dialog-footer">
			<el-button @click="handlechangevisible">{{$i18n.t('btnText.cancel')}}</el-button>
			<el-button :disabled="!groupName" type="primary" @click="handleSubmit">{{$i18n.t('btnText.ok')}}</el-button>
			<el-button v-if="showAddApi" :disabled="!groupName" type="primary" @click="handleSubmitAndGo">{{$i18n.t('importAPI')}}</el-button>
		</div>
	</div>
</template>

<script>
import { GROUP_TYPE } from '../../constant'

export default {
  name: 'GroupDialog',
  data () {
    return {
      isEN: false,
      groupName: '',
      group: undefined,
      groupDesc: '',
      showAddApi: false
    }
  },
  props: {
    type: {
      type: String,
      default: GROUP_TYPE.API
    },
    defaultData: {
      type: Object,
      default () {
        return {
          groupID: undefined,
          groupName: "",
          isChild: 0,
          childGroupList: []
        }
      }
    }
  },
  computed: {
    groupList () {
      if (this.type === GROUP_TYPE.API) {
        return this.$store.getters.groupList.filter(item => item.groupID > 0)
      } else {
        return this.$store.getters.indexGroupList.filter(item => item.groupID > 0)
      }
    }
  },
  watch: {
    defaultData: {
      handler (newVal) {
        this.groupName = newVal.groupName || ''
        this.group = newVal.groupID || undefined
        this.groupDesc = newVal.groupDesc || ''
      },
      immediate: true,
      deep: true
    },
    type: {
      handler (val) {
        this.showAddApi = val === GROUP_TYPE.INDEX
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
  },
  methods: {
    handlechangevisible () {
      this.$emit('handlechangevisible')
    },
    handleSubmit () {
      if (!this.groupName) {
        this.$message.error(this.$i18n.t('errorMessage.pleaseGroupName'))
        return
      }
      this.handlechangevisible()
      this.$emit('submit', { groupName: this.groupName, groupDesc: this.groupDesc, group: this.group, isChild: this.defaultData.isChild, indexDoc: this.defaultData.indexDoc || '' })
    },
    handleSubmitAndGo () {
      this.handlechangevisible()
      this.$emit('submitAndGo', { groupName: this.groupName, groupDesc: this.groupDesc, group: this.group, isChild: this.defaultData.isChild, indexDoc: this.defaultData.indexDoc || '' })
    }
  }
}
</script>

<style lang="scss" scoped>
	.g-group-dialog{
		padding: 16px 16px 0;
	}
	.dialog-footer{
		text-align: right;
	}
</style>

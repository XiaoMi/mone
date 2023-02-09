<template>
	<div class="project-add">
		<el-form ref="ruleForm" hide-required-asterisk :rules="rules" label-width="auto" :model="{projectName, projectVersion, desc, projectGroupID}" >
			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="18">
					<el-form-item prop="projectName" :label="`${$i18n.t('name')}:`">
						<el-input :placeholder="`${$i18n.t('placeholder.enterProjectName')}`" v-model="projectName" autocomplete="off"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="18">
					<el-form-item prop="projectGroupID" :label="`${$i18n.t('grouping')}:`">
						<el-select style="width: 100%" v-model="projectGroupID" :placeholder="`${$i18n.t('placeholder.selectGroup')}`">
							<el-option v-for="v in projectGroups" :key="v.groupId" :label="v.groupName" :value="v.groupId"></el-option>
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="18">
					<el-form-item prop="projectVersion" :label="`${$i18n.t('version')}:`">
						<el-input :placeholder="`${$i18n.t('placeholder.enterVersion')}`" v-model="projectVersion" autocomplete="off"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="18">
					<el-form-item prop="isPublic" :label="`${$i18n.t('authority')}:`">
						<el-radio-group v-model="isPublic">
							<el-radio-button :label="projectAuthority.PUBLIC.value">{{projectAuthority.PUBLIC.name}}</el-radio-button>
							<el-radio-button :label="projectAuthority.PRIVATE.value">{{projectAuthority.PRIVATE.name}}</el-radio-button>
						</el-radio-group>
						<p class="authority">{{$i18n.t('addProjectDialogTips')}}</p>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="18">
					<el-form-item prop="desc" :label="`${$i18n.t('description')}:`">
						<el-input type="textarea" :placeholder="`${$i18n.t('placeholder.enterDesc')}`" v-model="desc"></el-input>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<div class="dialog-footer">
			<el-button @click="handleclose">{{$i18n.t('btnText.cancel')}}</el-button>
			<el-button type="primary" @click="handleok">{{$i18n.t('btnText.ok')}}</el-button>
		</div>
	</div>
</template>

<script>
import { PROJECT_AUTHORITY } from '@/views/constant'

export default {
  name: 'AddProjectDialog',
  data () {
    return {
      projectGroupID: undefined,
      projectName: '',
      projectVersion: '',
      isPublic: 0, // 0私有 1共有
      desc: undefined,
      rules: {
        projectName: [
          { required: true, message: this.$i18n.t('placeholder.enterProjectName'), trigger: ['blur', 'change'] }
        ],
        projectVersion: [
          { required: true, message: this.$i18n.t('placeholder.enterVersion'), trigger: ['blur', 'change'] }
        ],
        projectGroupID: [
          { required: true, message: this.$i18n.t('placeholder.selectGroup'), trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  props: {
    row: {
      type: Object,
      default () {
        return {
          projectName: '',
          projectVersion: '',
          description: ''
        }
      }
    },
    curProjectGroupId: {
      default: undefined
    }
  },
  watch: {
    row: {
      handler (newVal) {
        this.projectName = newVal.projectName
        this.projectVersion = newVal.projectVersion
        this.desc = newVal.description
      },
      immediate: true,
      deep: true
    },
    curProjectGroupId: {
      handler (newVal) {
        if (newVal > 0) {
          this.projectGroupID = newVal
        }
      },
      immediate: true
    }
  },
  methods: {
    handleok () {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.$emit('submit', {
            projectName: this.projectName,
            projectVersion: this.projectVersion,
            desc: this.desc,
            projectGroupID: this.projectGroupID,
            isPublic: this.isPublic
          })
          this.$emit('handlechangevisible')
        }
      })
    },
    handleclose () {
      this.$emit('handlechangevisible')
    }
  },
  computed: {
    projectAuthority () {
      return PROJECT_AUTHORITY
    },
    projectGroups () {
      return this.$store.getters.projectGroups.filter(v => v.groupId > 0)
    }
  }
}
</script>

<style lang="scss" scoped>
	.project-add{
		padding: 0 30px;
		.dialog-footer{
			text-align: right;
		}
		.authority {
			font-size: 12px;
			color: #aaa;
		}
	}
</style>

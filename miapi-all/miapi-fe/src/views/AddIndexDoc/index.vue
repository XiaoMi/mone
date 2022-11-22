<template>
	<div class="add-index-doc-container">
		<div class="add-index-doc-wrap">
			<div class="index-doc-container">
				<div class="title-wrap">
					<span class="doc-title">{{$i18n.t('title')}}:</span>
					<el-input style="width: 467px" disabled v-model="title" :placeholder="$i18n.t('placeholder.pleaseEnter')"/>
				</div>
				<div class="doc-content-wrap">
					<span class="doc-title">{{$i18n.t('content')}}:</span>
					<div>
						<markdown-editor :content="content" @changeContent="handleChange" height="360px" />
					</div>
				</div>
				<el-button @click="handleAdd" :disabled="!content" type="primary">{{$i18n.t('btnText.save')}}</el-button>
				<el-button @click="handleBack">{{$i18n.t('btnText.back')}}</el-button>
			</div>
		</div>
	</div>
</template>

<script>
import MarkdownEditor from '@/components/MarkdownEditor'
import { mapGetters } from 'vuex'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { editIndex } from '@/api/apiindex'
import { PATH } from '@/router/constant'
export default {
  name: 'AddIndexDoc',
  components: { MarkdownEditor },
  data () {
    return {
      content: '',
      title: ''
    }
  },
  computed: {
    ...mapGetters([
      'indexGroupList'
    ]),
    projectID () {
      return this.$utils.getQuery('projectID')
    }
  },
  watch: {
    indexGroupList: {
      handler (val) {
        let groupID = this.$utils.getQuery('indexGroupID')
        if (groupID && val.length) {
          let arr = val.filter(v => v.groupID === Number(groupID))
          if (arr.length) {
            this.content = arr[0].indexDoc
            this.title = `${arr[0].groupName} ${this.$i18n.t('collectionDocument')}`
          }
        }
      },
      immediate: true
    }
  },
  methods: {
    changeType () {
      this.contentType = this.contentType === 0 ? 1 : 0
    },
    handleBack () {
      this.$router.push({ path: PATH.API_INDEX, query: { projectID: this.$utils.getQuery('projectID') } })
    },
		handleChange(val){
			this.content = val
		},
    handleAdd () {
      let groupID = this.$utils.getQuery('indexGroupID')
      let arr = this.indexGroupList.filter(v => v.groupID === Number(groupID))
      if (!arr.length) {
        this.$message.error(this.$i18n.t('notExistSelectCollection'))
        return
      }
      editIndex({
        indexID: arr[0].groupID,
        indexName: arr[0].groupName,
        projectID: this.$utils.getQuery('projectID'),
        indexDoc: this.content
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
        	this.$message.success(this.$i18n.t('editSuccessfully'))
          this.$store.dispatch('apiindex/getIndexGroupList', this.$utils.getQuery('projectID')).then(() => {
            this.handleBack()
          })
        }
      }).catch(e => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.add-index-doc-container {
	width: 100%;
	height: calc(100vh - 108px);
	padding: 0 20px 0;
	.add-index-doc-wrap {
		padding-top: 20px;
		height: 100%;
		overflow-y: auto;
		&::-webkit-scrollbar{
			display: none;
		}
	}
}
.index-doc-container{
	padding: 30px 20px 40px;
	background: #fff;
	.title-wrap {
		display: flex;
		align-items: center;
		justify-content: flex-start;
		white-space: nowrap;
		margin-bottom: 30px;
	}
	.doc-content-wrap {
		display: flex;
		align-items: center;
		justify-content: flex-start;
		width: 100%;
		>span{
			align-self: flex-start;
		}
		>div{
			position: relative;
			width: calc(100% - 50px);
			.change-btn {
				position: absolute;
				right: 0;
				top: 0;
				z-index: 1;
				cursor: pointer;
				user-select: none;
				display: inline-block;
				border-left: 1px solid #e6e6e6;
				padding: 0 8px;
				font-size: 12px;
				width: 75px;
				text-align: center;
			}
		}
	}
	.active_btn{
		background: #409EFF;
		border-color: #409EFF;
		color: #FFF;
	}
	.doc-title {
		display: inline-block;
		margin: 0 16px 8px 0;
		font-size: 14px;
	}
	&>.el-button{
		margin: 16px 16px 0 48px;
		&:last-child{
			margin-left: 0px;
		}
	}
}
</style>

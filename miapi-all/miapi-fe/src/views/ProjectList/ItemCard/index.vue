<template>
	<el-card :class="{'i-item-card':true, 'no-border': !border}" shadow="hover">
    <span v-if="star" class="star">
      <el-icon @click.stop="handleFocus" :size="16">
        <StarFilled v-if="hasFollow"/>
        <Star v-else />
      </el-icon>
    </span>
    <p :class="{title:true, project: type==='project'}" :title="data.name">{{data.name}}</p>
		<p v-if="type==='project'" class="desc project" :title="handleTime()">{{$i18n.t('updatedOn')}} {{handleTime()}}</p>
		<p v-else class="desc" :title="data.apiName">{{data.apiName}}</p>
	</el-card>
</template>
<script>
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { focusProject, unFocusProject } from '@/api/main'

import moment from 'moment'
export default {
  name: 'ItemCard',
  data () {
    return {
      hasFollow: false
    }
  },
  props: {
    border: {
      type: Boolean,
      default: true
    },
    star: {
      type: Boolean,
      default: true
    },
    type: {
      type: String,
      default: 'project'
    },
    data: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  watch: {
    data: {
      handler (val) {
        this.hasFollow = val.isFocus
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleTime () {
      return moment(this.data.utime).startOf('day').fromNow()
    },
    handleFocus () {
      if (this.hasFollow) {
        unFocusProject({ projectID: this.data.id }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.hasFollow = false
            this.$message.success(this.$i18n.t('unfavorite'))
          }
        }).catch(e => {})
      } else {
        focusProject({ projectID: this.data.id }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.hasFollow = true
            this.$message.success(this.$i18n.t('collectionSuccess'))
          }
        }).catch(e => {})
      }
    }
  }
}
</script>
<style scoped>
.i-item-card {
	height: 74px;
	position: relative;
	cursor: pointer;
}
.i-item-card .star{
	display: none;
}
.i-item-card:hover .star{
	display: block;
}
.i-item-card:not(.no-border):hover{
	background-color: rgba(230, 247, 255, 1);
	border-color: rgba(186, 231, 255, 1);
}
.i-item-card.no-border {
	border: none;
}
.i-item-card .star {
	position: absolute;
	right: 4px;
	top: 4px;
	cursor: pointer;
}
.i-item-card i.el-icon{
	color: #1890FF;
}
.i-item-card .title {
	font-size: 14px;
	color: #333;
	margin: 0;
	padding: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.i-item-card .title.project {
	font-size: 16px;
}
.i-item-card .desc {
	font-size: 12px;
	color: #aaa;
	margin: 9px 0 0 0;
	padding: 0;
}
.i-item-card .desc.project {
	font-size: 12px;
}
.i-item-card>>>.el-card__body {
	padding: 12px 16px;
}
</style>

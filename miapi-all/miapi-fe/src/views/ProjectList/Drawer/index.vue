<template>
	<el-drawer
		modal-class="project-drawer"
		v-model="show"
		:withHeader="false"
		:destroy-on-close="true"
		:size="591"
		:append-to-body="true"
		@closed="handleClose"
		z-index="8"
		direction="ltr">
		<ProjectItemList @handleClose="handleClose"/>
	</el-drawer>
</template>
<script>
import ProjectItemList from '../ProjectItemList'
export default {
  name: 'Drawer',
  components: {
    ProjectItemList
  },
  data () {
    return {
      show: false
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    visible: {
      handler (val) {
        this.show = val
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleClose () {
      this.$emit('handleClose')
    }
  }
}
</script>
<style lang="scss">
@import '@/styles/variables.scss';
.project-drawer{
	padding-top: $headerHeight;
	margin-left: 279px !important;
}
</style>
<style scope>
.project-drawer .el-drawer {
  position: relative !important;
}
</style>

<template>
	<el-container>
		<el-header>
			<div class="system-title">
        <img src="@/components/Header/assets/logo.png" />
        <span>{{$i18n.t('pageTitle')}}</span>
      </div>
		</el-header>
		<el-container class="content-wrap">
			<AppMain />
		</el-container>
	</el-container>
</template>

<script>

import AppMain from '@/components/AppMain'

export default {
  name: 'Outer',
  components: {
    AppMain
  }
}
</script>

<style lang="scss">
@import '@/styles/variables.scss';
	.el-header {
		background: #fff;
		box-shadow: 0px 3px 5px rgba(170,170,170 , 0.27);
		width: 100%;
		padding: 0 !important;
		height: $headerHeight !important;
		position: fixed;
		z-index: 10;
		.system-title {
			height: 100%;
			line-height: 50px;
			font-size: 20px;
			position: relative;
			padding: 0 20px;
			display: flex;
			align-items: center;
			justify-content: flex-start;
			color: #1890FF;
			img {
				width: 33px;
				height: 26px;
				margin-right: 8px;
			}
		}
	}
	.el-aside{
		background: transparent;
	}
	.el-main{
		background: #e9eef3;
		height: 100%;
	}
	.content-wrap {
		position: relative;
		padding-top: $headerHeight;
		min-width: 1200px !important;
		display: block !important;
	}
</style>

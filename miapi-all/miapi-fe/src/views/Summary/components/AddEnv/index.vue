<template>
	<div class="add-env-container">
		<EnvLeftMenu/>
    <EnvRightContent/>
	</div>
</template>

<script>
import EnvLeftMenu from './Left.vue'
import EnvRightContent from './Right.vue'

export default {
  name: 'AddEnv',
  components: {
    EnvLeftMenu,
    EnvRightContent
  },
  data () {
    return {}
  },
  beforeUnmount () {
    this.$store.dispatch('addEnv/resetAddEnvData')
  }
}
</script>
<style scoped>
.add-env-container {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  height: 100%;
}
</style>

<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->
<template>
  <div id="app" v-loading="isLoading">
    <keep-alive v-if="isRefresh && !isLoading">
      <router-view/>
    </keep-alive>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'app',
  provide () {
    return {
      reload: this.reload
    }
  },
  computed: {
    ...mapState('d2admin/page', ['isLoading'])
  },
  data () {
    return {
      isRefresh: true
    }
  },
  methods: {
    reload () {
      this.isRefresh = false
      this.$nextTick(() => {
        this.isRefresh = true
      })
    }
  }
}
</script>

<style lang="scss">
@import '~@/assets/style/normalize.scss';
@import '~@/assets/style/public-class.scss';
@import '~@/assets/style/iconfont/iconfont.css';
</style>

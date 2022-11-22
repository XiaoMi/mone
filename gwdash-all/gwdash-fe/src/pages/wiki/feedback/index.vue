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
  <d2-container>
    <d2-module>
    <div>
      <fb-form
        show-title
        show-member
        show-project
        show-exp-time
        :form="form"
        @submit="submit"></fb-form>
    </div>
    </d2-module>
  </d2-container>
</template>
<script>
import FbForm from '../components/comment/index'
import request from '@/plugin/axios/index'

export default {
  components: {
    FbForm
  },
  data () {
    return {
      form: {
        title: '',
        content: '',
        commentUsers: '',
        type: 3,
        status: 1,
        projectId: this.$route.query.projectId || 0
      }
    }
  },
  methods: {
    submit (formName) {
      window.location.href = '/gwdash/wiki/issue/feedbacks'
    }
  }
}
</script>
<style lang="scss" scoped>

.commont-form {
  margin: 20px;
}
</style>

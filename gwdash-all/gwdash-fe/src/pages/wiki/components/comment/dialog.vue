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
  <el-dialog
    :visible="blogVisible"
    @update:visible="updateBlogVisible"
    :title="title"
    width="60%"
  >
    <comment-form
      v-if="blogVisible"
      :show-title="showTitle"
      :show-type="showType"
      :show-member="showMember"
      :show-project="showProject"
      :show-exp-time="showExpTime"
      :form="form"
      @submit="submit"
    />
  </el-dialog>
</template>
<script>
import request from '@/plugin/axios/index'
import CommentForm from './index'

export default {
  name: 'BlogDialog',
  components: {
    CommentForm
  },
  props: {
    blogVisible: {
      type: Boolean,
      default: false
    },
    showTitle: {
      type: Boolean,
      default: false
    },
    showType: {
      type: Boolean,
      default: false
    },
    showMember: {
      type: Boolean,
      default: false
    },
    showProject: {
      type: Boolean,
      default: false
    },
    showExpTime: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: ''
    },
    form: {
      type: Object,
      default: () => ({})
    }
  },
  methods: {
    submit () {
      this.$emit('update:blogVisible', false)
      this.$emit('submit')
    },
    updateBlogVisible (event) {
      this.$emit('update:blogVisible', event)
    }
  }
}
</script>
<style lang="scss" scoped>
.mde {
  font-size: 16px;
}
.blog-form {
  margin: 20px;
}
</style>

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
  <div :class='`pagination ${marginTop ? "is-margin-top" : ""}`'>
    <el-pagination
      background
      :pager-count='pagerCount'
      :current-page.sync='currentPage'
      :page-size="pageSize"
      :total='total'
      :disabled='pageDisabled'
      :hide-on-single-page='hideSinglePage'
      :page-sizes="tuneUp ? [10,20,30] : []"
      :layout='tuneUp ? "sizes, prev, pager, next" : "prev, pager, next"'
      @size-change="doSizeChange"
      @current-change='doCurrentChange'>
    </el-pagination>
  </div>
</template>

<script>
export default {
  name: 'd2-pagination',
  props: {
    marginTop: {
      type: Boolean,
      default: false
    },
    pagerCount: {
      type: Number,
      default: 5
    },
    currentPage: {
      type: Number,
      default: 1
    },
    pageSize: {
      type: Number,
      default: 10
    },
    total: {
      type: Number,
      default: 0
    },
    pageDisabled: {
      type: Boolean,
      default: false
    },
    hideSinglePage: {
      type: Boolean,
      default: true
    },
    // 调整每页展示个数
    tuneUp: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {};
  },
  methods: {
    doCurrentChange(val) {
      this.$emit('doCurrentChange',val);
    },
    doSizeChange(val) {
      this.$emit('doSizeChange',val);
    }
  }
}
</script>

<style lang="scss" scoped>
.pagination {
  display: flex;
  justify-content: flex-end;
}
.is-margin-top {
  margin-top: 15px;
}
</style>
<style>
.pagination.is-margin-top .el-pagination.is-background .el-pager li {
  margin: 0px;
}
</style>
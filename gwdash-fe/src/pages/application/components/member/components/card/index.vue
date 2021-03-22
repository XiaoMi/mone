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
  <el-card
    :body-style='{padding:"0px"}'
    shadow='hover'
    class='project__member'>
    <div class='member__header'>
      <div class='member__header-title'>
        <slot/>
      </div>
      <div class='member__header-btn'>
        <div v-if='category=="tester"' class='tip' title='xx_replace_xx'>请联系xx_replace_xx添加测试人员</div>
        <el-link
          v-else
          icon="el-icon-edit-outline"
          :underline="false"
          style='font-size: 18px'
          @click='toClick'></el-link>
      </div>
    </div>
    <div class='member__content'>
      <div class='member__content-tags'>
        <el-tag
          v-for='item in tagsList'
          :key='item'>{{item}}</el-tag>
      </div>
    </div>
  </el-card>
</template>

<script>
export default {
  props: {
    tagsList: {
      type: Array,
      required: false,
      default: function () {
        return []
      }
    },
    category: {
      type: String,
      required: true
    }
  },
  data () {
    return {}
  },
  methods: {
    toClick () {
      this.$emit('handleClick', this.category)
    }
  }
}
</script>

<style lang="scss" scoped>
.project__member {
  margin-bottom: 20px;
  .member__header {
    margin-bottom: 8px;
    padding: 10px 20px 10px 20px;
    background: rgba(239,240,244,1);
    display: flex;
    justify-content: space-between;
    align-items: center;
    &-title {
      font-size: 14px;
      color: #909399;
      font-weight: bold
    }
    .tip {
      font-size: 14px;
      color: #999;
      cursor: pointer
    }
  }
  .member__content {
    background: #FFF;
    padding: 10px 20px 10px 20px;
    &-tags {
      width: 100%;
      display: flex;
      flex-wrap: wrap;
      .el-tag {
        width: 158px;
        text-align: center;
        margin-right: 8px;
        margin-top: 6px;
      }
    }
  }
}
</style>

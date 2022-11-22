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
   <d2-module margin-bottom>
      <div class="header-bar theme-separate">
          <div class="header-bar-right header">
                <div
                  class='filterContion'
                  v-if='paramsFiled && paramsFiled.length > 0'
                >
                  <el-form
                    :inline='true'
                    label-suffix=':'
                  >
                    <form-item
                      v-bind='field'
                      v-model='field.value'
                      :key='field.key'
                      v-for='field in paramsFiled'
                    ></form-item>
                  </el-form>

                 <el-form :inline='true'>
                  <el-form-item>
                    <el-button type="primary" size='small' @click="search">查询</el-button>
                  </el-form-item>
                </el-form>
                </div>
          </div>
      </div>
   </d2-module>
</template>

<script>
import FormItem from './FormItem.vue'
export default {
  name: 'project-search',
  components: {
    FormItem
  },
  props: {
    paramsFiled: {
      type: Array,
      default () {
        return []
      }
    },
    fetchData: {
      type: Function,
      default () {
        return () => {}
      }
    }
  },
  data () {
    return {

    }
  },
  computed: {
    searchParams () {
      let params = this.paramsFiled.reduce((acc, cur) => {
        if (cur.value === '' || cur.value === undefined) {
          return { ...acc }
        }
        return { ...acc, [cur.key]: cur.value }
      }, {})
      return {
        ...params
      }
    }
  },
  methods: {
    search () {
      this.fetchData(this.searchParams, 'search')
    }
  },
  watch: {
    // searchParams: {
    //   deep: true,
    //   handler(value) {
    //     this.throttledFetchData(value);
    //   }
    // }
  }
}
</script>

<style lang="scss" scoped>
.filterContion {
  background: #fff;
  // height: 68px;
  // margin-bottom: 10px;
  display: flex;
  // align-items: center;
  justify-content: flex-end;
  .el-form-item {
    margin-bottom: 0;
    .el-select--small {
      width: 180px;
    }
    /deep/ .el-form-item__label {
      // line-height: 28px;
      // height: 28px;
      color: #333;
      font-size: 13px;
    }
  }
  .el-button--small {
    height: 28px;
    line-height: 4px;
    font-size: 12px;
    font-weight:300;
    border-radius: 3px;
  }
}
    .font-demo {
        letter-spacing: 0px;
        font-family: PingFang SC;
        font-weight: regular;
        text-align: center;
    }
//  .header-bar-right {
//     justify-content: flex-end;
//     display: flex;
//     // .filterContion {
//     //   background: #fff;
//     //   height: 68px;
//     //   margin-bottom: 10px;
//     //   display: flex;
//     //   align-items: center;
//     //   justify-content: space-between;
//     //   .el-form-item {
//     //     margin-bottom: 0;
//     //     .el-select--small {
//     //       width: 180px;
//     //     }
//     //   }
//     // }
//       .header-bar-right-input {
//           width: 180px;
//           margin-right: 20px;
//       }
//       .header-bar-right-title {
//           @extend .font-demo;
//           line-height: 28px;
//           height: 28px;
//           color: #333333;
//           font-size: 13px;
//           // .el-input__suffix{
//           //     background:#666666
//           // }
//       }
//       .el-button--small {
//           height: 28px;
//           line-height: 4px;
//           font-size: 12px;
//           font-weight: 300;
//           border-radius: 3px;
//       }
//     }

</style>

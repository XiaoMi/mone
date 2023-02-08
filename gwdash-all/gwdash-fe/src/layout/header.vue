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
         <span class="firsttab-title">{{firstFloor}}&nbsp; </span>
         <span class="firsttab-icon">>&nbsp; </span>
         <span class="firsttab-title" @click="handleCLick()">{{secondFloor}}&nbsp; </span>
         <span class="firsttab-icon" v-if="thirdFloor">>&nbsp; </span>
         <span class="firsttab-title">{{thirdFloor}}</span>
      </div>
   </d2-module>
</template>

<script>
export default {
  name: "project-header",
  props: {
    firstFloor: {
      type: String,
      required: true
    },
    secondFloor: {
      type: String,
      required: false
    },
    thirdFloor: {
      type: String,
      required: false
    },
    secondRoute: {
      type: String,
      required: false
    }
  },
  data () {
    return {

    }
  },
  methods: {
    handleCLick () {
      if (this.secondRoute === undefined) {
        return
      }
      this.$router.push(this.secondRoute)
    }
  }
}
</script>

<style lang="scss" scoped>
.firsttab-title {
    color: #333333;
    font-family: PingFangSC-Medium;
    font-weight: medium;
    font-size: 14px;
    &:hover{
      color: #409EFF
    }
}
.firsttab-icon {
    color: #c8c8c8;
}

</style>

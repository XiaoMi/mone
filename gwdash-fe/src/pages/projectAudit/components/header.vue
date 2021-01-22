te<!--
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
         <span class="firsttab-title">{{secondFloor}}</span>
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
      required: true
    }
  },
  data() {
    return {
      
    };
  },
  methods: {
   
  }
};
</script>

<style lang="scss" scoped>
.firsttab-title {
    color: #333333;
    font-family: PingFangSC-Medium;
    font-weight: medium;
    font-size: 14px;
}
.firsttab-icon {
    color: #c8c8c8;
}

</style>
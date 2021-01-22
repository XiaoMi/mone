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
  <div class="search" @click="handleClick"></div>
</template>

<script>
export default {
  methods: {
    handleClick () {
      this.$emit('click')
    }
  }
}
</script>

<style scoped>
.search {
  width: 20px;
  height: 20px;
  margin-right: 21px;
  background-image: url(https://img.youpin.mi-img.com/middlewareGroup/816e1974ee525204e0fd1e593cd26e95.png?w=60&h=60);
  background-size: 100%;
  /* 雪碧图 */
  /* background-image: url(https://img.youpin.mi-img.com/ArchitectureGroup/edb6235598f19a4f28e0376da25fd403.png?w=261&h=200);
  background-repeat: no-repeat;
  background-position: -94px -88px */
}
</style>

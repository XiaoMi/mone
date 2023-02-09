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
  <div class="broadcast-right">
    <p class="username speaker"></p>
    <p class="text">{{message.message}}</p>
    <p class="username speaker">
      :{{message.name}}
    </p>
  </div>

</template>

<script>
export default {
  name: "Left-Bar",
  props: {
    message: {
      type: Object,
      required: true
    }
  }
}
</script>

<style lang="scss" scoped>
.broadcast-right{
  display: flex;
  font-size: 14px;
  p{
    margin: 5px auto;
  }
  .username{
    text-align: left;
    min-width: 50px;
    margin-left: 2px;
  }
  .text{
    flex: 1;
    text-align: right;
    background: #f7f7f7;
  }

}

</style>

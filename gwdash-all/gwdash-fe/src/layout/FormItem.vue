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

<script>
export default {
  name: 'form-item',
  props: ["label", "comp", "optComp", "value", 'options', 'styles', 'placeHolder'],
  // eslint-disable-next-line
  render(h) {
    let formEle = null
    formEle = (
      <this.comp size='mini' value={this.value} onInput={this.change} placeHolder={this.placeHolder ? this.placeHolder : ''}>
        {
          this.options && this.options.map((opt) => (
            <this.optComp value={opt.value} key={opt.value} label={opt.label}/>
          ))
        }
      </this.comp>
    )
    return (
      <el-form-item label={this.label} style={this.styles}>
        {formEle}
      </el-form-item>
    )
  },

  methods: {
    change (value) {
      this.$emit('input', value)
    }
  }
}
</script>

<style lang="less">
</style>

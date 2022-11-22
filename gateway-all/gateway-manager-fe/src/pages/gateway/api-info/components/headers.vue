<template>
  <div class="debug-headers">
    <div class="title">
      <div class="row-content" v-if="type == 1">
        <div class="select"></div>
        <div class="">KEY</div>
        <div class="">VALUE</div>
      </div>
      <div class="button" @click="objectFn" v-if="type == 1">Bulk Edit</div>
      <div class="button" @click="keyValueFn" v-if="type == 0">Key-Value Edit</div>
    </div>
    <div class="key-value" v-if="type == 1">
      <div class="row-content" v-for="(item, index) in keyValue" :key="index">
        <div class="select">
          <el-checkbox v-model="item.checked" v-if="keyValue.length > 1 && index+1 != keyValue.length" @change="objectChange"></el-checkbox>
        </div>
        <div class="">
          <Autocomplete v-model="item.key" :index="index" placeholder="Key" :options="optionList" triggerFocus style="width: 100%" @inputValue="inputValue" />
        </div>
        <div class="">
          <Autocomplete v-model="item.value" :index="index" placeholder="Value" :options="[]" style="width: 100%" @inputValue="inputValue" />
        </div>
        <div class="oper">
          <i class="el-icon el-icon-delete"
             v-if="keyValue.length > 1 && index+1 != keyValue.length"
             @click="delectDataFn(index)"
             title="删除" />
        </div>
      </div>
    </div>
    <codemirror v-if="type == 0" v-model="headers"
                id="headers-codemirror"
                :class="['headers-codemirror', {'headers-codemirror-placeholder': !headers}, {'max-height': vkHeight}]"
                :placeholder="placeholder"
                :options="cmOptions"></codemirror>
    <!--    <el-input v-if="type == 0"-->
    <!--              v-model="headers"-->
    <!--              type="textarea"-->
    <!--              :placeholder="placeholder" :rows="4" autocomplete="off"-->
    <!--              @input="valueChange"></el-input>-->
  </div>
</template>
<script>
import Autocomplete from './autocomplete'
export default {
  components: {
    Autocomplete
  },
  props: {
    value: {},
    placeholder: {
      type: String,
      default: '{"Content-Type":"application/json"}'
    }
  },
  watch: {
    value: {
      handler (newVal) {
        if (!newVal) {
          this.headers = ''
        } else if (newVal && newVal != this.headerData) {
          this.headers = newVal || {}
        }
        this.keyValueChange()
      },
      immediate: true,
      deep: true
    }
  },
  data () {
    return {
      headers: '', // 用户输入数据
      headerData: '', // 保存数据
      keyValue: [{
        'checked': true,
        'key': '',
        'value': ''
      }],
      type: 1, // 参数类型 0 textarea； 1 key-value
      optionList: [{ // key输入框建议提示下拉选项
        'value': 'Mishop-Client-VersionCode'
      }, {
        'value': 'Device-id'
      }, {
        'value': 'Device-Oaid'
      }, {
        'value': 'X-User-Agent'
      }, {
        'value': 'Cookie'
      }, {
        'value': 'X-User-Location'
      }],
      cmOptions: {
        tabSize: 4,
        indentUnit: 4,
        // mode: 'text/javascript',
        theme: 'default',
        lineNumbers: true,
        line: true,
        smartIndent: true,
        // json校验
        mode: 'application/json',
        gutters: ['CodeMirror-lint-markers'],
        lint: true
      },
      vkHeight: false // 设置编辑器最大高度
    }
  },
  methods: {
    // 展示key-value
    keyValueFn () {
      this.type = 1
      this.keyValueChange()
    },
    // object数据转换成key-value
    keyValueChange () {
      const arr = []
      if (typeof this.headers === 'string' && this.headers.slice(0, 1) == '{' && this.headers.slice(-1) == '}') {
        try {
          const newData = JSON.parse(this.headers)
          for (let item in newData) {
            const checked = item.slice(0, 0) != '//'
            arr.push({
              'checked': checked,
              'key': item || '',
              'value': `${newData[item]}` || ''
            })
          }
        } catch (e) {}
      }
      arr.push({
        'checked': true,
        'key': '',
        'value': ''
      })
      this.keyValue = arr
    },
    // 展示Bulk Edit
    objectFn () {
      this.type = 0
      this.objectChange()
      // 设置编辑器最大高度
      this.$nextTick(() => {
        const height = document.getElementById('headers-codemirror').getBoundingClientRect().height
        if (height > 300) {
          this.vkHeight = true // 设置编辑器最大高度300px
        }
      })
    },
    // key-value数据转换成object
    objectChange () {
      if (this.keyValue.length > 1) {
        let obj = ''
        let saveObj = ''
        this.keyValue.forEach((item, index) => {
          if (item.key || item.value) {
            const note = item.checked ? '' : '// '
            let value = item.value
            // 布尔值处理
            if (value == 'true' || value == 'false') {
              value = value == 'true'
            } else {
              value = `"${item.value}"`
            }
            const comma = index + 2 == this.keyValue.length ? '' : ',' // 逗号处理
            obj += `${note}"${item.key}": ${value || ''}${comma}\n`
            if (!note && item.key) {
              saveObj += `"${item.key}": ${value || ''}${comma}\n`
            }
          }
        })
        this.headers = `{\n${obj}}`
        this.headerData = `{\n${saveObj}}`
        // this.$emit('input', this.headerData); // 更新headers数据
      }
    },
    // 删除
    delectDataFn (index) {
      this.keyValue.splice(index, 1)
    },
    // 输入value后新增一行
    inputValue (index) {
      if (index + 1 === this.keyValue.length && !this.keyValue[index + 1]) {
        this.keyValue.push({
          'checked': true,
          'key': '',
          'value': ''
        })
      }
      this.objectChange() // key-value数据转换成object
    }
  }
}
</script>
<style scoped>
.debug-headers >>> .el-textarea__inner {
  border-radius: 0 0 4px 4px;
}
.debug-headers >>> .headers-codemirror-placeholder .CodeMirror-code::before{
  content: '{"Content-Type":"application/json"}';
  color:#bbb;
  position: absolute;
}
.debug-headers >>> .headers-codemirror .CodeMirror {
  min-height: 86px;
  height: auto;
}
.debug-headers >>> .headers-codemirror.max-height .CodeMirror {
  height: 300px;
}
</style>
<style lang="scss" scoped>
.debug-headers {
  .title {
    display: flex;
    justify-content: right;
    align-items: center;
    height: 32px;
    line-height: 32px;
    border: 1px solid #DCDFE6;
    border-bottom: none;
    border-radius: 4px 4px 0 0;
    overflow: hidden;
    .button {
      padding: 0 16px;
      font-size: 12px;
      color: #222;
      white-space: nowrap;
      border-left: 1px solid #DCDFE6;
      cursor: pointer;
      &:hover {
        background: #f0f7ff;
      }
    }
  }
  .row-content {
    width: 100%;
    display: flex;
    justify-content: left;
    align-items: center;
    border-bottom: 1px solid #ededed;
    &:last-child {
      border-bottom: none;
    }
    & > div {
      flex: 1;
      padding: 5px 10px;
      font-size: 12px;
      border-left: 1px solid #ededed;
      height: 28px;
      &:first-child {
        border-left: none
      }
      &.select {
        flex: initial;
        width: 20px;
        text-align: right;
      }
      &.oper {
        flex: initial;
        width: 60px;
        text-align: center;
      }
      .el-icon {
        display: none;
        font-size: 14px;
        cursor: pointer;
        &:hover {
          color: #409eff;
        }
      }
    }
    &:hover {
      background: #fafafa;
      .el-icon {
        display: inline-block;
      }
    }
  }
  .key-value {
    border: 1px solid #DCDFE6;
    border-radius: 0 0 4px 4px;
  }
  .headers-codemirror {
    border: 1px solid #DCDFE6;
    border-radius: 0 0 4px 4px;
  }
}
</style>

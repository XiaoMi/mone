<template>
  <div>
      <div class="console" id="terminal" ref="Xterminal"></div>
  </div>
</template>
<script>
import Terminal from './xterm'

export default {
  name: 'XtermConsole',
  props: {
    terminal: {
      type: Object,
      default: () => { return {} }
    },
    logtxt: {
      type: String,
      default: () => { return '' }
    }
  },
  data () {
    return {
      term: null,
      terminalSocket: null
    }
  },
  computed: {
    list: function () {
      return this.getLog(this.logtxt)
    }
  },
  watch: {
    list: function () {
      this.debounce(function () {
        // this.term.clear()
        this.term.write(this.list)
        this.term.fit()
        this.term.scrollToBottom()
      }, 1000)()
    }
  },
  mounted () {
    let terminalContainer = this.$refs.Xterminal
    this.term = new Terminal(this.terminal)
    this.term.clear()
    this.term.open(terminalContainer)
    this.term._initialized = true
    this.term.write(this.list)
    this.term.fit()
  },
  methods: {
    clear () {
      this.term.clear()
    },
    getLog (str) {
      let a = this.logtxt
      let log = a.split("\n")
      let logStr = log.map((item, index) => {
        return this.getLogType(item)
      }).join('\r\n')
      return logStr
    },
    getLogType (str) {
      if (str.toLowerCase().startsWith(`[info]`)) {
        str = '\x1b[1;37m' + str + '\x1B[0m'
      } else if (str.toLowerCase().startsWith(`[warning]`)) {
        str = '\x1b[1;33m' + str + '\x1B[0m'
      } else if (str.toLowerCase().startsWith(`[warn]`)) {
        str = '\x1b[1;33m' + str + '\x1B[0m'
      } else if (str.toLowerCase().startsWith(`[error]`)) {
        str = '\x1b[1;31m' + str + '\x1B[0m'
      } else if (str.toLowerCase(0).startsWith(`[success]`)) {
        str = '\x1b[1;32m' + str + '\x1B[0m'
      } else if (str.indexOf('successfully') > -1) {
        str = '\x1b[1;32m' + str + '\x1B[0m'
      } else {
        str = '\x1b[37m' + str + '\x1B[0m'
      }
      return str
    },
    debounce (func, wait) {
      let timeout = ''
      return (v) => {
        if (timeout) {
          clearTimeout(timeout)
        }
        timeout = setTimeout(() => {
          func.call(this, v)
        }, wait)
      }
    }
  },
  beforeDestroy () {
    // this.terminalSocket.close()
    this.term.clear()
    this.term.destroy()
    // this.logtxt = null
    // console.log('beforeDestroy')
  }
}
</script>

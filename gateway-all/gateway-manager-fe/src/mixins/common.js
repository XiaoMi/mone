import service from '@/plugin/axios/index'
import { throttle } from 'lodash'
import qs from 'qs'

export default {
  data () {
    return {
      gitAddressGroup: [],
      loading: false,
      commitForm: {},
      commitOptions: [],
      commitTimeList: [],
      commitBranch: ''
    }
  },
  methods: {
    // git仓库
    initGitAddressGroup () {
      service('custom/config/get?key=gitaddr').then(data => {
        this.gitAddressGroup = JSON.parse(data.content)
      })
    },
    updateBranchAndCommits () {
      this.getCommitOptions('').then((branchList) => {
        this.commitTimeList = []
        if (Array.isArray(branchList) && branchList.length) {
          this.commitBranch = branchList.find(item => item === 'master') || branchList[0]
          this.getCommitTimeList(this.commitBranch)
        } else {
          this.commitBranch = ''
        }
      })
    },
    // 获取options列表
    getCommitOptions (search) {
      return service({
        url: '/project/branch',
        method: 'POST',
        data: qs.stringify({
          group: this.projectForm.gitGroup,
          domain: this.projectForm.domain,
          name: this.projectForm.gitName,
          search: search
        })
      }).then(branchList => {
        this.commitOptions = branchList
        this.$emit('commitForm')
        return branchList
      })
    },
    // 获取时间线列表
    getCommitTimeList (val) {
      this.commitTimeList = []
      service({
        url: '/project/commits',
        method: 'POST',
        data: qs.stringify({
          group: this.projectForm.gitGroup,
          name: this.projectForm.gitName,
          domain: this.projectForm.domain,
          branch: val
        })
      }).then(res => {
        res.forEach(item => {
          item.sliceCommitId = item.id.substr(0, 6)
          item.timestamp = item.committed_date
        })
        res[0].color = '#409EFF'
        this.commitTimeList = res
      })
    },
    searchBranch: throttle(function (query) {
      if (query !== '') {
        this.loading = true
        this.getCommitOptions(query).then(() => {
          this.loading = false
        })
      }
    }),
    selectedBranch (branch) {
      this.getCommitTimeList(branch)
    }
  }
}

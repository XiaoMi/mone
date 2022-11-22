<template>
  <d2-container>
    <d2-module margin-bottom>
      <div class='header'>
        <el-button 
          size='mini'
          @click="handleMenuConfig('新增')">新增</el-button> 
      </div>
    </d2-module>
     
    <d2-module>
      <el-table stripe :data='tableData' class='table-list'>
          <el-table-column label="id" prop="id" width="80"></el-table-column>
          <el-table-column label="role" prop="role" width="150"></el-table-column>
          <el-table-column label="menu" prop="menu" width="280" show-overflow-tooltip></el-table-column>
          <!-- <el-table-column label="创建人" prop="creator" width="160"></el-table-column>
          <el-table-column label="更新人" prop="updater" width="160"></el-table-column> -->
          <el-table-column label="创建时间" prop="createTime" width="160"></el-table-column>
          <el-table-column label="更新时间" prop="updateTime" width="160"></el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="showMenu(scope.row)">查看菜单</el-button>
              <el-button type="text" size="mini" @click="handleMenuConfig('编辑',scope.row)">编辑</el-button>
              <el-button type="danger" size="mini" class='danger' @click="deleteMenuConfig(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        :currentPage='page'
        :pageSize='pageSize'
        :total='total'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>

      <el-dialog 
        :title='`${title}菜单配置`'
        :visible.sync="dialogVisible" 
        width='880px'
        :close-on-click-modal='false'
        :close-on-press-escape='false'>
        <el-form ref='form' :model='form' :rules='rules' label-width='110px' size='mini'>
          <el-form-item label='角色' prop='role'>
            <el-select v-model="form.role" style="width:50%">
              <el-option 
                v-for='item in roleOptions'
                :key='item.id'
                :label='item.role'
                :value='item.role'/>
            </el-select>
          </el-form-item>
          <el-form-item label='优先级' prop='priority'>
            <el-input-number v-model="form.priority" :min="0"></el-input-number>
          </el-form-item>
          <el-form-item label='菜单内容' prop='menu'>
            <el-input 
              type="textarea"
              :rows="12"
              show-word-limit
              v-model="form.menu" 
              placeholder="请输入菜单内容" 
              style="width:80%"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false" size="mini">取 消</el-button>
          <el-button type="primary" @click="submitForm('form')" size="mini">确 定</el-button>
        </div>
      </el-dialog>

      <el-dialog 
        title='菜单' 
        :visible.sync='menuDialogVisible' 
        width='880px'
        :before-close='handleMenuClose'
        class='show-menu'>
        <codemirror 
          v-model="codeMirrorMenuData" 
          :options="codeMirrorOptions" 
          class='codeMirror_content'/>
      </el-dialog>
    </d2-module>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios'
import bizutil from '@/common/bizutil'

export default {
  data() {
    return {
      tableData: [],
      page: 1,
      pageSize: 10,
      total: 0,
      title: '',
      dialogVisible: false,
      submitUrl: '',
      form: {
        role: '',
        priority: 0,
        menu: ''
      },
      roleOptions: [],
      rules: {
        role: [{ required: true, message: "请输入角色", trigger: "blur" }],
        priority: [{ required: true, message: "请输入优先级", trigger: "blur" }],
        menu: [{ required: true, message: "请输入菜单内容", trigger: "blur" }]
      },
      menuDialogVisible: false,
      codeMirrorMenuData: "",
      codeMirrorOptions: {
        tabSize: 2,
        indentUnit: 2,
        // mode: 'text/javascript',
        theme: 'base16-dark',
        // readOnly: 'nocursor',//支持选中复制
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
    }
  },
  created() {
    this.getInitList();
    this.getRoles();
  },
  methods: {
    getInitList() {
      service({
        url: `/menu/list?page=${this.page}&pageSize=${this.pageSize}`,
        method: 'GET',
        data: {
          page: this.page,
          pageSize: this.pageSize
        }
      }).then(res => {
        this.page = res.page || this.page;
        this.pageSize = res.pageSize || this.pageSize
        this.total = res.total;
        this.tableData = res && res.list && res.list.map(item => {
          return {
            ...item,
            createTime: bizutil.timeFormat(item.ctime),
            updateTime: bizutil.timeFormat(item.utime)
          }
        })
      })
    },
    getRoles() {
      service({
        url: '/menu/roles'
      }).then(res => {
        if (!Array.isArray(res)) return;
        this.roleOptions = res.map(item => ({
          id: item.id,
          role: item.name
        }))
      })
    },
    handleCurrentChange(val) {
      this.page = val;
      this.getInitList()
    },
    handleMenuConfig(tag,param) {
      if (param) {
        this.submitUrl = '/menu/update';
        let { role,priority,menu,id } = param;
        this.form = {
          id,
          role,
          priority,
          menu
        }
      } else {
        this.submitUrl = '/menu/create';
        this.form = {
          role: '',
          priority: 0,
          menu: ''
        }
      }
      this.title = tag;
      this.dialogVisible = true
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          });
          return
        }
        service({
          url: this.submitUrl,
          method: 'POST',
          data: {
            ...this.form
          }
        }).then( res => {
          this.$message.success(`${this.title}成功`);
          this.dialogVisible = false;
          this.getInitList()
        })
      })
    },
    deleteMenuConfig(id) {
      this.$confirm("此操作将永久删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        service({
          url: `/menu/delete?id=${id}`
        }).then(res => {
          this.$message.success('删除成功');
          this.getInitList()
        })
      }).catch(() => {
        this.$message.warning('已取消删除')
      })
    },
    showMenu(row) {
      this.codeMirrorMenuData = JSON.stringify(JSON.parse((row && row.menu) || 'null'),null,2);
      this.menuDialogVisible = true
    },
    handleMenuClose() {
      this.menuDialogVisible = false;
      this.codeMirrorMenuData = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: flex-end
}
.d2-layout-header-aside-group .table-list .el-button.danger {
  background-color: #F56C6C;
  border-color: #F56C6C
}
</style>
<style lang="scss">
.show-menu {
  .codeMirror_content, 
  .CodeMirror {
    height: 380px
  }
}
</style>
<template>
  <el-row>
    <el-row style="padding: 10px; text-align: center;">
      <el-col :span="6">
        <el-input
          clearable
          placeholder="用户名"
          size="small"
          v-model="search"
          @keyup.enter.native="searchUser"
          style="width: 180px;"
        >
        </el-input>
      </el-col>
      <el-col :span="2">
        <el-button icon="el-icon-search" size="small" @click="searchUser"></el-button>
      </el-col>
    </el-row>
    <el-row style="padding: 0 10px; flex-grow: 1;">
      <el-table
        border
        :data="pageTableData"
        highlight-current-row
        @current-change="currentChange"
        :header-cell-style="{ background: '#F2F6FC', padding: '11px 0' }"
        :cell-style="{ padding: '11px 0' }"
      >
        <el-table-column type="index" align="center" min-width="1"></el-table-column>
        <el-table-column prop="username" label="用户名" align="center" min-width="4"></el-table-column>
        <el-table-column prop="password" label="密码" align="center" min-width="4"></el-table-column>
        <el-table-column prop="department" label="部门" align="center" min-width="4"></el-table-column>
        <el-table-column prop="time" label="编辑时间" align="center" min-width="4"></el-table-column>
      </el-table>
    </el-row>
    <Pagination
      ref="pagination"
      :pagination="pagination"
      :allTableData="allTableData"
      @changeCurrentPage="changeCurrentPage"
      @updatePageTable="updatePageTable"
    />
    <Divider />
    <el-row style="padding: 10px 0; text-align: center;">
      <el-col :span="8" v-if="user.role === 1">
        <el-button size="medium" type="primary" @click="addOneUser">新增</el-button>
      </el-col>
      <el-col :span="user.role === 2 ? 24 : 8">
        <el-button size="medium" type="success" @click="editOneUser">编辑</el-button>
      </el-col>
      <el-col :span="8" v-if="user.role === 1">
        <el-button size="medium" type="danger" @click="delOneUser">删除</el-button>
      </el-col>
    </el-row>
    <UserDialog :userDialog="userDialog" @refreshAllTable="refreshAllTable" />
  </el-row>
</template>

<script>
import Pagination from '@/components/Pagination'
import Divider from '@/components/Divider'
import UserDialog from '@/components/DialogUser'
export default {
  name: 'UserManage',
  components: { Pagination, Divider, UserDialog },
  data() {
    return {
      search: '',

      allTableData: [],
      filterTableData: [],
      pageTableData: [],
      currentRow: null,

      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
        layout: 'total, prev, pager, next, jumper',
      },
      changeToPage: 1,

      userDialog: {
        title: '',
        show: false,
        action: '',
        userForm: {
          username: '',
          password: '',
          role: '',
          department: '',
        },
      },
    }
  },
  created() {
    this.getAllUserInfo().then(() => {
      this.initialPagination()
    })
  },
  methods: {
    getAllUserInfo() {
      let url
      this.user.role === 1 ? (url = '/user/getAllUser') : (url = '/user/getOneUser')
      return this.axios.get(url).then((res) => {
        if (res.data.code === '200') {
          this.allTableData = res.data.msg
          this.filterTableData = res.data.msg
        }
      })
    },

    filterUser() {
      this.allTableData = this.filterTableData.filter((item) => {
        return item.username.match(this.search)
      })
    },
    searchUser() {
      this.filterUser()
      this.initialPagination()
    },

    currentChange(row) {
      this.currentRow = row
    },
    refreshAllTable(action) {
      if (action === 'add') {
        this.search = ''
      }
      this.getAllUserInfo()
        .then(() => {
          this.filterUser()
          this.setPaginationTotal()
        })
        .then(() => {
          this.$refs.pagination.changeToPage(this.changeToPage)
        })
    },
    updatePageTable(pageTableData) {
      this.pageTableData = pageTableData
    },

    initialPagination() {
      this.pagination.currentPage = 1
      this.setPaginationTotal()
      this.pageTableData = this.allTableData.filter((item, index) => {
        return index < this.pagination.pageSize
      })
    },
    setPaginationTotal() {
      this.pagination.total = this.allTableData.length
    },
    changeCurrentPage(page) {
      this.pagination.currentPage = page
    },

    addOneUser() {
      this.userDialog = {
        title: '添加用户',
        show: true,
        action: 'add',
        userForm: {
          username: '',
          password: '',
          role: '',
          department: '',
        },
      }
      this.changeToPage = 1
    },
    editOneUser() {
      if (!this.currentRow) {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '请选择用户',
          position: 'bottom-right',
          offset: 30,
        })
      } else {
        this.userDialog = {
          title: '编辑用户',
          show: true,
          action: 'edit',
          userForm: {
            id: this.currentRow.id,
            username: this.currentRow.username,
            password: this.currentRow.password,
            role: this.currentRow.role,
            department: this.currentRow.department || '',
          },
          userFormData: {
            username: this.currentRow.username,
            password: this.currentRow.password,
            role: this.currentRow.role,
            department: this.currentRow.department || '',
          },
        }
        this.changeToPage = this.pagination.currentPage
      }
    },
    delOneUser() {
      if (!this.currentRow) {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '请选择用户',
          position: 'bottom-right',
          offset: 30,
        })
      } else {
        this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', { type: 'warning' })
          .then(() => {
            let data = {
              id: this.currentRow.id,
            }
            this.axios.post('/user/deleteOneUser', data).then((res) => {
              if (res.data.code === '200') {
                this.$notify.success({
                  title: '',
                  duration: 1000,
                  message: '删除成功',
                  position: 'bottom-right',
                  offset: 30,
                })
                if (this.pageTableData.length === 1) {
                  this.changeToPage = this.pagination.currentPage - 1
                } else {
                  this.changeToPage = this.pagination.currentPage
                }
                this.refreshAllTable()
              }
            })
          })
          .catch(() => {})
      }
    },
  },
  computed: {
    user() {
      return this.$store.getters.user
    },
  },
}
</script>

<style scoped></style>

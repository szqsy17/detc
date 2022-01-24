<template>
  <el-dialog
    @closed="closed"
    width="450px"
    :title="userDialog.title"
    :visible.sync="userDialog.show"
    :modal-append-to-body="false"
  >
    <el-form
      label-width="80px"
      :model="userDialog.userForm"
      :rules="userFormRule"
      ref="userForm"
      style="padding: 0 30px;"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="userDialog.userForm.username"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="userDialog.userForm.password"></el-input>
      </el-form-item>
      <el-form-item v-if="user.role === 1" label="账户类型" prop="role">
        <el-select v-model="userDialog.userForm.role" placeholder="请选择">
          <el-option v-for="(role, index) in roleList" :key="index" :label="role" :value="index + 1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item v-if="user.role === 1" label="部门" prop="department" style="margin: 0;">
        <el-input v-model="userDialog.userForm.department"></el-input>
      </el-form-item>
    </el-form>
    <el-row slot="footer" class="dialog-footer">
      <el-button @click="cancelUserDialog">取 消</el-button>
      <el-button type="primary" @click="submitUserDialog('userForm')">确 定</el-button>
    </el-row>
  </el-dialog>
</template>

<script>
export default {
  name: 'UserDialog',
  props: {
    userDialog: {},
  },
  data() {
    let checkSpace = (rule, value, callback) => {
      let reg = /\s/g
      if (reg.test(value)) {
        callback(new Error('请勿输入空格'))
      } else {
        callback()
      }
    }
    let checkChinese = (rule, value, callback) => {
      let regChinese = /[\u4E00-\u9FA5]/g
      if (regChinese.test(value)) {
        callback(new Error('请勿输入中文'))
      } else {
        callback()
      }
    }
    return {
      roleList: ['超级用户', '普通用户'],
      userFormRule: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { validator: checkSpace, trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { validator: checkSpace, trigger: 'blur' },
          { validator: checkChinese, trigger: 'blur' },
        ],
        role: [{ required: true, message: '请选择账户类型', trigger: 'blur' }],
      },
    }
  },
  methods: {
    submitUserDialog(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (
            this.userDialog.action === 'edit' &&
            this.userDialog.userFormData.username.trim() === this.userDialog.userForm.username.trim() &&
            this.userDialog.userFormData.password.trim() === this.userDialog.userForm.password.trim() &&
            this.userDialog.userFormData.role === this.userDialog.userForm.role &&
            this.userDialog.userFormData.department.trim() === this.userDialog.userForm.department.trim()
          ) {
            this.$notify.error({
              title: '',
              duration: 1000,
              message: '未作任何修改',
              position: 'bottom-right',
              offset: 30,
            })
          } else {
            let url = this.userDialog.action === 'add' ? '/user/addOneUser' : '/user/updateOneUser'
            let notifyTitle = this.userDialog.action === 'add' ? '添加成功' : '编辑成功'
            let data = {
              id: this.userDialog.userForm.id,
            }
            if (this.userDialog.action === 'add') {
              data.username = this.userDialog.userForm.username.trim()
              data.password = this.userDialog.userForm.password.trim()
              data.role = this.userDialog.userForm.role
              data.department = this.userDialog.userForm.department.trim()
            } else {
              data.username =
                this.userDialog.userForm.username.trim() !== this.userDialog.userFormData.username.trim()
                  ? this.userDialog.userForm.username.trim()
                  : ''
              data.password =
                this.userDialog.userForm.password.trim() !== this.userDialog.userFormData.password.trim()
                  ? this.userDialog.userForm.password.trim()
                  : ''
              data.role =
                this.userDialog.userForm.role !== this.userDialog.userFormData.role
                  ? this.userDialog.userForm.role
                  : null
              data.department =
                this.userDialog.userForm.department.trim() !== this.userDialog.userFormData.department.trim()
                  ? this.userDialog.userForm.department.trim()
                  : this.userDialog.userFormData.department.trim()
            }
            this.axios.post(url, data).then((res) => {
              if (res.data.code === '200') {
                this.$notify.success({
                  title: '',
                  duration: 1000,
                  message: notifyTitle,
                  position: 'bottom-right',
                  offset: 30,
                })

                this.$emit('refreshAllTable', this.userDialog.action)
                this.userDialog.show = false
              }
            })
          }
        } else {
          return false
        }
      })
    },
    cancelUserDialog() {
      this.userDialog.show = false
    },
    closed() {
      this.$refs['userForm'].resetFields()
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

<template>
  <el-row type="flex" justify="center" align="middle">
    <el-card :body-style="{ padding: '25px' }" style="width: 350px; height: 250px; margin-top: -150px;">
      <el-form :model="loginForm" :rules="loginFormRule" ref="loginForm">
        <el-row style="font-size: 20px; font-weight: bold; text-align: center; margin-bottom: 20px;">
          DETC后台管理系统
        </el-row>
        <el-form-item prop="username">
          <el-input
            prefix-icon="el-icon-user-solid"
            v-model="loginForm.username"
            @keyup.enter.native="submitForm('loginForm')"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            prefix-icon="el-icon-lock"
            v-model="loginForm.password"
            @keyup.enter.native="submitForm('loginForm')"
            placeholder="请输入密码"
            show-password
          >
          </el-input>
        </el-form-item>
        <el-row style="text-align: center;">
          <el-col :span="12">
            <el-button size="medium" type="primary" @click="submitForm('loginForm')" style="width: 80px;">
              登录
            </el-button>
          </el-col>
          <el-col :span="12">
            <el-button size="medium" @click="resetForm('loginForm')" style="width: 80px;">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </el-row>
</template>

<script>
import jwtDecode from 'jwt-decode'

export default {
  name: 'Login',
  data() {
    let checkUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入用户名'))
      } else {
        callback()
      }
    }
    let checkPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else {
        callback()
      }
    }
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
      loginForm: {
        username: '',
        password: '',
      },
      loginFormRule: {
        username: [
          { validator: checkUsername, trigger: 'blur' },
          { validator: checkSpace, trigger: 'blur' },
        ],
        password: [
          { validator: checkPassword, trigger: 'blur' },
          { validator: checkSpace, trigger: 'blur' },
          { validator: checkChinese, trigger: 'blur' },
        ],
      },
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let data = {
            username: this.loginForm.username.trim(),
            password: this.loginForm.password.trim(),
          }
          this.axios.post('/user/login', data).then((res) => {
            if (res.data.code === '200') {
              const { token } = res.data.msg
              localStorage.setItem('t', token)

              const decoded = jwtDecode(token)
              this.$store.dispatch('setAuthenticated', !this.isEmpty(decoded))
              this.$store.dispatch('setUser', decoded)

              this.$router.push('/home')
              this.$notify.success({
                title: '',
                duration: 1000,
                message: '欢迎登录',
                position: 'bottom-right',
                offset: 30,
              })
            }
          })
        } else {
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    isEmpty(value) {
      return (
        value === undefined ||
        value === null ||
        (typeof value === 'object' && Object.keys(value).length === 0) ||
        (typeof value === 'string' && value.trim().length === 0)
      )
    },
  },
}
</script>

<style scoped></style>

<template>
  <el-container>
    <el-header style="background-color: #409eff; color: #fff;">
      <el-row style="height: 60px; line-height: 60px;">
        <el-col :span="12" style="padding-left: 30px; font-size: 25px;">
          <span @click="goToHome" style="cursor: pointer; user-select: none;">DETC后台管理系统</span>
        </el-col>
        <el-col :span="12" style="text-align: right; padding-right: 30px;">
          欢迎{{ user.username }}
          <el-button
            style="margin-left: 10px;"
            type="danger"
            icon="el-icon-switch-button"
            @click="logout"
            size="mini"
            circle
          ></el-button>
        </el-col>
      </el-row>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu style="height: 100%; user-select: none;" :router="true" :default-active="currentPath" ref="menu">
          <el-submenu v-if="user.role === 1" index="/home/upgradeMenu" ref="subMenu">
            <template slot="title">
              <i class="el-icon-upload2"></i>
              升级管理
            </template>
            <el-menu-item index="/home/release" route="/home/release">
              <i class="el-icon-set-up"></i>
              版本发布
            </el-menu-item>
            <el-menu-item index="/home/upgradeManage" route="/home/upgradeManage">
              <i class="el-icon-place"></i>
              升级控制
            </el-menu-item>
            <el-menu-item index="/home/upgradeStatistic" route="/home/upgradeStatistic">
              <i class="el-icon-collection"></i>
              升级统计
            </el-menu-item>
          </el-submenu>
          <el-menu-item index="/home/log" route="/home/log">
            <i class="el-icon-document"></i>
            日志管理
          </el-menu-item>
          <el-menu-item index="/home/user" route="/home/user">
            <i class="el-icon-user"></i>
            账户管理
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main style="padding: 0;">
        <transition mode="out-in">
          <router-view style="display: flex; flex-direction: column; height: 100%;" />
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Home',
  methods: {
    goToHome() {
      this.$refs.menu.activeIndex = null
      this.$refs.menu.close('/home/upgradeMenu')
      this.$router.push('/home')
    },
    logout() {
      localStorage.removeItem('t')
      this.$store.dispatch('clearCurrentState')
      this.$router.push('/login')
      this.$notify.info({
        title: '',
        duration: 1000,
        message: '再见',
        position: 'bottom-right',
        offset: 30,
      })
    },
  },
  computed: {
    user() {
      return this.$store.getters.user
    },
  },
  data() {
    return {
      currentPath: '',
    }
  },
  created() {
    this.currentPath = this.$route.path
  },
}
</script>

<style scoped>
a {
  text-decoration: none;
}
</style>

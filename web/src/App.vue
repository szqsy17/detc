<template>
  <transition mode="out-in">
    <router-view style="height: 100vh;" />
  </transition>
</template>

<script>
import jwtDecode from 'jwt-decode'
export default {
  methods: {
    isEmpty(value) {
      return (
        value === undefined ||
        value === null ||
        (typeof value === 'object' && Object.keys(value).length === 0) ||
        (typeof value === 'string' && value.trim().length === 0)
      )
    },
  },
  created() {
    if (localStorage.t) {
      const decoded = jwtDecode(localStorage.t)
      //token存储到vuex中
      this.$store.dispatch('setAuthenticated', !this.isEmpty(decoded))
      this.$store.dispatch('setUser', decoded)
    }
  },
}
</script>

<style>
body {
  margin: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial,
    sans-serif;
  font-size: 14px;
  box-sizing: border-box;
  min-width: 1200px;
  min-height: 500px;
}

/* 解决el-table出现偏移的问题 */
body .el-table th.gutter {
  display: table-cell !important;
}

/* 动画 */
.v-enter {
  opacity: 0;
}
.v-enter-active {
  transition: 0.2s;
}
.v-enter-to {
  opacity: 1;
}
.v-leave {
  opacity: 1;
}
.v-leave-to {
  opacity: 0;
}
.v-leave-active {
  transition: 0.2s;
}
</style>

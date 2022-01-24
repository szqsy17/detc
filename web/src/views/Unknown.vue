<template>
  <el-row style="text-align: center; padding-top: 100px;">
    <h1
      style="
        position: relative;
        left: 0;
        right: 0;
        letter-spacing: -1px;
        line-height: 60px;
        font-size: 60px;
        font-weight: 100;
        text-shadow: 0 1px 0 #fff;
      "
    >
      404
    </h1>
    <p style="color: rgba(0, 0, 0, 0.5); line-height: 1.6;">
      <strong>
        您所访问的页面不存在
      </strong>
    </p>
    <router-link to="/home">Back to Home</router-link>
  </el-row>
</template>

<script>
export default {
  name: 'Unknown',
  mounted() {
    let _this = this
    setTimeout(function () {
      _this.$router.push('/home')
    }, 2000)
  },
}
</script>

<style scoped>
body {
  background-color: #f1f1f1;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}
a {
  color: #4183c4;
  text-decoration: none;
}
a:hover {
  text-decoration: underline;
}
</style>

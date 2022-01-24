<template>
  <el-pagination
    @current-change="changeToPage"
    :current-page="pagination.currentPage"
    :page-size="pagination.pageSize"
    :layout="pagination.layout"
    :total="pagination.total"
    style="text-align: right; padding: 15px;"
  >
  </el-pagination>
</template>

<script>
export default {
  name: 'Pagination',
  props: {
    pagination: {},
    allTableData: {},
  },
  methods: {
    changeToPage(page) {
      this.$emit('changeCurrentPage', page)

      let num1 = this.pagination.pageSize * (page - 1)
      let num2 = this.pagination.pageSize * page
      let pageTableData = []
      for (let i = num1; i < num2; i++) {
        if (this.allTableData[i]) {
          pageTableData.push(this.allTableData[i])
        }
      }

      this.$emit('updatePageTable', pageTableData)
    },
  },
}
</script>

<style scoped></style>

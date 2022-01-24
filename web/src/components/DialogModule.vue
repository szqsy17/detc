<template>
  <el-dialog
    title="模块升级信息"
    :visible.sync="moduleDialog.show"
    :modal-append-to-body="false"
    width="600px"
    custom-class="moduleDialog"
  >
    <el-table :data="moduleDialog.moduleInfo">
      <el-table-column align="center" label="模块名" :formatter="formatModule"></el-table-column>
      <el-table-column align="center" label="模块原版本" prop="beforeVersion"></el-table-column>
      <el-table-column align="center" label="模块目标版本" prop="targetVersion"></el-table-column>
      <el-table-column align="center" label="结果" prop="status">
        <template slot-scope="scope">
          {{ scope.row.status === 0 ? '成功' : '失败' }}
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script>
export default {
  name: 'DialogModule',
  props: {
    moduleDialog: {},
  },
  methods: {
    formatModule(row) {
      if (row.module === 1) {
        return 'IVI'
      } else if (row.module === 2) {
        return 'Cluster'
      } else if (row.module === 3) {
        return 'MCU'
      }
    },
  },
}
</script>

<style>
.moduleDialog .el-dialog__body {
  padding: 0 20px 40px;
}
</style>

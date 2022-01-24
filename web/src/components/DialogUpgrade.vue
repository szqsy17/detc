<template>
  <el-dialog title="修改目标版本" :visible.sync="upgradeDialog.show" :modal-append-to-body="false" width="400px">
    <el-form label-width="100px">
      <el-form-item label="当前目标版本">
        {{ upgradeDialog.targetVersion }}
      </el-form-item>
      <el-form-item label="修改目标版本">
        <el-select v-model="upgradeDialog.targetVersionId" filterable placeholder="请选择版本">
          <el-option
            v-for="(mirror, index) in upgradeDialog.mirrorList"
            :key="index"
            :label="mirror.mirrorVersion"
            :value="mirror.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <el-row slot="footer" class="dialog-footer">
      <el-button @click="cancelUpgradeDialog">取 消</el-button>
      <el-button type="primary" @click="submitUpgradeDialog()">确 定</el-button>
    </el-row>
  </el-dialog>
</template>

<script>
export default {
  name: 'DialogUpgrade',
  props: {
    upgradeDialog: {},
  },
  data() {
    return {}
  },
  methods: {
    cancelUpgradeDialog() {
      this.upgradeDialog.show = false
    },
    submitUpgradeDialog() {
      if (this.upgradeDialog.mirrorList.length === 0) {
        this.upgradeDialog.show = false
        return
      }

      if (this.upgradeDialog.mirrorList.length > 0 && this.upgradeDialog.targetVersionId === '') {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '请选择目标版本',
          position: 'bottom-right',
          offset: 30,
        })
        return
      }

      if (this.upgradeDialog.targetVersionId === this.upgradeDialog.targetVersionIdData) {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '未作修改',
          position: 'bottom-right',
          offset: 30,
        })
        return
      }

      let data = {
        id: this.upgradeDialog.carId,
        targetVersionId: this.upgradeDialog.targetVersionId,
      }
      this.axios.post('/car/changeCarTargetVersion', data).then((res) => {
        if (res.data.code === '200') {
          this.upgradeDialog.show = false
          this.$notify.success({
            title: '',
            duration: 1000,
            message: '修改成功',
            position: 'bottom-right',
            offset: 30,
          })
          this.$emit(
            'refreshTargetVersion',
            this.upgradeDialog.carId,
            this.upgradeDialog.mirrorList.find((item) => {
              return item.id === this.upgradeDialog.targetVersionId
            })
          )
        }
      })
    },
  },
}
</script>

<style scoped></style>

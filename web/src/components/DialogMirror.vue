<template>
  <el-dialog
    @closed="closed"
    width="460px"
    :title="mirrorDialog.title"
    :visible.sync="mirrorDialog.show"
    :modal-append-to-body="false"
    custom-class="mirrorDialog"
    top="40px"
  >
    <el-form
      label-width="110px"
      :model="mirrorDialog.mirrorForm"
      :rules="mirrorFormRule"
      ref="mirrorForm"
      style="padding: 0 20px;"
      size="medium"
    >
      <el-form-item label="项目" prop="projectName">
        <el-select size="medium" v-model="mirrorDialog.mirrorForm.projectName" placeholder="请选择">
          <el-option v-for="(project, index) in projectList" :key="index" :label="project" :value="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="总版本号" prop="mirrorVersion">
        <el-input size="medium" v-model="mirrorDialog.mirrorForm.mirrorVersion"></el-input>
      </el-form-item>
      <el-form-item label="IVI版本号" prop="iviVersion">
        <el-input size="medium" v-model="mirrorDialog.mirrorForm.iviVersion"></el-input>
      </el-form-item>
      <el-form-item label="Cluster版本号" prop="clusterVersion">
        <el-input size="medium" v-model="mirrorDialog.mirrorForm.clusterVersion"></el-input>
      </el-form-item>
      <el-form-item label="MCU版本号" prop="mcuVersion">
        <el-input size="medium" v-model="mirrorDialog.mirrorForm.mcuVersion"></el-input>
      </el-form-item>
      <el-form-item v-if="mirrorDialog.action === 'add'" label="文件">
        <el-upload
          action=""
          accept=".zip"
          ref="uploadFile"
          :limit="1"
          :auto-upload="false"
          :show-file-list="false"
          :http-request="uploadFile"
          :on-exceed="fileExceed"
          :on-change="fileAdd"
          :before-upload="checkFile"
        >
          <el-input class="uploadInput" slot="trigger" placeholder="点击上传" readonly :value="mirrorName"></el-input>
          <el-button circle size="mini" icon="el-icon-close" style="margin-left: 10px;" @click="clearFile"></el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 3 }"
          v-model="mirrorDialog.mirrorForm.description"
          maxlength="255"
        ></el-input>
      </el-form-item>
      <el-form-item label="时间" prop="time">
        <el-date-picker
          size="small"
          :clearable="false"
          v-model="mirrorDialog.mirrorForm.time"
          type="datetime"
        ></el-date-picker>
      </el-form-item>
    </el-form>
    <el-row slot="footer" class="dialog-footer">
      <el-button @click="cancelUserDialog">取 消</el-button>
      <el-button type="primary" @click="submitUserDialog('mirrorForm')">确 定</el-button>
    </el-row>
    <el-dialog
      custom-class="mirrorUploadDialog"
      v-if="mirrorDialog.action === 'add'"
      title="上传中..."
      append-to-body
      :visible="showUpdateProgress"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      width="350px"
      top="30vh"
    >
      <el-row slot="title">
        <i class="el-icon-loading"></i>
        上传中...
      </el-row>
      <el-row type="flex" align="middle">
        <el-col :span="18">
          <el-progress :text-inside="true" :stroke-width="20" :percentage="uploadPercentage"></el-progress>
        </el-col>
        <el-col :span="6" style="text-align: right;">
          <el-tag size="mini" effect="plain" type="info">
            {{ uploadSpeed >= 1024 ? (uploadSpeed / 1024).toFixed(2) + 'MB/s' : uploadSpeed.toFixed(2) + 'KB/S' }}
          </el-tag>
        </el-col>
      </el-row>
      <el-row slot="footer">
        <el-button size="mini" type="text" @click="cancelUploadFile" :loading="serverIsSaveFile">
          {{ serverIsSaveFile ? '储存中...' : '取消上传' }}
        </el-button>
      </el-row>
    </el-dialog>
  </el-dialog>
</template>

<script>
export default {
  name: 'DialogMirror',
  props: {
    mirrorDialog: {},
  },
  data() {
    let checkSpaceAndChinese = (rule, value, callback) => {
      let regSpace = /\s/g
      let regChinese = /[\u4E00-\u9FA5]/g
      if (regSpace.test(value)) {
        callback(new Error('请勿输入空格'))
      } else if (regChinese.test(value)) {
        callback(new Error('请勿输入中文'))
      } else {
        callback()
      }
    }
    return {
      mirrorName: '',
      projectList: ['D760'],
      mirrorFormRule: {
        mirrorVersion: [
          { required: true, message: '请输入总版本号', trigger: 'blur' },
          { validator: checkSpaceAndChinese, trigger: 'blur' },
        ],
        iviVersion: [
          { required: true, message: '请输入IVI版本号', trigger: 'blur' },
          { validator: checkSpaceAndChinese, trigger: 'blur' },
        ],
        clusterVersion: [
          { required: true, message: '请输入Cluster版本号', trigger: 'blur' },
          { validator: checkSpaceAndChinese, trigger: 'blur' },
        ],
        mcuVersion: [
          { required: true, message: '请输入MCU版本号', trigger: 'blur' },
          { validator: checkSpaceAndChinese, trigger: 'blur' },
        ],
      },
      showUpdateProgress: false,
      uploadPercentage: 0,
      uploadSpeed: 0,
      serverIsSaveFile: false,
      source: null,
    }
  },
  methods: {
    fileAdd(file) {
      this.mirrorName = file.status === 'ready' ? file.name : ''
    },
    fileExceed() {
      this.$notify.error({
        title: '',
        duration: 1000,
        message: '一次只能上传一个文件',
        position: 'bottom-right',
        offset: 30,
      })
    },
    checkFile(file) {
      if (file.size / 1024 / 1024 > 1024) {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '文件不能大于1G',
          position: 'bottom-right',
          offset: 30,
        })
        this.mirrorName = ''
        return false
      }

      if (file.name.substring(file.name.lastIndexOf('.')) !== '.zip') {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '仅支持.zip格式',
          position: 'bottom-right',
          offset: 30,
        })
        this.mirrorName = ''
        return false
      }
    },
    uploadFile(file) {
      let data = {
        mirrorVersion: this.mirrorDialog.mirrorForm.mirrorVersion,
      }
      this.axios.post('/mirror/beforeUpload', data).then((res) => {
        if (res.data.code === '200') {
          let formData = new FormData()
          formData.append('projectName', this.projectList[this.mirrorDialog.mirrorForm.projectName])
          formData.append('mirrorVersion', this.mirrorDialog.mirrorForm.mirrorVersion)
          formData.append('iviVersion', this.mirrorDialog.mirrorForm.iviVersion)
          formData.append('clusterVersion', this.mirrorDialog.mirrorForm.clusterVersion)
          formData.append('mcuVersion', this.mirrorDialog.mirrorForm.mcuVersion)
          formData.append('uploadFile', file.file)
          formData.append('description', this.mirrorDialog.mirrorForm.description)
          formData.append('time', this.mirrorDialog.mirrorForm.time)
          let _this = this
          this.source = this.axios.CancelToken.source()
          this.showUpdateProgress = true
          let loaded = 0
          let nowTime = new Date().getTime()
          this.axios
            .post('/mirror/upload', formData, {
              loading: false,
              onUploadProgress: function (progressEvent) {
                let now = new Date().getTime()
                _this.uploadPercentage = Number(((progressEvent.loaded / progressEvent.total) * 100).toFixed(0))
                _this.uploadSpeed = ((progressEvent.loaded - loaded) * 1000) / (now - nowTime) / 1024
                loaded = progressEvent.loaded
                nowTime = now
                if (progressEvent.loaded === progressEvent.total) {
                  _this.serverIsSaveFile = true
                }
              },
              cancelToken: this.source.token,
            })
            .then((res) => {
              if (res.data.code === '200') {
                this.showUpdateProgress = false
                this.serverIsSaveFile = false

                this.$notify.success({
                  title: '',
                  duration: 1000,
                  message: '发布成功',
                  position: 'bottom-right',
                  offset: 30,
                })

                this.$emit('refreshAllTable', this.mirrorDialog.action)
                this.mirrorDialog.show = false
              } else {
                this.showUpdateProgress = false
                this.clearUploadProgress()
              }
            })
        }
      })
    },
    cancelUploadFile() {
      if (this.source) {
        this.source.cancel({
          code: '205',
          msg: '取消上传',
        })
        this.showUpdateProgress = false
        this.clearUploadProgress()
      }
    },
    submitUserDialog(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.mirrorDialog.mirrorForm.time = this.timestampToString(this.mirrorDialog.mirrorForm.time)
          if (this.mirrorDialog.action === 'add') {
            this.$refs.uploadFile.submit()
          } else if (this.mirrorDialog.action === 'edit') {
            if (
              this.mirrorDialog.mirrorFormData.mirrorVersion.trim() ===
                this.mirrorDialog.mirrorForm.mirrorVersion.trim() &&
              this.mirrorDialog.mirrorFormData.iviVersion.trim() === this.mirrorDialog.mirrorForm.iviVersion.trim() &&
              this.mirrorDialog.mirrorFormData.clusterVersion === this.mirrorDialog.mirrorForm.clusterVersion &&
              this.mirrorDialog.mirrorFormData.mcuVersion.trim() === this.mirrorDialog.mirrorForm.mcuVersion.trim() &&
              this.mirrorDialog.mirrorFormData.description.trim() === this.mirrorDialog.mirrorForm.description.trim()
            ) {
              this.$notify.error({
                title: '',
                duration: 1000,
                message: '未作任何修改',
                position: 'bottom-right',
                offset: 30,
              })
            } else {
              let data = {
                id: this.mirrorDialog.mirrorForm.id,
                mirrorVersion:
                  this.mirrorDialog.mirrorFormData.mirrorVersion.trim() !==
                  this.mirrorDialog.mirrorForm.mirrorVersion.trim()
                    ? this.mirrorDialog.mirrorForm.mirrorVersion.trim()
                    : '',
                iviVersion:
                  this.mirrorDialog.mirrorFormData.iviVersion.trim() !== this.mirrorDialog.mirrorForm.iviVersion.trim()
                    ? this.mirrorDialog.mirrorForm.iviVersion.trim()
                    : '',
                clusterVersion:
                  this.mirrorDialog.mirrorFormData.clusterVersion.trim() !==
                  this.mirrorDialog.mirrorForm.clusterVersion.trim()
                    ? this.mirrorDialog.mirrorForm.clusterVersion.trim()
                    : '',
                mcuVersion:
                  this.mirrorDialog.mirrorFormData.mcuVersion.trim() !== this.mirrorDialog.mirrorForm.mcuVersion.trim()
                    ? this.mirrorDialog.mirrorForm.mcuVersion.trim()
                    : '',
                description:
                  this.mirrorDialog.mirrorFormData.description.trim() !==
                  this.mirrorDialog.mirrorForm.description.trim()
                    ? this.mirrorDialog.mirrorForm.description.trim()
                    : this.mirrorDialog.mirrorFormData.description.trim(),
                time: this.timestampToString(this.mirrorDialog.mirrorForm.time),
              }
              this.axios.post('/mirror/updateOneMirror', data).then((res) => {
                if (res.data.code === '200') {
                  this.$notify.success({
                    title: '',
                    duration: 1000,
                    message: '更新成功',
                    position: 'bottom-right',
                    offset: 30,
                  })

                  this.$emit('refreshAllTable', this.mirrorDialog.action)
                  this.mirrorDialog.show = false
                }
              })
            }
          } else {
            return false
          }
        }
      })
    },
    cancelUserDialog() {
      this.mirrorDialog.show = false
    },
    closed() {
      this.$refs['mirrorForm'].resetFields()
      if (this.mirrorDialog.action === 'add') {
        this.clearFile()
      }
    },
    clearFile() {
      this.mirrorName = ''
      this.clearUploadProgress()
      this.$refs.uploadFile.clearFiles()
    },
    clearUploadProgress() {
      this.uploadPercentage = 0
      this.uploadSpeed = 0
    },
    timestampToString(date) {
      date = new Date(date)
      let yyyy = date.getFullYear() + '-'
      let MM = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-'
      let dd = date.getDate() < 10 ? '0' + date.getDate() : date.getDate()
      let HH = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':'
      let mm = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':'
      let ss = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
      return yyyy + MM + dd + ' ' + HH + mm + ss
    },
  },
}
</script>

<style>
.mirrorDialog .el-dialog__body {
  padding: 0 20px;
}
.uploadInput input {
  cursor: pointer;
}
.mirrorUploadDialog .el-dialog__body {
  padding: 10px 20px 20px;
}
</style>

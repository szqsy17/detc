<template>
  <el-row>
    <el-row style="padding: 10px 0; text-align: center;">
      <el-col :span="5">
        <el-input placeholder="车架号" size="small" v-model="filterData.vin" style="width: 160px;" clearable>
        </el-input>
      </el-col>
      <el-col :span="5">
        <el-input placeholder="日志名" size="small" v-model="filterData.logName" style="width: 160px;" clearable>
        </el-input>
      </el-col>
      <el-col :span="7">
        <el-date-picker
          v-model="filterData.time"
          type="datetimerange"
          range-separator="To"
          start-placeholder="上传日期"
          end-placeholder="上传日期"
          unlink-panels
          size="small"
          align="center"
          :default-time="['12:00:00', '12:00:00']"
          style="width: 300px;"
        >
        </el-date-picker>
      </el-col>
      <el-col :span="5">
        <el-select v-model="filterData.module" size="small" style="width: 160px;">
          <el-option v-for="(module, index) in moduleList" :key="index" :label="module" :value="index"> </el-option>
        </el-select>
      </el-col>
      <el-col :span="2">
        <el-button icon="el-icon-search" size="small" @click="searchLog"></el-button>
      </el-col>
    </el-row>
    <el-row style="padding: 0 10px; flex-grow: 1;">
      <el-table
        ref="logTable"
        border
        :data="pageTableData"
        @row-click="rowClick"
        @selection-change="selectionChange"
        :header-cell-style="{ background: '#F2F6FC', padding: '11px 0' }"
        :cell-style="{ padding: '11px 0' }"
      >
        <el-table-column type="selection" align="center" min-width="1"></el-table-column>
        <el-table-column prop="logFileName" label="日志文件名" align="center" min-width="4"></el-table-column>
        <el-table-column prop="vin" label="车架号" align="center" min-width="4"></el-table-column>
        <el-table-column label="设备" align="center" min-width="2" :formatter="formatModule"></el-table-column>
        <el-table-column prop="time" label="上传时间" align="center" min-width="4"></el-table-column>
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
      <el-col :span="12">
        <el-button size="medium" type="primary" @click="downloadOneLog">下载</el-button>
      </el-col>
      <el-col :span="12">
        <el-button size="medium" type="danger" @click="delLog">删除</el-button>
      </el-col>
    </el-row>
  </el-row>
</template>

<script>
import Pagination from '@/components/Pagination'
import Divider from '@/components/Divider'
/**
 * @description 对Log包含信息的描述，消除Warning警告
 * @property logFileName 日志名
 * @property logFilePath 日志路径
 * @property vin 车架号
 * @property module 设备
 * @property time 上传时间
 */
export default {
  name: 'LogManage',
  components: { Pagination, Divider },
  data() {
    return {
      filterData: {
        vin: '',
        logName: '',
        time: [],
        module: 0,
      },
      moduleList: ['全部设备', 'IVI', 'Cluster', 'MCU'],

      allTableData: [],
      filterTableData: [],
      pageTableData: [],
      selectedRows: {},

      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
        layout: 'total, prev, pager, next, jumper',
      },
      changeToPage: 1,
    }
  },
  created() {
    this.getAllLogInfo().then(() => {
      this.initialPagination()
    })
  },
  methods: {
    getAllLogInfo() {
      return this.axios.get('/log/getAllLog').then((res) => {
        if (res.data.code === '200') {
          this.allTableData = res.data.msg
          this.filterTableData = res.data.msg
        }
      })
    },

    filterLog() {
      let timeFilter = false
      let startTime, endTime
      if (this.filterData.time && this.filterData.time.length === 2) {
        timeFilter = true
        startTime = this.filterData.time[0].getTime()
        endTime = this.filterData.time[1].getTime()
      }

      let moduleFilter = false
      if (this.filterData.module && this.filterData.module !== 0) {
        moduleFilter = true
      }

      this.allTableData = this.filterTableData.filter((item) => {
        let date = new Date(item.time)
        let time = date.getTime()

        if (timeFilter && moduleFilter) {
          return (
            item.vin.match(this.filterData.vin) &&
            item.logFileName.indexOf(this.filterData.logName) === 0 &&
            time >= startTime &&
            time <= endTime &&
            item.module === this.filterData.module
          )
        } else if (timeFilter) {
          return (
            item.vin.match(this.filterData.vin) &&
            item.logFileName.indexOf(this.filterData.logName) === 0 &&
            time >= startTime &&
            time <= endTime
          )
        } else if (moduleFilter) {
          return (
            item.vin.match(this.filterData.vin) &&
            item.logFileName.indexOf(this.filterData.logName) === 0 &&
            item.module === this.filterData.module
          )
        } else {
          return item.vin.match(this.filterData.vin) && item.logFileName.indexOf(this.filterData.logName) === 0
        }
      })
    },
    searchLog() {
      this.filterLog()
      this.initialPagination()
    },

    formatModule(row) {
      if (row.module === 1) {
        return 'IVI'
      } else if (row.module === 2) {
        return 'Cluster'
      } else if (row.module === 3) {
        return 'MCU'
      }
    },
    rowClick(row) {
      this.$refs.logTable.toggleRowSelection(row)
    },
    selectionChange(rows) {
      this.selectedRows = rows
    },
    refreshAllTable() {
      this.getAllLogInfo()
        .then(() => {
          this.filterLog()
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

    downloadOneLog() {
      if (!this.selectedRows.length || this.selectedRows.length === 0) {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '请选择日志',
          position: 'bottom-right',
          offset: 30,
        })
      } else if (this.selectedRows.length > 1) {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '仅支持单文件下载',
          position: 'bottom-right',
          offset: 30,
        })
      } else {
        location.href = '/download' + this.selectedRows[0].logFilePath + '?t=' + localStorage.t
      }
    },
    delLog() {
      if (!this.selectedRows.length || this.selectedRows.length === 0) {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '请选择日志',
          position: 'bottom-right',
          offset: 30,
        })
      } else {
        this.$confirm('此操作将永久删除日志, 是否继续?', '提示', { type: 'warning' })
          .then(() => {
            let ids = []
            for (let row of this.selectedRows) {
              ids.push(row.id)
            }
            let data = {
              ids: ids,
            }
            this.axios.post('/log/deleteLog', data).then((res) => {
              if (res.data.code === '200') {
                this.$notify.success({
                  title: '',
                  duration: 1000,
                  message: '删除成功',
                  position: 'bottom-right',
                  offset: 30,
                })
                if (this.pageTableData.length === this.selectedRows.length) {
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
}
</script>

<style scoped></style>

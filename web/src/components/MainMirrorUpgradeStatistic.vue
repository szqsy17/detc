<template>
  <el-row>
    <el-row style="padding: 10px; text-align: center;">
      <el-col :span="7">
        <el-input placeholder="车架号" size="small" v-model="filterData.search" style="width: 180px;" clearable>
        </el-input>
      </el-col>
      <el-col :span="7">
        <el-select v-model="filterData.status" size="small" style="width: 180px;">
          <el-option v-for="(status, index) in statusList" :key="index" :label="status" :value="index"> </el-option>
        </el-select>
      </el-col>
      <el-col :span="3">
        <el-button icon="el-icon-search" size="small" @click="searchStatistic"></el-button>
      </el-col>
    </el-row>
    <el-row style="padding: 0 10px; flex-grow: 1;">
      <el-table
        border
        :data="pageTableData"
        :header-cell-style="{ background: '#F2F6FC', padding: '11px 0' }"
        :cell-style="{ padding: '11px 0' }"
      >
        <el-table-column type="index" align="center" min-width="1"></el-table-column>
        <el-table-column prop="vin" label="车架号" align="center" min-width="5"></el-table-column>
        <el-table-column prop="beforeVersion" label="原版本" align="center" min-width="5"></el-table-column>
        <el-table-column prop="targetVersion" label="目标版本" align="center" min-width="5"></el-table-column>
        <el-table-column prop="time" label="升级时间" align="center" min-width="5"></el-table-column>
        <el-table-column prop="status" label="结果" align="center" min-width="2">
          <template slot-scope="scope">
            {{ scope.row.status === 0 ? '成功' : '失败' }}
          </template>
        </el-table-column>
        <el-table-column prop="gps" label="GPS" align="center" min-width="2"></el-table-column>
        <el-table-column label="模块" align="center" min-width="2">
          <template slot-scope="scope">
            <el-link icon="el-icon-view" @click="showModuleInfo(scope.row)">
              查看
            </el-link>
          </template>
        </el-table-column>
      </el-table>
    </el-row>
    <Pagination
      ref="pagination"
      :pagination="pagination"
      :allTableData="allTableData"
      @changeCurrentPage="changeCurrentPage"
      @updatePageTable="updatePageTable"
    />
    <DialogModule :moduleDialog="moduleDialog" />
  </el-row>
</template>

<script>
import Pagination from '@/components/Pagination'
import Divider from '@/components/Divider'
import DialogModule from '@/components/DialogModule'
/**
 * @description 对Statistic包含信息的描述，消除Warning警告
 * @property vin 车架号
 * @property beforeVersion 原版本
 * @property targetVersion 目标版本
 * @property time 时间
 * @property gps gps
 * @property status 结果
 * @property moduleStatisticModels 模块信息
 */
export default {
  name: 'UpdateStatistic',
  components: {
    Pagination,
    Divider,
    DialogModule,
  },
  data() {
    return {
      filterData: {
        search: '',
        status: 0,
      },
      statusList: ['全部状态', '成功', '失败'],

      allTableData: [],
      filterTableData: [],
      pageTableData: [],
      currentRow: null,

      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
        layout: 'total, prev, pager, next, jumper',
      },
      changeToPage: 1,

      moduleDialog: {
        show: false,
        moduleInfo: {},
      },
    }
  },
  created() {
    this.getAllStatisticInfo().then(() => {
      this.initialPagination()
    })
  },
  methods: {
    getAllStatisticInfo() {
      return this.axios.get('/statistic/getAllMirrorStatistic').then((res) => {
        if (res.data.code === '200') {
          this.allTableData = res.data.msg
          this.filterTableData = res.data.msg
        }
      })
    },

    filterStatistic() {
      let statusFilter = false
      if (this.filterData.status && this.filterData.status !== 0) {
        statusFilter = true
      }

      this.allTableData = this.filterTableData.filter((item) => {
        return statusFilter
          ? item.vin.match(this.filterData.search) && item.status === this.filterData.status - 1
          : item.vin.match(this.filterData.search)
      })
    },
    searchStatistic() {
      this.filterStatistic()
      this.initialPagination()
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

    showModuleInfo(row) {
      this.moduleDialog = {
        show: true,
        moduleInfo: row.moduleStatisticModels,
      }
    },
  },
}
</script>

<style scoped></style>

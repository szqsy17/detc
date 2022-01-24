<template>
  <el-row>
    <el-row style="padding: 10px; text-align: center;">
      <el-col :span="6">
        <el-input
          clearable
          placeholder="车架号"
          size="small"
          v-model="search"
          @keyup.enter.native="searchCar"
          style="width: 180px;"
        >
        </el-input>
      </el-col>
      <el-col :span="2">
        <el-button icon="el-icon-search" size="small" @click="searchCar"></el-button>
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
        <el-table-column prop="vin" label="车架号" align="center" min-width="4"></el-table-column>
        <el-table-column label="总版本号" align="center" min-width="4">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="bottom" width="250">
              <el-form size="mini" label-width="100px">
                <el-form-item>
                  <el-row slot="label" style="color: #99a9bf;">IVI版本号</el-row>
                  <el-row>{{ scope.row.iviVersion }}</el-row>
                </el-form-item>
                <el-form-item>
                  <el-row slot="label" style="color: #99a9bf;">Cluster版本号</el-row>
                  <el-row>{{ scope.row.clusterVersion }}</el-row>
                </el-form-item>
                <el-form-item style="margin-bottom: 0;">
                  <el-row slot="label" style="color: #99a9bf;">MCU版本号</el-row>
                  <el-row>{{ scope.row.mcuVersion }}</el-row>
                </el-form-item>
              </el-form>
              <el-row slot="reference">
                {{ scope.row.version }}
              </el-row>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="可升级" align="center" min-width="2">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.allowUpgrade === 1" @change="closeUpgrade(scope.row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="目标版本" align="center" min-width="4">
          <template slot-scope="scope" v-if="scope.row.allowUpgrade === 1">
            <el-row type="flex" align="middle">
              <el-col :span="18">{{ scope.row.targetVersion ? scope.row.targetVersion : '无' }}</el-col>
              <el-col :span="6" style="text-align: left;">
                <el-link icon="el-icon-edit-outline" @click="controlUpgrade(scope.row)">
                  修改
                </el-link>
              </el-col>
            </el-row>
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
    <DialogUpgrade :upgradeDialog="upgradeDialog" @refreshTargetVersion="refreshTargetVersion" />
  </el-row>
</template>

<script>
import Pagination from '@/components/Pagination'
import Divider from '@/components/Divider'
import DialogUpgrade from '@/components/DialogUpgrade'
/**
 * @description 对Car包含信息的描述，消除Warning警告
 * @property vin 车架号
 * @property version 镜像版本号
 * @property iviVersion IVI版本号
 * @property clusterVersion Cluster版本号
 * @property mcuVersion MCU版本号
 * @property targetVersionId 目标镜像id
 * @property targetVersion 目标镜像版本号
 * @property allowUpgrade 允许升级
 */
export default {
  name: 'MainUpdateManage',
  components: {
    Pagination,
    Divider,
    DialogUpgrade,
  },
  data() {
    return {
      search: '',

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

      upgradeDialog: {
        show: false,
        carId: '',
        targetVersion: '',
        targetVersionId: '',
        targetVersionIdData: '',
        mirrorList: {},
      },
    }
  },
  created() {
    this.getAllCarInfo().then(() => {
      this.initialPagination()
    })
  },
  methods: {
    getAllCarInfo() {
      return this.axios.get('/car/getAllCar').then((res) => {
        if (res.data.code === '200') {
          this.allTableData = res.data.msg
          this.filterTableData = res.data.msg
        }
      })
    },

    filterCar() {
      this.allTableData = this.filterTableData.filter((item) => {
        return item.vin.match(this.search)
      })
    },
    searchCar() {
      this.filterCar()
      this.initialPagination()
    },

    updatePageTable(pageTableData) {
      this.pageTableData = pageTableData
    },
    closeUpgrade(row) {
      let data = {
        id: row.id,
        before: row.allowUpgrade,
      }
      this.axios.post('/car/toggleCarUpgrade', data).then((res) => {
        if (res.data.code === '200') {
          row.allowUpgrade = row.allowUpgrade === 1 ? 0 : 1
          this.$notify.success({
            title: '',
            duration: 1000,
            message: '切换成功',
            position: 'bottom-right',
            offset: 30,
          })
        }
      })
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

    controlUpgrade(row) {
      let data = {
        currentVersion: row.version,
      }
      this.axios.post('/mirror/getMirrorAfterTargetVersion', data).then((res) => {
        if (res.data.code === '200') {
          this.upgradeDialog = {
            show: true,
            carId: row.id,
            targetVersion: row.targetVersion,
            targetVersionId: row.targetVersionId,
            targetVersionIdData: row.targetVersionId,
            mirrorList: res.data.msg,
          }
        }
      })
    },
    refreshTargetVersion(carId, targetMirror) {
      this.pageTableData.find((item) => {
        if (item.id === carId) {
          item.targetVersion = targetMirror.mirrorVersion
          item.targetVersionId = targetMirror.id
        }
      })
    },
  },
}
</script>

<style scoped></style>

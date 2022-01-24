<template>
  <el-row>
    <el-row style="padding: 10px; text-align: center;">
      <el-col :span="6">
        <el-input placeholder="关键字" size="small" v-model="filterData.search" style="width: 180px;" clearable>
        </el-input>
      </el-col>
      <el-col :span="10">
        <el-date-picker
          v-model="filterData.time"
          type="datetimerange"
          range-separator="To"
          start-placeholder="发布时间"
          end-placeholder="发布时间"
          unlink-panels
          size="small"
          align="center"
          :default-time="['12:00:00', '12:00:00']"
          style="width: 380px;"
        >
        </el-date-picker>
      </el-col>
      <el-col :span="2">
        <el-button icon="el-icon-search" size="small" @click="searchLog"></el-button>
      </el-col>
    </el-row>
    <el-row style="padding: 0 10px; flex-grow: 1;">
      <el-table
        border
        :data="pageTableData"
        highlight-current-row
        @current-change="currentChange"
        :header-cell-style="{ background: '#F2F6FC', padding: '11px 0' }"
        :cell-style="{ padding: '11px 0' }"
      >
        <el-table-column type="index" align="center" min-width="1"></el-table-column>
        <el-table-column label="版本号" align="center" prop="mirrorVersion" min-width="4"> </el-table-column>
        <el-table-column label="IVI版本号" align="center" prop="iviVersion" min-width="2"> </el-table-column>
        <el-table-column label="Cluster版本号" align="center" prop="clusterVersion" min-width="2"> </el-table-column>
        <el-table-column label="MCU版本号" align="center" prop="mcuVersion" min-width="2"> </el-table-column>
        <el-table-column label="描述" align="center" min-width="2">
          <template slot-scope="scope">
            <el-popover
              trigger="hover"
              placement="bottom"
              title="升级描述"
              width="200"
              :content="scope.row.description"
            >
              <el-row
                slot="reference"
                style="white-space: nowrap; text-overflow: ellipsis; overflow: hidden; text-align: left;"
              >
                {{ scope.row.description }}
              </el-row>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="发布时间" align="center" min-width="4"></el-table-column>
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
      <el-col :span="8">
        <el-button size="medium" type="primary" @click="addOneMirror">发布</el-button>
      </el-col>
      <el-col :span="8">
        <el-button size="medium" type="success" @click="editOneMirror">编辑</el-button>
      </el-col>
      <el-col :span="8">
        <el-button size="medium" type="danger" @click="deleteOneMirror">删除</el-button>
      </el-col>
    </el-row>
    <DialogMirror :mirrorDialog="mirrorDialog" @refreshAllTable="refreshAllTable" />
  </el-row>
</template>

<script>
import Pagination from '@/components/Pagination'
import Divider from '@/components/Divider'
import DialogMirror from '@/components/DialogMirror'
/**
 * @description 对Mirror包含信息的描述，消除Warning警告
 * @property projectName 项目名
 * @property mirrorVersion 镜像版本号
 * @property iviVersion IVI版本号
 * @property clusterVersion Cluster版本号
 * @property mcuVersion MCU版本号
 * @property mirrorFilePath 镜像路径
 * @property description 描述
 * @property time 上传时间
 */
export default {
  name: 'VersionManage',
  components: {
    Pagination,
    Divider,
    DialogMirror,
  },
  data() {
    return {
      filterData: {
        search: '',
        time: [],
      },

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

      mirrorDialog: {
        title: '',
        show: false,
        action: '',
        mirrorForm: {
          projectName: '',
          mirrorVersion: '',
          iviVersion: '',
          clusterVersion: '',
          mcuVersion: '',
          description: '',
          time: '',
        },
      },
    }
  },
  created() {
    this.getAllMirrorInfo().then(() => {
      this.initialPagination()
    })
  },
  methods: {
    getAllMirrorInfo() {
      return this.axios.get('/mirror/getAllMirror').then((res) => {
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

      this.allTableData = this.filterTableData.filter((item) => {
        let date = new Date(item.time)
        let time = date.getTime()

        if (timeFilter) {
          return (
            (item.mirrorVersion.match(this.filterData.search) ||
              item.iviVersion.match(this.filterData.search) ||
              item.clusterVersion.match(this.filterData.search) ||
              item.mcuVersion.match(this.filterData.search) ||
              item.description.match(this.filterData.search)) &&
            time >= startTime &&
            time <= endTime
          )
        } else {
          return (
            item.mirrorVersion.match(this.filterData.search) ||
            item.iviVersion.match(this.filterData.search) ||
            item.clusterVersion.match(this.filterData.search) ||
            item.mcuVersion.match(this.filterData.search) ||
            item.description.match(this.filterData.search)
          )
        }
      })
    },
    searchLog() {
      this.filterLog()
      this.initialPagination()
    },

    currentChange(row) {
      this.currentRow = row
    },
    refreshAllTable(action) {
      if (action === 'add') {
        this.filterData.search = ''
        this.filterData.time = []
      }

      this.getAllMirrorInfo()
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

    addOneMirror() {
      this.mirrorDialog = {
        title: '发布版本',
        show: true,
        action: 'add',
        mirrorForm: {
          projectName: 0,
          mirrorVersion: '',
          iviVersion: '',
          clusterVersion: '',
          mcuVersion: '',
          description: '',
          time: new Date(),
        },
      }
      this.changeToPage = 1
    },
    editOneMirror() {
      if (!this.currentRow) {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '请选择版本',
          position: 'bottom-right',
          offset: 30,
        })
      } else {
        this.mirrorDialog = {
          title: '编辑版本',
          show: true,
          action: 'edit',
          mirrorForm: {
            id: this.currentRow.id,
            projectName: 0,
            mirrorVersion: this.currentRow.mirrorVersion,
            iviVersion: this.currentRow.iviVersion,
            clusterVersion: this.currentRow.clusterVersion,
            mcuVersion: this.currentRow.mcuVersion,
            description: this.currentRow.description,
            time: new Date(),
          },
          mirrorFormData: {
            mirrorVersion: this.currentRow.mirrorVersion,
            iviVersion: this.currentRow.iviVersion,
            clusterVersion: this.currentRow.clusterVersion,
            mcuVersion: this.currentRow.mcuVersion,
            description: this.currentRow.description,
          },
        }
        this.changeToPage = this.pagination.currentPage
      }
    },
    deleteOneMirror() {
      if (!this.currentRow) {
        this.$notify.error({
          title: '',
          duration: 1000,
          message: '请选择版本',
          position: 'bottom-right',
          offset: 30,
        })
      } else {
        this.$confirm('此操作将永久删除该版本, 是否继续?', '提示', { type: 'warning' })
          .then(() => {
            let data = {
              id: this.currentRow.id,
            }
            this.axios.post('/mirror/deleteOneMirror', data).then((res) => {
              if (res.data.code === '200') {
                this.$notify.success({
                  title: '',
                  duration: 1000,
                  message: '删除成功',
                  position: 'bottom-right',
                  offset: 30,
                })
                if (this.pageTableData.length === 1) {
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

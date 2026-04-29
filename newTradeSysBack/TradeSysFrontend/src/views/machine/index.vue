<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>机器管理</span>
          <div class="header-actions">
            <el-button type="primary">
              <el-icon><Plus /></el-icon>
              新增机器
            </el-button>
            <el-button type="success">
              <el-icon><Upload /></el-icon>
              批量导入
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="machineNo" label="机器编号" width="150" />
        <el-table-column prop="productName" label="产品名称" width="120" />
        <el-table-column prop="sn" label="SN码" width="150" />
        <el-table-column prop="merchantName" label="绑定商户" width="120" />
        <el-table-column prop="agentName" label="所属代理商" width="120" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default>
            <el-button type="primary" link>编辑</el-button>
            <el-button type="warning" link>分配</el-button>
            <el-button type="danger" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const loading = ref(false)
const tableData = ref([
  { id: 1, machineNo: 'MAC000001', productName: '传统POS机-A款', sn: 'SN2024000001', merchantName: '北京国贸大饭店', agentName: '总部代理商', status: 2 },
  { id: 2, machineNo: 'MAC000002', productName: '传统POS机-A款', sn: 'SN2024000002', merchantName: '王府井百货', agentName: '总部代理商', status: 2 },
  { id: 3, machineNo: 'MAC000003', productName: '智能POS机', sn: 'SN2024000003', merchantName: '广州天河城', agentName: '华南区域总代理', status: 1 }
])

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '库存', 1: '已出库', 2: '已激活' }
  return texts[status] || '未知'
}
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}
</style>

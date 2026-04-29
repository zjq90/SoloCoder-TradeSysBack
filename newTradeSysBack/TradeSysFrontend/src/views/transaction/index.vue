<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>交易明细</span>
          <div class="header-actions">
            <el-button type="primary">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button type="success">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="transNo" label="交易单号" width="180" />
        <el-table-column prop="merchantName" label="商户名称" width="150" />
        <el-table-column prop="agentName" label="代理商" width="120" />
        <el-table-column prop="transType" label="交易类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.transType === 1 ? 'primary' : 'warning'">
              {{ row.transType === 1 ? '消费' : '退款' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="transAmount" label="交易金额" width="120" align="right">
          <template #default="{ row }">
            <span :class="row.transType === 1 ? 'amount-income' : 'amount-expense'">
              {{ row.transType === 1 ? '+' : '-' }}¥{{ row.transAmount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="feeAmount" label="手续费" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.feeAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="transTime" label="交易时间" width="180" />
        <el-table-column label="操作" width="150" align="center">
          <template #default>
            <el-button type="primary" link>详情</el-button>
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
  { id: 1, transNo: 'TRN2026042500001', merchantName: '北京国贸大饭店', agentName: '总部代理商', transType: 1, transAmount: 1580.00, feeAmount: 6.00, status: 1, transTime: '2026-04-25 09:15:30' },
  { id: 2, transNo: 'TRN2026042500002', merchantName: '北京国贸大饭店', agentName: '总部代理商', transType: 1, transAmount: 3200.00, feeAmount: 12.16, status: 1, transTime: '2026-04-25 09:30:45' },
  { id: 3, transNo: 'TRN2026042500003', merchantName: '王府井百货', agentName: '总部代理商', transType: 1, transAmount: 890.00, feeAmount: 3.38, status: 1, transTime: '2026-04-25 10:05:20' }
])

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'danger', 3: 'warning' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待处理', 1: '成功', 2: '失败', 3: '退款中' }
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

.amount-income {
  color: #67c23a;
  font-weight: bold;
}

.amount-expense {
  color: #f56c6c;
  font-weight: bold;
}
</style>

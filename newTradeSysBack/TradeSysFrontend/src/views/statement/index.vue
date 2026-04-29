<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>对账单</span>
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
        <el-table-column prop="statementNo" label="对账单号" width="180" />
        <el-table-column prop="agentName" label="代理商" width="120" />
        <el-table-column prop="statDate" label="对账日期" width="120" />
        <el-table-column prop="totalTransAmount" label="交易总额" width="130" align="right">
          <template #default="{ row }">
            ¥{{ row.totalTransAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalTransCount" label="交易笔数" width="100" align="center">
          <template #default="{ row }">
            {{ row.totalTransCount }}笔
          </template>
        </el-table-column>
        <el-table-column prop="totalFeeAmount" label="手续费总额" width="130" align="right">
          <template #default="{ row }">
            ¥{{ row.totalFeeAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalProfitAmount" label="分润总额" width="130" align="right">
          <template #default="{ row }">
            <span class="profit-amount">¥{{ row.totalProfitAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" link>详情</el-button>
            <el-button v-if="row.status === 0" type="success" link>确认</el-button>
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
  { id: 1, statementNo: 'STT2026042400001', agentName: '总部代理商', statDate: '2026-04-24', totalTransAmount: 22500.00, totalTransCount: 4, totalFeeAmount: 104.94, totalProfitAmount: 41.00, status: 1, confirmTime: '2026-04-25 09:00:00' },
  { id: 2, statementNo: 'STT2026042400002', agentName: '华南区域总代理', statDate: '2026-04-24', totalTransAmount: 15000.00, totalTransCount: 1, totalFeeAmount: 82.50, totalProfitAmount: 27.00, status: 1, confirmTime: '2026-04-25 09:30:00' },
  { id: 3, statementNo: 'STT2026042300001', agentName: '总部代理商', statDate: '2026-04-23', totalTransAmount: 6000.00, totalTransCount: 2, totalFeeAmount: 22.80, totalProfitAmount: 12.00, status: 1, confirmTime: '2026-04-24 09:00:00' }
])

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待确认', 1: '已确认' }
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

.profit-amount {
  color: #67c23a;
  font-weight: bold;
}
</style>

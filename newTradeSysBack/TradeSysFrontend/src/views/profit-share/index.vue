<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分润管理</span>
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
        <el-table-column prop="profitNo" label="分润单号" width="180" />
        <el-table-column prop="transNo" label="关联交易号" width="180" />
        <el-table-column prop="agentName" label="代理商" width="120" />
        <el-table-column prop="level" label="分润层级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.level === 1 ? 'danger' : row.level === 2 ? 'warning' : 'primary'">
              第{{ row.level }}级
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="transAmount" label="交易金额" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.transAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="profitRate" label="分润比例" width="100" align="center">
          <template #default="{ row }">
            {{ (row.profitRate * 100).toFixed(4) }}%
          </template>
        </el-table-column>
        <el-table-column prop="profitAmount" label="分润金额" width="120" align="right">
          <template #default="{ row }">
            <span class="profit-amount">+¥{{ row.profitAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已结算' : '待结算' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="settleTime" label="结算时间" width="180" />
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
  { id: 1, profitNo: 'PRF2026042500001', transNo: 'TRN2026042500001', agentName: '总部代理商', level: 1, transAmount: 1580.00, profitRate: 0.0020, profitAmount: 3.16, status: 1, settleTime: '2026-04-25 09:15:30' },
  { id: 2, profitNo: 'PRF2026042500002', transNo: 'TRN2026042500002', agentName: '总部代理商', level: 1, transAmount: 3200.00, profitRate: 0.0020, profitAmount: 6.40, status: 1, settleTime: '2026-04-25 09:30:45' },
  { id: 3, profitNo: 'PRF2026042500003', transNo: 'TRN2026042500003', agentName: '总部代理商', level: 1, transAmount: 890.00, profitRate: 0.0020, profitAmount: 1.78, status: 1, settleTime: '2026-04-25 10:05:20' }
])
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

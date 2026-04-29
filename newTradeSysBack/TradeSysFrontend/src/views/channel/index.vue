<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通道管理</span>
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            新增通道
          </el-button>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="channelNo" label="通道编号" width="120" />
        <el-table-column prop="channelName" label="通道名称" width="200" />
        <el-table-column prop="channelType" label="通道类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getChannelType(row.channelType)">
              {{ getChannelTypeText(row.channelType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rate" label="费率" width="100" align="center">
          <template #default="{ row }">
            {{ (row.rate * 100).toFixed(2) }}%
          </template>
        </el-table-column>
        <el-table-column prop="dailyLimit" label="日限额" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.dailyLimit.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default>
            <el-button type="primary" link>编辑</el-button>
            <el-button type="warning" link>配置</el-button>
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
  { id: 1, channelNo: 'CHL001', channelName: '微信支付-官方通道', channelType: 1, rate: 0.0038, dailyLimit: 5000000, status: 1 },
  { id: 2, channelNo: 'CHL002', channelName: '支付宝-官方通道', channelType: 2, rate: 0.0038, dailyLimit: 5000000, status: 1 },
  { id: 3, channelNo: 'CHL003', channelName: '银联云闪付', channelType: 3, rate: 0.0038, dailyLimit: 10000000, status: 1 }
])

const getChannelType = (type) => {
  const types = { 1: 'primary', 2: 'success', 3: 'warning' }
  return types[type] || 'info'
}

const getChannelTypeText = (type) => {
  const texts = { 1: '微信', 2: '支付宝', 3: '银联' }
  return texts[type] || '未知'
}
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

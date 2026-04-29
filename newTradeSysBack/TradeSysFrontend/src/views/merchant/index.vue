<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商户管理</span>
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            新增商户
          </el-button>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="merchantNo" label="商户编号" width="150" />
        <el-table-column prop="merchantName" label="商户名称" width="150" />
        <el-table-column prop="agentName" label="所属代理商" width="120" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="120" />
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
            <el-button type="warning" link>详情</el-button>
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
  { id: 1, merchantNo: 'M001', merchantName: '北京国贸大饭店', agentName: '总部代理商', contactName: '张经理', phone: '13800138001', status: 1 },
  { id: 2, merchantNo: 'M002', merchantName: '广州天河城', agentName: '华南区域总代理', contactName: '陈经理', phone: '13800138006', status: 1 },
  { id: 3, merchantNo: 'M003', merchantName: '深圳万象城', agentName: '深圳分公司', contactName: '郑经理', phone: '13800138009', status: 1 }
])
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

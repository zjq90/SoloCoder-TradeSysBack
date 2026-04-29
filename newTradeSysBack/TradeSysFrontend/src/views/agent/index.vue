<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>代理商管理</span>
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            新增代理商
          </el-button>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="agentNo" label="代理商编号" width="150" />
        <el-table-column prop="agentName" label="代理商名称" width="150" />
        <el-table-column prop="agentLevel" label="代理商等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.agentLevel === 1 ? 'danger' : row.agentLevel === 2 ? 'warning' : 'primary'">
              {{ row.agentLevel === 1 ? '一级' : row.agentLevel === 2 ? '二级' : '三级' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contact" label="联系人" width="100" />
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
            <el-button type="warning" link>下级</el-button>
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
  { id: 1, agentNo: 'A001', agentName: '总部代理商', agentLevel: 1, contact: '张三', phone: '13800138001', status: 1 },
  { id: 2, agentNo: 'A002', agentName: '北京代理商', agentLevel: 2, contact: '李四', phone: '13800138002', status: 1 },
  { id: 3, agentNo: 'A003', agentName: '上海代理商', agentLevel: 2, contact: '王五', phone: '13800138003', status: 1 }
])
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

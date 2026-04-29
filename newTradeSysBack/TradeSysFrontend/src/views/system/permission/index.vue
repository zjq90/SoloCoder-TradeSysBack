<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            新增权限
          </el-button>
        </div>
      </template>
      
      <el-table
        :data="tableData"
        style="width: 100%"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="permissionName" label="权限名称" min-width="200" />
        <el-table-column prop="permissionCode" label="权限编码" width="200" />
        <el-table-column prop="menuType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.menuType === 1 ? 'primary' : 'warning'">
              {{ row.menuType === 1 ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
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
            <el-button type="danger" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const tableData = ref([
  {
    id: 1,
    permissionName: '系统管理',
    permissionCode: 'system',
    menuType: 1,
    sort: 1,
    status: 1,
    children: [
      { id: 11, permissionName: '用户管理', permissionCode: 'system:user', menuType: 1, sort: 1, status: 1 },
      { id: 12, permissionName: '角色管理', permissionCode: 'system:role', menuType: 1, sort: 2, status: 1 },
      { id: 13, permissionName: '权限管理', permissionCode: 'system:permission', menuType: 1, sort: 3, status: 1 }
    ]
  },
  {
    id: 2,
    permissionName: '代理商管理',
    permissionCode: 'agent',
    menuType: 1,
    sort: 2,
    status: 1
  }
])
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

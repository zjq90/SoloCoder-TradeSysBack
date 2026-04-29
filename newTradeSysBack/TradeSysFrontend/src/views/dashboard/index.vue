<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-item">
          <div class="card-icon" style="background: #409EFF;">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-value">{{ stats.agentCount || 0 }}</p>
            <p class="card-label">代理商总数</p>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-item">
          <div class="card-icon" style="background: #67C23A;">
            <el-icon><Shop /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-value">{{ stats.merchantCount || 0 }}</p>
            <p class="card-label">商户总数</p>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-item">
          <div class="card-icon" style="background: #E6A23C;">
            <el-icon><Monitor /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-value">{{ stats.machineCount || 0 }}</p>
            <p class="card-label">机器总数</p>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-item">
          <div class="card-icon" style="background: #F56C6C;">
            <el-icon><Money /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-value">{{ formatMoney(stats.totalAmount) }}</p>
            <p class="card-label">交易总额</p>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <div class="action-item" @click="goToPage('/agent')">
              <el-icon size="40" color="#409EFF"><OfficeBuilding /></el-icon>
              <span>代理商管理</span>
            </div>
            <div class="action-item" @click="goToPage('/merchant')">
              <el-icon size="40" color="#67C23A"><Shop /></el-icon>
              <span>商户管理</span>
            </div>
            <div class="action-item" @click="goToPage('/machine')">
              <el-icon size="40" color="#E6A23C"><Monitor /></el-icon>
              <span>机器管理</span>
            </div>
            <div class="action-item" @click="goToPage('/transaction')">
              <el-icon size="40" color="#909399"><List /></el-icon>
              <span>交易明细</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card>
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统名称">交易管理系统</el-descriptions-item>
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="当前用户">
              {{ userStore.userInfo?.realName || userStore.userInfo?.username }}
            </el-descriptions-item>
            <el-descriptions-item label="登录时间">
              {{ currentTime }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  agentCount: 0,
  merchantCount: 0,
  machineCount: 0,
  totalAmount: 0
})

const currentTime = ref('')

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const formatMoney = (amount) => {
  if (!amount) return '¥0.00'
  return '¥' + Number(amount).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const goToPage = (path) => {
  router.push(path)
}

onMounted(() => {
  updateTime()
  setInterval(updateTime, 1000)
  
  stats.value = {
    agentCount: 50,
    merchantCount: 200,
    machineCount: 1500,
    totalAmount: 12580000.50
  }
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .card-item {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    
    .card-icon {
      width: 60px;
      height: 60px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 28px;
    }
    
    .card-info {
      margin-left: 15px;
      
      .card-value {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
        margin: 0;
      }
      
      .card-label {
        font-size: 14px;
        color: #909399;
        margin: 5px 0 0 0;
      }
    }
  }
  
  .quick-actions {
    display: flex;
    flex-wrap: wrap;
    
    .action-item {
      width: 25%;
      text-align: center;
      padding: 20px 0;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        background: #f5f7fa;
        
        span {
          color: #409EFF;
        }
      }
      
      span {
        display: block;
        margin-top: 10px;
        font-size: 14px;
        color: #606266;
      }
    }
  }
}
</style>

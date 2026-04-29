import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const isLoggedIn = ref(false)
  const userInfo = ref(null)
  const roles = ref([])
  const permissions = ref([])

  const setLoggedIn = (value) => {
    isLoggedIn.value = value
  }

  const setUserInfo = (info) => {
    userInfo.value = info
  }

  const setRoles = (roleList) => {
    roles.value = roleList
  }

  const setPermissions = (permList) => {
    permissions.value = permList
  }

  const login = async () => {
    isLoggedIn.value = true
    await getUserInfo()
  }

  const logout = async () => {
    isLoggedIn.value = false
    userInfo.value = null
    roles.value = []
    permissions.value = []
  }

  const getUserInfo = async () => {
    try {
      const res = await authApi.getCurrentUser()
      if (res.code === 200) {
        userInfo.value = res.data
        isLoggedIn.value = true
      }
      return res
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  const getMenus = async () => {
    try {
      const res = await authApi.getMenus()
      if (res.code === 200 && res.data) {
        if (res.data.roles) {
          roles.value = res.data.roles
        }
        if (res.data.permissions) {
          permissions.value = res.data.permissions
        }
      }
      return res
    } catch (error) {
      console.error('获取菜单失败:', error)
      throw error
    }
  }

  return {
    isLoggedIn,
    userInfo,
    roles,
    permissions,
    setLoggedIn,
    setUserInfo,
    setRoles,
    setPermissions,
    login,
    logout,
    getUserInfo,
    getMenus
  }
})

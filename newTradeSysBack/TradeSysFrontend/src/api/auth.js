import request from './request'

export function getCurrentUser() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

export function getMenus() {
  return request({
    url: '/auth/menus',
    method: 'get'
  })
}

export function checkLoginStatus() {
  return request({
    url: '/auth/check',
    method: 'get'
  })
}

export function changePassword(oldPassword, newPassword) {
  return request({
    url: '/auth/change-password',
    method: 'post',
    params: {
      oldPassword,
      newPassword
    }
  })
}

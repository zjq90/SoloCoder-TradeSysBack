import request from './request'

export function getUserList(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

export function getUserById(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

export function addUser(data, roleIds) {
  return request({
    url: '/system/user/add',
    method: 'post',
    data,
    params: {
      roleIds: roleIds?.join(',')
    }
  })
}

export function updateUser(data, roleIds) {
  return request({
    url: '/system/user/update',
    method: 'post',
    data,
    params: {
      roleIds: roleIds?.join(',')
    }
  })
}

export function deleteUser(id) {
  return request({
    url: `/system/user/delete/${id}`,
    method: 'post'
  })
}

export function deleteUsers(ids) {
  return request({
    url: '/system/user/delete/batch',
    method: 'post',
    data: ids
  })
}

export function resetPassword(id, newPassword) {
  return request({
    url: '/system/user/reset-password',
    method: 'post',
    params: {
      id,
      newPassword
    }
  })
}

export function checkUsername(username, excludeId) {
  return request({
    url: '/system/user/check-username',
    method: 'get',
    params: {
      username,
      excludeId
    }
  })
}

export function getAllActiveUsers() {
  return request({
    url: '/system/user/all-active',
    method: 'get'
  })
}

package com.tradesys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.entity.SysPermission;
import com.tradesys.entity.SysRole;
import com.tradesys.mapper.SysRoleMapper;
import com.tradesys.mapper.SysRolePermissionMapper;
import com.tradesys.mapper.SysUserRoleMapper;
import com.tradesys.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public Page<SysRole> queryPage(PageQuery pageQuery, SysRole role) {
        Page<SysRole> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        
        if (role != null) {
            if (StringUtils.hasText(role.getRoleName())) {
                wrapper.like(SysRole::getRoleName, role.getRoleName());
            }
            if (StringUtils.hasText(role.getRoleCode())) {
                wrapper.like(SysRole::getRoleCode, role.getRoleCode());
            }
            if (role.getStatus() != null) {
                wrapper.eq(SysRole::getStatus, role.getStatus());
            }
        }
        
        wrapper.orderByAsc(SysRole::getId);
        return this.page(page, wrapper);
    }

    @Override
    public SysRole getDetailById(Long id) {
        SysRole role = this.getById(id);
        if (role != null) {
            List<SysPermission> permissions = baseMapper.selectPermissionsByRoleId(id);
            role.setPermissions(permissions);
        }
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveWithPermissions(SysRole role, List<Long> permissionIds) {
        boolean result = this.save(role);
        if (result && !CollectionUtils.isEmpty(permissionIds)) {
            assignPermissions(role.getId(), permissionIds);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithPermissions(SysRole role, List<Long> permissionIds) {
        boolean result = this.updateById(role);
        if (result && permissionIds != null) {
            assignPermissions(role.getId(), permissionIds);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWithPermissions(Long id) {
        SysRole role = this.getById(id);
        if (role == null) {
            return false;
        }
        assignPermissions(id, new ArrayList<>());
        userRoleMapper.deleteByRoleId(id);
        return this.removeById(id);
    }

    @Override
    public boolean checkRoleCodeUnique(String roleCode, Long excludeId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        List<SysPermission> permissions = baseMapper.selectPermissionsByRoleId(roleId);
        return permissions.stream().map(SysPermission::getId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        baseMapper.deleteRolePermissions(roleId);
        if (!CollectionUtils.isEmpty(permissionIds)) {
            baseMapper.insertRolePermissions(roleId, permissionIds);
        }
    }

    @Override
    public List<SysRole> listAllEnabled() {
        return this.list(new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, 1).orderByAsc(SysRole::getId));
    }

    @Override
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        return baseMapper.selectPermissionsByRoleId(roleId);
    }
}

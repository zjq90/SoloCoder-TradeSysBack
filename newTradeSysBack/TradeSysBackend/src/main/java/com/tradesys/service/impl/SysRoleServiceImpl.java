package com.tradesys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.SysPermission;
import com.tradesys.entity.SysRole;
import com.tradesys.mapper.SysPermissionMapper;
import com.tradesys.mapper.SysRoleMapper;
import com.tradesys.mapper.SysRolePermissionMapper;
import com.tradesys.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色Service实现类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public Result<List<SysRole>> queryPage(PageQuery pageQuery, SysRole role) {
        Page<SysRole> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (role != null) {
            if (StrUtil.isNotBlank(role.getRoleName())) {
                wrapper.like(SysRole::getRoleName, role.getRoleName());
            }
            if (StrUtil.isNotBlank(role.getRoleCode())) {
                wrapper.like(SysRole::getRoleCode, role.getRoleCode());
            }
            if (role.getStatus() != null) {
                wrapper.eq(SysRole::getStatus, role.getStatus());
            }
        }

        wrapper.orderByDesc(SysRole::getCreateTime);
        this.page(page, wrapper);

        return Result.success(page.getRecords(), page.getTotal());
    }

    @Override
    public Result<SysRole> getDetailById(Long id) {
        if (id == null) {
            return Result.error("角色ID不能为空");
        }

        SysRole role = this.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }

        List<Long> permissionIds = sysRoleMapper.selectPermissionIdsByRoleId(id);
        role.setPermissionIds(permissionIds);

        return Result.success(role);
    }

    @Override
    public Result<Void> saveEntity(SysRole entity) {
        return Result.error("请使用saveWithPermissions方法保存角色");
    }

    @Override
    public Result<Void> updateEntity(SysRole entity) {
        return Result.error("请使用updateWithPermissions方法更新角色");
    }

    @Override
    public Result<Void> deleteById(Long id) {
        return deleteWithPermissions(id);
    }

    @Override
    public Result<Void> deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.error("删除ID列表不能为空");
        }

        for (Long id : ids) {
            Result<Void> result = deleteWithPermissions(id);
            if (!result.isSuccess()) {
                return result;
            }
        }

        return Result.ok();
    }

    @Override
    public Result<List<SysRole>> listAllActive() {
        List<SysRole> roles = this.list(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getId));
        return Result.success(roles);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> saveWithPermissions(SysRole role, List<Long> permissionIds) {
        if (role == null) {
            return Result.error("角色信息不能为空");
        }

        if (StrUtil.isBlank(role.getRoleName())) {
            return Result.error("角色名称不能为空");
        }

        if (StrUtil.isBlank(role.getRoleCode())) {
            return Result.error("角色编码不能为空");
        }

        if (!checkRoleCodeUnique(role.getRoleCode(), null)) {
            return Result.error("角色编码已存在");
        }

        if (role.getStatus() == null) {
            role.setStatus(1);
        }

        boolean saved = this.save(role);
        if (!saved) {
            return Result.error("保存角色失败");
        }

        if (!CollectionUtils.isEmpty(permissionIds)) {
            assignPermissions(role.getId(), permissionIds);
        }

        log.info("新增角色成功: roleName={}", role.getRoleName());
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateWithPermissions(SysRole role, List<Long> permissionIds) {
        if (role == null || role.getId() == null) {
            return Result.error("角色ID不能为空");
        }

        SysRole existRole = this.getById(role.getId());
        if (existRole == null) {
            return Result.error("角色不存在");
        }

        if (StrUtil.isNotBlank(role.getRoleCode())) {
            if (!checkRoleCodeUnique(role.getRoleCode(), role.getId())) {
                return Result.error("角色编码已存在");
            }
        }

        boolean updated = this.updateById(role);
        if (!updated) {
            return Result.error("更新角色失败");
        }

        if (permissionIds != null) {
            assignPermissions(role.getId(), permissionIds);
        }

        log.info("更新角色成功: roleId={}", role.getId());
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteWithPermissions(Long id) {
        if (id == null) {
            return Result.error("角色ID不能为空");
        }

        SysRole role = this.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }

        assignPermissions(id, new ArrayList<>());
        this.removeById(id);

        log.info("删除角色成功: roleId={}", id);
        return Result.ok();
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return sysRoleMapper.selectByUserId(userId);
    }

    @Override
    public boolean checkRoleCodeUnique(String roleCode, Long excludeId) {
        if (StrUtil.isBlank(roleCode)) {
            return false;
        }

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }

        return this.count(wrapper) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        if (roleId == null) {
            return;
        }

        sysRolePermissionMapper.deleteByRoleId(roleId);
        if (!CollectionUtils.isEmpty(permissionIds)) {
            sysRolePermissionMapper.insertBatch(roleId, permissionIds);
        }

        log.info("分配权限成功: roleId={}, permissionIds={}", roleId, permissionIds);
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return sysRoleMapper.selectPermissionIdsByRoleId(roleId);
    }
}

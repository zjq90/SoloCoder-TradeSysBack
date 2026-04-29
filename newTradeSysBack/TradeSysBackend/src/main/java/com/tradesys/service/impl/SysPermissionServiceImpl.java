package com.tradesys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.SysPermission;
import com.tradesys.mapper.SysPermissionMapper;
import com.tradesys.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限Service实现类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public Result<List<SysPermission>> queryPage(PageQuery pageQuery, SysPermission permission) {
        Page<SysPermission> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        if (permission != null) {
            if (StrUtil.isNotBlank(permission.getPermissionName())) {
                wrapper.like(SysPermission::getPermissionName, permission.getPermissionName());
            }
            if (StrUtil.isNotBlank(permission.getPermissionCode())) {
                wrapper.like(SysPermission::getPermissionCode, permission.getPermissionCode());
            }
            if (permission.getMenuType() != null) {
                wrapper.eq(SysPermission::getMenuType, permission.getMenuType());
            }
            if (permission.getStatus() != null) {
                wrapper.eq(SysPermission::getStatus, permission.getStatus());
            }
        }

        wrapper.orderByAsc(SysPermission::getParentId)
                .orderByAsc(SysPermission::getSort);
        this.page(page, wrapper);

        return Result.success(page.getRecords(), page.getTotal());
    }

    @Override
    public Result<SysPermission> getDetailById(Long id) {
        if (id == null) {
            return Result.error("权限ID不能为空");
        }

        SysPermission permission = this.getById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }

        if (permission.getParentId() != null && permission.getParentId() > 0) {
            SysPermission parent = this.getById(permission.getParentId());
            if (parent != null) {
                permission.setParentName(parent.getPermissionName());
            }
        }

        return Result.success(permission);
    }

    @Override
    public Result<Void> saveEntity(SysPermission entity) {
        if (entity == null) {
            return Result.error("权限信息不能为空");
        }

        if (StrUtil.isBlank(entity.getPermissionName())) {
            return Result.error("权限名称不能为空");
        }

        if (StrUtil.isNotBlank(entity.getPermissionCode())) {
            if (!checkPermissionCodeUnique(entity.getPermissionCode(), null)) {
                return Result.error("权限编码已存在");
            }
        }

        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }

        if (entity.getParentId() == null) {
            entity.setParentId(0L);
        }

        boolean saved = this.save(entity);
        if (!saved) {
            return Result.error("保存权限失败");
        }

        log.info("新增权限成功: permissionName={}", entity.getPermissionName());
        return Result.ok();
    }

    @Override
    public Result<Void> updateEntity(SysPermission entity) {
        if (entity == null || entity.getId() == null) {
            return Result.error("权限ID不能为空");
        }

        SysPermission existPermission = this.getById(entity.getId());
        if (existPermission == null) {
            return Result.error("权限不存在");
        }

        if (StrUtil.isNotBlank(entity.getPermissionCode())) {
            if (!checkPermissionCodeUnique(entity.getPermissionCode(), entity.getId())) {
                return Result.error("权限编码已存在");
            }
        }

        boolean updated = this.updateById(entity);
        if (!updated) {
            return Result.error("更新权限失败");
        }

        log.info("更新权限成功: permissionId={}", entity.getId());
        return Result.ok();
    }

    @Override
    public Result<Void> deleteById(Long id) {
        if (id == null) {
            return Result.error("权限ID不能为空");
        }

        SysPermission permission = this.getById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }

        if (hasChildren(id)) {
            return Result.error("存在子权限，无法删除");
        }

        this.removeById(id);

        log.info("删除权限成功: permissionId={}", id);
        return Result.ok();
    }

    @Override
    public Result<Void> deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.error("删除ID列表不能为空");
        }

        for (Long id : ids) {
            Result<Void> result = deleteById(id);
            if (!result.isSuccess()) {
                return result;
            }
        }

        return Result.ok();
    }

    @Override
    public Result<List<SysPermission>> listAllActive() {
        List<SysPermission> permissions = this.list(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getStatus, 1)
                .orderByAsc(SysPermission::getParentId)
                .orderByAsc(SysPermission::getSort));
        return Result.success(permissions);
    }

    @Override
    public Result<List<SysPermission>> getPermissionTree() {
        List<SysPermission> allPermissions = sysPermissionMapper.selectAllForTree();
        List<SysPermission> tree = buildTree(allPermissions);
        return Result.success(tree);
    }

    @Override
    public List<SysPermission> getPermissionsByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return sysPermissionMapper.selectByUserId(userId);
    }

    @Override
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return sysPermissionMapper.selectByRoleId(roleId);
    }

    @Override
    public boolean checkPermissionCodeUnique(String permissionCode, Long excludeId) {
        if (StrUtil.isBlank(permissionCode)) {
            return false;
        }

        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getPermissionCode, permissionCode);
        if (excludeId != null) {
            wrapper.ne(SysPermission::getId, excludeId);
        }

        return this.count(wrapper) == 0;
    }

    @Override
    public boolean hasChildren(Long parentId) {
        if (parentId == null) {
            return false;
        }
        return sysPermissionMapper.countChildren(parentId) > 0;
    }

    @Override
    public Result<List<SysPermission>> getMenuTree(Long userId) {
        List<SysPermission> allPermissions;
        if (userId == null) {
            allPermissions = sysPermissionMapper.selectAllForTree();
        } else {
            allPermissions = getPermissionsByUserId(userId);
        }

        List<SysPermission> menuList = allPermissions.stream()
                .filter(p -> p.getMenuType() == null || p.getMenuType() == 1)
                .collect(Collectors.toList());

        List<SysPermission> tree = buildTree(menuList);
        return Result.success(tree);
    }

    private List<SysPermission> buildTree(List<SysPermission> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            return new ArrayList<>();
        }

        Map<Long, SysPermission> permissionMap = permissions.stream()
                .collect(Collectors.toMap(SysPermission::getId, p -> p));

        List<SysPermission> roots = new ArrayList<>();

        for (SysPermission permission : permissions) {
            Long parentId = permission.getParentId();
            if (parentId == null || parentId == 0) {
                roots.add(permission);
            } else {
                SysPermission parent = permissionMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(permission);
                }
            }
        }

        return roots;
    }
}

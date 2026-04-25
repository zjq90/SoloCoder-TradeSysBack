package com.tradesys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.entity.SysPermission;
import com.tradesys.mapper.SysPermissionMapper;
import com.tradesys.mapper.SysRolePermissionMapper;
import com.tradesys.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public List<SysPermission> treeList() {
        List<SysPermission> permissions = this.list(
                new LambdaQueryWrapper<SysPermission>()
                        .orderByAsc(SysPermission::getSort)
                        .orderByAsc(SysPermission::getId)
        );
        return buildTree(permissions);
    }

    @Override
    public List<SysPermission> listByUserId(Long userId) {
        return baseMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<SysPermission> listByRoleId(Long roleId) {
        return baseMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    public List<SysPermission> listAllEnabled() {
        return this.list(
                new LambdaQueryWrapper<SysPermission>()
                        .eq(SysPermission::getStatus, 1)
                        .orderByAsc(SysPermission::getSort)
        );
    }

    @Override
    public boolean checkPermissionCodeUnique(String permissionCode, Long excludeId) {
        if (!StringUtils.hasText(permissionCode)) {
            return true;
        }
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getPermissionCode, permissionCode);
        if (excludeId != null) {
            wrapper.ne(SysPermission::getId, excludeId);
        }
        return this.count(wrapper) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWithChildren(Long id) {
        List<Long> allIds = new ArrayList<>();
        collectChildIds(id, allIds);
        allIds.add(id);
        
        for (Long pid : allIds) {
            rolePermissionMapper.deleteByPermissionId(pid);
        }
        
        return this.removeByIds(allIds);
    }

    private void collectChildIds(Long parentId, List<Long> ids) {
        List<SysPermission> children = this.list(
                new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, parentId)
        );
        for (SysPermission child : children) {
            ids.add(child.getId());
            collectChildIds(child.getId(), ids);
        }
    }

    @Override
    public boolean hasChildren(Long id) {
        return this.count(
                new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id)
        ) > 0;
    }

    @Override
    public List<SysPermission> buildTree(List<SysPermission> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            return new ArrayList<>();
        }
        
        Map<Long, List<SysPermission>> parentMap = permissions.stream()
                .collect(Collectors.groupingBy(p -> p.getParentId() == null ? 0L : p.getParentId()));
        
        permissions.forEach(p -> p.setChildren(parentMap.getOrDefault(p.getId(), new ArrayList<>())));
        
        return parentMap.getOrDefault(0L, new ArrayList<>());
    }
}

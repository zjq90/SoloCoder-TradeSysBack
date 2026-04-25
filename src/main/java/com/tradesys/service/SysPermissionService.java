package com.tradesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.entity.SysPermission;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {

    List<SysPermission> treeList();

    List<SysPermission> listByUserId(Long userId);

    List<SysPermission> listByRoleId(Long roleId);

    List<SysPermission> listAllEnabled();

    boolean checkPermissionCodeUnique(String permissionCode, Long excludeId);

    boolean deleteWithChildren(Long id);

    boolean hasChildren(Long id);

    List<SysPermission> buildTree(List<SysPermission> permissions);
}

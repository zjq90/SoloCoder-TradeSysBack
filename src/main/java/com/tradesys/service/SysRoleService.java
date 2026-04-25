package com.tradesys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.entity.SysPermission;
import com.tradesys.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    Page<SysRole> queryPage(PageQuery pageQuery, SysRole role);

    SysRole getDetailById(Long id);

    boolean saveWithPermissions(SysRole role, List<Long> permissionIds);

    boolean updateWithPermissions(SysRole role, List<Long> permissionIds);

    boolean deleteWithPermissions(Long id);

    boolean checkRoleCodeUnique(String roleCode, Long excludeId);

    List<Long> getRolePermissionIds(Long roleId);

    void assignPermissions(Long roleId, List<Long> permissionIds);

    List<SysRole> listAllEnabled();

    List<SysPermission> getPermissionsByRoleId(Long roleId);
}

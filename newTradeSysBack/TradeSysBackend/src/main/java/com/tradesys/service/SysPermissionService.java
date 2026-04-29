package com.tradesys.service;

import com.tradesys.common.Result;
import com.tradesys.entity.SysPermission;

import java.util.List;

/**
 * 权限Service接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
public interface SysPermissionService extends BaseService<SysPermission> {

    /**
     * 获取权限树形结构
     *
     * @return 权限树形结构
     */
    Result<List<SysPermission>> getPermissionTree();

    /**
     * 根据用户ID获取权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<SysPermission> getPermissionsByUserId(Long userId);

    /**
     * 根据角色ID获取权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<SysPermission> getPermissionsByRoleId(Long roleId);

    /**
     * 检查权限编码是否唯一
     *
     * @param permissionCode 权限编码
     * @param excludeId      排除的权限ID（编辑时使用）
     * @return true 唯一, false 重复
     */
    boolean checkPermissionCodeUnique(String permissionCode, Long excludeId);

    /**
     * 检查是否有子权限
     *
     * @param parentId 父级权限ID
     * @return true 有子权限, false 没有
     */
    boolean hasChildren(Long parentId);

    /**
     * 获取菜单树形结构（用于前端菜单展示）
     *
     * @param userId 用户ID（用于数据权限过滤）
     * @return 菜单树形结构
     */
    Result<List<SysPermission>> getMenuTree(Long userId);
}

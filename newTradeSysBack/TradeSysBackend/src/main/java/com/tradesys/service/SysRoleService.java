package com.tradesys.service;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.SysRole;

import java.util.List;

/**
 * 角色Service接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
public interface SysRoleService extends BaseService<SysRole> {

    /**
     * 分页查询角色列表
     *
     * @param pageQuery 分页参数
     * @param role      查询条件
     * @return 角色列表
     */
    @Override
    Result<List<SysRole>> queryPage(PageQuery pageQuery, SysRole role);

    /**
     * 根据ID查询角色详情（包含权限）
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @Override
    Result<SysRole> getDetailById(Long id);

    /**
     * 新增角色（包含权限分配）
     *
     * @param role          角色信息
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    Result<Void> saveWithPermissions(SysRole role, List<Long> permissionIds);

    /**
     * 更新角色（包含权限分配）
     *
     * @param role          角色信息
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    Result<Void> updateWithPermissions(SysRole role, List<Long> permissionIds);

    /**
     * 删除角色（包含权限解绑）
     *
     * @param id 角色ID
     * @return 操作结果
     */
    Result<Void> deleteWithPermissions(Long id);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 检查角色编码是否唯一
     *
     * @param roleCode  角色编码
     * @param excludeId 排除的角色ID（编辑时使用）
     * @return true 唯一, false 重复
     */
    boolean checkRoleCodeUnique(String roleCode, Long excludeId);

    /**
     * 为角色分配权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
}

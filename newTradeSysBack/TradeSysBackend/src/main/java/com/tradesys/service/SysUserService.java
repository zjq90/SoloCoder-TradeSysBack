package com.tradesys.service;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * 系统用户Service接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
public interface SysUserService extends BaseService<SysUser>, UserDetailsService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);

    /**
     * 分页查询用户列表
     *
     * @param pageQuery 分页参数
     * @param user      查询条件
     * @return 用户列表
     */
    @Override
    Result<List<SysUser>> queryPage(PageQuery pageQuery, SysUser user);

    /**
     * 根据ID查询用户详情（包含角色和权限）
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @Override
    Result<SysUser> getDetailById(Long id);

    /**
     * 新增用户（包含角色分配）
     *
     * @param user    用户信息
     * @param roleIds 角色ID列表
     * @return 操作结果
     */
    Result<Void> saveWithRoles(SysUser user, List<Long> roleIds);

    /**
     * 更新用户（包含角色分配）
     *
     * @param user    用户信息
     * @param roleIds 角色ID列表
     * @return 操作结果
     */
    Result<Void> updateWithRoles(SysUser user, List<Long> roleIds);

    /**
     * 删除用户（包含角色解绑）
     *
     * @param id 用户ID
     * @return 操作结果
     */
    Result<Void> deleteWithRoles(Long id);

    /**
     * 重置密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     * @return 操作结果
     */
    Result<Void> resetPassword(Long id, String newPassword);

    /**
     * 检查用户名是否唯一
     *
     * @param username  用户名
     * @param excludeId 排除的用户ID（编辑时使用）
     * @return true 唯一, false 重复
     */
    boolean checkUsernameUnique(String username, Long excludeId);

    /**
     * 获取用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> getUserRoles(Long userId);

    /**
     * 获取用户的权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 为用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息
     */
    Result<SysUser> getCurrentUser();
}

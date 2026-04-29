package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.tradesys.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统用户Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户（包含关联的代理商信息）
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT u.*, a.agent_name AS agentName FROM sys_user u " +
            "LEFT JOIN agent a ON u.agent_id = a.id " +
            "WHERE u.username = #{username} AND u.deleted = 0")
    SysUser selectByUsernameWithAgent(@Param("username") String username);

    /**
     * 查询用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0 AND ur.deleted = 0")
    List<com.tradesys.entity.SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 1 AND p.deleted = 0 " +
            "AND rp.deleted = 0 AND ur.deleted = 0 " +
            "ORDER BY p.sort ASC")
    List<com.tradesys.entity.SysPermission> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户列表（包含代理商信息）
     *
     * @param queryWrapper 查询条件
     * @return 用户列表
     */
    @Select("SELECT u.*, a.agent_name AS agentName FROM sys_user u " +
            "LEFT JOIN agent a ON u.agent_id = a.id " +
            "${ew.customSqlSegment} " +
            "ORDER BY u.create_time DESC")
    List<SysUser> selectWithAgent(@Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);
}

package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0 AND ur.deleted = 0")
    List<SysRole> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @Select("SELECT rp.permission_id FROM sys_role_permission rp " +
            "WHERE rp.role_id = #{roleId} AND rp.deleted = 0")
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询所有角色（包含权限数量）
     *
     * @return 角色列表
     */
    @Select("SELECT r.*, " +
            "(SELECT COUNT(*) FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.deleted = 0) AS permissionCount " +
            "FROM sys_role r WHERE r.deleted = 0 ORDER BY r.create_time DESC")
    List<SysRole> selectAllWithPermissionCount();
}

package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.status = 1 AND p.deleted = 0 AND rp.deleted = 0 " +
            "ORDER BY p.sort ASC")
    List<SysPermission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限列表
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
    List<SysPermission> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询所有菜单（树形结构使用）
     *
     * @return 权限列表
     */
    @Select("SELECT * FROM sys_permission WHERE deleted = 0 ORDER BY parent_id ASC, sort ASC")
    List<SysPermission> selectAllForTree();

    /**
     * 查询子权限数量
     *
     * @param parentId 父级ID
     * @return 子权限数量
     */
    @Select("SELECT COUNT(*) FROM sys_permission WHERE parent_id = #{parentId} AND deleted = 0")
    int countChildren(@Param("parentId") Long parentId);
}

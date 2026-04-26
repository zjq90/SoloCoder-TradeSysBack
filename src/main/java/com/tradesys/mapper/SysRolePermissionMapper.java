package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.SysRolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Delete("DELETE FROM sys_role_permission WHERE permission_id = #{permissionId}")
    void deleteByPermissionId(@Param("permissionId") Long permissionId);

    @Insert("<script>" +
            "INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES " +
            "<foreach collection='permissionIds' item='permissionId' separator=','>" +
            "(#{roleId}, #{permissionId}, NOW())" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
}

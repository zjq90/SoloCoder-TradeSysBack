package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer status;

    @TableField(exist = false)
    private List<SysRole> roles;

    @TableField(exist = false)
    private List<SysPermission> permissions;
}

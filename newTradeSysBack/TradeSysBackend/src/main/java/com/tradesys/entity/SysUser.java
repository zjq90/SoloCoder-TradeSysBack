package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 系统用户实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String username;

    /**
     * 密码（加密）
     */
    @Size(max = 255, message = "密码长度不能超过255个字符")
    private String password;

    /**
     * 真实姓名
     */
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    /**
     * 手机号
     */
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 状态
     * 0: 禁用
     * 1: 启用
     */
    private Integer status;

    /**
     * 关联代理商ID（用于数据权限）
     */
    private Long agentId;

    /**
     * 代理商名称（非数据库字段）
     */
    @TableField(exist = false)
    private String agentName;

    /**
     * 角色列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysRole> roles;

    /**
     * 权限列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysPermission> permissions;

    /**
     * 关联代理商信息（非数据库字段）
     */
    @TableField(exist = false)
    private Agent agent;
}

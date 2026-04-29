package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 权限实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父级ID
     * 0表示顶级菜单
     */
    private Long parentId;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 100, message = "权限名称长度不能超过100个字符")
    private String permissionName;

    /**
     * 权限编码
     */
    @Size(max = 100, message = "权限编码长度不能超过100个字符")
    private String permissionCode;

    /**
     * 菜单类型
     * 1: 菜单
     * 2: 按钮
     */
    private Integer menuType;

    /**
     * 路由路径
     */
    @Size(max = 255, message = "路由路径长度不能超过255个字符")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 255, message = "组件路径长度不能超过255个字符")
    private String component;

    /**
     * 图标
     */
    @Size(max = 50, message = "图标长度不能超过50个字符")
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     * 0: 禁用
     * 1: 启用
     */
    private Integer status;

    /**
     * 子菜单列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysPermission> children;

    /**
     * 父级权限名称（非数据库字段）
     */
    @TableField(exist = false)
    private String parentName;
}

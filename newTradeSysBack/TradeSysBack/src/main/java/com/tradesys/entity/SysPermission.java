package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long parentId;
    private String permissionName;
    private String permissionCode;
    private Integer menuType;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer status;

    @TableField(exist = false)
    private List<SysPermission> children;
}

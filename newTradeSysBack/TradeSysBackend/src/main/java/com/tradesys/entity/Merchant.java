package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 商户实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant")
public class Merchant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商户编号
     */
    @Size(max = 50, message = "商户编号长度不能超过50个字符")
    private String merchantNo;

    /**
     * 商户名称
     */
    @NotBlank(message = "商户名称不能为空")
    @Size(max = 100, message = "商户名称长度不能超过100个字符")
    private String merchantName;

    /**
     * 所属代理商ID
     */
    @NotNull(message = "所属代理商不能为空")
    private Long parentId;

    /**
     * 层级路径
     * 如: /1/5/10/
     */
    @Size(max = 500, message = "层级路径长度不能超过500个字符")
    private String parentPath;

    /**
     * 联系人
     */
    @Size(max = 50, message = "联系人长度不能超过50个字符")
    private String contactName;

    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 经营地址
     */
    @Size(max = 255, message = "经营地址长度不能超过255个字符")
    private String address;

    /**
     * 营业执照号
     */
    @Size(max = 100, message = "营业执照号长度不能超过100个字符")
    private String businessLicense;

    /**
     * 法人身份证号
     */
    @Size(max = 30, message = "法人身份证号长度不能超过30个字符")
    private String idCard;

    /**
     * 开户银行
     */
    @Size(max = 100, message = "开户银行长度不能超过100个字符")
    private String bankName;

    /**
     * 银行账号
     */
    @Size(max = 50, message = "银行账号长度不能超过50个字符")
    private String bankAccount;

    /**
     * 状态
     * 0: 禁用
     * 1: 启用
     */
    private Integer status;

    /**
     * 所属代理商名称（非数据库字段）
     */
    @TableField(exist = false)
    private String agentName;

    /**
     * 绑定机器数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer machineCount;
}

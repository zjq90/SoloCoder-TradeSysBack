package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 代理商实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent")
public class Agent extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 代理商编号
     */
    @Size(max = 50, message = "代理商编号长度不能超过50个字符")
    private String agentNo;

    /**
     * 代理商名称
     */
    @NotBlank(message = "代理商名称不能为空")
    @Size(max = 100, message = "代理商名称长度不能超过100个字符")
    private String agentName;

    /**
     * 上级代理商ID
     * 0表示顶级代理商
     */
    private Long parentId;

    /**
     * 代理商级别
     * 1: 一级, 2: 二级, 3: 三级...
     */
    private Integer level;

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
     * 地址
     */
    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address;

    /**
     * 身份证号
     */
    @Size(max = 30, message = "身份证号长度不能超过30个字符")
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
     * 直属商户数量
     */
    private Integer directMerchantCount;

    /**
     * 总商户数量（含下级）
     */
    private Integer totalMerchantCount;

    /**
     * 下级代理商列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Agent> children;

    /**
     * 父级代理商名称（非数据库字段）
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 账户余额（非数据库字段）
     */
    @TableField(exist = false)
    private BigDecimal balance;

    /**
     * 冻结金额（非数据库字段）
     */
    @TableField(exist = false)
    private BigDecimal frozenAmount;
}

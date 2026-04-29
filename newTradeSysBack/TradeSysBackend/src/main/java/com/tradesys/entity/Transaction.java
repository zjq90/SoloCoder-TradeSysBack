package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易明细实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("transaction")
public class Transaction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 交易流水号
     */
    @Size(max = 50, message = "交易流水号长度不能超过50个字符")
    private String transNo;

    /**
     * 外部交易号
     */
    @Size(max = 100, message = "外部交易号长度不能超过100个字符")
    private String outTransNo;

    /**
     * 商户ID
     */
    @NotNull(message = "商户不能为空")
    private Long merchantId;

    /**
     * 所属代理商ID
     */
    @NotNull(message = "代理商不能为空")
    private Long agentId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 机器ID
     */
    private Long machineId;

    /**
     * 通道ID
     */
    private Long channelId;

    /**
     * 交易类型
     * 1: 消费
     * 2: 撤销
     * 3: 退款
     */
    private Integer transType;

    /**
     * 交易金额
     */
    @NotNull(message = "交易金额不能为空")
    private BigDecimal transAmount;

    /**
     * 手续费金额
     */
    private BigDecimal feeAmount;

    /**
     * 交易费率
     */
    private BigDecimal rate;

    /**
     * 分润金额
     */
    private BigDecimal profitAmount;

    /**
     * 交易状态
     * 0: 处理中
     * 1: 成功
     * 2: 失败
     * 3: 已撤销
     */
    private Integer status;

    /**
     * 交易时间
     */
    private LocalDateTime transTime;

    /**
     * 商户名称（非数据库字段）
     */
    @TableField(exist = false)
    private String merchantName;

    /**
     * 代理商名称（非数据库字段）
     */
    @TableField(exist = false)
    private String agentName;

    /**
     * 产品名称（非数据库字段）
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 通道名称（非数据库字段）
     */
    @TableField(exist = false)
    private String channelName;
}

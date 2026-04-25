package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("transaction")
public class Transaction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String transNo;
    private String outTransNo;
    private Long merchantId;
    private Long agentId;
    private Long productId;
    private Long machineId;
    private Long channelId;
    private Integer transType;
    private BigDecimal transAmount;
    private BigDecimal feeAmount;
    private BigDecimal rate;
    private BigDecimal profitAmount;
    private Integer status;
    private LocalDateTime transTime;

    @TableField(exist = false)
    private String merchantName;

    @TableField(exist = false)
    private String agentName;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String machineNo;

    @TableField(exist = false)
    private String channelName;
}

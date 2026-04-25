package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("profit_share")
public class ProfitShare extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String profitNo;
    private Long transactionId;
    private String transNo;
    private Long agentId;
    private Long productId;
    private BigDecimal transAmount;
    private BigDecimal profitRate;
    private BigDecimal profitAmount;
    private Integer level;
    private Long parentAgentId;
    private Integer status;
    private LocalDateTime settleTime;

    @TableField(exist = false)
    private String agentName;

    @TableField(exist = false)
    private String parentAgentName;

    @TableField(exist = false)
    private String productName;
}

package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("profit_share")
public class ProfitShare implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String agentName;

    @TableField(exist = false)
    private String parentAgentName;

    @TableField(exist = false)
    private String productName;
}

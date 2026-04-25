package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_product")
public class AgentProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long agentId;
    private Long productId;
    private BigDecimal profitRate;
    private Integer status;

    @TableField(exist = false)
    private String agentName;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private BigDecimal defaultRate;
}

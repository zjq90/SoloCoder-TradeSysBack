package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_account")
public class AgentAccount extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long agentId;
    private BigDecimal balance;
    private BigDecimal frozenAmount;
    private BigDecimal totalIncome;
    private BigDecimal totalWithdraw;
    private Integer status;

    @TableField(exist = false)
    private String agentName;

    @TableField(exist = false)
    private String agentNo;
}

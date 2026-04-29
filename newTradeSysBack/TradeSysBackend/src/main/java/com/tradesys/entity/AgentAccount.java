package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 代理账户实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_account")
public class AgentAccount extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 代理商ID
     */
    @NotNull(message = "代理商不能为空")
    private Long agentId;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;

    /**
     * 累计收益
     */
    private BigDecimal totalIncome;

    /**
     * 累计提现
     */
    private BigDecimal totalWithdraw;

    /**
     * 状态
     * 0: 冻结
     * 1: 正常
     */
    private Integer status;

    /**
     * 代理商名称（非数据库字段）
     */
    @TableField(exist = false)
    private String agentName;

    /**
     * 代理商编号（非数据库字段）
     */
    @TableField(exist = false)
    private String agentNo;
}

package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("withdraw")
public class Withdraw extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 提现单号
     */
    private String withdrawNo;

    /**
     * 代理商ID
     */
    @NotNull(message = "代理商不能为空")
    private Long agentId;

    /**
     * 提现金额
     */
    @NotNull(message = "提现金额不能为空")
    private BigDecimal amount;

    /**
     * 提现手续费
     */
    private BigDecimal fee;

    /**
     * 实际到账金额
     */
    private BigDecimal actualAmount;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 账户名
     */
    private String accountName;

    /**
     * 状态
     * 0: 申请中
     * 1: 处理中
     * 2: 已完成
     * 3: 已拒绝
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 处理时间
     */
    private LocalDateTime processTime;

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

package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 账户明细实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("account_detail")
public class AccountDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 明细单号
     */
    private String detailNo;

    /**
     * 代理商ID
     */
    @NotNull(message = "代理商不能为空")
    private Long agentId;

    /**
     * 账户类型
     * 1: 分润账户
     * 2: 提现账户
     */
    private Integer accountType;

    /**
     * 交易类型
     * 1: 分润收入
     * 2: 提现
     * 3: 提现退款
     * 4: 调账
     */
    private Integer transType;

    /**
     * 变动金额
     * 正为增加, 负为减少
     */
    @NotNull(message = "变动金额不能为空")
    private BigDecimal amount;

    /**
     * 变动前余额
     */
    private BigDecimal beforeBalance;

    /**
     * 变动后余额
     */
    private BigDecimal afterBalance;

    /**
     * 关联单号
     * 分润号/提现单号
     */
    private String relateNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 代理商名称（非数据库字段）
     */
    @TableField(exist = false)
    private String agentName;
}

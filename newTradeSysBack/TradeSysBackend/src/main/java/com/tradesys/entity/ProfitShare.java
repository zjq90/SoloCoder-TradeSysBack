package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 分润信息实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("profit_share")
public class ProfitShare extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分润流水号
     */
    private String profitNo;

    /**
     * 交易ID
     */
    @NotNull(message = "交易ID不能为空")
    private Long transactionId;

    /**
     * 交易流水号
     */
    private String transNo;

    /**
     * 获得分润的代理商ID
     */
    @NotNull(message = "代理商不能为空")
    private Long agentId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 交易金额
     */
    private BigDecimal transAmount;

    /**
     * 分润费率
     */
    private BigDecimal profitRate;

    /**
     * 分润金额
     */
    @NotNull(message = "分润金额不能为空")
    private BigDecimal profitAmount;

    /**
     * 分润层级
     * 1: 直接代理
     */
    private Integer level;

    /**
     * 上级代理商ID（用于层级展示）
     */
    private Long parentAgentId;

    /**
     * 状态
     * 0: 待结算
     * 1: 已结算
     */
    private Integer status;

    /**
     * 结算时间
     */
    private LocalDateTime settleTime;

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
}

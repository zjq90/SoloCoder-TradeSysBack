package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 对账单实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("statement")
public class Statement extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 对账单号
     */
    private String statementNo;

    /**
     * 代理商ID
     */
    @NotNull(message = "代理商不能为空")
    private Long agentId;

    /**
     * 对账日期
     */
    @NotNull(message = "对账日期不能为空")
    private LocalDate statDate;

    /**
     * 交易总额
     */
    private BigDecimal totalTransAmount;

    /**
     * 交易笔数
     */
    private Integer totalTransCount;

    /**
     * 手续费总额
     */
    private BigDecimal totalFeeAmount;

    /**
     * 分润总额
     */
    private BigDecimal totalProfitAmount;

    /**
     * 状态
     * 0: 待确认
     * 1: 已确认
     * 2: 有异议
     */
    private Integer status;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 备注
     */
    private String remark;

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

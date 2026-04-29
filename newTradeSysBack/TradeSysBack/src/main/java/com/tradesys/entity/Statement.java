package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("statement")
public class Statement extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String statementNo;
    private Long agentId;
    private LocalDate statDate;
    private BigDecimal totalTransAmount;
    private Integer totalTransCount;
    private BigDecimal totalFeeAmount;
    private BigDecimal totalProfitAmount;
    private Integer status;
    private LocalDateTime confirmTime;
    private String remark;

    @TableField(exist = false)
    private String agentName;

    @TableField(exist = false)
    private String agentNo;
}

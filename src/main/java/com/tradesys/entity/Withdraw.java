package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("withdraw")
public class Withdraw extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String withdrawNo;
    private Long agentId;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal actualAmount;
    private String bankName;
    private String bankAccount;
    private String accountName;
    private Integer status;
    private String rejectReason;
    private LocalDateTime applyTime;
    private LocalDateTime processTime;

    @TableField(exist = false)
    private String agentName;

    @TableField(exist = false)
    private String agentNo;
}

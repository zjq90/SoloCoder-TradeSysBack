package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("account_detail")
public class AccountDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String detailNo;
    private Long agentId;
    private Integer accountType;
    private Integer transType;
    private BigDecimal amount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private String relateNo;
    private String remark;

    @TableField(exist = false)
    private String agentName;

    @TableField(exist = false)
    private String agentNo;
}

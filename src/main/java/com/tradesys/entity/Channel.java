package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("channel")
public class Channel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String channelNo;
    private String channelName;
    private Integer channelType;
    private String provider;
    private BigDecimal rate;
    private BigDecimal dailyLimit;
    private BigDecimal singleLimit;
    private BigDecimal merchantLimit;
    private Integer status;

    @TableField(exist = false)
    private BigDecimal todayAmount;

    @TableField(exist = false)
    private Integer todayCount;

    @TableField(exist = false)
    private BigDecimal remainingAmount;
}

package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("channel_daily_stat")
public class ChannelDailyStat extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long channelId;
    private LocalDate statDate;
    private BigDecimal totalAmount;
    private Integer totalCount;

    @TableField(exist = false)
    private String channelName;

    @TableField(exist = false)
    private String channelNo;
}

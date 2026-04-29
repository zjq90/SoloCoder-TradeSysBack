package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 通道日交易统计实体类
 * 用于日限额控制
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("channel_daily_stat")
public class ChannelDailyStat extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 通道ID
     */
    @NotNull(message = "通道不能为空")
    private Long channelId;

    /**
     * 统计日期
     */
    @NotNull(message = "统计日期不能为空")
    private LocalDate statDate;

    /**
     * 当日交易总额
     */
    private BigDecimal totalAmount;

    /**
     * 当日交易笔数
     */
    private Integer totalCount;

    /**
     * 通道名称（非数据库字段）
     */
    @TableField(exist = false)
    private String channelName;
}

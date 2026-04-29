package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 通道实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("channel")
public class Channel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 通道编号
     */
    @Size(max = 50, message = "通道编号长度不能超过50个字符")
    private String channelNo;

    /**
     * 通道名称
     */
    @NotBlank(message = "通道名称不能为空")
    @Size(max = 100, message = "通道名称长度不能超过100个字符")
    private String channelName;

    /**
     * 交易阈值百分比
     */
    @Size(max = 4, message = "交易阈值百分比长度不能超过4个字符")
    private String thresholdPercent;

    /**
     * 通道类型
     * 1: 微信
     * 2: 支付宝
     * 3: 银联
     * 4: 其他
     */
    private Integer channelType;

    /**
     * 服务商
     */
    @Size(max = 100, message = "服务商长度不能超过100个字符")
    private String provider;

    /**
     * 通道费率
     */
    private BigDecimal rate;

    /**
     * 日交易额上限（0表示不限制）
     */
    private BigDecimal dailyLimit;

    /**
     * 单笔限额（0表示不限制）
     */
    private BigDecimal singleLimit;

    /**
     * 商户单笔限额
     */
    private BigDecimal merchantLimit;

    /**
     * 状态
     * 0: 关闭
     * 1: 开启
     */
    private Integer status;
}

package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("machine")
public class Machine extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String machineNo;
    private Long productId;
    private String sn;
    private String macAddress;
    private Long merchantId;
    private Long agentId;
    private Integer status;
    private LocalDate purchaseDate;
    private LocalDateTime activateTime;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String merchantName;

    @TableField(exist = false)
    private String agentName;
}

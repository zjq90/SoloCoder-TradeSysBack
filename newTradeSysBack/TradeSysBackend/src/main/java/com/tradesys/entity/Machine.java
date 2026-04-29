package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 机器实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("machine")
public class Machine extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 机器编号
     */
    @NotBlank(message = "机器编号不能为空")
    @Size(max = 50, message = "机器编号长度不能超过50个字符")
    private String machineNo;

    /**
     * 产品ID
     */
    @NotNull(message = "产品不能为空")
    private Long productId;

    /**
     * SN码
     */
    @Size(max = 100, message = "SN码长度不能超过100个字符")
    private String sn;

    /**
     * MAC地址
     */
    @Size(max = 50, message = "MAC地址长度不能超过50个字符")
    private String macAddress;

    /**
     * 绑定商户ID（空表示未绑定）
     */
    private Long merchantId;

    /**
     * 所属代理商ID
     */
    private Long agentId;

    /**
     * 状态
     * 0: 库存
     * 1: 已出库
     * 2: 已绑定
     * 3: 故障
     */
    private Integer status;

    /**
     * 采购日期
     */
    private LocalDate purchaseDate;

    /**
     * 激活时间
     */
    private LocalDateTime activateTime;

    /**
     * 产品名称（非数据库字段）
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 产品类型（非数据库字段）
     */
    @TableField(exist = false)
    private Integer productType;

    /**
     * 商户名称（非数据库字段）
     */
    @TableField(exist = false)
    private String merchantName;

    /**
     * 代理商名称（非数据库字段）
     */
    @TableField(exist = false)
    private String agentName;
}

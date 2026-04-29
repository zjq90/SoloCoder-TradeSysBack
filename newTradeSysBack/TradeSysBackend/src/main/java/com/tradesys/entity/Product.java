package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 产品实体类
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 产品编号
     */
    @Size(max = 50, message = "产品编号长度不能超过50个字符")
    private String productNo;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 100, message = "产品名称长度不能超过100个字符")
    private String productName;

    /**
     * 产品类型
     * 1: POS机
     * 2: 聚合支付
     * 3: 其他
     */
    private Integer productType;

    /**
     * 产品描述
     */
    @Size(max = 500, message = "产品描述长度不能超过500个字符")
    private String description;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 成本价
     */
    private BigDecimal costPrice;

    /**
     * 默认费率
     */
    private BigDecimal defaultRate;

    /**
     * 状态
     * 0: 禁用
     * 1: 启用
     */
    private Integer status;
}

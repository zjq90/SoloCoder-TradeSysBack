package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String productNo;
    private String productName;
    private Integer productType;
    private String description;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    private BigDecimal defaultRate;
    private Integer status;
}

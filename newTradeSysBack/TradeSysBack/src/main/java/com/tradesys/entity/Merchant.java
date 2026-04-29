package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant")
public class Merchant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String merchantNo;
    private String merchantName;
    private Long parentId;
    private String parentPath;
    private String contactName;
    private String phone;
    private String email;
    private String address;
    private String businessLicense;
    private String idCard;
    private String bankName;
    private String bankAccount;
    private Integer status;

    @TableField(exist = false)
    private String parentName;

    @TableField(exist = false)
    private String agentName;
}

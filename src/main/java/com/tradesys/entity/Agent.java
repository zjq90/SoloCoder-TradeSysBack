package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent")
public class Agent extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String agentNo;
    private String agentName;
    private Long parentId;
    private Integer level;
    private String contactName;
    private String phone;
    private String email;
    private String address;
    private String idCard;
    private String bankName;
    private String bankAccount;
    private Integer status;
    private Integer directMerchantCount;
    private Integer totalMerchantCount;

    @TableField(exist = false)
    private String parentName;

    @TableField(exist = false)
    private List<Agent> children;

    @TableField(exist = false)
    private List<AgentProduct> agentProducts;

    @TableField(exist = false)
    private AgentAccount agentAccount;
}

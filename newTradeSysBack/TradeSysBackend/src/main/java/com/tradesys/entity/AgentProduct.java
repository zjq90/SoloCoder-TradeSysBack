package com.tradesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 代理产品关联实体类
 * 代理商可代理不同产品
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_product")
public class AgentProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 代理商ID
     */
    @NotNull(message = "代理商不能为空")
    private Long agentId;

    /**
     * 产品ID
     */
    @NotNull(message = "产品不能为空")
    private Long productId;

    /**
     * 分润费率
     * 如 0.0012 表示万12
     */
    private BigDecimal profitRate;

    /**
     * 状态
     * 0: 禁用
     * 1: 启用
     */
    private Integer status;

    /**
     * 代理商名称（非数据库字段）
     */
    @TableField(exist = false)
    private String agentName;

    /**
     * 产品名称（非数据库字段）
     */
    @TableField(exist = false)
    private String productName;
}

package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.ProfitShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 分润信息Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface ProfitShareMapper extends BaseMapper<ProfitShare> {

    /**
     * 根据代理商ID查询分润列表
     *
     * @param agentId 代理商ID
     * @return 分润列表
     */
    @Select("SELECT ps.*, a.agent_name AS agentName, p.product_name AS productName " +
            "FROM profit_share ps " +
            "LEFT JOIN agent a ON ps.agent_id = a.id AND a.deleted = 0 " +
            "LEFT JOIN product p ON ps.product_id = p.id AND p.deleted = 0 " +
            "WHERE ps.agent_id = #{agentId} AND ps.deleted = 0 " +
            "ORDER BY ps.create_time DESC")
    List<ProfitShare> selectByAgentId(@Param("agentId") Long agentId);

    /**
     * 统计指定代理商的分润总额
     *
     * @param agentId 代理商ID
     * @return 分润总额
     */
    @Select("SELECT COALESCE(SUM(profit_amount), 0) FROM profit_share " +
            "WHERE agent_id = #{agentId} AND status = 1 AND deleted = 0")
    BigDecimal sumProfitByAgentId(@Param("agentId") Long agentId);

    /**
     * 统计指定代理商在指定日期的分润总额
     *
     * @param agentId 代理商ID
     * @param date    日期
     * @return 分润总额
     */
    @Select("SELECT COALESCE(SUM(profit_amount), 0) FROM profit_share " +
            "WHERE agent_id = #{agentId} AND DATE(create_time) = #{date} AND status = 1 AND deleted = 0")
    BigDecimal sumProfitByAgentAndDate(@Param("agentId") Long agentId, @Param("date") LocalDate date);

    /**
     * 根据交易ID查询分润列表
     *
     * @param transactionId 交易ID
     * @return 分润列表
     */
    @Select("SELECT ps.*, a.agent_name AS agentName, p.product_name AS productName " +
            "FROM profit_share ps " +
            "LEFT JOIN agent a ON ps.agent_id = a.id AND a.deleted = 0 " +
            "LEFT JOIN product p ON ps.product_id = p.id AND p.deleted = 0 " +
            "WHERE ps.transaction_id = #{transactionId} AND ps.deleted = 0 " +
            "ORDER BY ps.level ASC")
    List<ProfitShare> selectByTransactionId(@Param("transactionId") Long transactionId);

    /**
     * 统计指定日期的总分润金额
     *
     * @param date 日期
     * @return 分润总额
     */
    @Select("SELECT COALESCE(SUM(profit_amount), 0) FROM profit_share " +
            "WHERE DATE(create_time) = #{date} AND status = 1 AND deleted = 0")
    BigDecimal sumProfitByDate(@Param("date") LocalDate date);
}

package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易明细Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {

    /**
     * 根据代理商ID查询交易列表（包含关联信息）
     *
     * @param agentId 代理商ID
     * @return 交易列表
     */
    @Select("SELECT t.*, m.merchant_name AS merchantName, a.agent_name AS agentName, " +
            "p.product_name AS productName, c.channel_name AS channelName " +
            "FROM transaction t " +
            "LEFT JOIN merchant m ON t.merchant_id = m.id AND m.deleted = 0 " +
            "LEFT JOIN agent a ON t.agent_id = a.id AND a.deleted = 0 " +
            "LEFT JOIN product p ON t.product_id = p.id AND p.deleted = 0 " +
            "LEFT JOIN channel c ON t.channel_id = c.id AND c.deleted = 0 " +
            "WHERE t.agent_id = #{agentId} AND t.deleted = 0 " +
            "ORDER BY t.trans_time DESC")
    List<Transaction> selectByAgentId(@Param("agentId") Long agentId);

    /**
     * 根据商户ID查询交易列表
     *
     * @param merchantId 商户ID
     * @return 交易列表
     */
    @Select("SELECT t.*, m.merchant_name AS merchantName, a.agent_name AS agentName, " +
            "p.product_name AS productName, c.channel_name AS channelName " +
            "FROM transaction t " +
            "LEFT JOIN merchant m ON t.merchant_id = m.id AND m.deleted = 0 " +
            "LEFT JOIN agent a ON t.agent_id = a.id AND a.deleted = 0 " +
            "LEFT JOIN product p ON t.product_id = p.id AND p.deleted = 0 " +
            "LEFT JOIN channel c ON t.channel_id = c.id AND c.deleted = 0 " +
            "WHERE t.merchant_id = #{merchantId} AND t.deleted = 0 " +
            "ORDER BY t.trans_time DESC")
    List<Transaction> selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 统计指定日期的交易总额
     *
     * @param date 日期
     * @return 交易总额
     */
    @Select("SELECT COALESCE(SUM(trans_amount), 0) FROM transaction " +
            "WHERE DATE(trans_time) = #{date} AND status = 1 AND deleted = 0")
    BigDecimal sumAmountByDate(@Param("date") LocalDate date);

    /**
     * 统计指定日期的交易笔数
     *
     * @param date 日期
     * @return 交易笔数
     */
    @Select("SELECT COUNT(*) FROM transaction " +
            "WHERE DATE(trans_time) = #{date} AND status = 1 AND deleted = 0")
    long countByDate(@Param("date") LocalDate date);

    /**
     * 统计指定代理商在指定日期的交易总额
     *
     * @param agentId 代理商ID
     * @param date    日期
     * @return 交易总额
     */
    @Select("SELECT COALESCE(SUM(trans_amount), 0) FROM transaction " +
            "WHERE agent_id = #{agentId} AND DATE(trans_time) = #{date} AND status = 1 AND deleted = 0")
    BigDecimal sumAmountByAgentAndDate(@Param("agentId") Long agentId, @Param("date") LocalDate date);

    /**
     * 统计指定代理商在指定日期的分润总额
     *
     * @param agentId 代理商ID
     * @param date    日期
     * @return 分润总额
     */
    @Select("SELECT COALESCE(SUM(profit_amount), 0) FROM transaction " +
            "WHERE agent_id = #{agentId} AND DATE(trans_time) = #{date} AND status = 1 AND deleted = 0")
    BigDecimal sumProfitByAgentAndDate(@Param("agentId") Long agentId, @Param("date") LocalDate date);

    /**
     * 统计指定日期范围的交易总额
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 交易总额
     */
    @Select("SELECT COALESCE(SUM(trans_amount), 0) FROM transaction " +
            "WHERE trans_time >= #{startTime} AND trans_time < #{endTime} AND status = 1 AND deleted = 0")
    BigDecimal sumAmountByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定日期范围的交易笔数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 交易笔数
     */
    @Select("SELECT COUNT(*) FROM transaction " +
            "WHERE trans_time >= #{startTime} AND trans_time < #{endTime} AND status = 1 AND deleted = 0")
    long countByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}

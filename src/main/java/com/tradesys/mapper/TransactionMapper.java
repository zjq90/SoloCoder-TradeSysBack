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
import java.util.Map;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {

    @Select("SELECT COUNT(*) FROM transaction WHERE trans_time >= #{startTime} AND trans_time < #{endTime}")
    long countByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(trans_amount), 0) FROM transaction WHERE trans_time >= #{startTime} AND trans_time < #{endTime}")
    BigDecimal sumAmountByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COUNT(*) FROM transaction WHERE merchant_id = #{merchantId} AND trans_time >= #{startTime} AND trans_time < #{endTime}")
    long countByMerchantAndTimeRange(@Param("merchantId") Long merchantId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(trans_amount), 0) FROM transaction WHERE agent_id = #{agentId} AND trans_time >= #{startTime} AND trans_time < #{endTime}")
    BigDecimal sumAmountByAgentAndTimeRange(@Param("agentId") Long agentId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT DATE(trans_time) AS trans_date, COUNT(*) AS trans_count, COALESCE(SUM(trans_amount), 0) AS trans_amount " +
            "FROM transaction " +
            "WHERE trans_time >= #{startTime} AND trans_time < #{endTime} " +
            "GROUP BY DATE(trans_time) " +
            "ORDER BY trans_date")
    List<Map<String, Object>> selectDailyStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT t.*, m.merchant_name, a.agent_name, p.product_name, mac.machine_no, c.channel_name " +
            "FROM transaction t " +
            "LEFT JOIN merchant m ON t.merchant_id = m.id " +
            "LEFT JOIN agent a ON t.agent_id = a.id " +
            "LEFT JOIN product p ON t.product_id = p.id " +
            "LEFT JOIN machine mac ON t.machine_id = mac.id " +
            "LEFT JOIN channel c ON t.channel_id = c.id " +
            "WHERE t.id = #{id}")
    Transaction selectDetailById(@Param("id") Long id);
}

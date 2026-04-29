package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.ProfitShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface ProfitShareMapper extends BaseMapper<ProfitShare> {

    @Select("SELECT COALESCE(SUM(profit_amount), 0) FROM profit_share WHERE agent_id = #{agentId} AND create_time >= #{startTime} AND create_time < #{endTime}")
    BigDecimal sumByAgentAndTimeRange(@Param("agentId") Long agentId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(profit_amount), 0) FROM profit_share WHERE create_time >= #{startTime} AND create_time < #{endTime}")
    BigDecimal sumByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}

package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Statement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface StatementMapper extends BaseMapper<Statement> {

    @Select("SELECT s.*, a.agent_name, a.agent_no " +
            "FROM statement s " +
            "LEFT JOIN agent a ON s.agent_id = a.id " +
            "WHERE s.agent_id = #{agentId} AND s.stat_date = #{statDate}")
    Statement selectByAgentAndDate(@Param("agentId") Long agentId, @Param("statDate") LocalDate statDate);
}

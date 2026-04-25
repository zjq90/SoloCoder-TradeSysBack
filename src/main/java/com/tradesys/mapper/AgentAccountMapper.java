package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.AgentAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface AgentAccountMapper extends BaseMapper<AgentAccount> {

    @Select("SELECT aa.*, a.agent_name, a.agent_no " +
            "FROM agent_account aa " +
            "LEFT JOIN agent a ON aa.agent_id = a.id " +
            "WHERE aa.agent_id = #{agentId}")
    AgentAccount selectDetailByAgentId(@Param("agentId") Long agentId);

    @Update("UPDATE agent_account SET balance = balance + #{amount}, total_income = total_income + #{amount} WHERE agent_id = #{agentId}")
    void addBalance(@Param("agentId") Long agentId, @Param("amount") BigDecimal amount);

    @Update("UPDATE agent_account SET balance = balance - #{amount}, frozen_amount = frozen_amount + #{amount} WHERE agent_id = #{agentId} AND balance >= #{amount}")
    int freezeBalance(@Param("agentId") Long agentId, @Param("amount") BigDecimal amount);

    @Update("UPDATE agent_account SET frozen_amount = frozen_amount - #{amount}, total_withdraw = total_withdraw + #{amount} WHERE agent_id = #{agentId} AND frozen_amount >= #{amount}")
    int confirmWithdraw(@Param("agentId") Long agentId, @Param("amount") BigDecimal amount);

    @Update("UPDATE agent_account SET frozen_amount = frozen_amount - #{amount}, balance = balance + #{amount} WHERE agent_id = #{agentId} AND frozen_amount >= #{amount}")
    int unfreezeBalance(@Param("agentId") Long agentId, @Param("amount") BigDecimal amount);
}

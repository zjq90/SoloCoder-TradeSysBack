package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.AgentAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 代理账户Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface AgentAccountMapper extends BaseMapper<AgentAccount> {

    /**
     * 根据代理商ID查询账户（包含代理商信息）
     *
     * @param agentId 代理商ID
     * @return 账户信息
     */
    @Select("SELECT aa.*, a.agent_name AS agentName, a.agent_no AS agentNo " +
            "FROM agent_account aa " +
            "LEFT JOIN agent a ON aa.agent_id = a.id AND a.deleted = 0 " +
            "WHERE aa.agent_id = #{agentId} AND aa.deleted = 0")
    AgentAccount selectByAgentIdWithInfo(@Param("agentId") Long agentId);

    /**
     * 查询所有账户（包含代理商信息）
     *
     * @return 账户列表
     */
    @Select("SELECT aa.*, a.agent_name AS agentName, a.agent_no AS agentNo " +
            "FROM agent_account aa " +
            "LEFT JOIN agent a ON aa.agent_id = a.id AND a.deleted = 0 " +
            "WHERE aa.deleted = 0 ORDER BY aa.create_time DESC")
    List<AgentAccount> selectAllWithAgentInfo();

    /**
     * 增加账户余额
     *
     * @param agentId 代理商ID
     * @param amount  增加金额
     * @return 影响行数
     */
    @Update("UPDATE agent_account SET balance = balance + #{amount} WHERE agent_id = #{agentId}")
    int addBalance(@Param("agentId") Long agentId, @Param("amount") BigDecimal amount);

    /**
     * 减少账户余额（防止余额为负）
     *
     * @param agentId 代理商ID
     * @param amount  减少金额
     * @return 影响行数
     */
    @Update("UPDATE agent_account SET balance = balance - #{amount} " +
            "WHERE agent_id = #{agentId} AND balance >= #{amount}")
    int subtractBalance(@Param("agentId") Long agentId, @Param("amount") BigDecimal amount);

    /**
     * 冻结金额
     *
     * @param agentId 代理商ID
     * @param amount  冻结金额
     * @return 影响行数
     */
    @Update("UPDATE agent_account SET frozen_amount = frozen_amount + #{amount} " +
            "WHERE agent_id = #{agentId}")
    int freezeAmount(@Param("agentId") Long agentId, @Param("amount") BigDecimal amount);

    /**
     * 解冻金额
     *
     * @param agentId 代理商ID
     * @param amount  解冻金额
     * @return 影响行数
     */
    @Update("UPDATE agent_account SET frozen_amount = frozen_amount - #{amount} " +
            "WHERE agent_id = #{agentId} AND frozen_amount >= #{amount}")
    int unfreezeAmount(@Param("agentId") Long agentId, @Param("amount") BigDecimal amount);

    /**
     * 增加累计收益
     *
     * @param agentId 代理商ID
     * @param amount  增加金额
     * @return 影响行数
     */
    @Update("UPDATE agent_account SET total_income = total_income + #{amount} " +
            "WHERE agent_id = #{agentId}")
    int addTotalIncome(@Param("agentId") Long agentId, @Param("amount") BigDecimal amount);

    /**
     * 增加累计提现
     *
     * @param agentId 代理商ID
     * @param amount  增加金额
     * @return 影响行数
     */
    @Update("UPDATE agent_account SET total_withdraw = total_withdraw + #{amount} " +
            "WHERE agent_id = #{agentId}")
    int addTotalWithdraw(@Param("agentId") Long agentId, @Param("amount") BigDecimal amount);
}

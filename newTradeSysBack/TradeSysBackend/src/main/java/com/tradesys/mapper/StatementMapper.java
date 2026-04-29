package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Statement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 对账单Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface StatementMapper extends BaseMapper<Statement> {

    /**
     * 根据代理商ID查询对账单列表
     *
     * @param agentId 代理商ID
     * @return 对账单列表
     */
    @Select("SELECT s.*, a.agent_name AS agentName, a.agent_no AS agentNo " +
            "FROM statement s " +
            "LEFT JOIN agent a ON s.agent_id = a.id AND a.deleted = 0 " +
            "WHERE s.agent_id = #{agentId} AND s.deleted = 0 " +
            "ORDER BY s.stat_date DESC")
    List<Statement> selectByAgentId(@Param("agentId") Long agentId);

    /**
     * 根据代理商ID和日期查询对账单
     *
     * @param agentId  代理商ID
     * @param statDate 对账日期
     * @return 对账单信息
     */
    @Select("SELECT s.*, a.agent_name AS agentName, a.agent_no AS agentNo " +
            "FROM statement s " +
            "LEFT JOIN agent a ON s.agent_id = a.id AND a.deleted = 0 " +
            "WHERE s.agent_id = #{agentId} AND s.stat_date = #{statDate} AND s.deleted = 0")
    Statement selectByAgentIdAndDate(@Param("agentId") Long agentId, @Param("statDate") LocalDate statDate);

    /**
     * 查询所有对账单（包含代理商信息）
     *
     * @return 对账单列表
     */
    @Select("SELECT s.*, a.agent_name AS agentName, a.agent_no AS agentNo " +
            "FROM statement s " +
            "LEFT JOIN agent a ON s.agent_id = a.id AND a.deleted = 0 " +
            "WHERE s.deleted = 0 " +
            "ORDER BY s.stat_date DESC")
    List<Statement> selectAllWithAgentInfo();
}

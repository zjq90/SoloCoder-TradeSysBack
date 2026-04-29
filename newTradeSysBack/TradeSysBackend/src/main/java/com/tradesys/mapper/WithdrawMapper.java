package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Withdraw;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 提现记录Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface WithdrawMapper extends BaseMapper<Withdraw> {

    /**
     * 根据代理商ID查询提现记录列表
     *
     * @param agentId 代理商ID
     * @return 提现记录列表
     */
    @Select("SELECT w.*, a.agent_name AS agentName, a.agent_no AS agentNo " +
            "FROM withdraw w " +
            "LEFT JOIN agent a ON w.agent_id = a.id AND a.deleted = 0 " +
            "WHERE w.agent_id = #{agentId} AND w.deleted = 0 " +
            "ORDER BY w.apply_time DESC")
    List<Withdraw> selectByAgentId(@Param("agentId") Long agentId);

    /**
     * 查询所有提现记录（包含代理商信息）
     *
     * @return 提现记录列表
     */
    @Select("SELECT w.*, a.agent_name AS agentName, a.agent_no AS agentNo " +
            "FROM withdraw w " +
            "LEFT JOIN agent a ON w.agent_id = a.id AND a.deleted = 0 " +
            "WHERE w.deleted = 0 " +
            "ORDER BY w.apply_time DESC")
    List<Withdraw> selectAllWithAgentInfo();

    /**
     * 统计指定状态的提现数量
     *
     * @param status 状态
     * @return 提现数量
     */
    @Select("SELECT COUNT(*) FROM withdraw WHERE status = #{status} AND deleted = 0")
    long countByStatus(@Param("status") Integer status);

    /**
     * 统计指定代理商的提现数量
     *
     * @param agentId 代理商ID
     * @param status  状态（可选）
     * @return 提现数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM withdraw WHERE agent_id = #{agentId} AND deleted = 0 " +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</script>")
    long countByAgentId(@Param("agentId") Long agentId, @Param("status") Integer status);
}

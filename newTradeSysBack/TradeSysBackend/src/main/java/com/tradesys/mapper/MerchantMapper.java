package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商户Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

    /**
     * 根据代理商ID查询商户列表
     *
     * @param agentId 代理商ID
     * @return 商户列表
     */
    @Select("SELECT m.*, a.agent_name AS agentName FROM merchant m " +
            "LEFT JOIN agent a ON m.parent_id = a.id AND a.deleted = 0 " +
            "WHERE m.parent_id = #{agentId} AND m.status = 1 AND m.deleted = 0 " +
            "ORDER BY m.create_time DESC")
    List<Merchant> selectByAgentId(@Param("agentId") Long agentId);

    /**
     * 统计指定代理商的商户数量
     *
     * @param agentId 代理商ID
     * @return 商户数量
     */
    @Select("SELECT COUNT(*) FROM merchant WHERE parent_id = #{agentId} AND status = 1 AND deleted = 0")
    int countByAgentId(@Param("agentId") Long agentId);

    /**
     * 查询指定代理商及其所有下级代理商的商户列表
     *
     * @param agentId 代理商ID
     * @return 商户列表
     */
    @Select("WITH RECURSIVE agent_tree AS (" +
            "  SELECT id FROM agent WHERE id = #{agentId} AND deleted = 0 " +
            "  UNION ALL " +
            "  SELECT a.id FROM agent a " +
            "  INNER JOIN agent_tree t ON a.parent_id = t.id AND a.deleted = 0 " +
            ") " +
            "SELECT m.*, a.agent_name AS agentName FROM merchant m " +
            "LEFT JOIN agent a ON m.parent_id = a.id AND a.deleted = 0 " +
            "INNER JOIN agent_tree t ON m.parent_id = t.id " +
            "WHERE m.status = 1 AND m.deleted = 0 " +
            "ORDER BY m.create_time DESC")
    List<Merchant> selectAllByAgentPath(@Param("agentId") Long agentId);

    /**
     * 统计指定代理商及其所有下级代理商的商户数量
     *
     * @param agentId 代理商ID
     * @return 商户数量
     */
    @Select("WITH RECURSIVE agent_tree AS (" +
            "  SELECT id FROM agent WHERE id = #{agentId} AND deleted = 0 " +
            "  UNION ALL " +
            "  SELECT a.id FROM agent a " +
            "  INNER JOIN agent_tree t ON a.parent_id = t.id AND a.deleted = 0 " +
            ") " +
            "SELECT COUNT(*) FROM merchant m " +
            "INNER JOIN agent_tree t ON m.parent_id = t.id " +
            "WHERE m.status = 1 AND m.deleted = 0")
    int countAllByAgentPath(@Param("agentId") Long agentId);
}

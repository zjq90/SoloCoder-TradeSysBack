package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Agent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 代理商Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface AgentMapper extends BaseMapper<Agent> {

    /**
     * 根据父级ID查询子代理商列表
     *
     * @param parentId 父级代理商ID
     * @return 子代理商列表
     */
    @Select("SELECT * FROM agent WHERE parent_id = #{parentId} AND status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<Agent> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询指定代理商的所有下级代理商（递归查找）
     *
     * @param agentId 代理商ID
     * @return 下级代理商列表
     */
    @Select("WITH RECURSIVE agent_tree AS (" +
            "  SELECT * FROM agent WHERE id = #{agentId} AND deleted = 0 " +
            "  UNION ALL " +
            "  SELECT a.* FROM agent a " +
            "  INNER JOIN agent_tree t ON a.parent_id = t.id AND a.deleted = 0 " +
            ") " +
            "SELECT * FROM agent_tree WHERE id != #{agentId} AND status = 1 ORDER BY level ASC, id ASC")
    List<Agent> selectAllChildren(@Param("agentId") Long agentId);

    /**
     * 统计指定代理商的直属商户数量
     *
     * @param agentId 代理商ID
     * @return 直属商户数量
     */
    @Select("SELECT COUNT(*) FROM merchant WHERE parent_id = #{agentId} AND status = 1 AND deleted = 0")
    int countDirectMerchants(@Param("agentId") Long agentId);

    /**
     * 统计指定代理商的总商户数量（包括下级代理商的商户）
     * 使用递归CTE优化查询性能
     *
     * @param agentId 代理商ID
     * @return 总商户数量
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
    int countTotalMerchants(@Param("agentId") Long agentId);

    /**
     * 增加直属商户数量
     *
     * @param agentId 代理商ID
     */
    @Update("UPDATE agent SET direct_merchant_count = direct_merchant_count + 1 WHERE id = #{agentId}")
    void incrementDirectMerchantCount(@Param("agentId") Long agentId);

    /**
     * 减少直属商户数量（防止负数）
     *
     * @param agentId 代理商ID
     */
    @Update("UPDATE agent SET direct_merchant_count = direct_merchant_count - 1 WHERE id = #{agentId} AND direct_merchant_count > 0")
    void decrementDirectMerchantCount(@Param("agentId") Long agentId);

    /**
     * 查询代理商列表（包含父级名称）
     *
     * @return 代理商列表
     */
    @Select("SELECT a.*, pa.agent_name AS parentName FROM agent a " +
            "LEFT JOIN agent pa ON a.parent_id = pa.id AND pa.deleted = 0 " +
            "WHERE a.deleted = 0 ORDER BY a.create_time DESC")
    List<Agent> selectAllWithParentName();
}

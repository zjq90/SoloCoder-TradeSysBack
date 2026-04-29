package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.AgentProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 代理产品关联Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface AgentProductMapper extends BaseMapper<AgentProduct> {

    /**
     * 根据代理商ID查询代理产品列表
     *
     * @param agentId 代理商ID
     * @return 代理产品列表
     */
    @Select("SELECT ap.*, a.agent_name AS agentName, p.product_name AS productName " +
            "FROM agent_product ap " +
            "LEFT JOIN agent a ON ap.agent_id = a.id AND a.deleted = 0 " +
            "LEFT JOIN product p ON ap.product_id = p.id AND p.deleted = 0 " +
            "WHERE ap.agent_id = #{agentId} AND ap.status = 1 AND ap.deleted = 0 " +
            "ORDER BY ap.create_time DESC")
    List<AgentProduct> selectByAgentId(@Param("agentId") Long agentId);

    /**
     * 根据产品ID查询代理商列表
     *
     * @param productId 产品ID
     * @return 代理产品列表
     */
    @Select("SELECT ap.*, a.agent_name AS agentName, p.product_name AS productName " +
            "FROM agent_product ap " +
            "LEFT JOIN agent a ON ap.agent_id = a.id AND a.deleted = 0 " +
            "LEFT JOIN product p ON ap.product_id = p.id AND p.deleted = 0 " +
            "WHERE ap.product_id = #{productId} AND ap.status = 1 AND ap.deleted = 0 " +
            "ORDER BY ap.create_time DESC")
    List<AgentProduct> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据代理商ID和产品ID查询代理产品
     *
     * @param agentId   代理商ID
     * @param productId 产品ID
     * @return 代理产品信息
     */
    @Select("SELECT ap.*, a.agent_name AS agentName, p.product_name AS productName " +
            "FROM agent_product ap " +
            "LEFT JOIN agent a ON ap.agent_id = a.id AND a.deleted = 0 " +
            "LEFT JOIN product p ON ap.product_id = p.id AND p.deleted = 0 " +
            "WHERE ap.agent_id = #{agentId} AND ap.product_id = #{productId} AND ap.deleted = 0")
    AgentProduct selectByAgentIdAndProductId(@Param("agentId") Long agentId, @Param("productId") Long productId);
}

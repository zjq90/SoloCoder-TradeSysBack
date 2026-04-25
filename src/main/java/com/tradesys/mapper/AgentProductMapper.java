package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.AgentProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AgentProductMapper extends BaseMapper<AgentProduct> {

    @Select("SELECT ap.*, p.product_name, p.default_rate " +
            "FROM agent_product ap " +
            "LEFT JOIN product p ON ap.product_id = p.id " +
            "WHERE ap.agent_id = #{agentId} AND ap.status = 1")
    List<AgentProduct> selectByAgentId(@Param("agentId") Long agentId);

    @Select("SELECT ap.*, a.agent_name, p.product_name " +
            "FROM agent_product ap " +
            "LEFT JOIN agent a ON ap.agent_id = a.id " +
            "LEFT JOIN product p ON ap.product_id = p.id " +
            "WHERE ap.agent_id = #{agentId} AND ap.product_id = #{productId}")
    AgentProduct selectByAgentIdAndProductId(@Param("agentId") Long agentId, @Param("productId") Long productId);

    @Select("SELECT * FROM agent_product WHERE product_id = #{productId} AND status = 1")
    List<AgentProduct> selectByProductId(@Param("productId") Long productId);
}

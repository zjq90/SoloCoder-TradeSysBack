package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Agent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AgentMapper extends BaseMapper<Agent> {

    @Select("SELECT * FROM agent WHERE parent_id = #{parentId} AND status = 1 ORDER BY create_time DESC")
    List<Agent> selectByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM agent WHERE parent_path LIKE CONCAT('%/', #{agentId}, '/%')")
    List<Agent> selectAllChildren(@Param("agentId") Long agentId);

    @Select("SELECT COUNT(*) FROM merchant WHERE parent_id = #{agentId} AND status = 1")
    int countDirectMerchants(@Param("agentId") Long agentId);

    @Update("UPDATE agent SET direct_merchant_count = direct_merchant_count + 1 WHERE id = #{agentId}")
    void incrementDirectMerchantCount(@Param("agentId") Long agentId);

    @Update("UPDATE agent SET direct_merchant_count = direct_merchant_count - 1 WHERE id = #{agentId} AND direct_merchant_count > 0")
    void decrementDirectMerchantCount(@Param("agentId") Long agentId);

    @Select("SELECT COUNT(*) FROM merchant WHERE parent_path LIKE CONCAT('%/', #{agentId}, '/%') OR parent_id = #{agentId}")
    int countTotalMerchants(@Param("agentId") Long agentId);
}

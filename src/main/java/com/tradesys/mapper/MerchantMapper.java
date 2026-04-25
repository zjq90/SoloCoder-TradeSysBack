package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

    @Select("SELECT m.*, a.agent_name AS parent_name FROM merchant m " +
            "LEFT JOIN agent a ON m.parent_id = a.id " +
            "WHERE m.parent_id = #{agentId} AND m.status = 1")
    List<Merchant> selectByAgentId(@Param("agentId") Long agentId);

    @Select("SELECT COUNT(*) FROM merchant WHERE parent_id = #{agentId} AND status = 1")
    int countByAgentId(@Param("agentId") Long agentId);

    @Select("SELECT * FROM merchant WHERE parent_path LIKE CONCAT('%/', #{agentId}, '/%') AND status = 1")
    List<Merchant> selectAllByAgentPath(@Param("agentId") Long agentId);
}

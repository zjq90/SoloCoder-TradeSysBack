package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.AccountDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 账户明细Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface AccountDetailMapper extends BaseMapper<AccountDetail> {

    /**
     * 根据代理商ID查询账户明细列表
     *
     * @param agentId 代理商ID
     * @return 账户明细列表
     */
    @Select("SELECT ad.*, a.agent_name AS agentName " +
            "FROM account_detail ad " +
            "LEFT JOIN agent a ON ad.agent_id = a.id AND a.deleted = 0 " +
            "WHERE ad.agent_id = #{agentId} AND ad.deleted = 0 " +
            "ORDER BY ad.create_time DESC")
    List<AccountDetail> selectByAgentId(@Param("agentId") Long agentId);

    /**
     * 根据交易类型查询账户明细
     *
     * @param agentId   代理商ID
     * @param transType 交易类型
     * @return 账户明细列表
     */
    @Select("SELECT ad.*, a.agent_name AS agentName " +
            "FROM account_detail ad " +
            "LEFT JOIN agent a ON ad.agent_id = a.id AND a.deleted = 0 " +
            "WHERE ad.agent_id = #{agentId} AND ad.trans_type = #{transType} AND ad.deleted = 0 " +
            "ORDER BY ad.create_time DESC")
    List<AccountDetail> selectByAgentIdAndTransType(@Param("agentId") Long agentId, @Param("transType") Integer transType);

    /**
     * 查询指定日期的账户明细
     *
     * @param agentId 代理商ID
     * @param date    日期
     * @return 账户明细列表
     */
    @Select("SELECT ad.*, a.agent_name AS agentName " +
            "FROM account_detail ad " +
            "LEFT JOIN agent a ON ad.agent_id = a.id AND a.deleted = 0 " +
            "WHERE ad.agent_id = #{agentId} AND DATE(ad.create_time) = #{date} AND ad.deleted = 0 " +
            "ORDER BY ad.create_time DESC")
    List<AccountDetail> selectByAgentIdAndDate(@Param("agentId") Long agentId, @Param("date") LocalDate date);
}

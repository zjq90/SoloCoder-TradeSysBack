package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Machine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 机器Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface MachineMapper extends BaseMapper<Machine> {

    /**
     * 根据代理商ID查询机器列表
     *
     * @param agentId 代理商ID
     * @return 机器列表
     */
    @Select("SELECT m.*, p.product_name AS productName, p.product_type AS productType, " +
            "a.agent_name AS agentName, mer.merchant_name AS merchantName " +
            "FROM machine m " +
            "LEFT JOIN product p ON m.product_id = p.id AND p.deleted = 0 " +
            "LEFT JOIN agent a ON m.agent_id = a.id AND a.deleted = 0 " +
            "LEFT JOIN merchant mer ON m.merchant_id = mer.id AND mer.deleted = 0 " +
            "WHERE m.agent_id = #{agentId} AND m.deleted = 0 " +
            "ORDER BY m.create_time DESC")
    List<Machine> selectByAgentId(@Param("agentId") Long agentId);

    /**
     * 根据商户ID查询机器列表
     *
     * @param merchantId 商户ID
     * @return 机器列表
     */
    @Select("SELECT m.*, p.product_name AS productName, p.product_type AS productType, " +
            "a.agent_name AS agentName, mer.merchant_name AS merchantName " +
            "FROM machine m " +
            "LEFT JOIN product p ON m.product_id = p.id AND p.deleted = 0 " +
            "LEFT JOIN agent a ON m.agent_id = a.id AND a.deleted = 0 " +
            "LEFT JOIN merchant mer ON m.merchant_id = mer.id AND mer.deleted = 0 " +
            "WHERE m.merchant_id = #{merchantId} AND m.deleted = 0 " +
            "ORDER BY m.create_time DESC")
    List<Machine> selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 根据产品ID查询机器列表
     *
     * @param productId 产品ID
     * @return 机器列表
     */
    @Select("SELECT m.*, p.product_name AS productName, p.product_type AS productType, " +
            "a.agent_name AS agentName, mer.merchant_name AS merchantName " +
            "FROM machine m " +
            "LEFT JOIN product p ON m.product_id = p.id AND p.deleted = 0 " +
            "LEFT JOIN agent a ON m.agent_id = a.id AND a.deleted = 0 " +
            "LEFT JOIN merchant mer ON m.merchant_id = mer.id AND mer.deleted = 0 " +
            "WHERE m.product_id = #{productId} AND m.deleted = 0 " +
            "ORDER BY m.create_time DESC")
    List<Machine> selectByProductId(@Param("productId") Long productId);

    /**
     * 统计指定状态的机器数量
     *
     * @param status 状态
     * @return 机器数量
     */
    @Select("SELECT COUNT(*) FROM machine WHERE status = #{status} AND deleted = 0")
    long countByStatus(@Param("status") Integer status);

    /**
     * 统计指定代理商的机器数量
     *
     * @param agentId 代理商ID
     * @return 机器数量
     */
    @Select("SELECT COUNT(*) FROM machine WHERE agent_id = #{agentId} AND deleted = 0")
    long countByAgentId(@Param("agentId") Long agentId);

    /**
     * 统计指定商户的机器数量
     *
     * @param merchantId 商户ID
     * @return 机器数量
     */
    @Select("SELECT COUNT(*) FROM machine WHERE merchant_id = #{merchantId} AND deleted = 0")
    long countByMerchantId(@Param("merchantId") Long merchantId);
}

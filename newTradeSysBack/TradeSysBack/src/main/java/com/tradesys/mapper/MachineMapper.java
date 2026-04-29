package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Machine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MachineMapper extends BaseMapper<Machine> {

    @Select("SELECT * FROM machine WHERE product_id = #{productId}")
    List<Machine> selectByProductId(@Param("productId") Long productId);

    @Select("SELECT * FROM machine WHERE agent_id = #{agentId}")
    List<Machine> selectByAgentId(@Param("agentId") Long agentId);

    @Select("SELECT * FROM machine WHERE merchant_id = #{merchantId}")
    List<Machine> selectByMerchantId(@Param("merchantId") Long merchantId);

    @Select("SELECT * FROM machine WHERE sn = #{sn}")
    Machine selectBySn(@Param("sn") String sn);

    @Select("SELECT * FROM machine WHERE machine_no = #{machineNo}")
    Machine selectByMachineNo(@Param("machineNo") String machineNo);

    @Update("UPDATE machine SET merchant_id = #{merchantId}, status = 2 WHERE id = #{machineId}")
    void bindMerchant(@Param("machineId") Long machineId, @Param("merchantId") Long merchantId);

    @Update("UPDATE machine SET merchant_id = NULL, status = 1 WHERE id = #{machineId}")
    void unbindMerchant(@Param("machineId") Long machineId);

    @Select("SELECT COUNT(*) FROM machine WHERE status = #{status}")
    int countByStatus(@Param("status") Integer status);
}

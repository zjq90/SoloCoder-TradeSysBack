package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Channel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 通道Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface ChannelMapper extends BaseMapper<Channel> {

    /**
     * 查询所有启用的通道
     *
     * @return 通道列表
     */
    @Select("SELECT * FROM channel WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<Channel> selectAllActive();

    /**
     * 根据通道类型查询通道列表
     *
     * @param channelType 通道类型
     * @return 通道列表
     */
    @Select("SELECT * FROM channel WHERE channel_type = #{channelType} AND status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<Channel> selectByType(@Param("channelType") Integer channelType);

    /**
     * 统计通道数量
     *
     * @param status 状态（可选）
     * @return 通道数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM channel WHERE deleted = 0 " +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</script>")
    long countByStatus(@Param("status") Integer status);
}

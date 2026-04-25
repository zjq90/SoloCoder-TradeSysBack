package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.Channel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ChannelMapper extends BaseMapper<Channel> {

    @Select("SELECT c.*, " +
            "COALESCE(cds.total_amount, 0) AS today_amount, " +
            "COALESCE(cds.total_count, 0) AS today_count, " +
            "CASE WHEN c.daily_limit > 0 THEN (c.daily_limit - COALESCE(cds.total_amount, 0)) ELSE NULL END AS remaining_amount " +
            "FROM channel c " +
            "LEFT JOIN channel_daily_stat cds ON c.id = cds.channel_id AND cds.stat_date = CURDATE() " +
            "WHERE c.id = #{channelId}")
    Channel selectDetailById(@Param("channelId") Long channelId);

    @Select("SELECT * FROM channel WHERE channel_type = #{channelType} AND status = 1 ORDER BY id")
    java.util.List<Channel> selectByType(@Param("channelType") Integer channelType);
}

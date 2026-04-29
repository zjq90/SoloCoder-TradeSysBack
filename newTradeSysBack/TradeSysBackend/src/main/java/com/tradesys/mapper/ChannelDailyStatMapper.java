package com.tradesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradesys.entity.ChannelDailyStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 通道日交易统计Mapper接口
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Mapper
public interface ChannelDailyStatMapper extends BaseMapper<ChannelDailyStat> {

    /**
     * 根据通道ID查询统计列表
     *
     * @param channelId 通道ID
     * @return 统计列表
     */
    @Select("SELECT cds.*, c.channel_name AS channelName " +
            "FROM channel_daily_stat cds " +
            "LEFT JOIN channel c ON cds.channel_id = c.id AND c.deleted = 0 " +
            "WHERE cds.channel_id = #{channelId} AND cds.deleted = 0 " +
            "ORDER BY cds.stat_date DESC")
    List<ChannelDailyStat> selectByChannelId(@Param("channelId") Long channelId);

    /**
     * 根据日期查询统计列表
     *
     * @param statDate 统计日期
     * @return 统计列表
     */
    @Select("SELECT cds.*, c.channel_name AS channelName " +
            "FROM channel_daily_stat cds " +
            "LEFT JOIN channel c ON cds.channel_id = c.id AND c.deleted = 0 " +
            "WHERE cds.stat_date = #{statDate} AND cds.deleted = 0 " +
            "ORDER BY cds.channel_id ASC")
    List<ChannelDailyStat> selectByDate(@Param("statDate") LocalDate statDate);

    /**
     * 根据通道ID和日期查询统计
     *
     * @param channelId 通道ID
     * @param statDate  统计日期
     * @return 统计信息
     */
    @Select("SELECT cds.*, c.channel_name AS channelName " +
            "FROM channel_daily_stat cds " +
            "LEFT JOIN channel c ON cds.channel_id = c.id AND c.deleted = 0 " +
            "WHERE cds.channel_id = #{channelId} AND cds.stat_date = #{statDate} AND cds.deleted = 0")
    ChannelDailyStat selectByChannelIdAndDate(@Param("channelId") Long channelId, @Param("statDate") LocalDate statDate);
}
